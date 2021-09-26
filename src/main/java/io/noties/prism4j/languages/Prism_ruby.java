package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.*;

@Extend("clike")
public class Prism_ruby {
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        final Prism4j.Token interpolation = token("interpolation");
        final Prism4j.Token methodDefinition = token("method-definition");
        
        final Prism4j.Grammar ruby = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "ruby",
            token("comment",
                pattern(compile("#.*")),
                pattern(compile("^=begin\\s[\\s\\S]*?^=end", MULTILINE),
                    false,
                    true
                )
            ),
            token("class-name",
                pattern(compile("(\\b(?:class)\\s+|\\bcatch\\s+\\()[\\w.\\\\]+", CASE_INSENSITIVE),
                    true,
                    false,
                    null,
                    grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
                )
            ),
            token("keyword", pattern(compile("\\b(?:BEGIN|END|alias|and|begin|break|case|class|def|define_method|defined|do|each|else|elsif|end|ensure|extend|for|if|in|include|module|new|next|nil|not|or|prepend|private|protected|public|raise|redo|require|rescue|retry|return|self|super|then|throw|undef|unless|until|when|while|yield)\\b"))),
            token("string",
                pattern(compile("%[qQiIwWxs]?(?:([^a-zA-Z0-9\\s{(\\[<])(?:(?!\\1)[^\\\\]|\\\\[\\s\\S])*\\1|\\((?:[^()\\\\]|\\\\[\\s\\S])*\\)|\\{(?:[^#{}\\\\]|#(?:\\{[^}]+\\})?|\\\\[\\s\\S])*\\}|\\[(?:[^\\[\\]\\\\]|\\\\[\\s\\S])*\\]|<(?:[^<>\\\\]|\\\\[\\s\\S])*>)"),
                    false,
                    true,
                    null,
                    grammar("inside", interpolation)
                ),
                pattern(compile("(\"|')(?:#\\{[^}]+\\}|#(?!\\{)|\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\#\\r\\n])*\\1"),
                    false,
                    true,
                    null,
                    grammar("inside", interpolation)
                ),
                pattern(compile("<<[-~]?([a-z_]\\w*)[\\r\\n](?:.*[\\r\\n])*?[\\t ]*\\1", CASE_INSENSITIVE),
                    false,
                    true,
                    "heredoc-string",
                    grammar("inside",
                        token("delimiter", pattern(compile("^<<[-~]?[a-z_]\\w*|[a-z_]\\w*$", CASE_INSENSITIVE),
                                false,
                                false,
                                "symbol",
                                grammar("inside", token("punctuation", pattern(compile("^<<[-~]?"))))
                            )
                        ),
                        interpolation
                    )
                ),
                pattern(compile("<<[-~]?'([a-z_]\\w*)'[\\r\\n](?:.*[\\r\\n])*?[\\t ]*\\1", CASE_INSENSITIVE),
                    false,
                    true,
                    "heredoc-string",
                    grammar("inside",
                        token("delimiter", pattern(compile("^<<[-~]?'[a-z_]\\w*'|[a-z_]\\w*$", CASE_INSENSITIVE),
                            false,
                            false,
                            "symbol",
                            grammar("inside", token("punctuation", pattern(compile("^<<[-~]?'|'$"))))
                        ))
                    )
                )
            )
        );
        
        ruby.tokens().removeIf(e -> e.name().equals("function"));
        
        GrammarUtils.insertBeforeToken(ruby, "keyword",
            token("regex",
                pattern(compile("%r(?:([^a-zA-Z0-9\\s{(\\[<])(?:(?!\\1)[^\\\\]|\\\\[\\s\\S])*\\1|\\((?:[^()\\\\]|\\\\[\\s\\S])*\\)|\\{(?:[^#{}\\\\]|#(?:\\{[^}]+\\})?|\\\\[\\s\\S])*\\}|\\[(?:[^\\[\\]\\\\]|\\\\[\\s\\S])*\\]|<(?:[^<>\\\\]|\\\\[\\s\\S])*>)[egimnosux]{0,6}"),
                    false,
                    true,
                    null,
                    grammar("inside",
                        interpolation
                    )
                ),
                pattern(compile("(^|[^/])\\/(?!\\/)(?:\\[[^\\r\\n\\]]+\\]|\\\\.|[^\\[/\\\\\\r\\n])+\\/[egimnosux]{0,6}(?=\\s*(?:$|[\\r\\n,.;})#]))"),
                    true,
                    true,
                    null,
                    grammar("inside",
                        interpolation
                    )
                )
            ),
            token("variable", pattern(compile("[@$]+[a-zA-Z_]\\w*(?:[?!]|\\b)"))),
            token("symbol", pattern(compile("(^|[^:]):[a-zA-Z_]\\w*(?:[?!]|\\b)"), true)),
            methodDefinition
        );
        
        GrammarUtils.insertBeforeToken(ruby, "number",
            token("builtin", pattern(compile("\\b(?:Array|Bignum|Binding|Class|Continuation|Dir|Exception|FalseClass|File|Fixnum|Float|Hash|IO|Integer|MatchData|Method|Module|NilClass|Numeric|Object|Proc|Range|Regexp|Stat|String|Struct|Symbol|TMS|Thread|ThreadGroup|Time|TrueClass)\\b"))),
            token("constant", pattern(compile("\\b[A-Z]\\w*(?:[?!]|\\b)")))
        );
        
        final Prism4j.Grammar insideInterpolation;
        {
            final List<Prism4j.Token> tokens = new ArrayList<>(ruby.tokens().size() + 1);
            tokens.add(token("delimiter", pattern(compile("^#\\{|\\}$"), false, false, "tag")));
            tokens.addAll(ruby.tokens());
            insideInterpolation = grammar("inside", tokens);
        }
        interpolation.patterns().add(pattern(compile("#\\{[^}]+\\}"), false, false, null, insideInterpolation));
        
        final Prism4j.Grammar insideMethodDefinition;
        {
            final List<Prism4j.Token> tokens = new ArrayList<>(ruby.tokens().size() + 1);
            tokens.add(token("function", pattern(compile("\\w+$"))));
            tokens.addAll(ruby.tokens());
            insideMethodDefinition = grammar("inside", tokens);
        }
        methodDefinition.patterns().add(pattern(compile("(\\bdef\\s+)[\\w.]+"), true, false, null, insideMethodDefinition));
        
        return ruby;
    }
    
}
