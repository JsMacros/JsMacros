package xyz.wagyourtail.jsmacros.client;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JsMacrosEarlyRiser implements IMixinConnector {
    public static final Logger LOGGER  = LogManager.getLogger("JsMacros EarlyRiser");
    public static final List<URL> urls = new ArrayList<>();
    public static final Unsafe unsafe;
    public static ClassLoader loader;

    static {
        Unsafe unsafe1;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe1 = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            unsafe1 = null;
        }
        unsafe = unsafe1;
    }
    @Override
    public void connect() {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        LOGGER.log(Level.INFO, "[JsMacros] Loading Mixins.");
        Mixins.addConfiguration("jsmacros.mixins.json");
        Mixins.addConfiguration("jsmacros-forge.mixins.json");
        Mixins.addConfiguration("fabric-command-api-v1.mixins.json");
        try {
            loadFakeFabricDeps();
            loadManifestDeps();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //        try {
        //            LOGGER.info(ClassLoader.getPlatformClassLoader());
        //            ClassLoader parentCL = ClassLoader.getSystemClassLoader();
        //            Field ucpf = parentCL.getClass().getSuperclass().getDeclaredField("ucp");
        //            Object ucp = unsafe.getObject(parentCL, unsafe.objectFieldOffset(ucpf));
        //            unsafe.putObject(ucp, unsafe.objectFieldOffset(ucp.getClass().getDeclaredField("loaders")), new ArrayList<>());
        //            for (URL url : urls) {
        //                addURL(ucp, url);
        //            }
        //            LOGGER.info("ATTEMPT PRELOAD ENGINE");
        //            LOGGER.info(parentCL.loadClass("org.graalvm.polyglot.Engine"));
        //        } catch (NoSuchFieldException | MalformedURLException | ClassNotFoundException e) {
        //            e.printStackTrace();
        //        }
        TransformingClassLoader cl = ((TransformingClassLoader) JsMacrosEarlyRiser.class.getClassLoader());
        cl.setFallbackClassLoader(loader = new URLClassLoader(urls.toArray(URL[]::new), ClassLoader.getPlatformClassLoader()));
    }

    //    public void addURL(Object ucp, URL url) throws NoSuchFieldException, MalformedURLException {
    //        url = new URL(url.toString().replaceAll("^(?:file|union):/", "jar:file:/") + "!/");
    //        System.out.println("ADDING URL: " + url.toString());
    //        Class<?> ucpc = ucp.getClass();
    //        synchronized (ucp) {
    //            if (url == null || unsafe.getBoolean(ucp, unsafe.objectFieldOffset(ucpc.getDeclaredField("closed")))) {
    //                System.out.println("CLOSED?");
    //                return;
    //            }
    //            ArrayDeque<URL> unopenedURLS = (ArrayDeque<URL>) unsafe.getObject(ucp, unsafe.objectFieldOffset(ucpc.getDeclaredField("unopenedUrls")));
    //            synchronized (unopenedURLS) {
    //                ArrayList<URL> path = (ArrayList<URL>) unsafe.getObject(ucp, unsafe.objectFieldOffset(ucpc.getDeclaredField("path")));
    //                if (!path.contains(url)) {
    //                    unopenedURLS.addLast(url);
    //                    path.add(url);
    //                }
    //            }
    //        }
    //    }

    public void loadFakeFabricDeps() throws Exception {
        new FakeFabricLoader(new File(FMLLoader.getGamePath().toFile(), "mods/jsmacros")).loadMixins();
    }

    public void loadManifestDeps() throws IOException {
        Manifest manifest = FMLLoader.getLoadingModList().getModFileById("jsmacros").getFile().getSecureJar().getManifest();
        Attributes attr = manifest.getMainAttributes();
        String[] value = attr.getValue("JsMacrosDeps").split("\\s+");
        extract(value);
    }

    public void extract(String[] mods) throws IOException {
        File modFolder = new File(FMLLoader.getGamePath().toFile(), "mods/jsmacros/dependencies");
        if (!modFolder.exists() && !modFolder.mkdirs()) throw new RuntimeException("failed to create deps folder dir");
        for (String mod : mods) {
            File modfile = new File(modFolder, mod);
            LOGGER.log(Level.INFO, "[JsMacros] Extracting Dependency: " + mod + " to: " + modfile);
            java.nio.file.Files.copy(JsMacrosEarlyRiser.class.getResourceAsStream("/META-INF/jars/"+mod),
                modfile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
            urls.add(modfile.toURI().toURL());
        }
    }

    public static class ShimClassLoader extends URLClassLoader {
        public ShimClassLoader() {
            super(new URL[] {}, ClassLoader.getPlatformClassLoader());
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }

    }
}