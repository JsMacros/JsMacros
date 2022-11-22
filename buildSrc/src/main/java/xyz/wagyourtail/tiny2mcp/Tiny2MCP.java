package xyz.wagyourtail.tiny2mcp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Tiny2MCP {
    private static final String mcpconfig_url = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp/%1$s/mcp-%1$s-srg.zip";
    private static final String intermediary_url = "https://maven.legacyfabric.net/net/fabricmc/intermediary/%1$s/intermediary-%1$s-v2.jar";
    private static final String yarn_url = "https://maven.legacyfabric.net/net/fabricmc/yarn/%1$s+%2$s/yarn-%1$s+%2$s-v2.jar";


    public static String remap(File outDir, String mc_version, String yarn_version) throws IOException {
        if (!outDir.exists()) outDir.mkdirs();
        String yarnName = "yarn" + yarn_version.split("\\.")[1] + "_custom";
        File outFile = new File(outDir, String.format("mcp_%s-%s.zip", yarnName.replace("_", "-"), mc_version));
        if (outFile.exists()) {
            return yarnName;
        }


        // download mcp config and get srg file
        URL mcpConfig = new URL(String.format(mcpconfig_url, mc_version));
        String srgMappings = readZipContentFromURL(mcpConfig, "joined.srg").get("joined.srg");

        // download intermediary mappings
        URL intermediary = new URL(String.format(intermediary_url, mc_version));
        String intermediaryMappings = readZipContentFromURL(intermediary, "mappings/mappings.tiny").get("mappings/mappings.tiny");

        // downnload yarn mappings
        URL yarn = new URL(String.format(yarn_url, mc_version, yarn_version));
        String yarnMappings = readZipContentFromURL(yarn, "mappings/mappings.tiny").get("mappings/mappings.tiny");

        // remap
        Mappings mappings = new Mappings();

        mappings.parseSRG(srgMappings);
        mappings.parseTiny(intermediaryMappings, Mappings.MappingType.OBF, Mappings.MappingType.INTERMEDIARY);
        mappings.parseTiny(yarnMappings, Mappings.MappingType.INTERMEDIARY, Mappings.MappingType.YARN);

        Map<String, String> mappingsMap = mappings.exportMCP(Mappings.MappingType.YARN);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                for (Map.Entry<String, String> entry : mappingsMap.entrySet()) {
                    ZipEntry zipEntry = new ZipEntry(entry.getKey());
                    zos.putNextEntry(zipEntry);
                    zos.write(entry.getValue().getBytes());
                    zos.closeEntry();
                }
            }
        }

        System.out.println("MCP mappings " + yarnName + " created!");
        return yarnName;
    }

    private static Map<String, String> readZipContentFromURL(URL remote, String... files) throws IOException {
        try (ZipInputStream is = new ZipInputStream(new BufferedInputStream(remote.openStream(), 1024))) {
            byte[] buff = new byte[1024];
            ZipEntry entry;
            Set<String> fileList = new HashSet<>(Arrays.asList(files));
            Map<String, String> fileContents = new HashMap<>();
            while ((entry = is.getNextEntry()) != null) {
                if (fileList.contains(entry.getName())) {
                    StringBuilder builder = new StringBuilder();
                    int read;
                    while ((read = is.read(buff, 0, 1024)) > 0) {
                        builder.append(new String(buff, 0, read));
                    }
                    fileContents.put(entry.getName(), builder.toString());
                }
            }
            return fileContents;
        }
    }

}
