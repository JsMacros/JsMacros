package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.*;

/**
 * This class is from <a target="_blank" href="https://github.com/noties/Prism4j">Prism4j</a>
 * under the <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a>
 * updated for format strings and other stuff from upstream
 */
@SuppressWarnings("unused")
public class Prism_python {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {

        final Prism4j.Token interpolation = token("interpolation");

        final Prism4j.Grammar py = grammar("python",
                token("comment", pattern(
                        compile("(^|[^\\\\])#.*"),
                        true,
                        true
                )),
                token("string-interpolation", pattern(compile("(?:f|fr|rf)(?:(\"\"\"|''')[\\s\\S]*?\\1|(\"|')(?:\\\\.|(?!\\2)[^\\\\\\r\\n])*\\2)", CASE_INSENSITIVE), false, true, null,
                        grammar("inside",
                                interpolation,
                                token("string", pattern(compile("[\\s\\S]+")))
                        )
                )),
                token("triple-quoted-string", pattern(
                        compile("(?:[rub]|br|rb)?(\"\"\"|''')[\\s\\S]*?\\1", CASE_INSENSITIVE),
                        false,
                        true,
                        "string"
                )),
                token("string", pattern(
                        compile("(?:[rub]|br|rb)?(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1", CASE_INSENSITIVE),
                        false,
                        true
                )),
                token("function", pattern(
                        compile("\\b[a-zA-Z_]\\w*(?=\\s*\\()", CASE_INSENSITIVE), /* changed from `/((?:^|\s)def[ \t]+)[a-zA-Z_]\w*(?=\s*\()/, true` to more closely match how JS does it... don't do multiline tho */
                        false
                )),
                token("class-name", pattern(
                        compile("(\\bclass\\s+)\\w+", CASE_INSENSITIVE),
                        true
                )),
                token("decorator", pattern(compile("(^[\\t ]*)@\\w+(?:\\.\\w+)*", MULTILINE), true, false, "annotation",
                        grammar("inside",
                                token("punctuation", pattern(compile("\\.")))
                        )
                )),
                token("keyword", pattern(compile("\\b(?:_(?=\\s*:)|and|as|assert|async|await|break|case|class|continue|def|del|elif|else|except|exec|finally|for|from|global|if|import|in|is|lambda|match|nonlocal|not|or|pass|print|raise|return|try|while|with|yield)\\b"))),
                token("builtin", pattern(compile("\\b(?:__import__|abs|all|any|apply|ascii|basestring|bin|bool|buffer|bytearray|bytes|callable|chr|classmethod|cmp|coerce|compile|complex|delattr|dict|dir|divmod|enumerate|eval|execfile|file|filter|float|format|frozenset|getattr|globals|hasattr|hash|help|hex|id|input|int|intern|isinstance|issubclass|iter|len|list|locals|long|map|max|memoryview|min|next|object|oct|open|ord|pow|property|range|raw_input|reduce|reload|repr|reversed|round|set|setattr|slice|sorted|staticmethod|str|sum|super|tuple|type|unichr|unicode|vars|xrange|zip)\\b"))),
                token("boolean", pattern(compile("\\b(?:False|None|True)\\b"))),
                token("number", pattern(
                        compile("\\b0(?:b(?:_?[01])+|o(?:_?[0-7])+|x(?:_?[a-f0-9])+)\\b|(?:\\b\\d+(?:_\\d+)*(?:\\.(?:\\d+(?:_\\d+)*)?)?|\\B\\.\\d+(?:_\\d+)*)(?:e[+-]?\\d+(?:_\\d+)*)?j?(?!\\w)", CASE_INSENSITIVE)
                )),
                token("operator", pattern(compile("[-+%=]=?|!=|:=|\\*\\*?=?|\\/\\/?=?|<[<=>]?|>[=>]?|[&|^~]"))),
                token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        );

        final Prism4j.Grammar insideInterpolation;
        {
            final List<Prism4j.Token> tokens = new ArrayList<>(py.tokens().size() + 3);
            tokens.add(token(
                    "format-spec",
                    pattern(compile("(:)[^:(){}]+(?=\\}$)"), true)
            ));
            tokens.add(token(
                    "conversion-option",
                    pattern(compile("![sra](?=[:}]$)"), false, false, "punctuation")
            ));
            tokens.add(token(
                    "interpolation-punctuation",
                    pattern(compile("^\\{|}$"), false, false, "punctuation")
            ));
            tokens.addAll(py.tokens());
            insideInterpolation = grammar("inside", tokens);
        }

        interpolation.patterns().add(pattern(
                compile("((?:^|[^{])(?:\\{\\{)*)\\{(?!\\{)(?:[^{}]|\\{(?!\\{)(?:[^{}]|\\{(?!\\{)(?:[^{}])+\\})+\\})+\\}"),
                true,
                false,
                null,
                insideInterpolation
        ));

        return py;
    }

}
