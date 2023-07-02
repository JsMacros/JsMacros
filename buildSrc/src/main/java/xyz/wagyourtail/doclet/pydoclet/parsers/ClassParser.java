package xyz.wagyourtail.doclet.pydoclet.parsers;

import com.sun.source.doctree.*;
import com.sun.source.util.DocTreePath;
import xyz.wagyourtail.doclet.pydoclet.Main;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.util.*;

public class ClassParser {
    public final TypeElement type;
    private final List<TypeMirror> imports = new LinkedList<>();
    private boolean importOverload = false, importList = false, importTypeVar = false, importAny = false, importMapping = false, importSet = false, importGeneric = false;
    HashMap<String, Map.Entry<String, Boolean>> typeVars = new HashMap<>();
    private final HashMap<String, String> types = new HashMap<>(),
                                          withArg = new HashMap<>(),
                                          unwantedClass = new HashMap<>();
    private final ArrayList<String> keywords = new ArrayList<>(Arrays.asList("False", "await", "else", "import", "pass", "", "None", "break", "except", "in", "raise", "", "True", "class", "finally", "is", "return", "", "and", "continue", "for", "lambda", "try", "", "as", "def", "from", "nonlocal", "while", "", "assert", "del", "global", "not", "with", "", "async", "elif", "if", "or", "yield"));





    public ClassParser(TypeElement type) {
        this.type = type;

        types.put("java.lang.Object", "object");
        types.put("java.lang.String", "str");
        types.put("java.lang.Integer", "int");
        types.put("java.lang.Boolean", "bool");
        types.put("java.lang.Double", "float");
        types.put("java.lang.annotation.Annotation", "");
        types.put("java.lang.Enum", "");
        types.put("java.util.Iterator", "iter");

        withArg.put("java.util.Set", "Set");
        withArg.put("java.util.List", "List");
        withArg.put("java.util.Map", "Mapping");
        withArg.put("java.util.Collection", "List");

        unwantedClass.put("java.lang.Object", "");
        unwantedClass.put("java.lang.annotation.Annotation", "");
        unwantedClass.put("java.lang.Enum", "");
        unwantedClass.put("java.util.Collection", "");
    }


    public String parseClass(){
        StringBuilder sb = new StringBuilder();

        //ClassLine e.g class Test(AbstractTest, ):
        sb.append(getClassLine());

        //Fields
        sb.append(getFields()).append("\n");

        //Methods / Constructor
        sb.append(getMethods());

        //safety pass
        sb.append(getTabs(1)).append("pass\n\n");

        //imports + abstract types
        sb.insert(0, getImports()).append("\n");

        //Main.reporter.print(Diagnostic.Kind.NOTE, type + ", " + imports + "");

        return sb.toString();
    }

    private String getMethods() {
        StringBuilder sb = new StringBuilder();

        type.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR).forEach(el ->{
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            ExecutableElement method = (ExecutableElement) el;
            importOverload = true;
            sb.append(getTabs(1)).append("@overload\n");
            sb.append(getTabs(1)).append("def ");
            if(el.getKind() == ElementKind.CONSTRUCTOR){
                sb.append("__init__");
            }else{
                sb.append(getVarName(method.getSimpleName().toString()));
            }

            sb.append("(self");

            method.getParameters().forEach(parameter -> sb.append(", ").append(getVarName(parameter.getSimpleName().toString())).append(": ").append(getTypeMirrorName(parameter.asType(), false)));
            sb.append(") -> ");
            if (method.getReceiverType() != null) {
                String return_type = getTypeMirrorName(method.getReturnType(), false);
//                Main.reporter.print(Diagnostic.Kind.NOTE, return_type);  // TODO hey! why weird type?

                sb.append(return_type);
            };
            sb.append(":\n");

            sb.append(getMethodDoc(method));
            sb.append(getTabs(2)).append("pass\n\n");
        });

        return sb.toString();
    }

    private String getSince(Element element){
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        SinceTree since = tree == null ? null : (SinceTree) tree.getBlockTags().stream().filter(e -> e.getKind().equals(DocTree.Kind.SINCE)).findFirst().orElse(null);
        if(since != null){
            return createDescription(element, since.getBody());
        }else return "";
    }

    private String getClassDoc(Element element){
        StringBuilder sb = new StringBuilder();
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        if(tree != null) {
            sb.append(getTabs(1)).append("\"\"\"").append(createDescription(element, tree.getFullBody()).strip());
            String since = getSince(element);
            if (since.length() > 0) {
                if (sb.length() > 4) sb.append("\\n");
                sb.append("\n");
                sb.append(getTabs(1)).append("Since: ").append(since).append("\n");
            } else {
                sb.append("\n");
            }
            sb.append(getTabs(1)).append("\"\"\"\n");
        }

        return sb.toString();
    }

    private String getMethodDoc(ExecutableElement element){
        StringBuilder sb = new StringBuilder();
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        if(tree != null) {
            sb.append(getTabs(2)).append("\"\"\"").append(createDescription(element, tree.getFullBody()).strip());
            String since = getSince(element);
            if (since.length() > 0) {
                if (sb.length() > 5) sb.append("\\n");
                sb.append("\n");
                sb.append(getTabs(2)).append("Since: ").append(since).append("\n");
            }else{
                sb.append("\n");
            }

            Map<String, String> paramMap = getParamDescriptions(element);
            if(paramMap.size() > 0){
                sb.append("\n").append(getTabs(2)).append("Args:\n");
                for(Map.Entry<String, String> entry : paramMap.entrySet()){
                    sb.append(getTabs(3)).append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }

            String returnString = getReturnDescription(element);
            if(returnString.length() > 0){
                sb.append("\n").append(getTabs(2)).append("Returns:\n");
                sb.append(getTabs(3)).append(returnString).append("\n");
            }

            sb.append(getTabs(2)).append("\"\"\"\n");
        }


        return sb.toString();
    }

    public Map<String, String> getParamDescriptions(ExecutableElement element) {
        Map<String, String> paramMap = new HashMap<>();
        DocCommentTree comment = Main.treeUtils.getDocCommentTree(element);
        if (comment == null) return paramMap;
        comment.getBlockTags().stream().filter(e -> e.getKind() == DocTree.Kind.PARAM).forEach(e -> paramMap.put(((ParamTree) e).getName().getName().toString(), createDescription(element, ((ParamTree) e).getDescription())));
        return paramMap;
    }

    private String getReturnDescription(ExecutableElement element) {
        DocCommentTree dct = Main.treeUtils.getDocCommentTree(element);
        if (dct == null) return "";
        ReturnTree t = (ReturnTree) dct.getBlockTags().stream().filter(e -> e.getKind() == DocTree.Kind.RETURN).findFirst().orElse(null);
        if (t == null) return "";
        return createDescription(element, t.getDescription());
    }

    private String getVarName(String simpleName){
        return keywords.contains(simpleName) ? simpleName + "_" : simpleName;
    }

    private String createDescription(Element element, List<? extends DocTree> inlineDoc){
        StringBuilder sb = new StringBuilder();

        //Main.reporter.print(Diagnostic.Kind.NOTE, element + "");
        for(DocTree docTree : inlineDoc){
            //Main.reporter.print(Diagnostic.Kind.NOTE, " - " + docTree + ", " + docTree.getKind());
            switch (docTree.getKind()){
                case TEXT -> sb.append(docTree.toString().strip()).append(" "); //.replace("\n", "")
                case CODE -> sb.append("'").append(((LiteralTree) docTree).getBody()).append("' ");
                case LINK, LINK_PLAIN -> {
                    Element ele = Main.treeUtils.getElement(new DocTreePath(new DocTreePath(Main.treeUtils.getPath(element), Main.treeUtils.getDocCommentTree(element)), ((LinkTree) docTree).getReference()));

                    if (ele != null){

                        if (List.of(ElementKind.INTERFACE, ElementKind.CLASS, ElementKind.ANNOTATION_TYPE, ElementKind.ENUM).contains(ele.getKind())) {
                            sb.append(getClassName((TypeElement) ele));
                        } else {
                            sb.append(getClassName((TypeElement) ele.getEnclosingElement())).append("#").append(ele);
                        }

                    }else{
                        sb.append(((LinkTree) docTree).getReference().getSignature().replace("\n", ""));
                    }
                    sb.append(" ");
                }
                case START_ELEMENT -> {
                    if(Objects.equals(docTree.toString(), "<li>")) sb.append("\n - ");
                    if(Objects.equals(docTree.toString(), "<p>")) sb.append("\n");
                }
            }
        }

        return sb.toString().replace("\n ", "\n");
    }

    private String getImports(){
        StringBuilder sb = new StringBuilder();

        List<String> imp = new LinkedList<>();
        //Main.reporter.print(Diagnostic.Kind.NOTE, type + ": " + importList);

        imports.forEach(t -> {
            if(!types.containsKey(getClearedNameFromTypeMirror(t))){
                if(!withArg.containsKey(getClearedNameFromTypeMirror(t))) {

                    if (!typeVars.isEmpty() && !(t + "").endsWith("int") && !(t + "").endsWith("str") && !(t + "").endsWith("void")) Main.reporter.print(Diagnostic.Kind.NOTE, t.toString());  // TODO hey! look at me!

                    if ((t + "").startsWith("xyz")) {
                        if (Main.typeUtils.asElement(t) != null) {
                            if (!getClassName((TypeElement) Main.typeUtils.asElement(t)).equals(getClassName(type))) {
                                String path = getClassName((TypeElement) Main.typeUtils.asElement(t)); //getPackage((TypeElement) Main.typeUtils.asElement(t)) + "." +
                                if (!imp.contains(path)) imp.add(path);
                            }
                        }
                    } else if ((t + "").startsWith("net") || (t + "").startsWith("com") || (t + "").startsWith("io") || (t + "").startsWith("java.util") ||
                            (t + "").startsWith("java.lang.Runnable") || (t + "").startsWith("java.lang.Thread") || (t + "").startsWith("java.lang.Throwable") ||
                            (t + "").startsWith("java.util.function") || (t + "").startsWith("java.lang.ref") || (t + "").startsWith("java.io") || (t + "").startsWith("org") || (t + "").startsWith("java.lang.Iterable") ||
                            (t + "").startsWith("java.lang.StackTraceElement")) {
//                        Main.reporter.print(Diagnostic.Kind.NOTE, typeVars + "");  // TODO hey! look at me!
                        if (!importTypeVar) importTypeVar = true;
                        typeVars.put(
                                getClassName((TypeElement) Main.typeUtils.asElement(t)),
                                new AbstractMap.SimpleEntry<>(t + "", false)
                        );
                    };
                }
            }
        });

        if (importOverload)
            sb.append("from typing import overload\n");
        if (importList)
            sb.append("from typing import List\n");
        if (importTypeVar)
            sb.append("from typing import TypeVar\n");
        if (importAny)
            sb.append("from typing import Any\n");
        if (importMapping)
            sb.append("from typing import Mapping\n");
        if(importSet)
            sb.append("from typing import Set\n");
        if(importGeneric)
            sb.append("from typing import Generic\n");

        imp.forEach(s -> sb.append("from .").append(s).append(" import ").append(s).append("\n"));

        sb.append("\n");
        String type_name;
        for(Map.Entry<String, Map.Entry<String, Boolean>> entry : typeVars.entrySet()){
            type_name = entry.getValue().getKey().replace("<", "_").replace(">", "_").replace("?", "").replace(".", "_");
            if(Objects.equals(type_name.toString(), "T") || Objects.equals(type_name.toString(), "U") || Objects.equals(type_name.toString(), "R")){
                sb.append(entry.getKey()).append(" = TypeVar(\"").append(entry.getKey()).append("\")\n");
            } else {
                sb.append(type_name).append(" = TypeVar(\"").append(type_name).append("\")\n");
                sb.append(entry.getKey()).append(" = ").append(type_name).append("\n\n");
            };
        }

        return sb.toString();
    }

    private String getClearedNameFromTypeMirror(TypeMirror type){
        StringBuilder s = new StringBuilder(type + "");
        if (s.indexOf("<") != -1){
            s.delete(s.indexOf("<"), s.length());
        }
        if (s.indexOf(" ") != -1){
            s.delete(0, s.indexOf(" ") + 1);
        }
        return s.toString();
    }

    private String getFields(){
        StringBuilder sb = new StringBuilder();

        type.getEnclosedElements().stream().filter(e -> e.getKind().equals(ElementKind.FIELD)|| e.getKind().equals(ElementKind.ENUM_CONSTANT)).forEach(el ->{
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            addImport(el.asType());
            sb.append(getTabs(1));
            sb.append(getVarName(el.getSimpleName().toString()));
            sb.append(": ");
            sb.append(getTypeMirrorName(el.asType(), false));
            sb.append("\n");
        });

        return sb.toString();
    }


    private String getTabs(int amount){
        return new String(new char[amount]).replace("\0", "\t");
    }

    private String getClassLine(){
        StringBuilder sb = new StringBuilder("\nclass "); // for PEP8
        sb.append(getClassName(type));

        List<? extends TypeMirror> implement = type.getInterfaces();
        TypeMirror extend = type.getSuperclass();

        if (!(implement.size() == 0 && extend.getKind().equals(TypeKind.NONE))){
            sb.append("(");
            implement.forEach(i -> {
                sb.append(getTypeMirrorName(i, true)).append(", ");
                addImport(i);
            });
            if(type.getTypeParameters().size() > 0){
                importGeneric = true;
                addImport(type.getTypeParameters().get(0).asType());
                sb.append("Generic[");
                sb.append(getTypeMirrorName(type.getTypeParameters().get(0).asType(), true));
                for(int i = 1; i < type.getTypeParameters().size(); i++){
                    sb.append(", ").append(getTypeMirrorName(type.getTypeParameters().get(i).asType(), true));
                    addImport(type.getTypeParameters().get(0).asType());
                }
                sb.append("], ");
            }
            if (!extend.getKind().equals(TypeKind.NONE)){
                sb.append(getTypeMirrorName(extend, true));
                addImport(extend);
            }
            //Main.reporter.print(Diagnostic.Kind.NOTE, sb.lastIndexOf(", ") + ", " + sb.length());
            if (sb.lastIndexOf(", ") == sb.length() - 2) sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
            if (sb.lastIndexOf("()") != -1) sb.delete(sb.length() - 2, sb.length());
        }


        sb.append(":\n");

        sb.append(getClassDoc(type));

        return sb.toString();
    }

    private void addImport(TypeMirror type){
        if(type != null) {
            if (!imports.contains(type)) imports.add(type);
            if (type.getKind().equals(TypeKind.ARRAY))
                addImport(((ArrayType) type).getComponentType());
        }

    }

    private String getTypeMirrorName(TypeMirror type, boolean cls) {
        imports.add(type);
//        Main.reporter.print(Diagnostic.Kind.MANDATORY_WARNING, type + "");
//        Main.reporter.print(Diagnostic.Kind.NOTE, type.toString());
        // TODO  hey! why weird names?
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "bool";
            }
            case BYTE, SHORT, LONG, FLOAT, DOUBLE -> {
                return "float";
            }
            case INT -> {
                return "int";
            }
            case CHAR -> {
                return "str";
            }
            case VOID, NONE, NULL -> {
                return "None";
            }
            case ARRAY -> {
                //List[NAME]
                importList = true;
                return "List[" + getTypeMirrorName(((ArrayType) type).getComponentType(), false) + "]";
            }
            case DECLARED -> {
                return parseDeclared(type, cls);
            }
            case TYPEVAR -> {
                typeVars.put(
                        ((TypeVariable) type).asElement().getSimpleName().toString(),
                        new AbstractMap.SimpleEntry<>(((TypeVariable) type).asElement().getSimpleName().toString(), true)
                );
                importTypeVar = true;
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case WILDCARD -> {
                importAny = true;
                return "Any";
            }
            default -> throw new UnsupportedOperationException(String.valueOf(type.getKind()));
        }
    }

    private String parseDeclared(TypeMirror type, boolean cls){
        if (cls){
            if (unwantedClass.containsKey(getClearedNameFromTypeMirror(type))){
                return unwantedClass.get(getClearedNameFromTypeMirror(type));
            }
        }
        if (types.containsKey(getClearedNameFromTypeMirror(type))){

            if (types.get(getClearedNameFromTypeMirror(type)).contains("List")){
                importList = true;
            }
            return types.get(getClearedNameFromTypeMirror(type));
        }

        if(getClearedNameFromTypeMirror(this.type.asType()).equals(getClearedNameFromTypeMirror(type))) return "\"" + getClassName((TypeElement) Main.typeUtils.asElement(type)) + "\"";

        if(withArg.containsKey(getClearedNameFromTypeMirror(type))){
            if (withArg.get(getClearedNameFromTypeMirror(type)).contains("List")) importList = true;
            else if(withArg.get(getClearedNameFromTypeMirror(type)).contains("Mapping")) importMapping = true;
            else if(withArg.get(getClearedNameFromTypeMirror(type)).contains("Set")) importSet = true;

            if (((DeclaredType) type).getTypeArguments().size() > 0) {
                //Main.reporter.print(Diagnostic.Kind.NOTE, this.type + ", " + type + ": " + ((DeclaredType) type).getTypeArguments());
                StringBuilder sb = new StringBuilder(withArg.get(getClearedNameFromTypeMirror(type)) + "[" + getTypeMirrorName(((DeclaredType) type).getTypeArguments().get(0), false));
                for (int i = 1; i < ((DeclaredType) type).getTypeArguments().size(); i++) {
                    sb.append(", ").append(getTypeMirrorName(((DeclaredType) type).getTypeArguments().get(i), false));
                }
                sb.append("]");
                //System.out.println(sb.toString());
                return sb.toString();
            }else{
                System.out.println("ERROR: " + type);
                return withArg.get(getClearedNameFromTypeMirror(type)) + "[]";
            }
        }
        return getClassName((TypeElement) Main.typeUtils.asElement(type));
    }




    /**
     * @return class name with _ for inner class
     */
    public static String getClassName(TypeElement type) {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element t2 = type.getEnclosingElement();
        while (t2.getKind() == ElementKind.INTERFACE || t2.getKind() == ElementKind.CLASS) {
            s.insert(0, t2.getSimpleName() + "_");
            t2 = t2.getEnclosingElement();
        }
        return s.toString();
    }

}
