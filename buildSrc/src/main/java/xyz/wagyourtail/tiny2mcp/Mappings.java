package xyz.wagyourtail.tiny2mcp;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mappings {
    Map<String, ClassMapping> classMappings = new LinkedHashMap<>();
    Map<String, List<MethodMapping>> srgMethodMappings = new LinkedHashMap<>();
    Map<String, List<FieldMapping>> srgFieldMappings = new LinkedHashMap<>();
    Map<String, String> overwrites = new LinkedHashMap<>();

    public Mappings() {
        overwrites.put("func_76709_c", "isChunkSaved");
    }

    public void parseSRG(String mappingFile) {
        for (String line : mappingFile.split("\r?\n")) {
            if (line.startsWith("CL:")) {
                String[] split = line.split(" ");
                String obfName = split[1];
                String srgName = split[2];
                classMappings.computeIfAbsent(obfName, ClassMapping::new).mappings.put(MappingType.SRG, srgName);
            } else if (line.startsWith("FD:")) {
                String[] split = line.split(" ");
                String obfName = split[1];
                String srgName = split[2];
                String obfClassPart = obfName.substring(0, obfName.lastIndexOf('/'));
                String srgClassPart = srgName.substring(0, srgName.lastIndexOf('/'));
                String obfFieldName = obfName.substring(obfName.lastIndexOf('/') + 1);
                String srgFieldName = srgName.substring(srgName.lastIndexOf('/') + 1);
                ClassMapping mapping = classMappings.computeIfAbsent(obfClassPart, ClassMapping::new);
                mapping.mappings.put(MappingType.SRG, srgClassPart);
                FieldMapping fmapping = mapping.fields.computeIfAbsent(obfFieldName, k -> new FieldMapping(mapping, k));
                fmapping.mappings.put(MappingType.SRG, srgFieldName);
                if (srgFieldName.startsWith("field_"))
                    srgFieldMappings.computeIfAbsent(srgFieldName, k -> new ArrayList<>()).add(fmapping);
            } else if (line.startsWith("MD:")) {
                String[] split = line.split("\\s+");
                String obfName = split[1];
                String obfDescriptor = split[2];
                String srgName = split[3];
                String obfClassPart = obfName.substring(0, obfName.lastIndexOf('/'));
                String srgClassPart = srgName.substring(0, srgName.lastIndexOf('/'));
                String obfMethodName = obfName.substring(obfName.lastIndexOf('/') + 1);
                String srgMethodName = srgName.substring(srgName.lastIndexOf('/') + 1);
                ClassMapping mapping = classMappings.computeIfAbsent(obfClassPart, ClassMapping::new);
                mapping.mappings.put(MappingType.SRG, srgClassPart);
                MethodMapping mmapping = mapping.methods.computeIfAbsent(obfMethodName + obfDescriptor, k -> new MethodMapping(mapping, k));
                mmapping.mappings.put(MappingType.SRG, srgMethodName);
                if (srgMethodName.startsWith("func_"))
                    srgMethodMappings.computeIfAbsent(srgMethodName, (k) -> new ArrayList<>()).add(mmapping);
            }
        }
    }

    public ClassMapping getOrAddClass(String cName, MappingType type) {
        if (type == MappingType.OBF) {
            return classMappings.computeIfAbsent(cName, ClassMapping::new);
        } else {
            for (ClassMapping mapping : classMappings.values()) {
                if (mapping.mappings.get(type).equals(cName)) {
                    return mapping;
                }
            }
            ClassMapping c = classMappings.computeIfAbsent(cName, ClassMapping::new);
            c.mappings.put(type, cName);
            return c;
        }
    }

    public void parseTiny(String mappingFile, MappingType from, MappingType to) {
        ClassMapping currentClass = null;
        for (String line : mappingFile.split("\n")) {
            String[] split = line.split("\t", -1);
            try {
                switch (split[0]) {
                    case "c":
                        currentClass = getOrAddClass(split[1], from);
                        currentClass.mappings.put(to, split[2]);
                        break;
                    case "":
                        switch (split[1]) {
                            case "f":
                                String obfName = split[3];
                                String tinyName = split[4];
                                FieldMapping fmapping = currentClass.getOrAddField(obfName, from);
                                fmapping.mappings.put(to, tinyName);
                                break;
                            case "m":
                                String obfMethodDescriptor = split[2];
                                String obfMethodName = split[3];
                                String tinyMethodName = split[4];
                                MethodMapping mmapping = currentClass.getOrAddMethod(
                                    obfMethodName, obfMethodDescriptor,
                                    from
                                );
                                mmapping.mappings.put(to, tinyMethodName);
                                break;
                            case "":
                                switch (split[2]) {
                                    case "p":
                                        //TODO: parse parameters
                                        break;
                                }
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(String.join(",", split));
                throw e;
            }
        }
    }

    public Map<String, String> exportMCP(MappingType type) {
        Map<String, String> mcpMappings = new HashMap<>();

        StringBuilder methods = new StringBuilder("searge,name,side,desc\n");
        Map<String, String> entries = new LinkedHashMap<>();
        for (Map.Entry<String, List<MethodMapping>> mapping : srgMethodMappings.entrySet()) {
            for (MethodMapping methodMapping : mapping.getValue()) {
                if (methodMapping.mappings.containsKey(type)) {
                    if (overwrites.containsKey(mapping.getKey())) {
                        entries.put(
                            mapping.getKey(),
                            overwrites.get(mapping.getKey())
                        );
                    } else {
                        entries.put(
                            mapping.getKey(),
                            methodMapping.mappings.get(type)
                        );
                    }
                    break;
                }
            }
        }
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            methods.append(entry.getKey()).append(",").append(entry.getValue()).append(",2,\n");
        }
        mcpMappings.put("methods.csv", methods.toString());

        StringBuilder fields = new StringBuilder("searge,name,side,desc\n");
        for (Map.Entry<String, List<FieldMapping>> mapping : srgFieldMappings.entrySet()) {
            for (FieldMapping fieldMapping : mapping.getValue()) {
                if (fieldMapping.mappings.containsKey(type)) {
                    fields.append(mapping.getKey()).append(",").append(fieldMapping.mappings.get(type)).append(",2,\n");
                    break;
                }
            }
        }
        mcpMappings.put("fields.csv", fields.toString());

        StringBuilder params = new StringBuilder("param,name,side\n");

        mcpMappings.put("params.csv", params.toString());

        return mcpMappings;
    }

    public static enum MappingType {
        OBF, INTERMEDIARY, SRG, YARN
    }

    public class AbstractMapping {
        public final Map<MappingType, String> mappings = new LinkedHashMap<>();

        public AbstractMapping(String obfName) {
            mappings.put(MappingType.OBF, obfName);
        }
    }

    public class ClassMapping extends AbstractMapping {
        public final Map<String, MethodMapping> methods = new HashMap<>();
        public final Map<String, FieldMapping> fields = new HashMap<>();

        public ClassMapping(String obfName) {
            super(obfName);
        }

        public FieldMapping getOrAddField(String name, MappingType type) {
            if (type == MappingType.OBF) {
                return fields.computeIfAbsent(name, k -> new FieldMapping(this, k));
            } else {
                for (FieldMapping mapping : fields.values()) {
                    if (mapping.mappings.getOrDefault(type, "DONT MATCH PLZ").equals(name)) {
                        return mapping;
                    }
                }
                FieldMapping f = fields.computeIfAbsent(name, k -> new FieldMapping(this, k));
                f.mappings.put(type, name);
                return f;
            }
        }

        public MethodMapping getOrAddMethod(String name, String typeDesc, MappingType type) {
            if (type == MappingType.OBF) {
                return methods.computeIfAbsent(name + typeDesc, k -> new MethodMapping(this, k));
            } else {
                for (MethodMapping mapping : methods.values()) {
                    if (mapping.mappings.getOrDefault(type, "DONT MATCH PLZ").equals(name)) {
                        String obfDesc = mapping.mappings.get(MappingType.OBF);
                        if (obfDesc.indexOf('(') == -1) {
                            return mapping;
                        }
                        obfDesc = obfDesc.substring(obfDesc.indexOf('('));
                        if (obfDesc.equals(remapDesc(typeDesc, type))) {
                            return mapping;
                        }
                    }
                }
                MethodMapping m = methods.computeIfAbsent(name + remapDesc(typeDesc, type), k -> new MethodMapping(this, k));
                m.mappings.put(type, name);
                return m;
            }
        }

        public String remapDesc(String desc, MappingType type) {
            if (type == MappingType.OBF) {
                return desc;
            }
            return replaceFunction(desc, Pattern.compile("L([^;]+);"), (m) -> {
                String className = m[1];
                for (ClassMapping mapping : classMappings.values()) {
                    if (mapping.mappings.get(type).equals(className)) {
                        return "L" + mapping.mappings.get(MappingType.OBF) + ";";
                    }
                }
                return m[0];
            });
        }

        public String replaceFunction(String str, Pattern pattern, Function<String[], String> replaceFn) {
            Matcher m = pattern.matcher(str);
            int offset = 0;
            while (m.find()) {
                String[] args = new String[m.groupCount() + 1];
                for (int i = 0; i < args.length; ++i) {
                    args[i] = m.group(i);
                }
                String replacement = replaceFn.apply(args);
                int len = m.end() - m.start();
                str = str.substring(0, m.start() + offset) + replacement + str.substring(m.end() + offset);
                offset += replacement.length() - len;
            }
            return str;
        }
    }

    public abstract class ClassItemMapping extends AbstractMapping {
        public final ClassMapping classMapping;
        public ClassItemMapping(ClassMapping parent, String obfName) {
            super(obfName);
            classMapping = parent;
        }
    }

    public class MethodMapping extends ClassItemMapping {
        public MethodMapping(ClassMapping parent, String obfName) {
            super(parent, obfName);
        }
    }

    public class FieldMapping extends ClassItemMapping {
        public FieldMapping(ClassMapping parent, String obfName) {
            super(parent, obfName);
        }
    }
}
