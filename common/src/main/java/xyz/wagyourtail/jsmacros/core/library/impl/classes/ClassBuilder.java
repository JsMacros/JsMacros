package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.*;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.Neighbor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T>
 * @since 1.6.5
 */
 @SuppressWarnings("unused")
public class ClassBuilder<T> {
    public static final Map<String, MethodWrapper<Object, Object, Object, ?>> methodWrappers = new ConcurrentHashMap<>();
    ClassPool defaultPool = ClassPool.getDefault();
    public final CtClass ctClass;
    private final String className;
    private final AnnotationsAttribute classAnnotations;


    public ClassBuilder(String name, Class<T> parent, Class<?> ...interfaces) throws NotFoundException, CannotCompileException {
        className = name.replaceAll("\\.", "\\$");
        ctClass = defaultPool.makeClass("xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage." + className);
        ctClass.setSuperclass(defaultPool.getCtClass(parent.getName()));
        for (Class<?> i : interfaces) {
            ctClass.addInterface(defaultPool.getCtClass(i.getName()));
        }
        classAnnotations = new AnnotationsAttribute(ctClass.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
        ctClass.getClassFile().addAttribute(classAnnotations);
    }

    public FieldBuilder addField(Class<?> fieldType, String name) throws NotFoundException {
        return new FieldBuilder(defaultPool.getCtClass(fieldType.getName()), name);
    }

    public MethodBuilder addMethod(Class<?> returnType, String name, Class<?> ...params) throws NotFoundException {
        CtClass[] paramCtClasses = new CtClass[params.length];
        for (int i = 0; i < params.length; i++) {
            paramCtClasses[i] = defaultPool.getCtClass(params[i].getName());
        }
        return new MethodBuilder(defaultPool.getCtClass(returnType.getName()), name, paramCtClasses);
    }

    public ConstructorBuilder addConstructor(Class<?> ...params) throws NotFoundException {
        CtClass[] paramCtClasses = new CtClass[params.length];
        for (int i = 0; i < params.length; i++) {
            paramCtClasses[i] = defaultPool.getCtClass(params[i].getName());
        }
        return new ConstructorBuilder(paramCtClasses, false);
    }

    public ConstructorBuilder addClinit() {
        return new ConstructorBuilder(new CtClass[0], true);
    }


    public AnnotationBuilder<ClassBuilder<T>> addAnnotation(Class<?> type) throws NotFoundException {
        Annotation annotation = new Annotation(ctClass.getClassFile().getConstPool(), defaultPool.getCtClass(type.getName()));
        classAnnotations.addAnnotation(annotation);
        return new AnnotationBuilder<>(annotation, ctClass.getClassFile().getConstPool(), this);
    }

    public class FieldBuilder {
        private final CtClass fieldType;
        private String fieldName;
        private int fieldMods = 0;
        private final AnnotationsAttribute fieldAnnotations = new AnnotationsAttribute(ctClass.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
        public CtField.Initializer fieldInitializer;

        public FieldBuilder(CtClass fieldType, String name) {
            this.fieldType = fieldType;
            this.fieldName = name;
        }

        public ClassBuilder<T> compile(String code) throws CannotCompileException {
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

        public AnnotationBuilder<FieldBuilder> addAnnotation(Class<?> type) throws NotFoundException {
            Annotation annotation = new Annotation(ctClass.getClassFile().getConstPool(), defaultPool.getCtClass(type.getName()));
            fieldAnnotations.addAnnotation(annotation);
            return new AnnotationBuilder<>(annotation, ctClass.getClassFile().getConstPool(), this);
        }

        public FieldInitializerBuilder initializer() {
            return new FieldInitializerBuilder();
        }

        public ClassBuilder<T> end() throws CannotCompileException {
            CtField field = new CtField(fieldType, fieldName, ctClass);
            field.setModifiers(fieldMods);
            field.getFieldInfo().addAttribute(fieldAnnotations);
            ctClass.addField(field, fieldInitializer);
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

            public FieldBuilder callStaticMethodInThisClass(String methodName, String ...code_arg) {
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
        final AnnotationsAttribute methodAnnotations = new AnnotationsAttribute(ctClass.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
        int methodMods = 0;


        public MethodBuilder(CtClass methodReturnType, String methodName, CtClass ...params) {
            this.methodReturnType = methodReturnType;
            this.methodName = methodName;
            this.params = params;
        }

        public ClassBuilder<T> compile(String code) throws CannotCompileException {
            ctClass.addMethod(CtMethod.make(code, ctClass));
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

        public ClassBuilder<T> body(String code_src) throws CannotCompileException {
            CtMethod method = CtNewMethod.make(this.methodMods, this.methodReturnType, this.methodName, this.params, this.exceptions, code_src, ctClass);
            method.getMethodInfo().addAttribute(methodAnnotations);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public ClassBuilder<T> guestBody(MethodWrapper<Object, Object, Object, ?> methodBody) throws CannotCompileException, NotFoundException {
            CtMethod method = new CtMethod(this.methodReturnType, this.methodName, this.params, ctClass);
            method.setModifiers(this.methodMods);
            method.setExceptionTypes(this.exceptions);
            boolean voidReturn = methodReturnType.equals(CtClass.voidType);
            String guestName = ClassBuilder.this.className + ";" + methodName + Descriptor.ofMethod(methodReturnType, params);
            StringBuilder body = new StringBuilder();
            body.append("{");
            if (!voidReturn) {
                    body.append(" return ");
                    if (!methodReturnType.isPrimitive()) {
                        body.append("((").append(methodReturnType.getName()).append(")");
                    } else {
                        if (methodReturnType.equals(CtClass.booleanType)) {
                            body.append("((java.lang.Boolean)");
                        } else {
                            body.append("((java.lang.Number)");
                        }
                    }
            } else {
                body.append("(");
            }
            body.append("((xyz.wagyourtail.jsmacros.core.MethodWrapper)")
                .append("xyz.wagyourtail.jsmacros.core.library.impl.classes.ClassBuilder.methodWrappers.get(\"")
                .append(guestName)
                .append("\")).")
                .append(voidReturn ? "accept" : "apply")
                .append("(")
                .append("this, new Object[]{");
            int i = 0;
            for (CtClass param : params) {
                if (!param.isPrimitive()) {
                    body.append("$").append(++i).append(",");
                } else {
                    if (param.equals(CtClass.booleanType)) {
                        body.append("java.lang.Boolean.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.byteType)) {
                        body.append("java.lang.Byte.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.charType)) {
                        body.append("java.lang.Character.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.shortType)) {
                        body.append("java.lang.Short.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.intType)) {
                        body.append("java.lang.Integer.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.longType)) {
                        body.append("java.lang.Long.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.floatType)) {
                        body.append("java.lang.Float.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.doubleType)) {
                        body.append("java.lang.Double.valueOf($").append(++i).append("),");
                    } else {
                        throw new RuntimeException("Unknown primitive type: " + param.getName());
                    }
                }
            }
            if (params.length > 0) {
                body.deleteCharAt(body.length() - 1);
            }
            body.append("}))");
            if (!voidReturn) {
                if (!methodReturnType.isPrimitive()) {
                    body.append(");");
                } else {
                    if (methodReturnType.equals(CtClass.booleanType)) {
                        body.append(".booleanValue();");
                    } else if (methodReturnType.equals(CtClass.byteType)) {
                        body.append(".byteValue();");
                    } else if (methodReturnType.equals(CtClass.charType)) {
                        body.append(".charValue();");
                    } else if (methodReturnType.equals(CtClass.shortType)) {
                        body.append(".shortValue();");
                    } else if (methodReturnType.equals(CtClass.intType)) {
                        body.append(".intValue();");
                    } else if (methodReturnType.equals(CtClass.longType)) {
                        body.append(".longValue();");
                    } else if (methodReturnType.equals(CtClass.floatType)) {
                        body.append(".floatValue();");
                    } else if (methodReturnType.equals(CtClass.doubleType)) {
                        body.append(".doubleValue();");
                    } else {
                        throw new RuntimeException("Unknown primitive type: " + methodReturnType.getName());
                    }
                }
            } else {
                body.append(";");
            }
            body.append("}");
            method.setBody(body.toString());
            method.getMethodInfo().addAttribute(methodAnnotations);
            ctClass.addMethod(method);
            methodWrappers.put(guestName, methodBody);
            return ClassBuilder.this;
        }

        public ClassBuilder<T> body(MethodWrapper<CtClass, CtBehavior, Object, ?> buildBody) throws CannotCompileException {
            CtMethod method = new CtMethod(this.methodReturnType, this.methodName, this.params, ctClass);
            method.setModifiers(this.methodMods);
            method.getMethodInfo().addAttribute(methodAnnotations);
            buildBody.apply(ctClass, method);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public ClassBuilder<T> endAbstract() throws NotFoundException, CannotCompileException {
            CtMethod method = CtNewMethod.abstractMethod(this.methodReturnType, this.methodName, this.params, this.exceptions, ctClass);
            method.getMethodInfo().addAttribute(methodAnnotations);
            ctClass.addMethod(method);
            return ClassBuilder.this;
        }

        public AnnotationBuilder<MethodBuilder> addAnnotation(Class<?> type) throws NotFoundException {
            Annotation annotation = new Annotation(ctClass.getClassFile().getConstPool(), defaultPool.getCtClass(type.getName()));
            methodAnnotations.addAnnotation(annotation);
            return new AnnotationBuilder<>(annotation, ctClass.getClassFile().getConstPool(), this);
        }
    }

    public class ConstructorBuilder extends MethodBuilder {
        public ConstructorBuilder(CtClass[] params, boolean clInit) {
            super(CtClass.voidType, clInit ? MethodInfo.nameClinit : MethodInfo.nameInit, params);
            if (clInit)
                this.methodMods |= Modifier.STATIC;
        }

        @Override
        public ClassBuilder<T> body(String code_src) throws CannotCompileException {
            CtConstructor constructor = CtNewConstructor.make(this.params, this.exceptions, code_src, ctClass);
            constructor.setModifiers(this.methodMods);
            constructor.getMethodInfo().addAttribute(methodAnnotations);
            ctClass.addConstructor(constructor);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder<T> guestBody(MethodWrapper<Object, Object, Object, ?> methodBody) throws CannotCompileException, NotFoundException {
            CtConstructor method = new CtConstructor(this.params, ctClass);
            method.setModifiers(this.methodMods);
            method.setExceptionTypes(this.exceptions);
            String guestName = ClassBuilder.this.className + ";" + methodName + Descriptor.ofMethod(methodReturnType, params);
            StringBuilder body = new StringBuilder();
            body.append("{(((xyz.wagyourtail.jsmacros.core.MethodWrapper)")
                .append("xyz.wagyourtail.jsmacros.core.library.impl.classes.ClassBuilder.methodWrappers.get(\"")
                .append(guestName)
                .append("\")).")
                .append("apply")
                .append("(")
                .append("this, new Object[]{");
            int i = 0;
            for (CtClass param : params) {
                if (!param.isPrimitive()) {
                    body.append("(java.lang.Object) $").append(++i).append(",");
                } else {
                    if (param.equals(CtClass.booleanType)) {
                        body.append("java.lang.Boolean.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.byteType)) {
                        body.append("java.lang.Byte.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.charType)) {
                        body.append("java.lang.Character.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.shortType)) {
                        body.append("java.lang.Short.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.intType)) {
                        body.append("java.lang.Integer.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.longType)) {
                        body.append("java.lang.Long.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.floatType)) {
                        body.append("java.lang.Float.valueOf($").append(++i).append("),");
                    } else if (param.equals(CtClass.doubleType)) {
                        body.append("java.lang.Double.valueOf($").append(++i).append("),");
                    } else {
                        throw new RuntimeException("Unknown primitive type: " + param.getName());
                    }
                }
            }
            if (params.length > 0) {
                body.deleteCharAt(body.length() - 1);
            }
            body.append("}));}");
            method.setBody(body.toString());
            method.getMethodInfo().addAttribute(methodAnnotations);
            ctClass.addConstructor(method);
            methodWrappers.put(guestName, methodBody);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder<T> body(MethodWrapper<CtClass, CtBehavior, Object, ?> buildBody) throws CannotCompileException {
            CtConstructor constructor = new CtConstructor(this.params, ctClass);
            constructor.setModifiers(this.methodMods);
            constructor.getMethodInfo().addAttribute(methodAnnotations);
            buildBody.apply(ctClass, constructor);
            ctClass.addConstructor(constructor);
            return ClassBuilder.this;
        }

        @Override
        public ClassBuilder<T> endAbstract() {
            throw new UnsupportedOperationException("Cannot end abstract constructors");
        }

    }

    public Class<? extends T> finishBuildAndFreeze() throws CannotCompileException, NotFoundException {
        return (Class<? extends T>) ctClass.toClass(Neighbor.class);
    }

    public class AnnotationBuilder<T> {
        final Annotation annotationInstance;
        final ConstPool constPool;
        private final T member;

        private AnnotationBuilder(Annotation annotationInstance, ConstPool constPool, T member) {
            this.annotationInstance = annotationInstance;
            this.constPool = constPool;
            this.member = member;
        }

        public AnnotationBuilder<T> putString(String key, String value) {
            annotationInstance.addMemberValue(key, new StringMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putBoolean(String key, boolean value) {
            annotationInstance.addMemberValue(key, new BooleanMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putByte(String key, byte value) {
            annotationInstance.addMemberValue(key, new ByteMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putChar(String key, char value) {
            annotationInstance.addMemberValue(key, new CharMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putShort(String key, short value) {
            annotationInstance.addMemberValue(key, new ShortMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putInt(String key, int value) {
            annotationInstance.addMemberValue(key, new IntegerMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putLong(String key, long value) {
            annotationInstance.addMemberValue(key, new LongMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putFloat(String key, float value) {
            annotationInstance.addMemberValue(key, new FloatMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putDouble(String key, double value) {
            annotationInstance.addMemberValue(key, new DoubleMemberValue(value, constPool));
            return this;
        }

        public AnnotationBuilder<T> putClass(String key, Class<?> value) {
            annotationInstance.addMemberValue(key, new ClassMemberValue(value.getName(), constPool));
            return this;
        }

        public AnnotationBuilder<T> putEnum(String key, Enum<?> value) {
            EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
            enumMemberValue.setType(value.getDeclaringClass().getName());
            enumMemberValue.setValue(value.name());
            annotationInstance.addMemberValue(key, enumMemberValue);
            return this;
        }

        public AnnotationBuilder<AnnotationBuilder<T>> putAnnotation(String key, Class<?> annotationClass) throws NotFoundException {
            Annotation annotation = new Annotation(constPool, defaultPool.getCtClass(annotationClass.getName()));
            annotationInstance.addMemberValue(key, new AnnotationMemberValue(annotation, constPool));
            return new AnnotationBuilder<>(annotation, constPool, this);
        }

        public AnnotationArrayBuilder<AnnotationBuilder<T>> putArray(String key, Class<?> annotationClass) throws NotFoundException {
            AnnotationArrayBuilder ab = new AnnotationArrayBuilder<>(this, constPool);
            annotationInstance.addMemberValue(key, ab.arrayMemberValue);
            return ab;
        }


        public T finish() {
            return member;
        }

        public class AnnotationArrayBuilder<U> {
            ArrayMemberValue arrayMemberValue;
            private final ConstPool constPool;
            private final List<MemberValue> mv = new ArrayList<>();
            private final U parent;

            public AnnotationArrayBuilder(U parent, ConstPool constPool) {
                this.constPool = constPool;
                this.parent = parent;
                this.arrayMemberValue = new ArrayMemberValue(constPool);
            }

            public AnnotationArrayBuilder<U> putString(String value) {
                mv.add(new StringMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putBoolean(boolean value) {
                mv.add(new BooleanMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putByte(byte value) {
                mv.add(new ByteMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putChar(char value) {
                mv.add(new CharMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putShort(short value) {
                mv.add(new ShortMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putInt(int value) {
                mv.add(new IntegerMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putLong(long value) {
                mv.add(new LongMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putFloat(float value) {
                mv.add(new FloatMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putDouble(double value) {
                mv.add(new DoubleMemberValue(value, constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putClass(Class<?> value) {
                mv.add(new ClassMemberValue(value.getName(), constPool));
                return this;
            }

            public AnnotationArrayBuilder<U> putEnum(Enum<?> value) {
                EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
                enumMemberValue.setType(value.getDeclaringClass().getName());
                enumMemberValue.setValue(value.name());
                mv.add(enumMemberValue);
                return this;
            }

            public AnnotationBuilder<AnnotationArrayBuilder<U>> putAnnotation(Class<?> annotationClass) throws NotFoundException {
                Annotation annotation = new Annotation(constPool, defaultPool.getCtClass(annotationClass.getName()));
                mv.add(new AnnotationMemberValue(annotation, constPool));
                return new AnnotationBuilder<>(annotation, constPool, this);
            }

            public AnnotationArrayBuilder<AnnotationArrayBuilder<U>> putArray(Class<?> annotationClass) throws NotFoundException {
                AnnotationArrayBuilder ab = new AnnotationArrayBuilder<>(this, constPool);
                mv.add(ab.arrayMemberValue);
                return ab;
            }

            public U finish() {
                ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
                arrayMemberValue.setValue(mv.toArray(new MemberValue[0]));
                return parent;
            }
        }

    }
}
