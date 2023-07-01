package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_regex {
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        return grammar("regex",
                token("charset", pattern(compile("((?:^|[^\\\\])(?:\\\\\\\\)*)\\[(?:[^\\\\\\]]|\\\\[\\s\\S])*\\]"), true, false, null,
                        grammar("inside",
                                token("charset-negation", pattern(compile("(^\\[)\\^"), true, false, "operator")),
                                token("charset-punctuation", pattern(compile("^\\[|\\]$"), false, false, "punctuation")),
                                token("range", pattern(compile("(?:[^\\\\-]|\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|c[a-zA-Z]|0[0-7]{0,2}|[123][0-7]{2}|.))-(?:[^\\\\-]|\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|c[a-zA-Z]|0[0-7]{0,2}|[123][0-7]{2}|.))"), false, false, null,
                                        grammar("inside",
                                                token("escape", pattern(compile("\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|0[0-7]{0,2}|[123][0-7]{2}|c[a-zA-Z]|.)"))),
                                                token("range-punctuation", pattern(compile("-"), false, false, "operator"))
                                        )
                                )),
                                token("special-escape", pattern(compile("\\\\[\\\\(){}\\[\\]^$+*?|.]"), false, false, "escape")),
                                token("charclass", pattern(compile("\\\\[wsd]|\\\\p\\{[^\\{}]+\\}", CASE_INSENSITIVE), false, false, "class-name")),
                                token("escape", pattern(compile("\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|0[0-7]{0,2}|[123][0-7]{2}|c[a-zA-Z]|.)")))
                        )
                )),
                token("special-escape", pattern(compile("\\\\[\\\\()\\{}\\[\\]^$+*?|.]"), false, false, "escape")),
                token("charclass", pattern(compile("\\.|\\\\[wsd]|\\\\p\\{[^{}]+}", CASE_INSENSITIVE), false, false, "class-name")),
                token("backreference",
                        pattern(compile("\\\\(?![123][0-7]{2})[1-9]"), false, false, "keyword"),
                        pattern(compile("\\\\k<[^<>']+>"), false, false, "keyword",
                                grammar("inside",
                                        token("group-name", pattern(compile("(<|')[^<>']+(?=[>']$)"), true, false, "variable"))
                                )
                        )
                ),
                token("anchor", pattern(compile("[$^]|\\\\[ABbGZz]"), false, false, "function")),
                token("escape", pattern(compile("\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|0[0-7]{0,2}|[123][0-7]{2}|c[a-zA-Z]|.)"))),
                token("group",
                        pattern(compile("\\((?:\\?(?:<[^<>']+>|'[^<>']+'|[>:]|<?[=!]|[idmnsuxU]+(?:-[idmnsuxU]+)?:?))?"), false, false, "punctuation",
                                grammar("inside",
                                        token("group-name", pattern(compile("(<|')[^<>']+(?=[>']$)"), true, false, "variable"))
                                )
                        ),
                        pattern(compile("\\)"), false, false, "punctuation")
                ),
                token("quantifier", pattern(compile("(?:[+*?]|\\{\\d+(?:,\\d*)?\\})[?+]?"), false, false, "number")),
                token("alternation", pattern(compile("\\|"), false, false, "keyword"))
        );
    }

}
