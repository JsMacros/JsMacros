package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Aliases;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Aliases("groovy")
@Extend("clike")
public class Prism_groovy {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        final Prism4j.Token interpolation = token("interpolation");

        final Prism4j.Grammar groovy = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "groovy",
                token("string", pattern(compile("'''(?:[^\\\\]|\\\\[\\s\\S])*?'''|'(?:\\\\.|[^\\\\'\\r\\n])*'"),
                        false,
                        true
                )),
                token("keyword", pattern(compile("\\b(?:abstract|as|assert|boolean|break|byte|case|catch|char|class|const|continue|def|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|in|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|trait|transient|try|void|volatile|while)\\b"))),
                token("number", pattern(compile("\\b(?:0b[01_]+|0x[\\da-f_]+(?:\\.[\\da-f_p\\-]+)?|[\\d_]+(?:\\.[\\d_]+)?(?:e[+-]?\\d+)?)[glidf]?\\b", CASE_INSENSITIVE))),
                token("operator", pattern(compile("(^|[^.])(?:~|==?~?|\\?[.:]?|\\*(?:[.=]|\\*=?)?|\\.[@&]|\\.\\.<|\\.\\.(?!\\.)|-[-=>]?|\\+[+=]?|!=?|<(?:<=?|=>?)?|>(?:>>?=?|=)?|&[&=]?|\\|[|=]?|\\/=?|\\^=?|%=?)"),
                        true
                )),
                token("punctuation", pattern(compile("\\.+|[{}\\[\\];(),:$]")))
        );

        GrammarUtils.insertBeforeToken(groovy, "string",
                token("shebang", pattern(compile("#!.+"),
                        false,
                        true,
                        "comment"
                )),
                token("interpolation-string", pattern(compile("\"\"\"(?:[^\\\\]|\\\\[\\s\\S])*?\"\"\"|([\"/])(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1|\\$\\/(?:[^/$]|\\$(?:[/$]|(?![/$]))|\\/(?!\\$))*\\/\\$"),
                        false,
                        true,
                        null,
                        grammar("inside",
                                interpolation,
                                token("string", pattern(compile("[\\s\\S]+")))
                        )
                ))
        );

        GrammarUtils.insertBeforeToken(groovy, "punctuation",
                token("spock-block", pattern(compile("\\b(?:and|cleanup|expect|given|setup|then|when|where):")))
        );

        GrammarUtils.insertBeforeToken(groovy, "function",
                token("annotation", pattern(compile("(^|[^.])@\\w+"),
                        true,
                        false,
                        "punctuation"
                ))
        );

        interpolation.patterns().add(
                pattern(compile("((?:^|[^\\\\$])(?:\\\\{2})*)\\$(?:\\w+|\\{[^{}]*\\})"),
                        true,
                        false,
                        null,
                        grammar("inside",
                                token("interpolation-punctuation", pattern(compile("^\\$\\{?|\\}$"),
                                        false,
                                        false,
                                        "punctuation"
                                )),
                                token("expression", pattern(compile("[\\s\\S]+"),
                                        false,
                                        false,
                                        null,
                                        grammar("inside", groovy.tokens())
                                ))
                        )
                )
        );

        return groovy;
    }

}
