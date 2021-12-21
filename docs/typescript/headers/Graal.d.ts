
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
    export function type<T>(className: string):Java.Class<T>;
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
 * Declare the java typings inside the Java namespace, because why not.
 * java types should all be interfaces so new users don't try to call `new` on them directly.
 * java typings should be namespaced by their package name, for organizational/asthetic reasons,
 * except for java.lang.*, those go top level if they need to be implemented, 
 * tho they should probably get upstreamed to this file.
 * 
 * Declaring this namespace for appending to it is expected of the user if they would like typing for other java classes.
 * 
 * It would be nice if any libraries that add java classes / functions, used in paramethers or return values,
 * would also include the classes in the same way with a re-declaration to extend the namespace in the libraries typescript file.
 */
declare namespace Java {
    //It might be a good idea to check the javadoc because it's hard to typescript constructors like this...
    export interface Class<T extends Java.Object> extends Java.Object {
        new(...value: any): T;
    }
    export interface Object {}
    export interface Interface {}

    /**
     * I know this one isn't really a class in java, but please use it as a wrapper for java arrays, 
     * so we can differentiate it from other ArrayLike structures.
     */
    export interface Array<T> extends Java.Object, ArrayLike<T> {
        [n: number]: T;
        length: number;
    }

    export interface StackTraceElement extends Java.Object, Java.java.io.Serializable {	
		
		getFileName():string;
		getLineNumber():number;
		getClassName():string;
		getMethodName():string;
		isNativeMethod():boolean;
		toString():string;
		equals(arg0: any):boolean;
		hashCode():number;
		
	}

    export interface Throwable extends Java.Object, Java.java.io.Serializable, Error {	
		
		getMessage():string;
		getLocalizedMessage():string;
		getCause():Java.Throwable;
		initCause(arg0: Java.Throwable):Java.Throwable;
		toString():string;
		fillInStackTrace():Java.Throwable;
		getStackTrace():Java.Array<Java.StackTraceElement>;
		setStackTrace(arg0: Java.Array<Java.StackTraceElement>):void;
		addSuppressed(arg0: Java.Throwable):void;
		getSuppressed():Java.Array<Java.Throwable>;
		
    }
    

    export namespace java {
        export namespace util {
            
            export interface Collection<T> extends Java.Object, ArrayLike<T> {
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
            export interface List<T> extends Collection<T> {
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
            export interface Map<K, V> extends Java.Object {
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
            export interface Set<T> extends Collection<T> {}
        }
    
        export namespace io {
            export interface File extends Java.Object {
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
                list(): Java.Array<string>;
                listFiles(): Java.Array<File>;
                mkdir(): boolean;
                mkdirs(): boolean;
                renameTo(dest: File): boolean;
                setExecutable(executable: boolean, ownerOnly?: boolean): boolean;
                setLastModified(time: number);
                setReadable(readable: boolean, ownerOnly?: boolean): boolean;
                setWritable(writable: boolean, ownerOnly?: boolean): boolean;
                toString(): string;
                toURI(): Java.java.net.URI;
            }
            
			export interface Serializable extends Java.Object {	
				
				
			}
        }
        
        export namespace net {
            export interface URL extends Java.Object {
                getFile(): string;
                getPath(): string;
                getProtocol(): string;
                getRef(): string;
                getQuery(): string;
                toString(): string;
                toURI(): URI;
            }
            export interface URI extends Java.Object {
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
                toURL(): Java.java.net.URL;

            }
        }
    }
}