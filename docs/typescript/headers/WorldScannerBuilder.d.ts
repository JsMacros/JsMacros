
/**
 * These are for WorldScannerBuilder, making the class has clear types
 * 
 * @author aMelonRind
 * @version 1.9.0
 * @see Packages.xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.WorldScannerBuilder
 */
declare namespace TypedWorldScannerBuilder {

  export interface Initial {

    withStateFilter<M extends BooStrNumMethod<BlockStateHelper>>(method: M): FilterFor<BlockStateHelper[M]>;
    withBlockFilter<M extends BooStrNumMethod<BlockHelper>>(method: M): FilterFor<BlockHelper[M]>;

    withStringBlockFilter(): StringFunctionFilter<BlockToString>;
    withStringStateFilter(): StringFunctionFilter<StateToString>;

    build(): WorldScanner;

  }

  export interface Main extends Initial {

    andStateFilter<M extends BooStrNumMethod<BlockStateHelper>>(method: M): FilterFor<BlockStateHelper[M]>;
    orStateFilter<M extends BooStrNumMethod<BlockStateHelper>>(method: M): FilterFor<BlockStateHelper[M]>;
    notStateFilter(): this;

    andBlockFilter<M extends BooStrNumMethod<BlockHelper>>(method: M): FilterFor<BlockHelper[M]>;
    orBlockFilter<M extends BooStrNumMethod<BlockHelper>>(method: M): FilterFor<BlockHelper[M]>;
    notBlockFilter(): this;

    andStringBlockFilter(): StringFunctionFilter<BlockToString>;
    orStringBlockFilter(): StringFunctionFilter<BlockToString>;

    andStringStateFilter(): StringFunctionFilter<StateToString>;
    orStringStateFilter(): StringFunctionFilter<StateToString>;

  }

  type BooStrNumMethod<T> =
    { [K in keyof T]: K extends 'hashCode' ? never : T[K] extends () => (infer R extends boolean | string | number) ?
    IsStrictAny<R> extends false ? K : never : never }[keyof T];

  type FilterFor<M> = M extends () => (infer R extends boolean | string | number) ?
    IsStrictAny<R> extends false ?
      [R] extends [infer S extends string] ? StringFilter<S> :
      R extends number ? NumberFilter :
      R extends boolean ? BooleanFilter
    : never : never : never;

  type BlockToString = `BlockHelper:{"id": "${BlockId}"}` | never;

  type StateToString = `BlockStateHelper:{"id": "${BlockId}", "properties": {${string}}}` | never;

  interface StringFunctionFilter<T extends string = string> {

    equals(...anyOf: T[]): Main;
    contains<S extends string>(...anyOf: Contains<T, S>[]): Main;
    startsWith<S extends string>(...anyOf: StartsWith<T, S>[]): Main;
    endsWith<S extends string>(...anyOf: EndsWith<T, S>[]): Main;
    matches(...anyOf: string[]): Main;

  }

  type Contains<T extends string, S extends string>   = { [A in S]: T extends `${string}${A}${string}` ? A : never }[S] | T;
  type StartsWith<T extends string, S extends string> = { [A in S]: T extends          `${A}${string}` ? A : never }[S] | T;
  type EndsWith<T extends string, S extends string>   = { [A in S]: T extends `${string}${A}`          ? A : never }[S] | T;

  // `test` methods does exist but commented out, see WorldScannerBuilder's document
  // methods that has `methodArgs` param are commented out because they're useless

  interface StringFilter<T extends string = string> {

    is(operation: 'EQUALS', compareTo: T): Main; // for ts performance
    is<M extends StringFilterOperation, S extends string>(operation: M, compareTo: StringCompareTo<M, T, S>): Main;
    // test<M extends StringFilterOperation>(operation: M, compareTo: StringCompareTo<M, T>): Main;
    // is<M extends StringFilterOperation>(methodArgs: []?, filterArgs: [operation: M, compareTo: StringCompareTo<M, T>]): Main;
    // test<M extends StringFilterOperation>(methodArgs: []?, filterArgs: [operation: M, compareTo: StringCompareTo<M, T>]): Main;

  }

  type StringFilterOperation = 'EQUALS' | 'CONTAINS' | 'STARTS_WITH' | 'ENDS_WITH' | 'MATCHES';
  type StringCompareTo<M extends StringFilterOperation, T extends string, S extends string> = 
    M extends 'EQUALS' ? T :
    M extends 'CONTAINS' ? Contains<T, S> :
    M extends 'STARTS_WITH' ? StartsWith<T, S> :
    M extends 'ENDS_WITH' ? EndsWith<T, S> :
    string;

  /**
   * can't distinguish between CharFilter and NumberFilter, so they're merged
   */
  interface NumberFilter {

    /** for number value */
    is(operation: NumberFilterOperation, compareTo: number): Main;
    // test(operation: NumberFilterOperation, compareTo: number): Main;
    // is(methodArgs: []?, filterArgs: [operation: NumberFilterOperation, compareTo: number]): Main;
    // test(methodArgs: []?, filterArgs: [operation: NumberFilterOperation, compareTo: number]): Main;

    /** for char value */
    is(compareTo: char): Main;
    // test(compareTo: char): Main;
    // is(methodArgs: []?, filterArgs: [compareTo: char]): Main;
    // test(methodArgs: []?, filterArgs: [compareTo: char]): Main;

  }

  type NumberFilterOperation = '>' | '>=' | '<' | '<=' | '==' | '!=';

  interface BooleanFilter {

    is(compareTo: boolean): Main;
    // test(compareTo: boolean): Main;
    // is(methodArgs: []?, filterArgs: [compareTo: boolean]): Main;
    // test(methodArgs: []?, filterArgs: [compareTo: boolean]): Main;

  }

}
