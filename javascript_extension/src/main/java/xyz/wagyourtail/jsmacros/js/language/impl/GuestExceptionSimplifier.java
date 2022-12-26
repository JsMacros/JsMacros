package xyz.wagyourtail.jsmacros.js.language.impl;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.js.runtime.objects.Nullish;
import org.graalvm.polyglot.PolyglotException;
import xyz.wagyourtail.jsmacros.js.ValueAccessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public final class GuestExceptionSimplifier {

    private static final String QUALIFIER = "[\\w.$]+";
    private static final Pattern TYPE_ERROR_PATTERN = Pattern.compile("TypeError: invokeMember \\((?<function>[\\w_$]+)\\) on (?<class>" + QUALIFIER + ")(?:@[\\w]+)? failed due to: (?<error>.*)");

    private GuestExceptionSimplifier() {
    }

    /**
     * The exceptions are all stringified when created, so we have to parse the message rather than
     * the exception itself.
     *
     * @param ex the exception to simplify
     * @return the simplified exception if it is a known type, otherwise the original exception
     */
    public static String simplifyException(Throwable ex) throws ClassNotFoundException {
        if (ex instanceof PolyglotException) {
            PolyglotException p = (PolyglotException) ex;
            if (p.isGuestException()) {
                Object guest = ValueAccessor.getReceiver(((PolyglotException) ex).getGuestObject());
                if (guest instanceof AbstractTruffleException) {
                    Throwable cause = ((AbstractTruffleException) guest).getCause();
                    if (cause instanceof ArityException) {
                        return simplifyArityError(ex.getMessage(), (ArityException) cause);
                    } else if (cause instanceof UnsupportedTypeException) {
                        return simplifyOverloadError(ex.getMessage(), (UnsupportedTypeException) cause);
                    }
                }
            }
        }
        return ex.getMessage();
    }

    private static String simplifyArityError(String originalMessage, ArityException arityException) throws ClassNotFoundException {
        Matcher matcher = TYPE_ERROR_PATTERN.matcher(originalMessage);
        if (matcher.matches()) {
            String functionName = matcher.group("function");
            String className = matcher.group("class");
            String error = matcher.group("error");
            Class<?> clazz = Class.forName(className);
            StringBuilder builder = new StringBuilder();
            builder.append("ArityError on method ").append(className).append(".").append(functionName).append("\n");
            // Only count public methods, including static ones, defined in that class
            int[] possibleArities = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(functionName))
                .mapToInt(Method::getParameterCount)
                .sorted()
                .distinct()
                .toArray();
            builder.append("  Expected amount of arguments: ")
                .append(possibleArities.length == 1 ? possibleArities[0] : Arrays.toString(possibleArities))
                .append(", but got ")
                .append(arityException.getActualArity())
                .append(" arguments.");
            return builder.toString();
        }
        return originalMessage;
    }

    private static String simplifyOverloadError(String originalMessage, UnsupportedTypeException exception) throws ClassNotFoundException {
        Matcher matcher = TYPE_ERROR_PATTERN.matcher(originalMessage);
        if (matcher.matches()) {
            String functionName = matcher.group("function");
            String className = matcher.group("class");
            String error = matcher.group("error");
            Class<?> clazz = Class.forName(className);
            List<String> lines = new ArrayList<>();

            String classNameNoPackage = clazz.getName();
            int lastDot = classNameNoPackage.lastIndexOf('.');
            if (lastDot != -1) {
                classNameNoPackage = classNameNoPackage.substring(lastDot + 1);
            }

            lines.add("OverloadError on method " + classNameNoPackage + "." + functionName);
            lines.add("  Expected arguments: ");
            Arrays.stream(clazz.getMethods()).filter(e -> e.getName().equals(functionName)).sorted((a, b) -> {
                if (a.getParameterCount() == b.getParameterCount()) {
                    // Sort by parameter type
                    Class<?>[] aParams = a.getParameterTypes();
                    Class<?>[] bParams = b.getParameterTypes();
                    for (int i = 0; i < aParams.length; i++) {
                        if (aParams[i] != bParams[i]) {
                            return aParams[i].getName().compareTo(bParams[i].getName());
                        }
                    }
                }
                return a.getParameterCount() < b.getParameterCount() ? -1 : 1;
            }).forEach(e -> {
                StringBuilder builder = new StringBuilder();
                builder.append("    (");
                Class<?>[] params = e.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    // Remove package name
                    String param = params[i].getName();
                    int lastDotParam = param.lastIndexOf('.');
                    if (lastDotParam != -1) {
                        param = param.substring(lastDotParam + 1);
                    }
                    builder.append(param);
                    if (i != params.length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(")");
                lines.add(builder.toString());
            });

            lines.add("  Given arguments:");

            StringBuilder line = new StringBuilder();
            line.append("    (");

            for (Object part : exception.getSuppliedValues()) {
                if (part instanceof Number) {
                    line.append(part).append(" ").append(part.getClass().getSimpleName());
                } else if (part instanceof Nullish) {
                    line.append(((Nullish) part).getClassName().toString());
                } else {
                    if (part instanceof TruffleString) {
                        //                        line.append("\"" + part.toString().replace("\"", "\\\"") + "\"");
                        line.append("String");
                    } else {
                        // get the class name without the package, but if inner class, keep the parent class
                        String paramName = part.getClass().getName();
                        int dot = paramName.lastIndexOf('.');

                        if (lastDot != -1) {
                            paramName = paramName.substring(dot + 1);
                        }
                        line.append(paramName);
                    }
                }
                line.append(", ");
            }
            line.delete(line.length() - 2, line.length());
            line.append(')');
            lines.add(line.toString());
            return String.join("\n", lines);
        }
        return originalMessage;
    }
}