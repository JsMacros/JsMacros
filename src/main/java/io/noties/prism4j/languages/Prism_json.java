package io.noties.prism4j.languages;

import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Aliases;
import org.jetbrains.annotations.NotNull;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

/**
 * This class is from <a target="_blank" href="https://github.com/noties/Prism4j">Prism4j</a>
 * under the <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0 license</a>
 */
@SuppressWarnings("unused")
@Aliases("jsonp")
public class Prism_json {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        return grammar(
                "json",
                token("property", pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?=\\s*:)", CASE_INSENSITIVE))),
                token("string", pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?!\\s*:)"), false, true)),
                token("number", pattern(compile("\\b0x[\\dA-Fa-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:[Ee][+-]?\\d+)?"))),
                token("punctuation", pattern(compile("[{}\\[\\]);,]"))),
                // not sure about this one...
                token("operator", pattern(compile(":"))),
                token("boolean", pattern(compile("\\b(?:false|true)\\b", CASE_INSENSITIVE))),
                token("null", pattern(compile("\\bnull\\b", CASE_INSENSITIVE)))
        );
    }

}
