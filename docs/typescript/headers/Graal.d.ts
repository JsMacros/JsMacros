
/// <reference lib="ES2022"/>

declare function load(source: string | Packages.java.io.File | Packages.java.net.URL): void;
declare function loadWithNewGlobal(source: string | Packages.java.io.File | Packages.java.net.URL, arguments: any): void;
// these two are commented out because it's useless, doesn't even show in game log
// declare function print(...arg: any[]): void;
// declare function printErr(...arg: any[]): void;

/**
 * Information about the graal runner.
 * Can someone tell me if this should be a namespace, I thought since it only had values in it, it would be best to declare this way.
 */
declare const Graal: {

    readonly language: string;
    readonly versionGraalVM: string;
    readonly versionECMAScript: number;

    isGraalRuntime(): boolean;

}

/**
 * This would be a namespace as well, but export/import are reserved terms in typescript
 */
declare const Polyglot: {

    import(key: string): any;
    export(key: string, value: any): void;
    eval(languageId: string, sourceCode: string): any;
    evalFile(languageId: string, sourceFileName: string): () => any;

}

/**
 * Java namespace for graal's Java functions.
 */
declare namespace Java {

    export function type<C extends keyof JavaTypeDict>(className: C): JavaTypeDict[C];
    export function type(className: string): unknown;
    export function from<T>(javaData: JavaArray<T>): T[];
    export function from<T>(javaData: JavaList<T>): T[];
    export function from<T>(javaData: JavaCollection<T>): T[];
    export function to<T>(jsArray: T[]): JavaArray<T>;
    export function to<T extends JavaObject>(jsData: object, toType: JavaClass<T>): T; // does this really exist
    export function isJavaObject(obj: JavaObject): boolean;
    export function isType(obj: JavaClass): boolean;
    export function typeName(obj: JavaObject): string | undefined;
    export function isJavaFunction(fn: JavaObject): boolean;
    export function isScriptObject(obj: any): boolean;
    export function isScriptFunction(fn: Function): boolean;
    export function addToClasspath(location: string): void;

}

type JavaTypeDict = Required<UnionToIntersection<FlattenPackage<typeof Packages>>>;

declare const java:   JavaPackage<typeof Packages.java>;
declare const javafx: JavaPackage<typeof Packages.javafx>;
declare const javax:  JavaPackage<typeof Packages.javax>;
declare const com:    JavaPackage<typeof Packages.com>;
declare const org:    JavaPackage<typeof Packages.org>;
declare const edu:    JavaPackage<typeof Packages.edu>;

type UnionToIntersection<U> =
    (U extends any ? (k: U) => 0 : never) extends ((k: infer I) => 0) ? I : never;

type IsStrictAny<T> = UnionToIntersection<T extends never ? 1 : 0> extends never ? true : false;

type FlattenPackage<T, P extends string = ''> =
    IsStrictAny<T> extends true ? never : T extends new (...args: any[]) => any ?
        { [PP in P]: T } :
        { [K in keyof T]: FlattenPackage<T[K], P extends '' ? K : `${P}.${string & K}`> }[keyof T];

type JavaPackage<T> = (IsStrictAny<T> extends true ? unknown : T) & {
    new (javaPackage: never): never;
    /** @deprecated */ Symbol: unknown;
    /** @deprecated */ apply: unknown;
    /** @deprecated */ arguments: unknown;
    /** @deprecated */ bind: unknown;
    /** @deprecated */ call: unknown;
    /** @deprecated */ caller: unknown;
    /** @deprecated */ length: unknown;
    /** @deprecated */ name: unknown;
    /** @deprecated */ prototype: unknown;
};

declare namespace Packages {

    namespace java {

        namespace lang {

            class Class<T> extends Object {

                static forName(className: string): JavaClass<any>;
                static forName(name: string, initialize: boolean, loader: ClassLoader): JavaClass<any>;
                static forName(module: Module, name: string): JavaClass<any>;

            }

            class Object {

                static readonly class: any;

                static Symbol: unknown;
                static apply: unknown;
                static arguments: unknown;
                static bind: unknown;
                static call: unknown;
                static caller: unknown;
                static length: unknown;
                static name: unknown;
                static prototype: unknown;

                getClass(): JavaClass<JavaObject>;
                hashCode(): number;
                equals(obj: JavaObject): object;
                toString(): string;
                notify(): void;
                notifyAll(): void;
                wait(): void;
                wait(var1: number): void;
                wait(timeoutMillis: number, nanos: number): void;

            }

            class Interface {}

            interface Comparable<T> extends Interface {

                compareTo(var1: T): number;

            }

            class Array<T> extends Object, ArrayLike<T> {

                constructor (abstract: never);

                [n: number]: T;
                length: number;

            }

            class StackTraceElement extends Object, java.io.Serializable {

                constructor (declaringClass: string, methodName: string, fileName: string, lineNumber: number);
                constructor (classLoaderName: string, moduleName: string, moduleVersion: string, declaringClass: string, methodName: string, fileName: string, lineNumber: number);

                getFileName(): string;
                getLineNumber(): number;
                getClassName(): string;
                getMethodName(): string;
                isNativeMethod(): boolean;
                toString(): string;
                equals(arg0: any): boolean;
                hashCode(): number;

            }

            class Throwable extends Object, java.io.Serializable, Error {

                constructor ();
                constructor (message: string);
                constructor (message: string, cause: Throwable);

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

            interface Iterable<T> extends Interface, ArrayLike<T> {}

        }

        namespace util {

            class Collection<T> extends java.lang.Iterable<T> {

                constructor (abstract: never);

                readonly [n: number]: T;

                size(): number;
                get(index: number): T;
                add(element: T): boolean;
                contains(element: T): boolean;
                containsAll(elements: Collection<T>): boolean;
                isEmpty(): boolean;
                // the `| T` on the return is here to make List<T> compatible
                remove(element: T): boolean | T;
                removeAll(elements: Collection<T>): boolean;
                retainAll(elements: Collection<T>): boolean;
                toArray(): Array<T>;

            }

            class List<T> extends Collection<T> {

                constructor (abstract: never);

                set(index: number, element: T): T;
                // the `| T` and optional second arg are here to make this compatible with Collection<T>
                add(index: number | T, element?: T): boolean;
                addAll(elements: Collection<T>): boolean;
                addAll(index: number, elements: Collection<T>): boolean;
                clear(): void;
                remove(index: number | T): T | boolean;
                indexOf(element: T): number;
                lastIndexOf(element: T): number;

            }

            class Map<K, V> extends java.lang.Object {

                constructor (abstract: never);

                [P in K]: V;

                clear(): void;
                containsKey(key: K): boolean;
                containsValue(value: V): boolean;
                delete(key: K): boolean;
                get(key: K): V | undefined;
                getOrDefault(key: K, defaultValue: V): V;
                keySet(): Set<K>;
                put(ket: K, value: V): V;
                putAll(map: Map<K, V>): void;
                putIfAbsent(key: K, value: V): V;
                replace(key: K, value: V): V;
                replace(key: K, oldValue: V, newValue: V): boolean;
                size(): number;
                values(): Collection<V>;

            }

            class Set<T> extends Collection<T> {

                constructor (abstract: never);

            }

        }

        namespace io {

            class File extends java.lang.Object {

                constructor (pathName: string);
                constructor (parent: string, child: string);
                constructor (parent: File, child: string);
                constructor (uri: java.net.URI);

                static listRoots(): JavaArray<File>;

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
                setLastModified(time: number);
                setReadable(readable: boolean, ownerOnly?: boolean): boolean;
                setWritable(writable: boolean, ownerOnly?: boolean): boolean;
                toString(): string;
                toURI(): java.net.URI;

            }

            interface Serializable extends java.lang.Interface {}

        }

        namespace net {

            class URL extends java.lang.Object {

                constructor (protocol: string, host: string, port: number, file: string);
                constructor (protocol: string, host: string, file: string);
                constructor (spec: string);
                constructor (context: URL, spec: string);

                getFile(): string;
                getPath(): string;
                getProtocol(): string;
                getRef(): string;
                getQuery(): string;
                toString(): string;
                toURI(): URI;

            }

            class URI extends java.lang.Object, java.lang.Comparable<URI>, java.io.Serializable {

                constructor (str: string);
                constructor (scheme: string, userInfo: string, host: string, port: number, path: string, query: string, fragment: string);
                constructor (scheme: string, authority: string, path: string, query: string, fragment: string);
                constructor (scheme: string, host: string, path: string, fragment: string);
                constructor (scheme: string, ssp: string, fragment: string);
                constructor (scheme: string, path: string);

                static create(str: string): URI;

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

        }

    }

    namespace net {

        export const minecraft: any;

    }

}

type _  = { [none: symbol]: never }; // to trick vscode to rename types
type _r = { [none: symbol]: never };

type JavaObject                    = Packages.java.lang.Object & _;
type JavaClass<T = any>            = Packages.java.lang.Class<T>;
type JavaArray<T = any>            = Packages.java.lang.Array<T>;
type JavaCollection<T = any>       = Packages.java.util.Collection<T>;
type JavaList<T = any>             = Packages.java.util.List<T>;
type JavaSet<T = any>              = Packages.java.util.Set<T>;
type JavaMap<K = any, V = any>     = Packages.java.util.Map<K, V> & Record<K, V>;
type JavaHashMap<K = any, V = any> = Packages.java.util.HashMap<K, V> & Record<K, V>;
