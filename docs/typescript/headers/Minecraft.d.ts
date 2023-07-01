
declare namespace Packages {

    // remove this if someone made a d.ts for minecraft classes
    namespace net {

        export const minecraft: McPackage;
        interface McPackage extends JavaPackageColoring, Record<`class_${string}`, McClass> {}
        interface McClass extends SuppressProperties, Record<string, any> {
            new (...args: any[]): any;
            readonly class: JavaClass;
        }

    }

}


