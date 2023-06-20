package xyz.wagyourtail;

public class StringHelpers {

    public static String toSymbolsGTLT(String s) {
        return s.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
    }

    public static String fromSymbolsGTLT(String s) {
        return s.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
    }

    public static String tabIn(String string) {
        return "    " + string.replaceAll("\n", "\n    ");
    }

    public static String tabIn(String string, int count) {
        for (int i = 0; i < count; i++) {
            string = "    " + string.replaceAll("\n", "\n    ");
        }
        return string;
    }

    public static String tabOut(String string) {
        return ("\n" + string).replaceAll("\n    ", "\n").substring(1);
    }

    public static String addToLineStarts(String string, String start) {
        return start + string.replaceAll("\n", "\n" + start);
    }

}
