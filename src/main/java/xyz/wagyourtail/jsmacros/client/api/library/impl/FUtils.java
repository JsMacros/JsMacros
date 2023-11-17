package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.util.NameUtil;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Library("Utils")
@SuppressWarnings("unused")
public class FUtils extends BaseLibrary {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @param url the url to open
     * @since 1.8.4
     */
    public void openUrl(String url) throws MalformedURLException {
        Util.getOperatingSystem().open(new URL(url));
    }

    /**
     * @param path the path top open, relative the config folder
     * @since 1.8.4
     */
    public void openFile(String path) {
        Util.getOperatingSystem().open(JsMacros.core.config.configFolder.toPath().resolve(path).toFile());
    }

    /**
     * Copies the text to the clipboard.
     *
     * @param text the text to copy
     * @since 1.8.4
     */
    public void copyToClipboard(String text) {
        SelectionManager.setClipboard(mc, text);
    }

    /**
     * @return the text from the clipboard.
     * @since 1.8.4
     */
    public String getClipboard() {
        return SelectionManager.getClipboard(mc);
    }

    /**
     * Tries to guess the name of the sender of a given message. This is not guaranteed to work and
     * for specific servers it may be better to use regex instead.
     *
     * @param text the text to check
     * @return the name of the sender or {@code null} if it couldn't be guessed.
     * @since 1.8.4
     */
    public String guessName(TextHelper text) {
        return guessName(text.getStringStripFormatting());
    }

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
    public List<String> guessNameAndRoles(TextHelper text) {
        return guessNameAndRoles(text.getStringStripFormatting());
    }

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
     * Hashes the given string with sha-256 the selected algorithm.
     *
     * @param message   the message to hash
     * @param algorithm sha1 | sha256 | sha384 | sha512 | md2 | md5
     * @return the hashed message.
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

}
