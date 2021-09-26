package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Aliases;
import io.noties.prism4j.annotations.Extend;
import io.noties.prism4j.annotations.Modify;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

/**
 * This class is from <a target="_blank" href="https://github.com/noties/Prism4j">Prism4j</a>
 * under the <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a>
 * I updated template-strings to match with the JS version again, which made them work better. other updates.
 */
@SuppressWarnings("unused")
@Aliases("js")
@Modify("markup")
@Extend("clike")
public class Prism_javascript {
    
    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        
        final Prism4j.Grammar js = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "javascript",
            token("keyword",
                pattern(compile("((?:^|\\})\\s*)catch\\b"), true),
                pattern(compile("(^|[^.]|\\.\\.\\.\\s*)\\b(?:as|assert(?=\\s*\\{)|async(?=\\s*(?:function\\b|\\(|[$\\w\\xA0-\\uFFFF]|$))|await|break|case|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally(?=\\s*(?:\\{|$))|for|from(?=\\s*(?:['\"]|$))|function|(?:get|set)(?=\\s*(?:[#\\[$\\w\\xA0-\\uFFFF]|$))|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|static|super|switch|this|throw|try|typeof|undefined|var|void|while|with|yield)\\b"), true)
            ),
            token("function", pattern(compile("#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?=\\s*(?:\\.\\s*(?:apply|bind|call)\\s*)?\\()"))),
            token("number", pattern(compile("\\b(?:(?:0[xX](?:[\\dA-Fa-f](?:_[\\dA-Fa-f])?)+|0[bB](?:[01](?:_[01])?)+|0[oO](?:[0-7](?:_[0-7])?)+)n?|(?:\\d(?:_\\d)?)+n|NaN|Infinity)\\b|(?:\\b(?:\\d(?:_\\d)?)+\\.?(?:\\d(?:_\\d)?)*|\\B\\.(?:\\d(?:_\\d)?)+)(?:[Ee][+-]?(?:\\d(?:_\\d)?)+)?"))),
            token("operator", pattern(compile("--|\\+\\+|\\*\\*=?|=>|&&=?|\\|\\|=?|[!=]==|<<=?|>>>?=?|[-+*/%&|^!=<>]=?|\\.{3}|\\?\\?=?|\\?\\.?|[~:]")))
        );
        
        GrammarUtils.insertBeforeToken(js, "keyword",
            token("regex", pattern(
                compile("((?:^|[^$\\w\\xA0-\\uFFFF.\"'\\])\\s]|\\b(?:return|yield))\\s*)\\/(?:\\[(?:[^\\]\\\\\\r\\n]|\\\\.)*\\]|\\\\.|[^/\\\\\\[\\r\\n])+\\/[dgimyus]{0,7}(?=(?:\\s|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/)*(?:$|[\\r\\n,.;:})\\]]|\\/\\/))"),
                true,
                true,
                null,
                grammar("inside",
                    token("regex-source", pattern(compile("^(\\/)[\\s\\S]+(?=\\/[a-z]*$)"), true, false, "language-regex",
                        GrammarUtils.require(prism4j, "regex")
                    )),
                    token("regex-flags", pattern(compile("[a-z]+$"))),
                    token("regex-delimiter", pattern(compile("^\\/|\\/$"))),
                    token("regex-flags", pattern(compile("^[a-z]+$")))
                )
            )),
            token(
                "function-variable",
                pattern(
                    compile("#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?=\\s*[=:]\\s*(?:async\\s*)?(?:\\bfunction\\b|(?:\\((?:[^()]|\\([^()]*\\))*\\)|(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*)\\s*=>))"),
                    false,
                    false,
                    "function"
                )
            ),
            token(
                "parameter",
                pattern(compile("(function(?:\\s+(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*)?\\s*\\(\\s*)(?!\\s)(?:[^()\\s]|\\s+(?![\\s)])|\\([^()]*\\))+(?=\\s*\\))"), true, false, null, js),
                pattern(compile("(^|[^$\\w\\xA0-\\uFFFF])(?!\\s)[_$a-z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?=\\s*=>)", CASE_INSENSITIVE), true, false, null, js),
                pattern(compile("(\\(\\s*)(?!\\s)(?:[^()\\s]|\\s+(?![\\s)])|\\([^()]*\\))+(?=\\s*\\)\\s*=>)"), true, false, null, js),
                pattern(compile("((?:\\b|\\s|^)(?!(?:as|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally|for|from|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|try|typeof|undefined|var|void|while|with|yield)(?![$\\w\\xA0-\\uFFFF]))(?:(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*\\s*)\\(\\s*|\\]\\s*\\(\\s*)(?!\\s)(?:[^()\\s]|\\s+(?![\\s)])|\\([^()]*\\))+(?=\\s*\\)\\s*\\{)"), true, false, null, js)
            ),
            token("constant", pattern(compile("\\b[A-Z](?:[A-Z_]|\\dx?)*\\b")))
        );

        List<Prism4j.Pattern> className = GrammarUtils.findToken(js, "class-name").patterns();
        className.clear();
        className.add(pattern(
            compile("(\\b(?:class|extends|implements|instanceof|interface|new)\\s+)[\\w.\\\\]+", Pattern.CASE_INSENSITIVE),
            true,
            false,
            null,
            grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
        ));
        className.add(pattern(compile("(^|[^$\\w\\xA0-\\uFFFF])(?!\\s)[_$A-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?=\\.(?:constructor|prototype))"), true));
        
        final Prism4j.Token interpolation = token("interpolation");
        
        GrammarUtils.insertBeforeToken(js, "string",
            token(
                "hashbang",
                pattern(
                    compile("^#!.*"),
                    false,
                    true,
                    "comment"
                )
            ),
            token(
                "template-string",
                pattern(
                    compile("`(?:\\\\[\\s\\S]|\\$\\{(?:[^{}]|\\{(?:[^{}]|\\{[^}]*\\})*\\})+\\}|(?!\\$\\{)[^\\\\`])*`"),
                    false,
                    true,
                    null,
                    grammar(
                        "inside",
                        token("template-punctuation", pattern(compile("^`|`$"), false, false, "string")),
                        interpolation,
                        token("string", pattern(compile("[\\s\\S]+")))
                    )
                )
            )
        );
        
        final Prism4j.Grammar insideInterpolation;
        {
            final List<Prism4j.Token> tokens = new ArrayList<>(js.tokens().size() + 1);
            tokens.add(token(
                "interpolation-punctuation",
                pattern(compile("^\\$\\{|\\}$"), false, false, "punctuation")
            ));
            tokens.addAll(js.tokens());
            insideInterpolation = grammar("inside", tokens);
        }
        
        interpolation.patterns().add(pattern(
            compile("((?:^|[^\\\\])(?:\\\\{2})*)\\$\\{(?:[^{}]|\\{(?:[^{}]|\\{[^}]*\\})*\\})+\\}"),
            true,
            false,
            null,
            insideInterpolation
        ));
        
        final Prism4j.Grammar markup = prism4j.grammar("markup");
        if (markup != null) {
            GrammarUtils.insertBeforeToken(markup, "tag",
                token(
                    "script", pattern(
                        compile("(<script[\\s\\S]*?>)[\\s\\S]*?(?=<\\/script>)", CASE_INSENSITIVE),
                        true,
                        true,
                        "language-javascript",
                        js
                    )
                )
            );

            //Prism.languages.markup.tag.addAttribute(
            //		/on(?:abort|blur|change|click|composition(?:end|start|update)|dblclick|error|focus(?:in|out)?|key(?:down|up)|load|mouse(?:down|enter|leave|move|out|over|up)|reset|resize|scroll|select|slotchange|submit|unload|wheel)/.source,
            //		'javascript'
            //	);
        }
        
        return js;
    }
    
}
