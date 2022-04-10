package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import javassist.*;
import javassist.bytecode.MethodInfo;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.Neighbor;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @since 1.6.5
 */
public class ClassBuilder<T> {
    public final Map<String, MethodWrapper<Object, Object, Object, ?>> methodWrappers = new ConcurrentHashMap<>();
    ClassPool defaultPool = ClassPool.getDefault();
    private final CtClass ctClass;
    private final String className;

    public ClassBuilder(String name, Class<T> parent, Class<?> ...interfaces) throws NotFoundException, CannotCompileException {
        className = name.replaceAll("\\.", "\\$");
        ctClass = defaultPool.makeClass("xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage." + className);
        ctClass.setSuperclass(defaultPool.getCtClass(parent.getName()));
        for (Class<?> i : interfaces) {
            ctClass.addInterface(defaultPool.getCtClass(i.getName()));
        }
    }

    public FieldBuilder addField(Class<?> fieldType, String name) throws NotFoundException {
        return new FieldBuilder(defaultPool.getCtClass(fieldType.getName()), name);
    }

    public FieldBuilder addField(Class<?> fieldType) throws NotFoundException {
        return new FieldBuilder(defaultPool.getCtClass(fieldType.getName()), null);
    }

    public MethodBuilder addMethod(Class<?> returnType, String name, Class<?> ...params) throws NotFoundException {
        CtClass[] paramCtClasses = new CtClass[params.length];
        for (int i = 0; i < params.length; i++) {
            paramCtClasses[i] = defaultPool.getCtClass(params[i].getName());
        }
        return new MethodBuilder(defaultPool.getCtClass(returnType.getName()), name, paramCtClasses);
    }

    public ConstructorBuilder buildConstructor(Class<?> ...params) throws NotFoundException {
        CtClass[] paramCtClasses = new CtClass[params.length];
        for (int i = 0; i < params.length; i++) {
            paramCtClasses[i] = defaultPool.getCtClass(params[i].getName());
        }
        return new ConstructorBuilder(paramCtClasses, false);
    }

    public ConstructorBuilder buildClInit() {
        return new ConstructorBuilder(new CtClass[0], true);
    }

    public class FieldBuilder {
        CtClass fieldType;
        String fieldName;
        int fieldMods = 0;
        CtField.Initializer fieldInitializer;

        public FieldBuilder(CtClass fieldType, String name) {
            this.fieldType = fieldType;
            this.fieldName = name;
        }

        public ClassBuilder compile(String code) throws CannotCompileException {
            ctClass.addField(CtField.make(fieldType.getName() + " " + fieldName + ";", ctClass));
            return ClassBuilder.this;
        }

        public FieldBuilder rename(String name) {
            this.fieldName = name;
            return this;
        }

        public FieldBuilder makePrivate() {
            this.fieldMods = (Modifier.PRIVATE) | (this.fieldMods & Modifier.STATIC) | (this.fieldMods & Modifier.FINAL);
            return this;
        }

        public FieldBuilder makePublic() {
            this.fieldMods = (Modifier.PUBLIC) | (this.fieldMods & Modifier.STATIC) | (this.fieldMods & Modifier.FINAL);
            return this;
        }

        public FieldBuilder makeProtected() {
            this.fieldMods = (Modifier.PROTECTED) | (this.fieldMods & Modifier.STATIC) | (this.fieldMods & Modifier.FINAL);
            return this;
        }

        public FieldBuilder makePackagePrivate() {
            this.fieldMods = (this.fieldMods & Modifier.STATIC) | (this.fieldMods & Modifier.FINAL);
            return this;
        }

        public FieldBuilder toggleStatic() {
            this.fieldMods ^= Modifier.STATIC;
            return this;
        }

        public FieldBuilder toggleFinal() {
            this.fieldMods ^= Modifier.FINAL;
            return this;
        }

        public int getMods() {
            return fieldMods;
        }

        public String getModString() {
            return Modifier.toString(fieldMods);
        }

        public FieldInitializerBuilder initializer() {
            return new FieldInitializerBuilder();
        }

        public ClassBuilder end() throws CannotCompileException {
            ctClass.addField(new CtField(fieldType, fieldName, ctClass), fieldInitializer);
            return ClassBuilder.this;
        }


        public class FieldInitializerBuilder {

            public FieldBuilder setInt(int value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setLong(long value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setFloat(float value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setDouble(double value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setChar(char value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setString(String value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setBoolean(boolean value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setByte(byte value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder setShort(short value) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.constant(value);
                return FieldBuilder.this;
            }

            public FieldBuilder compile(String code) {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.byExpr(code);
                return FieldBuilder.this;
            }

            public FieldBuilder initClass(Class<?> clazz, String ...code_arg) throws NotFoundException {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.byNewWithParams(defaultPool.getCtClass(clazz.getName()), code_arg);
                return FieldBuilder.this;
            }

            public FieldBuilder callStaticMethod(Class<?> clazz, String methodName, String ...code_arg) throws NotFoundException {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.byCallWithParams(defaultPool.getCtClass(clazz.getName()), methodName, code_arg);
                return FieldBuilder.this;
            }

            public FieldBuilder callStaticMethodInThisClass(String methodName, String ...code_arg) throws NotFoundException {
                FieldBuilder.this.fieldInitializer = CtField.Initializer.byCallWithParams(ctClass, methodName, code_arg);
                return FieldBuilder.this;
            }
        }


    }

    public class MethodBuilder {
        CtClass methodReturnType;
        CtClass[] params;
        CtClass[] exceptions;
        String methodName;
        int methodMods = 0;


        public MethodBuilder(CtClass methodReturnType, String methodName, CtClass ...params) {
            this.methodReturnType = methodReturnType;
            this.methodName = methodName;
            this.params = params;
        }

        public ClassBuilder compile(String code) throws CannotCompileException {
            CtMethod method = CtMethod.make(code, ctClass);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public MethodBuilder makePrivate() {
            this.methodMods = (Modifier.PRIVATE) | (this.methodMods & Modifier.STATIC);
            return this;
        }

        public MethodBuilder makePublic() {
            this.methodMods = (Modifier.PUBLIC) | (this.methodMods & Modifier.STATIC);
            return this;
        }

        public MethodBuilder makeProtected() {
            this.methodMods = (Modifier.PROTECTED) | (this.methodMods & Modifier.STATIC);
            return this;
        }

        public MethodBuilder makePackagePrivate() {
            this.methodMods = (this.methodMods & Modifier.STATIC);
            return this;
        }

        public MethodBuilder toggleStatic() {
            this.methodMods ^= Modifier.STATIC;
            return this;
        }

        public MethodBuilder rename(String newName) {
            this.methodName = newName;
            return this;
        }

        public MethodBuilder exceptions(Class<?> ...exceptions) throws NotFoundException {
            this.exceptions = new CtClass[exceptions.length];
            for (int i = 0; i < exceptions.length; i++) {
                this.exceptions[i] = defaultPool.getCtClass(exceptions[i].getName());
            }
            return this;
        }

        public ClassBuilder body(String code_src) throws CannotCompileException {
            CtMethod method = CtNewMethod.make(this.methodMods, this.methodReturnType, this.methodName, this.params, this.exceptions, code_src, ctClass);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public ClassBuilder guestBody(MethodWrapper<Object, Object, Object, ?> methodBody) throws CannotCompileException, NotFoundException {
            CtMethod method = new CtMethod(this.methodReturnType, this.methodName, this.params, ctClass);
            method.setModifiers(this.methodMods);
            method.setExceptionTypes(this.exceptions);
            method.setBody("{ return xyz.wagyourtail.jsmacros.core.library.impl.classes.ClassBuilder.methodWrappers.get(\"" + ClassBuilder.this.className + ";" + methodName + "\").apply(this);}");
            ctClass.addMethod(method);
            methodWrappers.put(ClassBuilder.this.className + ";" + methodName, methodBody);
            return ClassBuilder.this;
        }

        public ClassBuilder body(MethodWrapper<CtClass, CtBehavior, Object, ?> buildBody) throws CannotCompileException {
            CtMethod method = new CtMethod(this.methodReturnType, this.methodName, this.params, ctClass);
            method.setModifiers(this.methodMods);
            buildBody.apply(ctClass, method);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public ClassBuilder endAbstract() throws NotFoundException, CannotCompileException {
            CtMethod method = CtNewMethod.abstractMethod(this.methodReturnType, this.methodName, this.params, this.exceptions, ctClass);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }
    }

    public class ConstructorBuilder extends MethodBuilder {
        public ConstructorBuilder(CtClass[] params, boolean clInit) {
            super(CtClass.voidType, clInit ? MethodInfo.nameClinit : MethodInfo.nameInit, params);
            if (clInit)
                this.methodMods |= Modifier.STATIC;
        }

        @Override
        public ClassBuilder body(String code_src) throws CannotCompileException {
            CtConstructor constructor = CtNewConstructor.make(this.params, this.exceptions, code_src, ctClass);
            constructor.setModifiers(this.methodMods);
            ctClass.addConstructor(constructor);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder guestBody(MethodWrapper<Object, Object, Object, ?> methodBody) throws CannotCompileException, NotFoundException {
            CtConstructor method = new CtConstructor(this.params, ctClass);
            method.setModifiers(this.methodMods);
            method.setExceptionTypes(this.exceptions);
            method.setBody("{ return xyz.wagyourtail.jsmacros.core.library.impl.classes.ClassBuilder.methodWrappers.get(\"" + ClassBuilder.this.className + ";" + methodName + "\").apply(this);}");
            ctClass.addConstructor(method);
            methodWrappers.put(ClassBuilder.this.className + ";" + methodName, methodBody);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder body(MethodWrapper<CtClass, CtBehavior, Object, ?> buildBody) throws CannotCompileException {
            CtConstructor constructor = new CtConstructor(this.params, ctClass);
            constructor.setModifiers(this.methodMods);
            buildBody.apply(ctClass, constructor);
            ctClass.addConstructor(constructor);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder endAbstract() {
            throw new UnsupportedOperationException("Cannot end abstract constructors");
        }

    }

    public Class<? extends T> finishBuildAndFreeze() throws CannotCompileException, NotFoundException {
        return (Class<? extends T>) ctClass.toClass(Neighbor.class);
    }
}
