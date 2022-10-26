package xyz.wagyourtail.jsmacros.js.language.impl;

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
public final class JsExceptionSimplifier {

    private static final String QUALIFIER = "[\\w.$]+";
    private static final Pattern TYPE_ERROR_PATTERN = Pattern.compile("TypeError: invokeMember \\((?<function>[\\w_$]+)\\) on (?<class>" + QUALIFIER + ")(?:@[\\w]+)? failed due to: (?<error>.*)");

    /**
     * raised in HostExecuteNode#noApplicableOverloadsException()
     */
    private static final String METHOD_OVERLOAD = "Method\\[\\w+ " + QUALIFIER + " " + QUALIFIER + "\\((?:[ .\\w],?)*\\)\\]";
    private static final Pattern OVERLOAD_ERROR_PATTERN = Pattern.compile("no applicable overload found \\(overloads: \\[(?<overloads>(" + METHOD_OVERLOAD + "(?:, )?)+)\\], arguments: \\[(?<arguments>.*)\\]\\)");

    private static final Pattern ARITY_ERROR_PATTERN = Pattern.compile("Arity error - expected: (?<min>\\d+)(?:-(?<max>\\d+))? actual: (?<actual>\\d+)");

    private JsExceptionSimplifier() {
    }

    /**
     * The exceptions are all stringified when created, so we have to parse the message rather than
     * the exception itself.
     *
     * @param ex the exception to simplify
     * @return the simplified exception if it is a known type, otherwise the original exception
     */
    public static String simplifyException(Throwable ex) {
        if (ex.getMessage() == null) {
            return null;
        }
        try {
            return simplifyExceptionInternal(ex);
        } catch (Exception e) {
            return ex.getMessage();
        }
    }

    private static String simplifyExceptionInternal(Throwable e) throws Exception {
        String message = e.getMessage();
        Matcher matcher = TYPE_ERROR_PATTERN.matcher(message);
        if (!matcher.matches()) {
            return e.getMessage();
        }
        String className = matcher.group("class");
        String functionName = matcher.group("function");
        String error = matcher.group("error");

        Matcher arityMatcher = ARITY_ERROR_PATTERN.matcher(error);
        if (arityMatcher.matches()) {
            return simplifyArityError(arityMatcher, className, functionName);
        }
        Matcher overloadMatcher = OVERLOAD_ERROR_PATTERN.matcher(error);
        if (overloadMatcher.matches()) {
            return simplifyOverloadError(overloadMatcher, className, functionName);
        }
        return message;
    }

    private static String simplifyArityError(Matcher arityMatcher, String className, String functionName) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        StringBuilder builder = new StringBuilder();
        builder.append("ArityError on method ").append(className).append(".").append(functionName).append("\n");
        // Only count public methods, including static ones, defined in that class
        int[] possibleArities = Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(functionName)).mapToInt(Method::getParameterCount).sorted().distinct().toArray();
        builder.append("Expected amount of arguments: ")
                .append(Arrays.toString(possibleArities))
                .append(", but got ")
                .append(arityMatcher.group("actual"))
                .append(" arguments.");
        return builder.toString();
    }

    private static String simplifyOverloadError(Matcher matcher, String clazz, String function) {
        String overloads = matcher.group("overloads");
        String arguments = matcher.group("arguments");

        List<String> lines = new ArrayList<>();
        lines.add("OverloadError on method " + clazz + "." + function);
        lines.add("Possible overloads:");
        int pos = 0;
        while (pos < overloads.length()) {
            // All arguments are wrapped in parentheses, so iterate until we find an opening one
            while (pos < overloads.length() && overloads.charAt(pos++) != '(') {
            }

            // If the next character is a closing parenthesis, then there are no arguments
            if (overloads.charAt(pos) != ')') {
                // We only want the simple name of the class, and the simple name always starts from the beginning, a comma or a dot.
                int lastClassPos = pos;
                StringBuilder line = new StringBuilder();
                line.append("[");
                while (pos < overloads.length()) {
                    char c = overloads.charAt(pos++);
                    if (c == ')') {
                        line.append(overloads, lastClassPos, pos);
                        break;
                    } else if (c == ',') {
                        line.append(overloads, lastClassPos, pos - 1);
                        line.append(",");
                        lastClassPos = pos;
                    } else if (c == '.') {
                        lastClassPos = pos;
                    }
                }
                line.deleteCharAt(line.length() - 1);
                line.append("]");
                lines.add(line.toString());
            } else {
                lines.add("[]");
            }
            pos++;
        }
        lines.add("Given arguments:");

        StringBuilder line = new StringBuilder();
        line.append("[");
        // This method doesn't work when there are Strings containing commas or paranthesis, but that will be extremely rare and there is no other way to filter these Strings correctly
        String[] parts = arguments.split(", ");
        for (String part : parts) {
            int index = part.lastIndexOf("(");
            String className = part.substring(index + 1, part.length() - 1);
            String value = part.substring(0, index == 0 ? 0 : index - 1);
            //show double values because they often cause confusion when not rounded correctly
            //Float should not show up here, but better safe than sorry
            if (className.equals("Double") || className.equals("Float")) {
                line.append(part);
            } else if (className.equals("Nullish")) {
                line.append("null");
            } else {
                if (className.equals("TruffleString")) {
                    className = "String";
                }
                line.append("(" + className + ")");
            }
            line.append(", ");
        }
        line.delete(line.length() - 2, line.length());
        line.append(']');
        lines.add(line.toString());
        return String.join("\n", lines);
    }

}