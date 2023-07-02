
declare function load(source: string | Java.java.io.File | Java.java.net.URL):void;
declare function loadWithNewGlobal(source: string | Java.java.io.File | Java.java.net.URL, arguments: any):void;
declare function print(...arg: any):void;
declare function printerr(...arg: any):void;
declare function quit(status: number):void;
declare function read(file: string |  Java.java.io.File | Java.java.net.URL):string;
declare function readbuffer(file: string | Java.java.io.File | Java.java.net.URL):ArrayBuffer;
/**
 * reads a line of input from stdin
 */
declare function readline():string;

/**
 * Information about the graal runner.
 * Can someone tell me if this should be a namespace, I thought since it only had values in it, it would be best to declare this way.
 */
declare const Graal: {
    readonly versionJS: string;
    readonly versionGraalVM: string;
    readonly isGraalRuntime: boolean;
}

/**
 * Java namespace for graal's Java functions.
 */
declare namespace Java {
    export function type<T>(className: string):_javatypes.java.lang.Class<T> & { new(...values): T }
    export function from<T>(javaData: Array<T> | java.util.Collection<T>):T[];
    export function to<T extends Java.Object>(jsData: any, toType: Java.Class<T>):T;
    export function isJavaObject(obj: Java.Object):boolean;
    export function isType(obj: Java.Class<any>):boolean;
    export function typeName(obj: Java.Object):string | undefined;
    export function isJavaFunction(fn: Java.Object):boolean;
    export function isScriptObject(obj: any):boolean;
    export function isScriptFunction(fn: Function):boolean;
    export function addToClasspath(location: string):void;


}

/**
 * This would be a namespace as well, but export/import are reserved terms in typescript
 */
declare const Polyglot: {
    export(key: string, value: any):void;
    import(key: string):any;

    eval(languageId: string, sourceCode: string):any;
    evalFile(languageId: string, sourceFileName: string):() => any;
}

/**
 *
 * Declare the java typings.
 * java typings should be namespaced by their package name, for organizational/asthetic reasons,
 * java.lang classes should probably get upstreamed to this file.
 *
 * Declaring this namespace for appending to it is expected of the user if they would like typing for other java classes.
 *
 * It would be nice if any libraries that add java classes / functions, used in paramethers or return values,
 * would also include the classes in the same way with a re-declaration to extend the namespace in the libraries typescript file.
 */
declare namespace Java {
    export function type(className: "java.lang.Class"):_javatypes.java.lang.Class<_javatypes.java.lang.Class> & _javatypes.java.lang.Class.static
    export function type(className: "java.lang.Object"):_javatypes.java.lang.Class<_javatypes.java.lang.Object> & _javatypes.java.lang.Object.static
    export function type(className: "java.lang.StackTraceElement"):_javatypes.java.lang.Class<_javatypes.java.lang.StackTraceElement> & _javatypes.java.lang.StackTraceElement.static
    export function type(className: "java.lang.Throwable"):_javatypes.java.lang.Class<_javatypes.java.lang.Throwable> & _javatypes.java.lang.Throwable.static
    export function type(className: "java.io.File"):_javatypes.java.lang.Class<_javatypes.java.io.File> & _javatypes.java.io.File.static
    export function type(className: "java.net.URI"):_javatypes.java.lang.Class<_javatypes.java.net.URI> & _javatypes.java.net.URI.static
    export function type(className: "java.net.URL"):_javatypes.java.lang.Class<_javatypes.java.net.URL> & _javatypes.java.net.URL.static
}


declare namespace _javatypes {
    namespace java {
        namespace lang {
            interface Class<T> extends Object {}
            namespace Class {
                interface static {
                    forName(className: string): Class<?>
                    forName(name: string, initialize: boolean, loader: ClassLoader): Class<?>
                    forName(module: Module, name: string): Class<?>
                }
            }

            interface Object {
                getClass(): Class<Object>
                hashCode(): number
                equals(obj: Object): object
                toString(): string
                notify(): void
                notifyAll(): void
                wait(): void
                wait(var1: number): void
                wait(timeoutMillis: number, nanos: number): void
            }
            namespace Object {
                interface static {
                    new (): Object
                }
            }

            interface Interface {}
            namespace Interface {
                interface static {}
            }

            interface Comparable<T> extends Interface {
                compareTo(var1: T): number
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
                getFileName():string;
                getLineNumber():number;
                getClassName():string;
                getMethodName():string;
                isNativeMethod():boolean;
                toString():string;
                equals(arg0: any):boolean;
                hashCode():number;
            }
            namespace StackTraceElement {
                interface static {
                    new (declaringClass: string, methodName: string, fileName: string, lineNumber: number): StackTraceElement
                    new (classLoaderName: string, moduleName: string, moduleVersion: string, declaringClass: string, methodName: string, fileName: string, lineNumber: number): StackTraceElement
                }
            }

            interface Throwable extends Object, java.io.Serializable, Error {
                getMessage():string;
                getLocalizedMessage():string;
                getCause():Throwable;
                initCause(arg0: Throwable):Throwable;
                toString():string;
                fillInStackTrace():Throwable;
                getStackTrace():Array<StackTraceElement>;
                setStackTrace(arg0: Array<StackTraceElement>):void;
                addSuppressed(arg0: Throwable):void;
                getSuppressed():Array<Throwable>;
            }
            namespace Throwable {
                interface static {
                    new (): Throwable
                    new (message: string): Throwable
                    new (message: string, cause: Throwable): Throwable
                }
            }

            interface Iterable<T> extends java.lang.Interface, ArrayLike<T> {
            }
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
                list(): java.lang.Array<string>;
                listFiles(): java.lang.Array<File>;
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
                    new (pathName: string): File
                    new (parent: string, child: string): File
                    new (parent: File, child: string): File
                    new (uri: java.net.URI): File
                    listRoots(): java.lang.Array<File>
                }
            }

            interface Serializable extends java.lang.Interface{}
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
                    new (protocol: string, host: string, port: number, file: string): URL
                    new (protocol: string, host: string, file: string): URL
                    new (spec: string): URL
                    new (context: URL, spec: string): URL
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
                    new (str: string): URI
                    new (scheme: string, userInfo: string, host: string, port: number, path: string, query: string, fragment: string)
                    new (scheme: string, authority: string, path: string, query: string, fragment: string)
                    new (scheme: string, host: string, path: string, fragment: string)
                    new (scheme: string, ssp: string, fragment: string)
                    new (scheme: string, path: string)
                    create(str: string): URI
                }
            }
        }
    }
}