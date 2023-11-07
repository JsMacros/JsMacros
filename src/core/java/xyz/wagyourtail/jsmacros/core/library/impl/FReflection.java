package xyz.wagyourtail.jsmacros.core.library.impl;

import com.google.common.collect.ImmutableList;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.jetbrains.annotations.Nullable;
import org.joor.Reflect;
import xyz.wagyourtail.Util;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.classes.Mappings;
import xyz.wagyourtail.jsmacros.core.classes.WrappedClassInstance;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.ClassBuilder;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.LibraryBuilder;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.ProxyBuilder;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.Neighbor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Functions for getting and using raw java classes, methods and functions.
 * <p>
 * An instance of this class is passed to scripts as the {@code Reflection} variable.
 *
 * @author Wagyourtail
 * @since 1.2.3
 */
@Library("Reflection")
@SuppressWarnings("unused")
public class FReflection extends PerExecLibrary {
    private static final Map<String, List<Class<?>>> JAVA_CLASS_CACHE = new HashMap<>();
    public static final CombinedVariableClassLoader classLoader = new CombinedVariableClassLoader(FReflection.class.getClassLoader());
    private static Mappings remapper = null;

    public FReflection(BaseScriptContext<?> context) {
        super(context);
    }

    /**
     * @param name name of class like {@code path.to.class}
     * @return resolved class
     * @throws ClassNotFoundException
     * @see FReflection#getClass(String, String)
     * @since 1.2.3
     */
    @DocletReplaceTypeParams("C extends string")
    @DocletReplaceParams(
            """
            name: C): GetJava.Type$Reflection<C>;
            function getClass<C extends JavaTypeList | keyof GetJava.Primitives>(name: C"""
    )
    @DocletReplaceReturn("GetJava.Type$Reflection<C>")
    public <T> Class<T> getClass(String name) throws ClassNotFoundException {
        switch (name) {
            case "boolean":
                return (Class<T>) boolean.class;
            case "byte":
                return (Class<T>) byte.class;
            case "short":
                return (Class<T>) short.class;
            case "int":
                return (Class<T>) int.class;
            case "long":
                return (Class<T>) long.class;
            case "float":
                return (Class<T>) float.class;
            case "double":
                return (Class<T>) double.class;
            case "char":
                return (Class<T>) char.class;
            case "void":
                return (Class<T>) void.class;
            default:
                return (Class<T>) Class.forName(name, true, classLoader);
        }
    }

    /**
     * Use this to specify a class with intermediary and yarn names of classes for cleaner code. also has support for
     * java primitives by using their name in lower case.
     *
     * @param name  first try
     * @param name2 second try
     * @return a {@link java.lang.Class Class} reference.
     * @throws ClassNotFoundException
     * @since 1.2.3
     */
    @DocletReplaceTypeParams("C extends string")
    @DocletReplaceParams(
            """
            name: C, name2: string): GetJava.Type$Reflection<C>;
            function getClass<C extends JavaTypeList | keyof GetJava.Primitives>(name: C, name2: JavaTypeList | keyof GetJava.Primitives"""
    )
    @DocletReplaceReturn("GetJava.Type$Reflection<C>")
    public <T> Class<T> getClass(String name, String name2) throws ClassNotFoundException {
        try {
            return getClass(name);
        } catch (ClassNotFoundException e) {
            return (Class<T>) Class.forName(name2);
        }
    }

    /**
     * @param c
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @see FReflection#getDeclaredMethod(Class, String, String, Class...)
     * @since 1.2.3
     */
    public Method getDeclaredMethod(Class<?> c, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return c.getDeclaredMethod(name, parameterTypes);
    }

    /**
     * Use this to specify a method with intermediary and yarn names of classes for cleaner code.
     *
     * @param c
     * @param name
     * @param name2
     * @param parameterTypes
     * @return a {@link java.lang.reflect.Method Method} reference.
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @since 1.2.3
     */
    public Method getDeclaredMethod(Class<?> c, String name, String name2, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getDeclaredMethod(name2, parameterTypes);
        }
    }

    /**
     * @param c
     * @param name
     * @param name2
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @since 1.6.0
     */
    public Method getMethod(Class<?> c, String name, String name2, Class<?>... parameterTypes) throws NoSuchMethodException {
        try {
            return c.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getMethod(name2, parameterTypes);
        }
    }

    /**
     * @param c
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @since 1.6.0
     */
    public Method getMethod(Class<?> c, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return c.getMethod(name, parameterTypes);
    }

    /**
     * @param c
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @see FReflection#getDeclaredField(Class, String, String)
     * @since 1.2.3
     */
    public Field getDeclaredField(Class<?> c, String name) throws NoSuchFieldException, SecurityException {
        return c.getDeclaredField(name);
    }

    /**
     * Use this to specify a field with intermediary and yarn names of classes for cleaner code.
     *
     * @param c
     * @param name
     * @param name2
     * @return a {@link java.lang.reflect.Field Field} reference.
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @since 1.2.3
     */
    public Field getDeclaredField(Class<?> c, String name, String name2) throws NoSuchFieldException, SecurityException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getDeclaredField(name2);
        }
    }

    /**
     * @param c
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @since 1.6.0
     */
    public Field getField(Class<?> c, String name) throws NoSuchFieldException {
        return c.getField(name);
    }

    /**
     * @param c
     * @param name
     * @param name2
     * @return
     * @throws NoSuchFieldException
     * @since 1.6.0
     */
    public Field getField(Class<?> c, String name, String name2) throws NoSuchFieldException {
        try {
            return c.getField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getField(name2);
        }
    }

    /**
     * Invoke a method on an object with auto type coercion for numbers.
     *
     * @param m       method
     * @param c       object (can be {@code null} for statics)
     * @param objects
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @since 1.2.3
     */
    public Object invokeMethod(Method m, Object c, Object... objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = Util.tryAutoCastNumber(params[i], objects[i]);
        }
        return m.invoke(c, objects);
    }

    /**
     * Attempts to create a new instance of a class. You probably don't have to use this one and can just call {@code
     * new} on a {@link java.lang.Class Class} unless you're in LUA, but then you also have the (kinda poorly
     * documented, can someone find a better docs link for me)
     * <a target="_blank" href= "http://luaj.sourceforge.net/api/3.2/org/luaj/vm2/lib/jse/LuajavaLib.html">LuaJava Library</a>.
     *
     * @param c
     * @param objects
     * @return
     * @since 1.2.7
     */
    public <T> T newInstance(Class<T> c, Object... objects) {
        Class<?>[] params = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            params[i] = objects[i].getClass();
        }
        try {
            Constructor<T> con = c.getConstructor(params);
            return con.newInstance(objects);
        } catch (Exception e) {
            for (Constructor<?> con : c.getConstructors()) {
                if (con.getParameterTypes().length != objects.length) {
                    continue;
                }
                params = con.getParameterTypes();
                Object[] tempObjects = new Object[objects.length];
                try {
                    for (int i = 0; i < objects.length; ++i) {
                        tempObjects[i] = Util.tryAutoCastNumber(params[i], objects[i]);
                    }
                    return (T) con.newInstance(tempObjects);
                } catch (Exception ignored) {
                }
            }
            throw new RuntimeException("Failed to create new instance, bad parameters?", e);
        }
    }

    /**
     * proxy for extending java classes in the guest language with proper threading support.
     *
     * @param clazz
     * @param interfaces
     * @param <T>
     * @return
     * @since 1.6.0
     */
    public <T> ProxyBuilder<T> createClassProxyBuilder(Class<T> clazz, Class<?>... interfaces) {
        return new ProxyBuilder<>(clazz, interfaces);
    }

    /**
     * @param cName
     * @param clazz
     * @param interfaces
     * @param <T>
     * @return
     * @throws NotFoundException
     * @throws CannotCompileException
     * @since 1.6.5
     */
    public <T> ClassBuilder<T> createClassBuilder(String cName, Class<T> clazz, Class<?>... interfaces) throws NotFoundException, CannotCompileException {
        return new ClassBuilder<>(cName, clazz, interfaces);
    }

    /**
     * @param cName
     * @return
     * @throws ClassNotFoundException
     * @since 1.6.5
     */
    public Class<?> getClassFromClassBuilderResult(String cName) throws ClassNotFoundException {
        return Class.forName("xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage." + cName.replaceAll("\\.", "\\$"), true, Neighbor.class.getClassLoader());
    }

    public LibraryBuilder createLibraryBuilder(String name, boolean perExec, String... acceptedLangs) throws NotFoundException, CannotCompileException {
        return new LibraryBuilder(name, perExec, acceptedLangs);
    }

    /**
     * A library class always has a {@link Library} annotation containing the name of the library,
     * which may differ from the actual class name. A library class must also extend
     * {@link BaseLibrary} in some way, either directly or through
     * {@link PerExecLibrary PerExecLibrary},
     * {@link xyz.wagyourtail.jsmacros.core.library.PerExecLanguageLibrary PerExecLanguageLibrary}
     * or {@link xyz.wagyourtail.jsmacros.core.library.PerLanguageLibrary PerLanguageLibrary}.
     *
     * @param className the fully qualified name of the class, including the package
     * @param javaCode  the source code of the library
     * @since 1.8.4
     */
    public void createLibrary(String className, String javaCode) {
        Core.getInstance().libraryRegistry.addLibrary((Class<? extends BaseLibrary>) compileJavaClass(className, javaCode));
    }

    /**
     * A Java Development Kit (JDK) must be installed (and potentially used to start Minecraft) in
     * order to compile whole classes.
     * <p>
     * Compiled classes can't be accessed from any guest language, but must be either stored through
     * {@link FGlobalVars#putObject(String, Object)} or retrieved from this library. Unlike normal
     * hot swapping, already created instances of the class will not be updated. Thus, it's
     * important to know which version of the class you're using when instantiating it.
     *
     * @param className the fully qualified name of the class, including the package
     * @param code      the java code to compile
     * @return the compiled class.
     * @since 1.8.4
     */
    public Class<?> compileJavaClass(String className, String code) {
        Class<?> clazz = Reflect.compile(className, code).type();
        JAVA_CLASS_CACHE.putIfAbsent(className, new ArrayList<>());
        JAVA_CLASS_CACHE.get(className).add(clazz);
        return clazz;
    }

    /**
     * @param className the fully qualified name of the class, including the package
     * @return the latest compiled class or {@code null} if it doesn't exist.
     * @since 1.8.4
     */
    public Class<?> getCompiledJavaClass(String className) {
        List<Class<?>> versions = JAVA_CLASS_CACHE.get(className);
        return versions == null ? null : versions.get(versions.size() - 1);
    }

    /**
     * @param className the fully qualified name of the class, including the package
     * @return all compiled versions of the class, in order of compilation.
     * @since 1.8.4
     */
    public List<Class<?>> getAllCompiledJavaClassVersions(String className) {
        List<Class<?>> versions = JAVA_CLASS_CACHE.get(className);
        return versions == null ? Collections.emptyList() : ImmutableList.copyOf(versions);
    }

    /**
     * See <a href="https://github.com/jOOQ/jOOR">jOOR Github</a> for more information.
     *
     * @param obj the object to wrap
     * @return a wrapper for the passed object to do help with java reflection.
     * @since 1.8.4
     */
    public Reflect getReflect(Object obj) {
        return Reflect.on(obj);
    }

    /**
     * Loads a jar file to be accessible with this library.
     *
     * @param file relative to the script's folder.
     * @return success value
     * @throws IOException
     * @since 1.2.6
     */
    public boolean loadJarFile(String file) throws IOException {
        File jarFile = ctx.getContainedFolder().toPath().resolve(file).toFile();
        if (classLoader.hasJar(jarFile)) {
            return true;
        }
        if (!jarFile.exists()) {
            throw new FileNotFoundException("Jar File Not Found");
        }
        return classLoader.addClassLoader(jarFile, new URLClassLoader(new URL[]{new URL("jar:file:" + jarFile.getCanonicalPath() + "!/")}));
    }

    /**
     * @return the previous mapping helper generated with {@link #loadMappingHelper(String)}
     * @since 1.3.1
     */
    @Nullable
    public Mappings loadCurrentMappingHelper() {
        return remapper;
    }

    /**
     * @param o class you want the name of
     * @return the fully qualified class name (with "."'s not "/"'s)
     * @since 1.3.1
     */
    public String getClassName(Object o) {
        if (o instanceof Class) {
            return ((Class<?>) o).getCanonicalName();
        } else {
            return o.getClass().getCanonicalName();
        }
    }

    /**
     * @param urlorfile a url or file path the the yarn mappings {@code -v2.jar} file, or {@code .tiny} file. for example {@code https://maven.fabricmc.net/net/fabricmc/yarn/1.16.5%2Bbuild.3/yarn-1.16.5%2Bbuild.3-v2.jar}, if same url/path as previous this will load from cache.
     * @return the associated mapping helper.
     * @since 1.3.1
     */
    public Mappings loadMappingHelper(String urlorfile) {
        if (remapper != null && remapper.mappingsource.equals(urlorfile)) {
            return remapper;
        }
        return remapper = new Mappings(urlorfile);
    }

    /**
     * @param instance
     * @param <T>
     * @return
     * @since 1.6.5
     */
    public <T> WrappedClassInstance<T> wrapInstace(T instance) {
        return new WrappedClassInstance<>(instance);
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @since 1.6.5
     */
    public WrappedClassInstance<?> getWrappedClass(String className) throws ClassNotFoundException {
        return new WrappedClassInstance(null, Class.forName(className.replace("/", ".")));
    }

    /**
     * I know this is probably bad practice, but lets be real, this whole library is bad practice, So I can make it
     * worse, right? at least this should work better than {@code try/catch}'ing using
     * {@link ClassLoader#loadClass(String)} to search through every {@link URLClassLoader} that
     * {@link FReflection#loadJarFile(String)} would make, or how I was previously doing it by pre-loading and caching
     * all the classes to a {@link Map}
     * <p>
     * This class is a modification to
     * <a target="_blank" href="https://www.source-code.biz/snippets/java/12.htm">Christian d'Heureuse's JoinClassLoader</a>, under the
     * <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a> to change it from a Class array to a
     * {@link Set}, to allow for modifications to the {@link ClassLoader ClassLoaders} contained in the classLoader.
     *
     * @author Wagyourtail, Christian d'Heureuse
     * @since 1.2.8
     */
    protected static class CombinedVariableClassLoader extends ClassLoader {
        private final Map<File, ClassLoader> siblingDelegates = new LinkedHashMap<>();

        public CombinedVariableClassLoader(ClassLoader parent) {
            super(parent);
        }

        public boolean addClassLoader(File jarPath, ClassLoader loader) throws IOException {
            return siblingDelegates.putIfAbsent(jarPath.getCanonicalFile(), loader) == null;
        }

        public boolean hasJar(File path) throws IOException {
            return siblingDelegates.containsKey(path.getCanonicalFile());
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String path = name.replace('.', '/') + ".class";
            URL url = findResource(path);
            if (url == null) {
                throw new ClassNotFoundException(name);
            }
            ByteBuffer byteCode;
            try {
                byteCode = loadResource(url);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
            return defineClass(name, byteCode, null);
        }

        private ByteBuffer loadResource(URL url) throws IOException {
            try (InputStream stream = url.openStream()) {
                int initialBufferCapacity = Math.min(0x40000, stream.available() + 1);
                if (initialBufferCapacity <= 2) {
                    initialBufferCapacity = 0x10000;
                } else {
                    initialBufferCapacity = Math.max(initialBufferCapacity, 0x200);
                }
                ByteBuffer buf = ByteBuffer.allocate(initialBufferCapacity);
                while (true) {
                    if (!buf.hasRemaining()) {
                        ByteBuffer newBuf = ByteBuffer.allocate(2 * buf.capacity());
                        buf.flip();
                        newBuf.put(buf);
                        buf = newBuf;
                    }
                    int len = stream.read(buf.array(), buf.position(), buf.remaining());
                    if (len <= 0) {
                        break;
                    }
                    buf.position(buf.position() + len);
                }
                buf.flip();
                return buf;
            }
        }

        @Override
        protected URL findResource(String name) {
            for (ClassLoader delegate : siblingDelegates.values()) {
                URL resource = delegate.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            return null;
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            Vector<URL> vector = new Vector<>();
            for (ClassLoader delegate : siblingDelegates.values()) {
                Enumeration<URL> enumeration = delegate.getResources(name);
                while (enumeration.hasMoreElements()) {
                    vector.add(enumeration.nextElement());
                }
            }
            return vector.elements();
        }

    }

}
