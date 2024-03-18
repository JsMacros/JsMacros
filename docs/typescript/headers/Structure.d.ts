
namespace Packages {

  namespace names {

    interface ExampleClass<T> extends ExampleInterface2 {} // omit this line if the class doesn't implement anything
    class ExampleClass<T> extends java.lang.Object { // add abstract modifier if it doesn't have any constructor
      static readonly class: JavaClass<ExampleClass<any>>; // every java class/interface has this
      /** @deprecated */ static prototype: undefined; // suppress this property because it's undefined in graaljs

      static readonly staticFinalField: 123;
      static staticFielda: number;
      static staticFieldb: string;
      // if you care about style, every multi line jsdoc should have an empty line here
      /**
       * description for this field
       */
      static staticFieldWithJsdoc: string;

      static staticMethoda(): void;
      static staticMethodb(): boolean;

      constructor ();
      constructor (arg: any, arg2: boolean);

      readonly finalField: 456;
      fielda: number;
      fieldb: ExampleInterface2;

      methoda(): void;
      methodb(): ExampleInterface;
      // if you care about style, this gap should be removed if the last element is field
    }

    abstract class ExampleInterface<T> extends java.lang.Interface { // should always be abstract and extends java.lang.Interface
      static readonly class: JavaClass<ExampleInterface<any>>;
      /** @deprecated */ static prototype: undefined;

      static readonly staticFinalField: 123;
      static staticFielda: number;
      static staticFieldb: string;

      static staticMethoda(): void;
      static staticMethodb(): boolean;
      // this gap, same as the class above
    }
    interface ExampleInterface<T> extends ExampleInterface2 {
      readonly finalField: 456;
      fielda: ExampleInterface2;
      fieldb: string;

      methoda(): void;
      methodb(i: ExampleInterface2<any>): object;
      // this gap, same as the class above
    }

    abstract class ExampleInterface2<T> extends java.lang.Interface {
      static readonly class: JavaClass<ExampleInterface2<any>>;
      /** @deprecated */ static prototype: undefined;
    }
    interface ExampleInterface2<T> {}

    namespace _function {} // if there's conflicting keyword, resolve it by exporting them

    export { // exporting this way instead of adding `export` keyword before classes is important for docs readability, if you don't do this the types will be long asf
      ExampleClass,
      ExampleInterface,
      ExampleInterface2,
      _function as function
    }

  }
  namespace names { // split namespace (`names` for this example) for exports and namespaces that doesn't need export

    namespace subnamespace {}

    namespace subnamespace2 {}

  }

}

export {} // ignore this, it's just preventing the classes in this example from exporting
