
/// <reference lib = "ES2022"/>

declare const __dirname: string;
declare const __filename: string;
declare const arguments: any[];
declare function require(path: string): any;
declare function load(source: string | Packages.java.io.File | Packages.java.net.URL): any;
declare function loadWithNewGlobal(source: string | Packages.java.io.File | Packages.java.net.URL, ...arguments: any[]): any;
declare function print(...arg: any[]): void;
declare function printErr(...arg: any[]): void;

declare namespace console {

    function log(message?: any, ...optionalParams: any[]): void;
    function info(message?: any, ...optionalParams: any[]): void;
    function debug(message?: any, ...optionalParams: any[]): void;
    function dir(item?: any, options?: any): void;
    function error(message?: any, ...optionalParams: any[]): void;
    function warn(message?: any, ...optionalParams: any[]): void;
    function assert(value: any, message?: string, ...optionalParams: any[]): void;
    function clear(): void;
    function count(label?: any): void;
    function countReset(label?: any): void;
    function group(...label: any[]): void;
    function groupCollapsed(...label: any[]): void;
    function groupEnd(): void;
    function time(label?: any): void;
    function timeLog(label?: any, ...data: any[]): void;
    function timeEnd(label?: any): void;

}

/**
 * Information about the graal runner.
 */
declare namespace Graal {
    export const language: string;
    export const versionGraalVM: string;
    export const versionECMAScript: number;

    export function isGraalRuntime(): boolean;

}

declare namespace Polyglot {

    function _import(key: string): any;
    function _export(key: string, value: any): void;
    export function eval(languageId: string, sourceCode: string): any;
    export function evalFile(languageId: string, sourceFileName: string): () => any;

    export { _import as import, _export as export }

}

/**
 * Java namespace for graal's Java functions.  
 * [Graal Docs](https://www.graalvm.org/latest/reference-manual/js/)
 */
declare namespace Java {

    // this overload order is for optimizing,
    // prevent it from flattening package if only the return type is needed
    /**
     * Gets a java type  
     * Supports array type. ex: `int[]`, `short[][]`, `java.lang.String[]`
     */
    export function type<C extends string>(className: C): GetJava.Type$Graal<C>;
    export function type<C extends JavaTypeList | keyof GetJava.Primitives>(className: C): GetJava.Type$Graal<C>;
    export function from<T>(javaData: JavaArray<T>): T[];
    export function from<T>(javaData: JavaList<T>): T[];
    export function from<T>(javaData: JavaCollection<T>): T[];

    /**
     * If toType is not present, converts to java.lang.Object[] by default
     */
    export function to<T>(jsArray: T[]): JavaArray<T>;
    export function to<T extends `${string}[]`>(jsArray: GetJava.GraalJsArr<T>, toType: T): GetJava.GraalTo<T>;
    export function to<T extends JavaArray<any>>(jsArray: GetJava.JavaToJsArr<T>, toType: JavaClass<T> | (new (length: int) => T)): T;
    export function to<T extends `${JavaTypeList | keyof GetJava.Primitives}[]`>(jsArray: GetJava.GraalJsArr<T>, toType: T): GetJava.GraalTo<T>;
    export function isJavaObject(obj: any): boolean;
    export function isType<T>(obj: T): T extends { readonly class: JavaClass } ? true : false;
    export function typeName<T>(obj: T): T extends JavaClassArg ? string : undefined;
    export function addToClasspath(location: string): void;

}

type JavaTypeList = ListPackages<typeof Packages>;

type ListPackages<T extends object, P extends string = ''> =
    IsStrictAny<T> extends true ? never : T extends JavaClassArg ? P :
    { [K in keyof T]: ListPackages<T[K], P extends '' ? K : `${P}.${K}`> }[keyof T];

namespace GetJava {

    type Type$Graal<P extends string, _K extends keyof Primitive<any> = 'type'> =
        IsStrictAny<P> extends true ? any :
        P extends `${string}[]` ? (ArrayType<P> extends JavaArray<infer T> ? typeof Packages.java.lang.Array<T> : never) :
        P extends keyof Primitives ? Primitives[P][_K] : Type<P>;

    type ArrayType<P extends string, _K extends keyof Primitive<any> = 'type'> =
        P extends `${infer C extends string}[]` ? JavaArray<ArrayType<C>> :
        Type$Graal<P, _K> extends { readonly class: JavaClass<infer T> } ? T : unknown;

    type GraalTo<P extends string> = IsStrictAny<P> extends true ? any : ArrayType<P>;
    type GraalJsArr<P extends string> = IsStrictAny<P> extends true ? any : JavaToJsArr<ArrayType<P, 'from'>>;
    type JavaToJsArr<T> = IsStrictAny<P> extends true ? any : T extends JavaArray<infer C> ? JavaToJsArr<C>[] : T;

    type Type$Reflection<P extends string> =
        P extends keyof Primitives ? Primitives[P]['type']['class'] : TypeClass<P>;

    type TypeClass<T> = Type<T>['class'] extends infer C extends JavaClass<any> ?
        IsStrictAny<C> extends false ? C : unknown : unknown;

    type Type<P extends string, T extends object = typeof Packages> =
        IsStrictAny<T> extends true ? unknown :
        P extends `${infer K}.${infer R}` ? Type<R, T[K]> :
        P extends '' ? T extends (abstract new (...args: any[]) => any) & { readonly class: JavaClass } ? T : unknown : Type<'', T[P]>;

    type Primitives = {
        boolean: Primitive<boolean, boolean>;
        byte:    Primitive<byte   , byte>;
        short:   Primitive<short  , short>;
        int:     Primitive<int    , int>;
        long:    Primitive<long   , long>;
        float:   Primitive<float  , float>;
        double:  Primitive<double , double>;
        char:    Primitive<string | char, string>;
        void:    Primitive<void, void>;
    }
    
    type Primitive<A, T = any> = {
        readonly from: A;
        readonly type: PrimitiveClass<T>;
    }

    interface PrimitiveClass<T> extends SuppressFunctionProperties {
        /** no constructor */
        new (none: never): never;
        readonly class: JavaClass<T>;
    }

}

type CanOmitNamespace<T extends string> = T extends `minecraft:${infer P}` ? T | P : T;

type IsStrictAny<T> = 0 | 1 extends (T extends never ? 1 : 0) ? true : false;

/** One of the root packages in java. */ declare var java:   JavaPackage<'java'>;
/** One of the root packages in java. */ declare var javafx: JavaPackage<'javafx'>;
/** One of the root packages in java. */ declare var javax:  JavaPackage<'javax'>;
/** One of the root packages in java. */ declare var com:    JavaPackage<'com'>;
/** One of the root packages in java. */ declare var edu:    JavaPackage<'edu'>;
/** One of the root packages in java. */ declare var org:    JavaPackage<'org'>;

type JavaPackage<R extends string> = JavaPackageColoring & typeof Packages[R];
interface JavaPackageColoring extends SuppressFunctionProperties {

    /** java package, no constructor, just for coloring */
    new (none: never): never;

}

interface SuppressFunctionProperties {
    /** @deprecated */ Symbol: unknown;
    /** @deprecated */ apply: unknown;
    /** @deprecated */ arguments: unknown;
    /** @deprecated */ bind: unknown;
    /** @deprecated */ call: unknown;
    /** @deprecated */ caller: unknown;
    /** @deprecated */ prototype: unknown;
    /** @deprecated */ length: unknown;
    /** @deprecated */ name: unknown;
}

/**
 * The global `Packages` object is provided by the GraalVM JavaScript engine, and allows
 * access to Java packages. Use it to interact with Java APIs and libraries from your
 * JavaScript code.
 *
 * To access a Java package, use the `Packages` object as a namespace, followed by the
 * fully-qualified package name. For example: `Packages.java.net.URL`.
 */
declare namespace Packages {

    namespace java {

        namespace lang {

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Class.html
            interface Class<T> extends io.Serializable, reflect.GenericDeclaration, reflect.Type, reflect.AnnotatedElement, invoke.TypeDescriptor$OfField<Class<T>>, constant.Constable {
                new (...args: any[]): T;
            }
            class Class<T> extends Object {
                static readonly class: Class<Class<any>>;
                private constructor ();

                static forName<C extends string>(className: C): GetJava.TypeClass<C>;
                static forName<C extends JavaTypeList>(className: C): GetJava.TypeClass<C>;
                static forName<C extends string>(name: C, initialize: boolean, loader: ClassLoader): GetJava.TypeClass<C>;
                static forName<C extends JavaTypeList>(name: C, initialize: boolean, loader: ClassLoader): GetJava.TypeClass<C>;
                static forName<C extends string>(module: Module, name: C): GetJava.TypeClass<C>;
                static forName<C extends JavaTypeList>(module: Module, name: C): GetJava.TypeClass<C>;

                arrayType(): Class<JavaArray<T>>;
                asSubclass<U>(clazz: Class<U>): Class<U>;
                cast(obj: Object): T;
                componentType(): T extends JavaArray<infer C> ? Class<C> : null;
                descriptorString(): string;
                desiredAssertionStatus(): boolean;
                getAnnotatedInterfaces(): JavaArray<reflect.AnnotatedType>;
                getAnnotatedSuperclass(): reflect.AnnotatedType;
                getAnnotation<A extends annotation.Annotation>(annotationClass: Class<A>): A?;
                getAnnotations(): JavaArray<annotation.Annotation>;
                getAnnotationsByType<A extends annotation.Annotation>(annotationClass: Class<A>): JavaArray<A>;
                getCanonicalName(): string;
                getClasses(): JavaArray<Class<any>>;
                getClassLoader(): ClassLoader;
                getComponentType(): T extends JavaArray<infer C> ? Class<C> : null;
                getConstructor(...parameterTypes: JavaVarArgs<JavaClassArg>): reflect.Constructor<T>;
                getConstructors(): JavaArray<reflect.Constructor<any>>;
                getDeclaredAnnotation<A extends annotation.Annotation>(annotationClass: Class<A>): A?;
                getDeclaredAnnotations(): JavaArray<annotation.Annotation>;
                getDeclaredAnnotationsByType<A extends annotation.Annotation>(annotationClass: Class<A>): JavaArray<A>;
                getDeclaredClasses(): JavaArray<Class<any>>;
                getDeclaredConstructor(...parameterTypes: JavaVarArgs<JavaClassArg>): reflect.Constructor<T>;
                getDeclaredConstructors(): JavaArray<reflect.Constructor<any>>;
                getDeclaredField(name: string): reflect.Field;
                getDeclaredFields(): JavaArray<reflect.Field>;
                getDeclaredMethod(name: string, ...parameterTypes: JavaVarArgs<JavaClassArg>): reflect.Method;
                getDeclaredMethods(): JavaArray<reflect.Method>;
                getDeclaringClass(): Class<any>;
                getEnclosingClass(): Class<any>;
                getEnclosingConstructor(): reflect.Constructor<any>;
                getEnclosingMethod(): reflect.Method;
                getEnumConstants(): JavaArray<T>?;
                getField(name: string): reflect.Field;
                getFields(): JavaArray<reflect.Field>;
                getGenericInterfaces(): JavaArray<reflect.Type>;
                getGenericSuperclass(): reflect.Type;
                getInterfaces(): JavaArray<Class<any>>;
                getMethod(name: string, ...parameterTypes: JavaVarArgs<JavaClassArg>): reflect.Method;
                getMethods(): JavaArray<reflect.Method>;
                getModifiers(): number;
                getModule(): Module;
                getName(): string;
                getNestHost(): Class<any>;
                getNestMembers(): JavaArray<Class<any>>;
                getPackage(): Package;
                getPackageName(): string;
                getPermittedSubclasses(): JavaArray<Class<any>>;
                getProtectionDomain(): security.ProtectionDomain;
                getRecordComponents(): JavaArray<reflect.RecordComponent>?;
                getResource(name: string): net.URL;
                getResourceAsStream(name: string): io.InputStream;
                getSigners(): JavaArray<Object>;
                getSimpleName(): string;
                getSuperclass(): Class<any>;
                getTypeName(): string;
                getTypeParameters(): JavaArray<reflect.TypeVariable<Class<T>>>;
                isAnnotation(): boolean;
                isAnnotationPresent(annotationClass: Class<annotation.Annotation>): boolean;
                isAnonymousClass(): boolean;
                isArray(): T extends JavaArray<any> ? true : false;
                isAssignableFrom(cls: JavaClassArg): boolean;
                isEnum(): boolean;
                isHidden(): boolean;
                isInstance(obj: Object): boolean;
                isInterface(): boolean;
                isLocalClass(): boolean;
                isMemberClass(): boolean;
                isNestmateOf(c: JavaClassArg): boolean;
                isPrimitive(): boolean;
                isRecord(): boolean;
                isSealed(): boolean;
                isSynthetic(): boolean;
                /** @deprecated */
                newInstance(): T;
                toGenericString(): string;
                toString(): string;

            }

            class Object {
                static readonly class: Class<Object>;
                /** @deprecated */ static Symbol: undefined;
                /** @deprecated */ static arguments: undefined;
                /** @deprecated */ static caller: undefined;
                /** @deprecated */ static prototype: undefined;

                equals(obj: Object): boolean;
                getClass(): Class<any>;
                hashCode(): number;
                notify(): void;
                notifyAll(): void;
                toString(): string;
                wait(timeoutMillis?: long, nanos?: int): void;

            }

            /** Not an actual class */
            abstract class Interface extends java.lang.Object {
                /** @deprecated */ static apply: undefined;
                /** @deprecated */ static bind: undefined;
                /** @deprecated */ static call: undefined;
                /** @deprecated */ static length: undefined;
                /** @deprecated */ static name: undefined;
            }

            abstract class Comparable<T> extends Interface {
                static readonly class: Class<Comparable<any>>;
                /** @deprecated */ static prototype: undefined;
            }
            interface Comparable<T> {

                compareTo(other: T): number;

            }

            interface Array<T> extends globalThis.Array<T> {}
            class Array<T> extends Object {
                static readonly class: Class<JavaArray<any>>;
                /** @deprecated */ static prototype: undefined;

                constructor (length: int);

                clone(): JavaArray<T>;

            }

            interface StackTraceElement extends io.Serializable {}
            class StackTraceElement extends Object {
                static readonly class: Class<StackTraceElement>;
                /** @deprecated */ static prototype: undefined;

                constructor (declaringClass: string, methodName: string, fileName: string, lineNumber: int);
                constructor (classLoaderName: string, moduleName: string, moduleVersion: string, declaringClass: string, methodName: string, fileName: string, lineNumber: int);

                getClassLoaderName(): string;
                getClassName(): string;
                getFileName(): string;
                getLineNumber(): number;
                getMethodName(): string;
                getModuleName(): string;
                getModuleVersion(): string;
                isNativeMethod(): boolean;

            }

            interface Throwable extends io.Serializable, Error {}
            class Throwable extends Object {
                static readonly class: Class<Throwable>;
                /** @deprecated */ static prototype: undefined;

                constructor (message?: string, cause?: Throwable);
                constructor (message: string, cause: Throwable, enableSuppression: boolean, writableStackTrace: boolean);
                constructor (cause: Throwable);

                addSuppressed(exception: Throwable): void;
                fillInStackTrace(): Throwable;
                getCause(): Throwable;
                getLocalizedMessage(): string;
                getMessage(): string;
                getStackTrace(): JavaArray<StackTraceElement>;
                getSuppressed(): JavaArray<Throwable>;
                initCause(cause: Throwable): Throwable;
                printStackTrace(s?: io.PrintStream | io.PrintWriter): void;
                setStackTrace(stackTrace: JavaArray<StackTraceElement>): void;

            }

            abstract class Iterable<T> extends Interface {
                static readonly class: Class<Iterable<any>>;
                /** @deprecated */ static prototype: undefined;
            }
            interface Iterable<T> extends globalThis.Iterable<T> {

                iterator(): util.Iterator<T>;
                forEach(action: MethodWrapper<T>): void;
                spliterator(): util.Spliterator<T>;

            }

            // by exporting this way, the types in popup will be shortened
            // for example Object will show up as `Object` instead of `Packages.java.lang.Object`
            export { Class, Object, Interface, Comparable, Array, StackTraceElement, Throwable, Iterable }

        }

        namespace util {

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collection.html
            abstract class Collection<E> extends java.lang.Interface {
                static readonly class: JavaClass<Collection<any>>;
                /** @deprecated */ static prototype: undefined;
            }
            interface Collection<E> extends lang.Iterable<E> {

                add(e: E): boolean;
                addAll(c: Collection<E>): boolean;
                clear(): void;
                contains(o: E): boolean;
                contains(o: any): boolean;
                containsAll(c: Collection<E>): boolean;
                isEmpty(): boolean;
                parallelStream(): stream.Stream<E>;
                remove(o: E): boolean;
                remove(o: any): boolean;
                removeAll(c: Collection<any>): boolean;
                removeIf(filter: MethodWrapper<E, any, boolean>): boolean;
                retainAll(c: Collection<any>): boolean;
                size(): number;
                stream(): stream.Stream<E>;
                toArray(a?: E[]): JavaArray<E>;
                toArray(generator: MethodWrapper<int, any, E[]>): JavaArray<E>;

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html
            abstract class List<E> extends java.lang.Interface {
                static readonly class: JavaClass<List<any>>;
                /** @deprecated */ static prototype: undefined;

                static copyOf<E>(coll: Collection<E>): JavaList<E>;
                static of<E>(...elements: JavaVarArgs<E>): JavaList<E>;

            }
            interface List<E> extends Collection<E>, lang.Iterable<E> {
                [n: int | `${bigint}`]: E;

                add(index: int, element: E): void;
                add(e: E): boolean;
                addAll(index: int, c: Collection<E>): boolean;
                addAll(c: Collection<E>): boolean;
                get(index: int): E;
                indexOf(e: E): number;
                indexOf(e: any): number;
                lastIndexOf(e: E): number;
                lastIndexOf(e: any): number;
                listIterator(): ListIterator<E>;
                listIterator(index: int): ListIterator<E>;
                remove(index: int): E;
                remove(o: E): boolean;
                remove(o: any): boolean;
                replaceAll(operator: MethodWrapper<E, any, E>): void;
                set(index: int, element: E): E;
                sort(c: MethodWrapper<E, E, number>): void;
                subList(fromIndex: int, toIndex: int): List<E>;

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html
            abstract class Map<K, V> extends java.lang.Interface {
                static readonly class: JavaClass<Map<any, any>>;
                /** @deprecated */ static prototype: undefined;

                static copyOf<K, V>(map: Map<K, V>): Map<K, V>;
                static entry<K, V>(k: K, v: V): Map$Entry<K, V>;
                static ofEntries<K, V>(...entries: JavaVarArgs<Map$Entry<K, V>>): Map<K, V>;
                static of<K, V>(k1: K, v1: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V, k9: K, v9: V): Map<K, V>;
                static of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V, k9: K, v9: V, k10: K, v10: V): Map<K, V>;

            }
            interface Map<K, V> {
                [k: string | number]: V;

                clear(): void;
                compute(key: K, remappingFunction: MethodWrapper<K, V, V>): V?;
                computeIfAbsent(key: K, mappingFunction: MethodWrapper<K, any, V>): V;
                computeIfPresent(key: K, remappingFunction: MethodWrapper<K, NonNullable<V>, V>): V;
                containsKey(key: K): boolean;
                containsKey(key: any): boolean;
                containsValue(value: V): boolean;
                containsValue(value: any): boolean;
                entrySet(): Set<Map$Entry<K, V>>;
                forEach(action: MethodWrapper<K, V>): void;
                get(key: K): V?;
                get(key: any): V?;
                getOrDefault(key: K, defaultValue: V): V;
                getOrDefault(key: any, defaultValue: V): V;
                isEmpty(): boolean;
                keySet(): Set<K>;
                merge(key: K, value: V, remappingFunction: MethodWrapper<V, V, V>): V;
                put(ket: K, value: V): V;
                putAll(m: Map<K, V>): void;
                putIfAbsent(key: K, value: V): V?;
                remove(key: K): V?;
                remove(key: any): V?;
                remove(key: K, value: V): boolean;
                remove(key: any, value: any): boolean;
                replace(key: K, value: V): V;
                replace(key: K, oldValue: V, newValue: V): boolean;
                replaceAll(fn: MethodWrapper<K, V, V>): void;
                size(): number;
                values(): Collection<V>;

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Set.html
            abstract class Set<E> extends java.lang.Interface {
                static readonly class: JavaClass<Set<any>>;
                /** @deprecated */ static prototype: undefined;

                static copyOf<T>(coll: JavaCollection<T>): Set<T>;
                static of<T>(...elements: JavaVarArgs<T>): Set<T>;

            }
            interface Set<E> extends Collection<E> {}

            export { Collection, List, Map, Set }

        }

        namespace io {

            interface File extends Serializable, lang.Comparable<File> {}
            class File extends java.lang.Object {
                static readonly class: JavaClass<File>;
                /** @deprecated */ static prototype: undefined;

                static readonly pathSeparator: string;
                static readonly pathSeparatorChar: char;
                static readonly separator: string;
                static readonly separatorChar: char;

                static createTempFile(prefix: string, suffix: string, directory?: File): File;
                static listRoots(): JavaArray<File>;

                constructor (parent: File | string, child: string);
                constructor (pathName: string);
                constructor (uri: net.URI);

                canExecute(): boolean;
                canRead(): boolean;
                canWrite(): boolean;
                createNewFile(): boolean;
                delete(): boolean;
                deleteOnExit(): void;
                exists(): boolean;
                getAbsoluteFile(): File;
                getAbsolutePath(): string;
                getCanonicalFile(): File;
                getCanonicalPath(): string;
                getFreeSpace(): number;
                getName(): string;
                getParent(): string;
                getParentFile(): File;
                getPath(): string;
                getTotalSpace(): number;
                getUsableSpace(): number;
                isAbsolute(): boolean;
                isDirectory(): boolean;
                isFile(): boolean;
                isHidden(): boolean;
                lastModified(): number;
                length(): number;
                list(): JavaArray<string>;
                list(filter: MethodWrapper<File, string, boolean>): JavaArray<string>;
                listFiles(): JavaArray<File>;
                listFiles(filter: MethodWrapper<File, any, boolean>): JavaArray<File>;
                listFiles(filter: MethodWrapper<File, string, boolean>): JavaArray<File>;
                mkdir(): boolean;
                mkdirs(): boolean;
                renameTo(dest: File): boolean;
                setExecutable(executable: boolean, ownerOnly?: boolean): boolean;
                setLastModified(time: long): boolean;
                setReadable(readable: boolean, ownerOnly?: boolean): boolean;
                setReadonly(): boolean;
                setWritable(writable: boolean, ownerOnly?: boolean): boolean;
                toPath(): nio.file.Path;
                toURI(): net.URI;
                toURL(): net.URL;

            }

            abstract class Serializable extends java.lang.Interface {
                static readonly class: JavaClass<Serializable>;
                /** @deprecated */ static prototype: undefined;
            }
            interface Serializable {}

            export { File, Serializable }

        }

        namespace net {

            interface URL extends io.Serializable {}
            class URL extends java.lang.Object {
                static readonly class: JavaClass<URL>;
                /** @deprecated */ static prototype: undefined;

                static setURLStreamHandlerFactory(fac: URLStreamHandlerFactory): void;

                constructor (spec: string);
                constructor (protocol: string, host: string, port: int, file: string, handler?: URLStreamHandler);
                constructor (protocol: string, host: string, file: string);
                constructor (context: URL, spec: string, handler?: URLStreamHandler);

                getAuthority(): string;
                getContent(classes?: JavaClassArg[]): any;
                getDefaultPort(): number;
                getFile(): string;
                getHost(): string;
                getPath(): string;
                getPort(): number;
                getProtocol(): string;
                getQuery(): string;
                getRef(): string;
                getUserInfo(): string;
                openConnection(proxy?: Proxy): URLConnection;
                openStream(): io.InputStream;
                sameFile(other: URL): boolean;
                toExternalForm(): string;
                toURI(): URI;

            }

            interface URI extends lang.Comparable<URI>, io.Serializable {}
            class URI extends java.lang.Object {
                static readonly class: JavaClass<URI>;
                /** @deprecated */ static prototype: undefined;

                static create(str: string): URI;

                constructor (str: string);
                constructor (scheme: string, ssp: string, fragment: string);
                constructor (scheme: string, userInfo: string, host: string, port: int, path: string, query: string, fragment: string);
                constructor (scheme: string, host: string, path: string, fragment: string);
                constructor (scheme: string, authority: string, path: string, query: string, fragment: string);

                getAuthority(): string;
                getFragment(): string;
                getHost(): string;
                getPath(): string;
                getPort(): number;
                getQuery(): string;
                getRawAuthority(): string;
                getRawFragment(): string;
                getRawPath(): string;
                getRawQuery(): string;
                getRawSchemeSpecificPart(): string;
                getRawUserInfo(): string;
                getScheme(): string;
                getSchemeSpecificPart(): string;
                getUserInfo(): string;
                isAbsolute(): boolean;
                isOpaque(): boolean;
                normalize(): URI;
                parseServerAuthority(): URI;
                relativize(uri: URI): URI;
                resolve(str: string): URI;
                resolve(uri: URI): URI;
                toASCIIString(): string;
                toURL(): URL;

            }

            export { URL, URI }

        }

    }

}

type char   = number | string;
type byte   = number & {};
type short  = number & {};
type int    = number & {};
type long   = number | BigInt;
type float  = number & {};
type double = number & {};

type JavaClassArg<T = any> = JavaClass<T> | { readonly class: JavaClass<T> };
type JavaVarArgs<T> = T[] | [T[]];

type JavaObject                = Packages.java.lang.Object;
type JavaClass<T = any>        = Packages.java.lang.Class<T>;
type JavaArray<T = any>        = Packages.java.lang.Array<T>;
type JavaCollection<T = any>   = Packages.java.util.Collection<T>;
type JavaList<T = any>         = Packages.java.util.List<T>;
type JavaSet<T = any>          = Packages.java.util.Set<T>;
type JavaMap<K = any, V = any> = Packages.java.util.Map<K, V> & Partial<Record<Extract<K, string | number>, V>>;
