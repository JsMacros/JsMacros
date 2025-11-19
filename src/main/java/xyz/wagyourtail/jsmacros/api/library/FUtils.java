package xyz.wagyourtail.jsmacros.api.library;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.util.NameUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Library("Utils")
@SuppressWarnings("unused")
public class FUtils extends BaseLibrary {

    public FUtils(Core<?, ?> runner) {
        super(runner);
    }
//
//    /**
//     * Tries to guess the name of the sender of a given message. This is not guaranteed to work and
//     * for specific servers it may be better to use regex instead.
//     *
//     * @param text the text to check
//     * @return the name of the sender or {@code null} if it couldn't be guessed.
//     * @since 1.8.4
//     */
//    public String guessName(TextHelper text) {
//        return guessName(text.getStringStripFormatting());
//    }

    /**
     * Tries to guess the name of the sender of a given message. This is not guaranteed to work and
     * for specific servers it may be better to use regex instead.
     *
     * @param text the text to check
     * @return the name of the sender or {@code null} if it couldn't be guessed.
     * @since 1.8.4
     */
    public String guessName(String text) {
        List<String> names = guessNameAndRoles(text);
        return names.isEmpty() ? null : names.get(0);
    }
//
//    /**
//     * Tries to guess the name, as well as the titles and roles of the sender of the given message.
//     * This is not guaranteed to work and for specific servers it may be better to use regex
//     * instead.
//     *
//     * @param text the text to check
//     * @return a list of names, titles and roles of the sender or an empty list if it couldn't be
//     * guessed.
//     * @since 1.8.4
//     */
//    public List<String> guessNameAndRoles(TextHelper text) {
//        return guessNameAndRoles(text.getStringStripFormatting());
//    }

    /**
     * Tries to guess the name, as well as the titles and roles of the sender of the given message.
     * This is not guaranteed to work and for specific servers it may be better to use regex
     * instead.
     *
     * @param text the text to check
     * @return a list of names, titles and roles of the sender or an empty list if it couldn't be
     * guessed.
     * @since 1.8.4
     */
    public List<String> guessNameAndRoles(String text) {
        return NameUtil.guessNameAndRoles(text);
    }

    /**
     * Hashes the given string with sha-256.
     *
     * @param message the message to hash
     * @return the hashed message.
     * @since 1.8.4
     */
    public String hashString(@Nullable String message) {
        return DigestUtils.sha256Hex(message);
    }

    /**
     * Hashes the given string with the selected algorithm.
     *
     * @param message the message to hash
     * @param algorithm sha1 | sha256 | sha384 | sha512 | md2 | md5
     * @return the hashed message (Hex)
     * @since 1.8.4
     */
    @Nullable
    public String hashString(@Nullable String message, String algorithm) {
        switch (algorithm) {
            case "sha256":
                return DigestUtils.sha256Hex(message);
            case "sha512":
                return DigestUtils.sha512Hex(message);
            case "sha1":
                return DigestUtils.sha1Hex(message);
            case "sha384":
                return DigestUtils.sha384Hex(message);
            case "md2":
                return DigestUtils.md2Hex(message);
            case "md5":
                return DigestUtils.md5Hex(message);
            default:
                return message;
        }
    }

    /**
     * Hashes the given string with the selected algorithm.
     *
     * @param message the message to hash
     * @param algorithm sha1 | sha256 | sha384 | sha512 | md2 | md5
     * @param base64 encode the result in base64
     * @return the hashed message (Hex or Base64)
     * @since 1.9.1
     */
    @Nullable
    public String hashString(@Nullable String message, String algorithm, Boolean base64) {
        switch (algorithm) {
            case "sha256":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.sha256(message)));
                return DigestUtils.sha256Hex(message);
            case "sha512":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.sha512(message)));
                return DigestUtils.sha512Hex(message);
            case "sha1":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.sha1(message)));
                return DigestUtils.sha1Hex(message);
            case "sha384":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.sha384(message)));
                return DigestUtils.sha384Hex(message);
            case "md2":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.md2(message)));
                return DigestUtils.md2Hex(message);
            case "md5":
                if (base64)
                    return new String(Base64.encodeBase64(DigestUtils.md5(message)));
                return DigestUtils.md5Hex(message);
            default:
                return message;
        }
    }

    /**
     * Encodes the given string with Base64.
     *
     * @param message the message to encode
     * @return the encoded message.
     * @since 1.8.4
     */
    public String encode(String message) {
        return new String(Base64.encodeBase64(message.getBytes()));
    }

    /**
     * Decodes the given string with Base64.
     *
     * @param message the message to decode
     * @return the decoded message.
     * @since 1.8.4
     */
    public String decode(String message) {
        return new String(Base64.decodeBase64(message.getBytes()));
    }

    /**
     * Checks that the specified object reference is not {@code null}.
     *
     * @param obj the object reference to check for nullity
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @since 1.9.1
     */
    @DocletReplaceReturn("T & {}")
    public <T> T requireNonNull(T obj) {
        return Objects.requireNonNull(obj);
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     *
     * @param obj     the object reference to check for nullity
     * @param message detail message to be used in the event that a {@code
     *                NullPointerException} is thrown
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @since 1.9.1
     */
    @DocletReplaceReturn("T & {}")
    public <T> T requireNonNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }

}
