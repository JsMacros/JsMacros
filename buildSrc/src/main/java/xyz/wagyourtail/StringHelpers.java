package xyz.wagyourtail;

@SuppressWarnings("unused")
public class StringHelpers {

    public static String toSymbolsGTLT(String s) {
        return s.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
    }

    public static String fromSymbolsGTLT(String s) {
        return s.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
    }

    public static String tabIn(String string) {
        return tabIn(string, 1);
    }

    public static String tabIn(String string, int count) {
        return string.replaceAll("(?<=^|\n)(?=[^\n])", "    ".repeat(count));
    }

    public static String tabOut(String string) {
        return string.replaceAll("(?<=^|\n)    ", "");
    }

    public static String addToLineStarts(String string, String start) {
        return start + string.replaceAll("\n", "\n" + start);
    }

}
