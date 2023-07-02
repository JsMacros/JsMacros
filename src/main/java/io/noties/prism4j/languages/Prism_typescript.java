package io.noties.prism4j.languages;

import io.noties.prism4j.GrammarUtils;
import io.noties.prism4j.Prism4j;
import io.noties.prism4j.annotations.Extend;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.noties.prism4j.Prism4j.*;
import static java.util.regex.Pattern.compile;

@Extend("javascript")
@SuppressWarnings("unused")
public class Prism_typescript {

    @NotNull
    public static Prism4j.Grammar create(@NotNull Prism4j prism4j) {
        Token class_name = token("class-name");

        Grammar ts = GrammarUtils.extend(GrammarUtils.require(prism4j, "javascript"), "typescript",
                class_name,
                token("builtin", pattern(compile("\\b(?:Array|Function|Promise|any|boolean|console|never|number|string|symbol|unknown)\\b")))
        );

        // doesn't work with TS because TS is too complex
        ts.tokens().remove(GrammarUtils.findToken(ts, "parameter"));
        ts.tokens().remove(GrammarUtils.findToken(ts, "literal-property"));

        Grammar typeInside = GrammarUtils.extend(ts, "inside-class");
        typeInside.tokens().remove(GrammarUtils.findToken(typeInside, "class-name"));

        List<Pattern> keywords = new ArrayList<>(3);
        keywords.add(pattern(compile("\\b(?:abstract|declare|is|keyof|readonly|require)\\b")));
        keywords.add(pattern(compile("\\b(?:asserts|infer|interface|module|namespace|type)\\b(?=\\s*(?:[{_$a-zA-Z\\xA0-\\uFFFF]|$))")));
        keywords.add(pattern(compile("\\btype\\b(?=\\s*(?:[\\{*]|$))")));

        GrammarUtils.findToken(ts, "keyword").patterns().addAll(keywords);

        GrammarUtils.insertBeforeToken(ts, "function",
                token("decorator",
                        pattern(compile("@[$\\w\\xA0-\\uFFFF]+"), false, false, null,
                                grammar("inside",
                                        token("at", pattern(compile("^@"), false, false, "operator")),
                                        token("function", pattern(compile("^[\\s\\S]+")))
                                )
                        )
                ),
                token("generic-function", pattern(compile("#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*\\s*<(?:[^<>]|<(?:[^<>]|<[^<>]*>)*>)*>(?=\\s*\\()"), false, true, null,
                        grammar("inside",
                                token("function", pattern(compile("^#?(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*"))),
                                token("generic", pattern(compile("<[\\s\\S]+"), false, false, "class-name", typeInside))
                        )
                ))
        );

        class_name.patterns().add(pattern(compile("(\\b(?:class|extends|implements|instanceof|interface|new|type)\\s+)(?!keyof\\b)(?!\\s)[_$a-zA-Z\\xA0-\\uFFFF](?:(?!\\s)[$\\w\\xA0-\\uFFFF])*(?:\\s*<(?:[^<>]|<(?:[^<>]|<[^<>]*>)*>)*>)?"), true, true, null, typeInside));

        return ts;
    }

}
