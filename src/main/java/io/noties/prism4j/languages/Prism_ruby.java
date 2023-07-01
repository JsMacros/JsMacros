package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.*;

@Extend("clike")
public class Prism_ruby {
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        final Prism4j.Token interpolation = token("interpolation");

        final Prism4j.Grammar ruby = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "ruby",
                token("comment",
                        pattern(compile("#.*")),
                        pattern(compile("#.*|^=begin\\s[\\s\\S]*?^=end", MULTILINE),
                                false,
                                true
                        )
                ),
                token("class-name",
                        pattern(compile("(\\b(?:class|module)\\s+|\\bcatch\\s+\\()[\\w.\\\\]+|\\b[A-Z_]\\w*(?=\\s*\\.\\s*new\\b)", CASE_INSENSITIVE),
                                true,
                                false,
                                null,
                                grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
                        )
                ),
                token("keyword", pattern(compile("\\b(?:BEGIN|END|alias|and|begin|break|case|class|def|define_method|defined|do|each|else|elsif|end|ensure|extend|for|if|in|include|module|new|next|nil|not|or|prepend|private|protected|public|raise|redo|require|rescue|retry|return|self|super|then|throw|undef|unless|until|when|while|yield)\\b"))),
                token("operator", pattern(compile("\\.{2,3}|&\\.|===|<?=>|[!=]?~|(?:&&|\\|\\||<<|>>|\\*\\*|[+\\-*/%<>!^&|=])=?|[?:]"))),
                token("punctuation", pattern(compile("[(){}\\[\\].,;]")))
        );

        ruby.tokens().removeIf(e -> e.name().equals("function"));

        GrammarUtils.insertBeforeToken(ruby, "operator",
                token("double-colon", pattern(compile("/::/"), false, false, "punctuation"))
        );

        String percentExpression = "(?:" + String.join("|",
                "([^a-zA-Z0-9\\s{(\\[<=])(?:(?!\\1)[^\\\\]|\\\\[\\s\\S])*\\1",
                "\\((?:[^()\\\\]|\\\\[\\s\\S]|\\((?:[^()\\\\]|\\\\[\\s\\S])*\\))*\\)",
                "\\{(?:[^{}\\\\]|\\\\[\\s\\S]|\\{(?:[^{}\\\\]|\\\\[\\s\\S])*\\})*\\}",
                "\\[(?:[^\\[\\]\\\\]|\\\\[\\s\\S]|\\[(?:[^\\[\\]\\\\]|\\\\[\\s\\S])*\\])*\\]",
                "<(?:[^<>\\\\]|\\\\[\\s\\S]|<(?:[^<>\\\\]|\\\\[\\s\\S])*>)*>"
        ) + ")";

        String symbolName = "(?:\"(?:\\\\.|[^\"\\\\\\r\\n])*\"|(?:\\b[a-zA-Z_]\\w*|[^\\s\\00-\\x7F]+)[?!]?|\\$.)";

        GrammarUtils.insertBeforeToken(ruby, "keyword",
                token("regex-literal",
                        pattern(compile("%r" +
                                        percentExpression
                                        + "[egimnosux]{0,6}"),
                                false,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("regex", pattern(compile("[\\s\\S]+")))
                                )
                        ),
                        pattern(compile("(^|[^/])\\/(?!\\/)(?:\\[[^\\r\\n\\]]+\\]|\\\\.|[^\\[/\\\\\\r\\n])+\\/[egimnosux]{0,6}(?=\\s*(?:$|[\\r\\n,.;})#]))"),
                                true,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("regex", pattern(compile("[\\s\\S]+")))
                                )
                        )
                ),
                token("variable", pattern(compile("[@$]+[a-zA-Z_]\\w*(?:[?!]|\\b)"))),
                token("symbol",
                        pattern(compile("(^|[^:]):" + symbolName), true, true),
                        pattern(compile("([\\r\\n{(,][ \\t]*)" + symbolName + "(?=:(?!:))"), true, true)
                ),
                token("method-definition",
                        pattern(compile("(\\bdef\\s+)\\w+(?:\\s*\\.\\s*\\w+)?"), true, false, null,
                                grammar("inside",
                                        token("function", pattern(compile("\\b\\w+$"))),
                                        token("keyword", pattern(compile("^self\\b"))),
                                        token("class-name", pattern(compile("^\\w+"))),
                                        token("punctuation", pattern(compile("\\.")))
                                )
                        )
                )
        );

        GrammarUtils.insertBeforeToken(ruby, "string",
                token("string-literal",
                        pattern(compile("%[qQiIwWs]?" + percentExpression),
                                false,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        ),
                        pattern(compile("(\"|')(?:#\\{[^}]+\\}|#(?!\\{)|\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\#\\r\\n])*\\1"),
                                false,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        ),
                        pattern(compile("<<[-~]?([a-z_]\\w*)[\\r\\n](?:.*[\\r\\n])*?[\\t ]*\\1", CASE_INSENSITIVE),
                                false,
                                true,
                                "heredoc-string",
                                grammar("inside",
                                        token("delimiter",
                                                pattern(compile("^<<[-~]?[a-z_]\\w*|\\b[a-z_]\\w*$", CASE_INSENSITIVE),
                                                        false,
                                                        false,
                                                        null,
                                                        grammar("inside",
                                                                token("symbol", pattern(compile("\\b\\w+"))),
                                                                token("punctuation", pattern(compile("^<<[-~]?")))
                                                        )
                                                )
                                        ),
                                        interpolation,
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        ),
                        pattern(compile("<<[-~]?'([a-z_]\\w*)'[\\r\\n](?:.*[\\r\\n])*?[\\t ]*\\1", CASE_INSENSITIVE),
                                false,
                                true,
                                "heredoc-string",
                                grammar("inside",
                                        token("delimiter",
                                                pattern(compile("^<<[-~]?'[a-z_]\\w*'|\\b[a-z_]\\w*$", CASE_INSENSITIVE),
                                                        false,
                                                        false,
                                                        null,
                                                        grammar("inside",
                                                                token("symbol", pattern(compile("\\b\\w+"))),
                                                                token("punctuation", pattern(compile("^<<[-~]?'|'$")))
                                                        )
                                                )
                                        ),
                                        token("string", pattern(compile("[\\s\\S]+")))
                                )
                        )
                ),
                token("command-literal",
                        pattern(compile("%x" + percentExpression),
                                false,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("command", pattern(compile("[\\s\\S]+"), false, false, "command"))
                                )
                        ),
                        pattern(compile("`(?:#\\{[^}]+\\}|#(?!\\{)|\\\\(?:\\r\\n|[\\s\\S])|[^\\\\`#\\r\\n])*`"),
                                false,
                                true,
                                null,
                                grammar("inside",
                                        interpolation,
                                        token("command", pattern(compile("[\\s\\S]+"), false, false, "string"))
                                )
                        )
                )
        );

        ruby.tokens().remove(GrammarUtils.findToken(ruby, "string"));

        GrammarUtils.insertBeforeToken(ruby, "number",
                token("builtin", pattern(compile("\\b(?:Array|Bignum|Binding|Class|Continuation|Dir|Exception|FalseClass|File|Fixnum|Float|Hash|IO|Integer|MatchData|Method|Module|NilClass|Numeric|Object|Proc|Range|Regexp|Stat|String|Struct|Symbol|TMS|Thread|ThreadGroup|Time|TrueClass)\\b"))),
                token("constant", pattern(compile("\\b[A-Z][A-Z0-9_]*(?:[?!]|\\b)")))
        );

        final Prism4j.Grammar insideInterpolation;
        insideInterpolation = grammar("inside",
                token("delimiter", pattern(compile("^#\\{|\\}$"), false, false, "punctuation")),
                token("content", pattern(compile("^(#\\{)[\\s\\S]+(?=\\}$)"), true, false, null, grammar("inside", ruby.tokens())))
        );
        interpolation.patterns().add(pattern(compile("((?:^|[^\\\\])(?:\\\\{2})*)#\\{(?:[^{}]|\\{[^{}]*\\})*\\}"), false, false, null, insideInterpolation));

        return ruby;
    }

}
