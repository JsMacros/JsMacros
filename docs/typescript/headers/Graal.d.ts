
type _ = { [none: never]: never }; // to trick vscode to rename types

type JavaObject                    = _javatypes.java.lang.Object & _;
type JavaClass<T = any>            = _javatypes.java.lang.Class<T>;
type JavaArray<T = any>            = _javatypes.java.lang.Array<T>;
type JavaCollection<T = any>       = _javatypes.java.util.Collection<T>;
type JavaList<T = any>             = _javatypes.java.util.List<T>;
type JavaSet<T = any>              = _javatypes.java.util.Set<T>;
type JavaMap<K = any, V = any>     = _javatypes.java.util.Map<K, V>;
type JavaHashMap<K = any, V = any> = _javatypes.java.util.HashMap<K, V>;

declare function load(source: string | _javatypes.java.io.File | _javatypes.java.net.URL): void;
declare function loadWithNewGlobal(source: string | _javatypes.java.io.File | _javatypes.java.net.URL, arguments: any): void;
declare function print(...arg: any): void;
// declare function printerr(...arg: any): void;
// declare function quit(status: number): void;
// declare function read(file: string |  _javatypes.java.io.File | _javatypes.java.net.URL): string;
// declare function readbuffer(file: string | _javatypes.java.io.File | _javatypes.java.net.URL): ArrayBuffer;
// /**
//  * reads a line of input from stdin
//  */
// declare function readline(): string;

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
    export(key: string, value: any): void;
    import(key: string): any;

    eval(languageId: string, sourceCode: string): any;
    evalFile(languageId: string, sourceFileName: string): () => any;
}

/**
 * Java namespace for graal's Java functions.
 */
declare namespace Java {
    export function type<T>(className: string): JavaClass<T> & { new(...values): T };
    export function type<C extends keyof JavaTypeDict>(className: C): JavaTypeDict[C];
    export function from<T>(javaData: JavaArray<T>): T[];
    export function from<T>(javaData: JavaList<T>): T[];
    export function from<T>(javaData: JavaCollection<T>): T[];
    export function to<T>(jsArray: any[]): JavaArray<T>;
    export function to<T extends JavaObject>(jsData: object, toType: JavaClass<T>): T; // does this really exist
    export function isJavaObject(obj: JavaObject): boolean;
    export function isType(obj: JavaClass): boolean;
    export function typeName(obj: JavaObject): string | undefined;
    export function isJavaFunction(fn: JavaObject): boolean;
    export function isScriptObject(obj: any): boolean;
    export function isScriptFunction(fn: Function): boolean;
    export function addToClasspath(location: string): void;
}

/**
 * Declare the java typings.
 * java typings should be namespaced by their package name, for organizational/asthetic reasons,
 * java.lang classes should probably get upstreamed to this file.
 *
 * Declaring this namespace for appending to it is expected of the user if they would like typing for other java classes.
 *
 * It would be nice if any libraries that add java classes / functions, used in paramethers or return values,
 * would also include the classes in the same way with a re-declaration to extend the namespace in the libraries typescript file.
 */
interface JavaTypeDict {
    "java.lang.Class": JavaClass<JavaClass> & _javatypes.java.lang.Class.static;
    "java.lang.Object": JavaClass<JavaObject> & _javatypes.java.lang.Object.static;
    "java.lang.StackTraceElement": JavaClass<_javatypes.java.lang.StackTraceElement> & _javatypes.java.lang.StackTraceElement.static;
    "java.lang.Throwable": JavaClass<_javatypes.java.lang.Throwable> & _javatypes.java.lang.Throwable.static;
    "java.io.File": JavaClass<_javatypes.java.io.File> & _javatypes.java.io.File.static;
    "java.net.URI": JavaClass<_javatypes.java.net.URI> & _javatypes.java.net.URI.static;
    "java.net.URL": JavaClass<_javatypes.java.net.URL> & _javatypes.java.net.URL.static;
}


declare namespace _javatypes {
    namespace java {
        namespace lang {
            interface Class<T> extends Object {}
            namespace Class {
                interface static {
                    forName(className: string): JavaClass<?>;
                    forName(name: string, initialize: boolean, loader: ClassLoader): JavaClass<?>;
                    forName(module: Module, name: string): JavaClass<?>;
                }
            }

            interface Object {
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
            namespace Object {
                interface static {
                    new (): JavaObject;
                }
            }

            interface Interface {}
            namespace Interface {
                interface static {}
            }

            interface Comparable<T> extends Interface {
                compareTo(var1: T): number;
            }
            namespace Comparable {
                interface static {}
            }

            interface Array<T> extends Object, ArrayLike<T> {
                [n: number]: T;
                length: number;
            }
            namespace Array {
                interface static {}
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
            namespace StackTraceElement {
                interface static {
                    new (declaringClass: string, methodName: string, fileName: string, lineNumber: number): StackTraceElement;
                    new (classLoaderName: string, moduleName: string, moduleVersion: string, declaringClass: string, methodName: string, fileName: string, lineNumber: number): StackTraceElement;
                }
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
            namespace Throwable {
                interface static {
                    new (): Throwable;
                    new (message: string): Throwable;
                    new (message: string, cause: Throwable): Throwable;
                }
            }

            interface Iterable<T> extends java.lang.Interface, ArrayLike<T> {}
            namespace Iterable {
                namespace static {}
            }
        }

        namespace util {
            interface Collection<T> extends java.lang.Iterable<T> {
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
            namespace Collection {
                interface static {}
            }

            interface List<T> extends Collection<T> {
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
            namespace List {
                interface static {}
            }

            interface Map<K, V> extends java.lang.Object {
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
            namespace Map {
                interface static {}
            }

            interface Set<T> extends Collection<T> {}
            namespace Set {
                interface static {}
            }
        }

        namespace io {
            interface File extends java.lang.Object {
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
            namespace File {
                interface static {
                    new (pathName: string): File;
                    new (parent: string, child: string): File;
                    new (parent: File, child: string): File;
                    new (uri: java.net.URI): File;
                    listRoots(): JavaArray<File>;
                }
            }

            interface Serializable extends java.lang.Interface {}
            namespace Serializable {
                interface static {}
            }
        }

        namespace net {
            interface URL extends java.lang.Object {
                getFile(): string;
                getPath(): string;
                getProtocol(): string;
                getRef(): string;
                getQuery(): string;
                toString(): string;
                toURI(): URI;
            }
            namespace URL {
                interface static {
                    new (protocol: string, host: string, port: number, file: string): URL;
                    new (protocol: string, host: string, file: string): URL;
                    new (spec: string): URL;
                    new (context: URL, spec: string): URL;
                }
            }

            interface URI extends java.lang.Object, java.lang.Comparable<URI>, java.io.Serializable {
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
            namespace URI {
                interface static {
                    new (str: string): URI;
                    new (scheme: string, userInfo: string, host: string, port: number, path: string, query: string, fragment: string);
                    new (scheme: string, authority: string, path: string, query: string, fragment: string);
                    new (scheme: string, host: string, path: string, fragment: string);
                    new (scheme: string, ssp: string, fragment: string);
                    new (scheme: string, path: string);
                    create(str: string): URI;
                }
            }
        }
    }
}