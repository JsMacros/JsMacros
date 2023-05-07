
/// <reference lib="ES2022"/>

declare function load(source: string | Packages.java.io.File | Packages.java.net.URL): any;
declare function loadWithNewGlobal(source: string | Packages.java.io.File | Packages.java.net.URL, ...arguments: any[]): any;
// These two are commented out because it's useless, doesn't even show in game log
// declare function print(...arg: any[]): void;
// declare function printErr(...arg: any[]): void;

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
    export function evalFile(languageId: string, sourceFileName: string): any;

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
     * If toType is not present, converts to [Ljava.lang.Object by default
     */
    export function to<T>(jsArray: T[]): JavaArray<T>;
    export function to<T extends `${string}[]`>(jsArray: GetJava.GraalJsArr<T>, toType: T): GetJava.GraalTo<T>;
    export function to<T extends JavaArray<any>>(jsArray: GetJava.JavaToJsArrRec<T>, toType: JavaClassArg<T>): T;
    export function to<T extends `${JavaTypeList | keyof GetJava.Primitives}[]`>(jsArray: GetJava.GraalJsArr<T>, toType: T): GetJava.GraalTo<T>;
    export function isJavaObject(obj: any): boolean;
    export function isType(obj: JavaClassArg): boolean;
    export function typeName(obj: JavaObject): string | undefined;
    export function addToClasspath(location: string): void;

}

type JavaTypeList = ListPackages<typeof Packages>;

type ListPackages<T extends object, P extends string = ''> =
    IsStrictAny<T> extends true ? never : IsConstructor<T> extends true ? P :
    { [K in keyof T]: ListPackages<T[K], P extends '' ? K : `${P}.${K}`> }[keyof T];

namespace GetJava {

    type Type$Graal<P extends string, _K extends keyof Primitive<any> = 'type'> =
        P extends `${string}[]` ? JavaArrayStatics<ArrayType<P>> :
        P extends keyof Primitives ? Primitives[P][_K] :
        Type<P>;

    type ArrayType<P extends string, _K extends keyof Primitive<any> = 'type'> =
        P extends `${infer C extends string}[]` ? JavaArray<ArrayType<C>> :
        Type$Graal<P, _K> extends JavaClassStatics<infer T> ? T : unknown;
    
    type GraalTo<P extends string> = ArrayType<P>;
    type GraalJsArr<P extends string> = JavaToJsArrRec<ArrayType<P, 'from'>>;
    type JavaToJsArrRec<T> = T extends JavaArray<infer C> ? JavaToJsArrRec<C>[] : T;

    type Type$Reflection<P extends string> =
        P extends keyof Primitives ? Primitives[P]['type']['class'] : TypeClass<P>;

    type TypeClass<T> = Type<T>['class'] extends infer C extends JavaClass<any> ?
        IsStrictAny<C> extends false ? C : unknown : unknown;

    type Type<P extends string, T extends object = typeof Packages> =
        IsStrictAny<T> extends true ? unknown :
        P extends `${infer K}.${infer R}` ? Type<R, T[K]> :
        P extends '' ? IsConstructor<T> extends true ? T : unknown : Type<'', T[P]>;

    interface Primitives {
        boolean: Primitive<boolean, boolean>;
        byte:    Primitive<byte   , byte>;
        short:   Primitive<short  , short>;
        int:     Primitive<int    , int>;
        long:    Primitive<long   , long>;
        float:   Primitive<float  , float>;
        double:  Primitive<double , double>;
        char:    Primitive<string | char, string>;
    }
    
    interface Primitive<A, T = any> {
        readonly from: A;
        readonly type: JavaClassStatics<T> & NoConstructor;
    }

}

type BooStrNumMethod<T> = // Used in worldscanner
    { [K in keyof T]: ReturnType<T[K]> extends infer R extends boolean | string | number ?
    IsStrictAny<R> extends false ? K : never : never }[keyof T];

type IsConstructor<T> = T extends new (...args: never) => any ? true : false;

type IsStrictAny<T> = 0 | 1 extends (T extends never ? 1 : 0) ? true : false;

/** One of the root packages in java. */
declare const java:   JavaPackageColoring & JavaPackage<typeof Packages.java>;
/** One of the root packages in java. */
declare const javafx: JavaPackageColoring & JavaPackage<typeof Packages.javafx>;
/** One of the root packages in java. */
declare const javax:  JavaPackageColoring & JavaPackage<typeof Packages.javax>;
/** One of the root packages in java. */
declare const com:    JavaPackageColoring & JavaPackage<typeof Packages.com>;
/** One of the root packages in java. */
declare const org:    JavaPackageColoring & JavaPackage<typeof Packages.org>;
/** One of the root packages in java. */
declare const edu:    JavaPackageColoring & JavaPackage<typeof Packages.edu>;

type JavaPackage<T> = IsStrictAny<T> extends true ? unknown : T;
interface JavaPackageColoring extends SuppressProperties {

    /** java package, no constructor, just for coloring */
    new (none: never): never;

}

interface JavaInterfaceStatics<T> extends SuppressProperties {

    /** interface, no constructor, just for coloring */
    new (none: never): never;

    readonly class: JavaClass<T>;

}

interface JavaClassStatics<T, C extends object = {}> extends C {
    readonly class: JavaClass<T> & C;
}

interface JavaArrayStatics<T extends JavaArray<any>> extends JavaClassStatics<T, JavaArrayConstructor<T>> {}

interface JavaArrayConstructor<T extends JavaArray<any>> extends SuppressProperties {

    new (length: int): T;

}

interface NoConstructor extends SuppressProperties {

    /** no constructor */
    new (none: never): never;

}

interface SuppressProperties {
    /** @deprecated */ Symbol: unknown;
    /** @deprecated */ apply: unknown;
    /** @deprecated */ arguments: unknown;
    /** @deprecated */ bind: unknown;
    /** @deprecated */ call: unknown;
    /** @deprecated */ caller: unknown;
    /** @deprecated */ length: unknown;
    /** @deprecated */ name: unknown;
    /** @deprecated */ prototype: unknown;
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
            const Class: JavaClassStatics<Class<any>> & NoConstructor & {

                forName<C extends string>(className: C): GetJava.TypeClass<C>;
                forName<C extends JavaTypeList>(className: C): GetJava.TypeClass<C>;
                forName<C extends string>(name: C, initialize: boolean, loader: java.lang.ClassLoader): GetJava.TypeClass<C>;
                forName<C extends JavaTypeList>(name: C, initialize: boolean, loader: java.lang.ClassLoader): GetJava.TypeClass<C>;
                forName<C extends string>(module: Module, name: C): GetJava.TypeClass<C>;
                forName<C extends JavaTypeList>(module: Module, name: C): GetJava.TypeClass<C>;

            }
            interface Class<T> extends Object {

                arrayType(): Class<any>;
                asSubclass(clazz: Class<any>): Class<any>;
                cast(obj: Object): T;
                componentType(): Class<any>;
                descriptorString(): string;
                desiredAssertionStatus(): boolean;
                getAnnotatedInterfaces(): JavaArray<java.lang.reflect.AnnotatedType>;
                getAnnotatedSuperclass(): java.lang.reflect.AnnotatedType;
                getAnnotation<A extends java.lang.annotation.Annotation>(annotationClass: Class<A>): A;
                getAnnotations(): JavaArray<java.lang.annotation.Annotation>;
                getAnnotationsByType<A extends java.lang.annotation.Annotation>(annotationClass: Class<A>): JavaArray<A>;
                getCanonicalName(): string;
                getClasses(): JavaArray<Class<any>>;
                getClassLoader(): java.lang.ClassLoader;
                getComponentType(): Class<any>;
                getConstructor(...parameterTypes: Class<any>): java.lang.reflect.Constructor<T>;
                getConstructors(): JavaArray<java.lang.reflect.Constructor<any>>;
                getDeclaredAnnotation<A extends java.lang.annotation.Annotation>(annotationClass: Class<A>): A;
                getDeclaredAnnotations(): JavaArray<java.lang.annotation.Annotation>;
                getDeclaredAnnotationsByType<A extends java.lang.annotation.Annotation>(annotationClass: Class<A>): JavaArray<A>;
                getDeclaredClasses(): JavaArray<Class<any>>;
                getDeclaredConstructor(...parameterTypes: Class<any>): java.lang.reflect.Constructor<T>;
                getDeclaredConstructors(): JavaArray<java.lang.reflect.Constructor<any>>;
                getDeclaredField(name: string): java.lang.reflect.Field;
                getDeclaredFields(): JavaArray<java.lang.reflect.Field>;
                getDeclaredMethod(name: string, ...parameterTypes: Class<any>): java.lang.reflect.Method;
                getDeclaredMethods(): JavaArray<java.lang.reflect.Method>;
                getDeclaringClass(): Class<any>;
                getEnclosingClass(): Class<any>;
                getEnclosingConstructor(): java.lang.reflect.Constructor<any>;
                getEnclosingMethod(): java.lang.reflect.Method;
                getEnumConstants(): JavaArray<T>;
                getField(name: string): java.lang.reflect.Field;
                getFields(): JavaArray<java.lang.reflect.Field>;
                getGenericInterfaces(): JavaArray<java.lang.reflect.Type>;
                getGenericSuperclass(): java.lang.reflect.Type;
                getInterfaces(): JavaArray<Class<any>>;
                getMethod(name: string, ...parameterTypes: Class<any>): java.lang.reflect.Method;
                getMethods(): JavaArray<java.lang.reflect.Method>;
                getModifiers(): number;
                getModule(): Module;
                getName(): string;
                getNestHost(): Class<any>;
                getNestMembers(): JavaArray<Class<any>>;
                getPackage(): Package;
                getPackageName(): string;
                getPermittedSubclasses(): JavaArray<Class<any>>;
                getProtectionDomain(): java.security.ProtectionDomain;
                getRecordComponents(): any; // JavaArray<java.lang.reflect.RecordComponent>;
                getResource(name: string): java.net.URL;
                getResourceAsStream(name: string): java.io.InputStream;
                getSigners(): JavaArray<Object>;
                getSimpleName(): string;
                getSuperclass(): Class<any>;
                getTypeName(): string;
                getTypeParameters(): JavaArray<java.lang.reflect.TypeVariable<Class<T>>>;
                isAnnotation(): boolean;
                isAnnotationPresent<T extends java.lang.annotation.Annotation>(annotationClass: Class<T>): boolean;
                isAnonymousClass(): boolean;
                isArray(): boolean;
                isAssignableFrom(cls: Class<any>): boolean;
                isEnum(): boolean;
                isHidden(): boolean;
                isInstance(obj: Object): boolean;
                isInterface(): boolean;
                isLocalClass(): boolean;
                isMemberClass(): boolean;
                isNestmateOf(c: Class<any>): boolean;
                isPrimitive(): boolean;
                isRecord(): boolean;
                isSealed(): boolean;
                isSynthetic(): boolean;
                /** @deprecated */
                newInstance(): T;
                toGenericString(): string;
                toString(): string;

            }

            const Object: JavaClassStatics<JavaObject, Object$$constructor>;
            interface Object$$constructor extends SuppressProperties {

                new (): JavaObject;

            }
            interface Object {

                equals(obj: JavaObject): boolean;
                getClass(): JavaClass<any>;
                hashCode(): number;
                notify(): void;
                notifyAll(): void;
                toString(): string;
                wait(): void;
                wait(var1: long): void;
                wait(timeoutMillis: long, nanos: int): void;

            }

            const Comparable: JavaInterfaceStatics<Comparable<any>>;
            interface Comparable<T> extends Object {

                compareTo(arg0: T): number;

            }

            const Array: JavaInterfaceStatics<Array<any>>;
            interface Array<T> extends Object, globalThis.Array<T> {

                clone(): Array<T>;

            }

            const StackTraceElement: JavaClassStatics<StackTraceElement, StackTraceElement$$constructor>;
            interface StackTraceElement$$constructor extends SuppressProperties {

                new (declaringClass: string, methodName: string, fileName: string, lineNumber: int): StackTraceElement;
                new (classLoaderName: string, moduleName: string, moduleVersion: string, declaringClass: string, methodName: string, fileName: string, lineNumber: int): StackTraceElement;

            }
            interface StackTraceElement extends Object, java.io.Serializable {

                getFileName(): string;
                getLineNumber(): number;
                getClassName(): string;
                getMethodName(): string;
                isNativeMethod(): boolean;
                toString(): string;
                equals(arg0: any): boolean;
                hashCode(): number;

            }

            const Throwable: JavaClassStatics<Throwable, Throwable$$constructor>;
            interface Throwable$$constructor extends SuppressProperties {

                new (): Throwable;
                new (message: string): Throwable;
                new (message: string, cause: Throwable): Throwable;

            }
            interface Throwable extends Object, java.io.Serializable, Error {

                getMessage(): string;
                getLocalizedMessage(): string;
                getCause(): Throwable;
                initCause(arg0: Throwable): Throwable;
                toString(): string;
                fillInStackTrace(): Throwable;
                getStackTrace(): Array<StackTraceElement>;
                setStackTrace(arg0: Array<StackTraceElement>): void;
                addSuppressed(arg0: Throwable): void;
                getSuppressed(): Array<Throwable>;

            }

            const Iterable: JavaInterfaceStatics<Iterable<any>>;
            interface Iterable<T> extends Object, globalThis.Iterable<T> {

                iterator(): java.util.Iterator<T>;
                forEach(arg0: java.util.function.Consumer<any>): void;
                spliterator(): java.util.Spliterator<T>;

            }

            export { Class, Object, Comparable, Array, StackTraceElement, Throwable, Iterable }

        }

        namespace util {

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collection.html
            const Collection: JavaInterfaceStatics<Collection<any>>;
            interface Collection<T> extends java.lang.Iterable<T> {
                readonly [n: int]: T;

                add(element: T): boolean;
                addAll(elements: JavaCollection<T>): boolean;
                clear(): void;
                contains(element: T): boolean;
                containsAll(elements: JavaCollection<T>): boolean;
                equals(object: JavaCollection<T>): boolean;
                hashCode(): number;
                isEmpty(): boolean;
                iterator(): Iterator<T>;
                remove(element: T): boolean;
                removeAll(elements: JavaCollection<T>): boolean;
                retainAll(elements: JavaCollection<T>): boolean;
                size(): number;
                toArray(): T[];

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html
            const List: JavaClassStatics<List<any>> & NoConstructor & {

                copyOf<T>(coll: JavaCollection<T>): JavaList<T>;
                of<T>(...elements: T[]): JavaList<T>;

            }
            interface List<T> extends Collection<T> {

                add(index: int, element: T): void;
                add(element: T): boolean;
                addAll(index: int, elements: JavaCollection<T>): boolean;
                addAll(elements: JavaCollection<T>): boolean;
                get(index: int): T;
                indexOf(element: T): number;
                lastIndexOf(element: T): number;
                remove(index: int): T;
                remove(element: T): boolean;
                set(index: int, element: T): T;
                subList(start: int, end: int): JavaList<T>;

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html
            const Map: JavaClassStatics<Map<any, any>> & NoConstructor & {

                copyOf<K, V>(map: JavaMap<K, V>): JavaMap<K, V>;
                entry<K, V>(k: K, v: V): java.util.Map$Entry<K, V>;
                ofEntries<K, V>(...entries: java.util.Map$Entry<K, V>[]): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V, k9: K, v9: V): JavaMap<K, V>;
                of<K, V>(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V, k5: K, v5: V, k6: K, v6: V, k7: K, v7: V, k8: K, v8: V, k9: K, v9: V, k10: K, v10: V): JavaMap<K, V>;

            }
            interface Map<K, V> extends JavaObject, Record<string | number, V> {

                clear(): void;
                containsKey(key: K): boolean;
                containsValue(value: V): boolean;
                entrySet(): JavaSet<java.util.Map$Entry<K, V>>;
                equals(object: JavaMap<K, V>): boolean;
                get(key: K): V | null;
                getOrDefault(key: K, defaultValue: V): V;
                isEmpty(): boolean;
                keySet(): JavaSet<K>;
                put(ket: K, value: V): V;
                putAll(map: JavaMap<K, V>): void;
                putIfAbsent(key: K, value: V): V | null;
                remove(key: K): V | null;
                remove(key: K, value: V): boolean;
                replace(key: K, value: V): V;
                replace(key: K, oldValue: V, newValue: V): boolean;
                size(): number;
                values(): JavaCollection<V>;

            }

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Set.html
            const Set: JavaClassStatics<Set<any>> & NoConstructor & {

                copyOf<T>(coll: JavaCollection<T>): JavaSet<T>;
                of<T>(...elements: T[]): JavaSet<T>;

            }
            interface Set<T> extends Collection<T> {}

            export { Collection, List, Map, Set }

        }

        namespace io {

            const File: JavaClassStatics<File, File$$constructor> & {

                listRoots(): JavaArray<File>;

            }
            interface File$$constructor extends SuppressProperties {

                new (pathName: string): File;
                new (parent: string, child: string): File;
                new (parent: File, child: string): File;
                new (uri: java.net.URI): File;

            }
            interface File extends JavaObject {

                canExecute(): boolean;
                canRead(): boolean;
                canWrite(): boolean;
                createNewFile(): boolean;
                delete(): boolean;
                deleteOnExit(): void;
                exists(): boolean;
                getAbsolutePath(): string;
                getCanonicalPath(): string;
                getName(): string;
                getParent(): string;
                getPath(): string;
                isAbsolute(): boolean;
                isDirectory(): boolean;
                isFile(): boolean;
                isHidden(): boolean;
                length(): number;
                list(): JavaArray<string>;
                listFiles(): JavaArray<File>;
                mkdir(): boolean;
                mkdirs(): boolean;
                renameTo(dest: File): boolean;
                setExecutable(executable: boolean, ownerOnly?: boolean): boolean;
                setLastModified(time: long): boolean;
                setReadable(readable: boolean, ownerOnly?: boolean): boolean;
                setWritable(writable: boolean, ownerOnly?: boolean): boolean;
                toString(): string;
                toURI(): java.net.URI;

            }

            const Serializable: JavaInterfaceStatics<Serializable>;
            interface Serializable extends JavaObject {}

            export { File, Serializable }

        }

        namespace net {

            const URL: JavaClassStatics<URL, URL$$constructor>;
            interface URL$$constructor extends SuppressProperties {

                new (protocol: string, host: string, port: int, file: string): URL;
                new (protocol: string, host: string, file: string): URL;
                new (spec: string): URL;
                new (context: URL, spec: string): URL;

            }
            interface URL extends JavaObject {

                getFile(): string;
                getPath(): string;
                getProtocol(): string;
                getRef(): string;
                getQuery(): string;
                toString(): string;
                toURI(): URI;

            }

            const URI: JavaClassStatics<URI, URI$$constructor> & {

                create(str: string): URI;

            }
            interface URI$$constructor extends SuppressProperties {

                new (str: string): URI;
                new (scheme: string, userInfo: string, host: string, port: int, path: string, query: string, fragment: string): URI;
                new (scheme: string, authority: string, path: string, query: string, fragment: string): URI;
                new (scheme: string, host: string, path: string, fragment: string): URI;
                new (scheme: string, ssp: string, fragment: string): URI;
                new (scheme: string, path: string): URI;

            }
            interface URI extends java.lang.Comparable<URI>, java.io.Serializable {

                getHost(): string;
                getPath(): string;
                getPort(): number;
                getQuery(): string;
                getScheme(): string;
                normalize(): URI;
                relativize(uri: URI): URI;
                resolve(str: string): URI;
                toASCIIString(): string;
                toString(): string;
                toURL(): URL;

            }

            export { URL, URI }

        }

    }

}

type char   = number & {};
type byte   = number & {};
type short  = number & {};
type int    = number & {};
type long   = number | BigInt;
type float  = number & {};
type double = number & {};

type JavaClassArg<T = any> = JavaClass<T> | { readonly class: JavaClass<T> };

type JavaObject                = Packages.java.lang.Object;
type JavaClass<T = any>        = Packages.java.lang.Class<T>;
type JavaArray<T = any>        = Packages.java.lang.Array<T>;
type JavaCollection<T = any>   = Packages.java.util.Collection<T>;
type JavaList<T = any>         = Packages.java.util.List<T>;
type JavaSet<T = any>          = Packages.java.util.Set<T>;
type JavaMap<K = any, V = any> = Packages.java.util.Map<K, V> & Record<Extract<K, string | number>, V>;
