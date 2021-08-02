package xyz.wagyourtail.jsmacros.core.classes;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.impl.FReflection;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Wagyourtail
 * @since 1.3.1
 */
 @SuppressWarnings("unused")
public class Mappings {
    public final String mappingsource;
    private final Map<String, ClassData> mappings = new LinkedHashMap<>();
    private final Map<String, ClassData> reversedMappings = new LinkedHashMap<>();
    
    public Mappings(String mappingsource) {
        this.mappingsource = mappingsource;
    }
    
    protected void loadMappings() throws IOException {
        StringBuilder builder = new StringBuilder();
        if (mappingsource.endsWith(".tiny")) {
            if (mappingsource.startsWith("http")) {
                builder.append(new BufferedReader(new InputStreamReader(new URL(mappingsource).openStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")));
            } else {
                builder.append(new BufferedReader(new FileReader(Core.instance.config.macroFolder.toPath().resolve(mappingsource).toFile())).lines().collect(Collectors.joining("\n")));
            }
        } else {
            ZipInputStream zis;
            if (mappingsource.startsWith("http")) {
                zis = new ZipInputStream(new BufferedInputStream(new URL(mappingsource).openStream(), 1024));
            } else {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(Core.instance.config.macroFolder.toPath().resolve(mappingsource).toFile()), 1024));
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
                String[] parts = line.split("\\s+", -1);
                if (parts[0].equals("c")) {
                    mappings.put(parts[1], currentClass = new ClassData(parts[2]));
                } else {
                    switch (parts[1]) {
                        case "m":
                            assert currentClass != null;
                            currentClass.methods.put(parts[3] + parts[2], new MethodData(parts[4], () -> remapSig(parts[2], mappings)));
                            break;
                        case "f":
                            assert currentClass != null;
                            currentClass.fields.put(parts[3], parts[4]);
                            break;
                        default:
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }

    protected void reverseMappings() throws IOException {
        if (mappings.isEmpty()) loadMappings();
        for (Map.Entry<String, ClassData> clazz : mappings.entrySet()) {
            ClassData mappedClass = clazz.getValue();
            ClassData reversedClass = new ClassData(clazz.getKey());
            reversedMappings.put(mappedClass.name, reversedClass);
            mappedClass.methods.forEach((name, methodData) -> {
                String[] methodParts = name.split("\\(");
                reversedClass.methods.put(methodData.name + methodData.sig.get(), new MethodData(methodParts[0], () -> "(" + methodParts[1]));
            });

            mappedClass.fields.forEach((obf, named) -> {
                reversedClass.fields.put(named, obf);
            });
        }
    }
    
    private static final Pattern methodParts = Pattern.compile("\\((.*?)\\)(.+)");
    private static final Pattern sig = Pattern.compile("L(.+?);");

    private static String remapSig(String sign, Map<String, ClassData> mappings) {
        Matcher m = methodParts.matcher(sign);
        if (!m.find()) throw new RuntimeException(String.format("method signature \"%s\" invalid", sign));
        Matcher cfinder = sig.matcher(sign);
        int offset = 0;

        while (cfinder.find()) {
            String cls = cfinder.group(1);
            if (mappings.containsKey(cls)) cls = mappings.get(cls).name;
            sign = sign.substring(0, cfinder.start(1) + offset) + cls + sign.substring(cfinder.end(1) + offset);
            offset += cls.length() - cfinder.group(1).length();
        }

        return sign;
    }

    /**
     * @return mappings from Intermediary to Named
     * @since 1.3.1
     * @throws IOException will throw if malformed url/path
     */
    public Map<String, ClassData> getMappings() throws IOException {
        if (mappings.isEmpty()) loadMappings();
        return mappings;
    }
    
    /**
     * @return mappings from Named to Intermediary
     * @since 1.3.1
     * @throws IOException will throw if malformed url/path
     */
    public Map<String, ClassData> getReversedMappings() throws IOException {
        if (reversedMappings.isEmpty()) reverseMappings();
        return reversedMappings;
    }

    /**
     * @since 1.6.0
     * @return
     */
    public <T> MappedClass<T> remapClass(T instance) throws IOException {
        getReversedMappings();
        return new MappedClass<T>(instance, (Class<T>) instance.getClass());
    }

    public static class ClassData {
        public final Map<String, MethodData> methods = new LinkedHashMap<>();
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
        public final Supplier<String> sig;
        
        public MethodData(String name, Supplier<String> sig) {
            this.name = name;
            this.sig = sig;
            
        }
        
        public String toString() {
            return name + sig.get();
        }
    }

    public class MappedClass<T> {
        private final T instance;
        private final Class<T> tClass;
        private final Set<Class<?>> inheritance = new LinkedHashSet<>();


        public MappedClass(T instance, Class<T> tClass) {
            this.instance = instance;
            this.tClass = tClass;
        }

        private synchronized Set<Class<?>> getInheritance() {
            if (!inheritance.isEmpty()) return inheritance;
            Class<?> current = tClass;
            do {
                inheritance.add(current);
                inheritance.addAll(Arrays.stream(current.getInterfaces()).toList());
            } while ((current = current.getSuperclass()) != Object.class);
            inheritance.add(Object.class);
            return inheritance;
        }

        private Field findField(Class<?> asClass, String fieldName) throws NoSuchFieldException, IOException {
            ClassData cls = getMappings().get(asClass.getCanonicalName());
            String intFieldName;
            if (cls != null) {
                ClassData revd = getReversedMappings().get(cls.name);
                intFieldName = revd.fields.get(fieldName);
            } else {
                intFieldName = fieldName;
            }

            Field fd;
            if (intFieldName != null) {
                try {
                    fd = asClass.getDeclaredField(intFieldName);
                } catch (NoSuchFieldException e) {
                    fd = asClass.getDeclaredField(fieldName);
                }
            } else {
                fd = asClass.getDeclaredField(fieldName);
            }
            fd.setAccessible(true);
            return fd;
        }

        public Object getFieldValue(String fieldName) throws NoSuchFieldException, IllegalAccessException, IOException {
            Field fd = null;
            for (Class<?> cls : getInheritance()) {
                try {
                    fd = findField(cls, fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }
            if (fd == null) throw new NoSuchFieldException();
            return fd.get(instance);
        }

        public Object getFieldValue(String asClass, String fieldName) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
            ClassData revd = getReversedMappings().get(asClass);
            Class<?> cls;
            if (revd != null) {
                try {
                    cls = Class.forName(revd.name.replace("/", "."));
                } catch (ClassNotFoundException ex) {
                    cls = Class.forName(asClass.replace("/", "."));
                }
            } else {
                cls = Class.forName(asClass.replace("/", "."));
            }
            return findField(cls, fieldName).get(instance);
        }

        public void setFieldValue(String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException, IOException {
            Field fd = null;
            for (Class<?> cls : getInheritance()) {
                try {
                    fd = findField(cls, fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }
            if (fd == null) throw new NoSuchFieldException();
            fd.set(instance, fieldValue);
        }

        public void setFieldValue(String asClass, String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException {
            ClassData revd = getReversedMappings().get(asClass);
            Class<?> cls;
            if (revd != null) {
                try {
                    cls = Class.forName(revd.name.replace("/", "."));
                } catch (ClassNotFoundException ex) {
                    cls = Class.forName(asClass.replace("/", "."));
                }
            } else {
                cls = Class.forName(asClass.replace("/", "."));
            }
            findField(cls, fieldName).set(instance, fieldValue);
        }

        public Object invokeMethod(String methodName, Object ...params) {
            throw new NullPointerException("NOT YET IMPLEMENTED");
        }
    }
}
