package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Aliases;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Aliases({"kotlin", "kt", "kts"})
@Extend("clike")
public class Prism_kotlin {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {

        final Prism4j.Grammar kotlin = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "kotlin",
                token("keyword", pattern(compile("(^|[^.])\\b(?:abstract|actual|annotation|as|break|by|catch|class|companion|const|constructor|continue|crossinline|data|do|dynamic|else|enum|expect|external|final|finally|for|fun|get|if|import|in|infix|init|inline|inner|interface|internal|is|lateinit|noinline|null|object|open|operator|out|override|package|private|protected|public|reified|return|sealed|set|super|suspend|tailrec|this|throw|to|try|typealias|val|var|vararg|when|where|while)\\b"),
                        true
                )),
                token("function",
                        pattern(compile("(?:`[^\\r\\n`]+`|\\b\\w+)(?=\\s*\\()"),
                                false,
                                true
                        ),
                        pattern(compile("(\\.)(?:`[^\\r\\n`]+`|\\w+)(?=\\s*\\{)"),
                                true,
                                true
                        )
                ),
                token("number", pattern(compile("\\b(?:0[xX][\\da-fA-F]+(?:_[\\da-fA-F]+)*|0[bB][01]+(?:_[01]+)*|\\d+(?:_\\d+)*(?:\\.\\d+(?:_\\d+)*)?(?:[eE][+-]?\\d+(?:_\\d+)*)?[fFL]?)\\b"))),
                token("operator", pattern(compile("\\+[+=]?|-[-=>]?|==?=?|!(?:!|==?)?|[\\/*%<>]=?|[?:]:?|\\.\\.|&&|\\|\\||\\b(?:and|inv|or|shl|shr|ushr|xor)\\b")))
        );

        kotlin.tokens().remove(GrammarUtils.findToken(kotlin, "class-name"));

        Prism4j.Token expression = token("expression");

        Prism4j.Grammar interpolationInside = grammar("inside",
                token("interpolation-punctuation", pattern(compile("^\\$\\{?|\\}$"),
                        false,
                        false,
                        "punctuation"
                )),
                expression
        );

        GrammarUtils.insertBeforeToken(kotlin, "string",
                token("string-literal",
                        pattern(compile("\"\"\"(?:[^$]|\\$(?:(?!\\{)|\\{[^{}]*\\}))*?\"\"\""),
                                false,
                                false,
                                "multiline",
                                grammar("inside",
                                        token("interpolation", pattern(compile("\\$(?:[a-z_]\\w*|\\{[^{}]*\\})", CASE_INSENSITIVE),
                                                false,
                                                false,
                                                null,
                                                interpolationInside
                                        )),
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        ),
                        pattern(compile("\"(?:[^\"\\\\\\r\\n$]|\\\\.|\\$(?:(?!\\{)|\\{[^{}]*\\}))*\""),
                                false,
                                false,
                                "singleline",
                                grammar("inside",
                                        token("interpolation", pattern(compile("((?:^|[^\\\\])(?:\\\\{2})*)\\$(?:[a-z_]\\w*|\\{[^{}]*\\})", CASE_INSENSITIVE),
                                                true,
                                                false,
                                                null,
                                                interpolationInside
                                        )),
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        )
                ),
                token("char", pattern(compile("'(?:[^'\\\\\\r\\n]|\\\\(?:.|u[a-fA-F0-9]{0,4}))'"),
                        false,
                        true
                ))
        );

        kotlin.tokens().remove(GrammarUtils.findToken(kotlin, "string"));

        GrammarUtils.insertBeforeToken(kotlin, "keyword",
                token("annotation", pattern(compile("\\B@(?:\\w+:)?(?:[A-Z]\\w*|\\[[^\\]]+\\])"),
                        false,
                        false,
                        "builtin"
                ))
        );

        GrammarUtils.insertBeforeToken(kotlin, "function",
                token("label", pattern(compile("\\b\\w+@|@\\w+\\b"),
                        false,
                        false,
                        "symbol"
                ))
        );

        expression.patterns().add(
                pattern(compile("[\\s\\S]+"),
                        false,
                        false,
                        null,
                        kotlin
                )
        );

        return kotlin;
    }

}
