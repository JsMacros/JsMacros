package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static io.noties.prism4j.Prism4j.pattern;
import static io.noties.prism4j.Prism4j.token;

@SuppressWarnings("unused")
public class Prism_lua {
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        return prism4j.grammar("lua",
            token("comment", pattern(compile("^#!.+|--(?:\\[(=*)\\[[\\s\\S]*?\\]\\1\\]|.*)", MULTILINE))),
            token("string", pattern(compile("([\"'])(?:(?!\\1)[^\\\\\\r\\n]|\\\\z(?:\\r\\n|\\s)|\\\\(?:\\r\\n|[^z]))*\\1|\\[(=*)\\[[\\s\\S]*?\\]\\2\\]"), false, true)),
            token("number", pattern(compile("\\b0x[a-f\\d]+\\.?[a-f\\d]*(?:p[+-]?\\d+)?\\b|\\b\\d+(?:\\.\\B|\\.?\\d*(?:e[+-]?\\d+)?\\b)|\\B\\.\\d+(?:e[+-]?\\d+)?\\b", CASE_INSENSITIVE))),
            token("keyword", pattern(compile("\\b(?:and|break|do|else|elseif|end|false|for|function|goto|if|in|local|nil|not|or|repeat|return|then|true|until|while)\\b"))),
            token("function", pattern(compile("(?!\\d)\\w+(?=\\s*(?:[({]))"))),
            token("operator", pattern(compile("[-+*%^&|#]|\\/\\/?|<[<=]?|>[>=]?|[=~]=?")), /* Match ".." but don't break "..." */ pattern(compile("(^|[^.])\\.\\.(?!\\.)"), true)),
            token("punctuation", pattern(compile("[\\[\\](){},;]|\\.+|:+")))
        );
    }
}
