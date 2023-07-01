package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.*;

@SuppressWarnings("unused")
public class Prism_lua {
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        return grammar("lua",
                token("comment", pattern(compile("^#!.+|--(?:\\[(=*)\\[[\\s\\S]*?\\]\\1\\]|.*)", MULTILINE))),
                token("string", pattern(compile("([\"'])(?:(?!\\1)[^\\\\\\r\\n]|\\\\z(?:\\r\\n|\\s)|\\\\(?:\\r\\n|[^z]))*\\1|\\[(=*)\\[[\\s\\S]*?\\]\\2\\]"), false, true)),
                token("number", pattern(compile("\\b0x[a-f\\d]+(?:\\.[a-f\\d]*)?(?:p[+-]?\\d+)?\\b|\\b\\d+(?:\\.\\B|(?:\\.\\d*)?(?:e[+-]?\\d+)?\\b)|\\B\\.\\d+(?:e[+-]?\\d+)?\\b", CASE_INSENSITIVE))),
                token("boolean", pattern(compile("\\b(?:true|false|nil)\\b", CASE_INSENSITIVE))),
                token("keyword", pattern(compile("\\b(?:and|break|do|else|elseif|end|for|function|goto|if|in|local|not|or|repeat|return|then|until|while)\\b", CASE_INSENSITIVE))),
                token("function", pattern(compile("(?!\\d)\\w+(?=\\s*(?:[({]))"))),
                token("operator", pattern(compile("[-+*%^&|#]|\\/\\/?|<[<=]?|>[>=]?|[=~]=?")), /* Match ".." but don't break "..." */ pattern(compile("(^|[^.])\\.\\.(?!\\.)"), true)),
                token("punctuation", pattern(compile("[\\[\\](){},;]|\\.+|:+")))
        );
    }

}
