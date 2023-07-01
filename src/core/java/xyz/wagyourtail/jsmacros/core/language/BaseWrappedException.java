package xyz.wagyourtail.jsmacros.core.language;

import java.io.File;

public class BaseWrappedException<T> {
    public final T stackFrame;
    public final SourceLocation location;
    public final String message;
    public final BaseWrappedException<?> next;

    public BaseWrappedException(T exception, String message, SourceLocation location, BaseWrappedException<?> next) {
        this.stackFrame = exception;
        this.message = message;
        this.location = location;
        this.next = next;
    }

    private static String unQualifyClassName(String name) {
        String[] parts = name.split("\\.");
        return parts[parts.length - 1];
    }

    public static BaseWrappedException<StackTraceElement> wrapHostElement(StackTraceElement t, BaseWrappedException<?> next) {
        String message = " at " + unQualifyClassName(t.getClassName()) + "." + t.getMethodName();
        HostLocation loc = new HostLocation(t.isNativeMethod() ? "Native Method" : (t.getFileName() != null && t.getLineNumber() >= 0 ?
                t.getFileName() + " " + t.getLineNumber() + ":?" :
                (t.getFileName() != null ? t.getFileName() : "Unknown Source")));
        return new BaseWrappedException<>(t, message, loc, next);
    }

    public static abstract class SourceLocation {
    }

    public static class GuestLocation extends SourceLocation {
        public final File file;
        public final int line;
        public final int column;
        public final int startIndex;
        public final int endIndex;

        public GuestLocation(File file, int startIndex, int endIndex, int line, int column) {
            this.file = file;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.line = line;
            this.column = column;
        }

        public String toString() {
            if (column == -1) {
                return String.format("%s %d:?", file == null ? "unknown" : file.getName(), line);
            }
            return String.format("%s %d:%d", file == null ? "unknown" : file.getName(), line, column);
        }

    }

    public static class HostLocation extends SourceLocation {
        public final String location;

        public HostLocation(String location) {
            this.location = location;
        }

        public String toString() {
            return location;
        }

    }

}
