package xyz.wagyourtail.jsmacros.core.classes;

import xyz.wagyourtail.jsmacros.core.Core;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Wagyourtail
 * @since 1.3.1
 */
public class Mappings {
    public final String mappingsource;
    public final Map<String, ClassData> intMappings = new LinkedHashMap<>();
    public final Map<String, ClassData> namedMappings = new LinkedHashMap<>();
    
    public Mappings(String mappingsource) {
        this.mappingsource = mappingsource;
    }
    
    protected void loadMappings() throws IOException {
        StringBuilder builder = new StringBuilder();
        if (mappingsource.endsWith(".tiny")) {
            if (mappingsource.startsWith("http")) {
                builder.append(new BufferedReader(new InputStreamReader(new URL(mappingsource).openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
            } else {
                builder.append(new BufferedReader(new FileReader(new File(Core.instance.config.macroFolder, mappingsource))).lines().collect(Collectors.joining("\n")));
            }
        } else {
            ZipInputStream zis;
            if (mappingsource.startsWith("http")) {
                zis = new ZipInputStream(new BufferedInputStream(new URL(mappingsource).openStream(), 1024));
            } else {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(new File(Core.instance.config.macroFolder, mappingsource)), 1024));
            }
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null && !e.getName().equals("mappings/mappings.tiny"));
            int read;
            byte[] buff = new byte[1024];
            while ((read = zis.read(buff, 0, 1024)) > 0) {
                builder.append(new String(buff, 0, read));
            }
        }
        parseMappings(builder.toString());
    }
    
    protected void parseMappings(String rawmappings) {
        ClassData currentClass = null;
        for (String line : rawmappings.split("\n")) {
            try {
                String[] parts = line.trim().split("\\s+");
                switch (parts[0]) {
                    case "c":
                        intMappings.put(parts[1], currentClass = new ClassData(parts[2]));
                        break;
                    case "m":
                        assert currentClass != null;
                        currentClass.methods.computeIfAbsent(parts[1], (e) -> new LinkedList<>()).add(new MethodData(parts[3], parts[2]));
                        break;
                    case "f":
                        assert currentClass != null;
                        currentClass.fields.put(parts[1], parts[3]);
                        break;
                    default:
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }
    
    private final Pattern methodParts = Pattern.compile("\\((.*?)\\)(.+)");
    private final Pattern sig = Pattern.compile("L(.+);");
    
    public void reverseMappings() throws IOException {
        if (intMappings.isEmpty()) loadMappings();
        for (Map.Entry<String, ClassData> clazz : intMappings.entrySet()) {
            ClassData currentClass;
            namedMappings.put(clazz.getValue().name, currentClass = new ClassData(clazz.getKey()));
            for (Map.Entry<String, List<MethodData>> method : clazz.getValue().methods.entrySet()) {
                for (MethodData md : method.getValue()) {
                    Matcher m = methodParts.matcher(md.sig);
                    if (!m.find()) throw new RuntimeException("failed to reverse mappings, method signature invalid");
                    String params = m.group(1);
                    String ret = m.group(2);
                    String namedname = md.name;
                    Matcher pm = sig.matcher(params);
                    int offset = 0;
                    while (pm.find()) {
                        String p = pm.group(1);
                        String np = p;
                        if (intMappings.containsKey(np)) np = intMappings.get(np).name;
                        params = params.substring(0, pm.start(1) + offset) + np + params.substring(pm.end(1) + offset);
                        offset = np.length() - p.length();
                    }
                    Matcher rm = sig.matcher(ret);
                    offset = 0;
                    while (rm.find()) {
                        String r = rm.group(1);
                        String nr = r;
                        if (intMappings.containsKey(nr)) nr = intMappings.get(nr).name;
                        ret = ret.substring(0, rm.start(1) + offset) + nr + ret.substring(rm.end(1) + offset);
                        offset = nr.length() - r.length();
                    }
                    currentClass.methods.computeIfAbsent(namedname, (e) -> new LinkedList<>()).add(new MethodData(method.getKey(), "(" + params + ")" + ret));
                }
            }
            for (Map.Entry<String, String> field : clazz.getValue().fields.entrySet()) {
                currentClass.fields.put(field.getValue(), field.getKey());
            }
        }
    }
    
    /**
     * @return mappings from Intermediary to Named
     * @since 1.3.1
     * @throws IOException
     */
    public Map<String, ClassData> getMappings() throws IOException {
        if (intMappings.isEmpty()) loadMappings();
        return intMappings;
    }
    
    /**
     * @return mappings from Named to Intermediary
     * @since 1.3.1
     * @throws IOException
     */
    public Map<String, ClassData> getReversedMappings() throws IOException {
        if (namedMappings.isEmpty()) reverseMappings();
        return namedMappings;
    }
    
    public static class ClassData {
        public final Map<String, List<MethodData>> methods = new LinkedHashMap<>();
        public final Map<String, String> fields =  new LinkedHashMap<>();
        public final String name;
        
        public ClassData(String name) {
            this.name = name;
        }
        
        public String toString() {
            return String.format("{\"methods\": %s, \"fields\": %s}", methods.toString(), fields.toString());
        }
    }
    
    public static class MethodData {
        public final String name;
        public final String sig;
        
        public MethodData(String name, String sig) {
            this.name = name;
            this.sig = sig;
            
        }
        
        public String toString() {
            return name + sig;
        }
    }
}
