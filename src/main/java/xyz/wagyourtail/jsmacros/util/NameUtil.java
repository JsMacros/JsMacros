package xyz.wagyourtail.jsmacros.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public final class NameUtil {

    private static final String VALID_NAME = "[A-Za-z0-9_]+";
    private static final Pattern PATTERN_NAME = Pattern.compile(VALID_NAME);
    private static final Pattern PATTERN_WHISPER = Pattern.compile("(" + VALID_NAME + ") whispers to you.*");

    private static final int MAX_STRING_LENGTH = 60;
    private static final int DEFAULT_BUILDER_CAPACITY = 16;

    private NameUtil() {
    }

    /**
     * An approach to retrieve the name of a player from a chat message.
     * <p>Iterate until <. If found, get the next valid name
     * <p>Iterate until :. If found, get the last valid name
     * <p>Iterate until >>. If found get the last valid name
     * <p>Iterate until ->. If found get the last valid name (continue if empty)
     * <p>The potential last name is the last value between [] or before >. It's used when the
     * sentence just continues.
     * <p>There are some edge cases like "name > <WrongName>", because it could also be "Guild >
     * name :". Since the last one is much more common, I will stay with this approach.
     *
     * @return a list of the name at index 0 and any potential roles as subsequent elements or an
     * empty list if the name could not be identified.
     */
    public static List<String> guessNameAndRoles(String text) {
        String toAnalyze = text.substring(0, Math.min(MAX_STRING_LENGTH, text.length()));

        Matcher whisperMatcher = PATTERN_WHISPER.matcher(toAnalyze);
        if (whisperMatcher.find()) {
            return Collections.singletonList(whisperMatcher.group(1));
        }

        List<String> roles = new LinkedList<>();

        boolean foundName = false;
        String potentialLastName = "";
        int spaceCounter = 0;
        String lastString = "";
        StringBuilder nameBuilder = new StringBuilder(DEFAULT_BUILDER_CAPACITY);

        char current;
        char peek;

        for (int i = 0; i < toAnalyze.length(); i++) {
            current = toAnalyze.charAt(i);
            peek = i < toAnalyze.length() - 1 ? toAnalyze.charAt(i + 1) : Character.MIN_VALUE;

            if (Character.isLetter(current) || Character.isDigit(current) || current == '_') {
                nameBuilder.append(current);
            } else {
                if (nameBuilder.length() != 0) {
                    lastString = nameBuilder.toString();
                    nameBuilder = new StringBuilder(DEFAULT_BUILDER_CAPACITY);
                }

                if (foundName) {
                    roles.add(0, getNameOrDefault(lastString));
                    return roles;
                }

                // Optional check for closing bracket >
                if (current == ' ') {
                    spaceCounter++;
                } else if (current == '<') {
                    foundName = true;
                } else if (current == ':') {
                    roles.add(0, getNameOrDefault(lastString));
                    return roles;
                } else if (current == ']' || current == ')') {
                    roles.add(lastString);
                    potentialLastName = lastString;
                    spaceCounter = 0;
                } else if (current == '-' && peek == '>' && !lastString.isEmpty()) {
                    roles.add(0, getNameOrDefault(lastString));
                    return roles;
                } else if (current == '>') {
                    if (peek == '>') {
                        roles.add(0, getNameOrDefault(lastString));
                        return roles;
                    } else {
                        potentialLastName = lastString;
                        spaceCounter = 0;
                    }
                }

                if (spaceCounter > 2) {
                    roles.remove(potentialLastName);
                    roles.add(0, getNameOrDefault(potentialLastName));
                    return roles;
                }

            }
        }
        return Collections.emptyList();
    }

    private static String getNameOrDefault(CharSequence potentialName) {
        return getNameOrDefault(potentialName, "");
    }

    private static String getNameOrDefault(CharSequence potentialName, String defaultValue) {
        return PATTERN_NAME.matcher(potentialName).matches() ? potentialName.toString() : StringUtils.isBlank(defaultValue) ? "" : getNameOrDefault(defaultValue, "");
    }

}
