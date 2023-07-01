package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.compile;

/**
 * This class is from <a target="_blank" href="https://github.com/noties/Prism4j">Prism4j</a>
 * under the <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a>
 * updated to match upstream
 */
@SuppressWarnings("unused")
public abstract class Prism_clike {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        return grammar(
                "clike",
                token(
                        "comment",
                        pattern(compile("(^|[^\\\\])\\/\\*[\\s\\S]*?(?:\\*\\/|$)"), true, true),
                        pattern(compile("(^|[^\\\\:])\\/\\/.*"), true, true)
                ),
                token(
                        "string",
                        pattern(compile("([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
                ),
                token(
                        "class-name",
                        pattern(
                                compile("(\\b(?:class|extends|implements|instanceof|interface|new|trait)\\s+|\\bcatch\\s+\\()[\\w.\\\\]+", Pattern.CASE_INSENSITIVE),
                                true,
                                false,
                                null,
                                grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
                        )
                ),
                token(
                        "keyword",
                        pattern(compile("\\b(?:break|catch|continue|do|else|finally|for|function|if|in|instanceof|new|null|return|throw|try|while)\\b"))
                ),
                token("boolean", pattern(compile("\\b(?:false|true)\\b"))),
                token("function", pattern(compile("\\b[a-zA-Z_]\\w*(?=\\s*\\()" /* this is more accurate */, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE))),
                token(
                        "number",
                        pattern(compile("\\b0x[\\da-f]+\\b|(?:\\b\\d+(?:\\.\\d*)?|\\B\\.\\d+)(?:e[+-]?\\d+)?", Pattern.CASE_INSENSITIVE))
                ),
                token("operator", pattern(compile("--?|\\+\\+?|!=?=?|<=?|>=?|==?=?|&&?|\\|\\|?|\\?|\\*|\\/|~|\\^|%"))),
                token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
        );
    }

    private Prism_clike() {
    }

}
