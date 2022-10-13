package xyz.wagyourtail.jsmacros.forge.client;

import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.*;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransformArchModsTask implements ITransformationService{
    public TransformStore transformStore;
    public Method addTransformer;

    public TransformArchModsTask(TransformStore ts) throws NoSuchMethodException {
        this.transformStore = ts;
        addTransformer = TransformStore.class.getDeclaredMethod("addTransformer", TransformTargetLabel.class, ITransformer.class, ITransformationService.class);
        addTransformer.setAccessible(true);
    }

    public static void run() throws IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // get list of arch tweaks
        Set<String> tweakNames = new HashSet<>();
        Files.list(FMLLoader.getGamePath().resolve("mods")).filter(e -> e.toString().endsWith(".jar")).forEach(f -> {
            URI uri = URI.create("jar:" + f.toUri());
            try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                Files.list(fs.getPath("/")).filter(e -> e.toString().contains("architectury_inject")).forEach(e -> {
                    String pkgName = e.toString().replace("/", " ").trim();
                    tweakNames.add(pkgName + "." + "PlatformMethods");
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FileSystemNotFoundException e) {
                e.printStackTrace();
            }
        });

        // add to transform list
        TransformingClassLoader cl = (TransformingClassLoader) TransformArchModsTask.class.getClassLoader();
        Field fd = TransformingClassLoader.class.getDeclaredField("classTransformer");
        fd.setAccessible(true);
        ClassTransformer ct = (ClassTransformer) fd.get(cl);
        fd = ClassTransformer.class.getDeclaredField("transformers");
        fd.setAccessible(true);
        TransformStore ts = (TransformStore) fd.get(ct);

        TransformArchModsTask tat = new TransformArchModsTask(ts);
        ArchTransformer at = new ArchTransformer(tweakNames);
        for (ITransformer.Target target : at.targets) {
            tat.addTransformer(createTL(target), at, tat);
        }
    }

    public static TransformTargetLabel createTL(ITransformer.Target target) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<TransformTargetLabel> c = TransformTargetLabel.class.getDeclaredConstructor(ITransformer.Target.class);
        c.setAccessible(true);
        return c.newInstance(target);
    }

    public <T> void addTransformer(TransformTargetLabel targetLabel, ITransformer<T> transformer, ITransformationService service) throws InvocationTargetException, IllegalAccessException {
        addTransformer.invoke(transformStore, targetLabel, transformer, service);
    }

    @NotNull
    @Override
    public String name() {
        return "JsMacros Architectury Mod Fixer";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void beginScanning(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @NotNull
    @Override
    public List<ITransformer> transformers() {
        return null;
    }

    public static class ArchTransformer implements ITransformer<MethodNode> {
        Set<String> tweakNames;
        Set<Target> targets;

        public ArchTransformer(Set<String> tweakNames) {
            this.tweakNames = tweakNames;
            targets = new HashSet<>();
            for (String tweakName : tweakNames) {
                targets.add(Target.targetMethod(tweakName, "getModLoader", "()Ljava/lang/String;"));
            }
        }

        @NotNull
        @Override
        public MethodNode transform(MethodNode input, ITransformerVotingContext context) {
            System.out.println("Transforming " + context.getClassName() + ";" + input.name);
            //create a method node to return "forge"
            MethodNode mn = new MethodNode(input.access, input.name, input.desc, input.signature, input.exceptions.toArray(new String[0]));
            mn.visitCode();
            mn.visitLdcInsn("forge");
            mn.visitInsn(Opcodes.ARETURN);
            mn.visitMaxs(1, 1);
            mn.visitEnd();
            return mn;
        }

        @NotNull
        @Override
        public TransformerVoteResult castVote(ITransformerVotingContext context) {
            if (tweakNames.contains(context.getClassName())) {
                return TransformerVoteResult.YES;
            }
            System.out.println("[ArchTransformer] No vote for " + context.getClassName());
            return TransformerVoteResult.NO;
        }

        @NotNull
        @Override
        public Set<Target> targets() {
            return targets;
        }

    }

}
