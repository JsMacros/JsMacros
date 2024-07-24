package xyz.wagyourtail.jsmacros.client.api.library;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FUtils;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.net.MalformedURLException;
import java.net.URI;

@Library("Utils")
public class FClientUtils extends FUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FClientUtils(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * @param url the url to open
     * @since 1.8.4
     */
    public void openUrl(String url) throws MalformedURLException {
        Util.getOperatingSystem().open(URI.create(url));
    }

    /**
     * @param path the path top open, relative the config folder
     * @since 1.8.4
     */
    public void openFile(String path) {
        Util.getOperatingSystem().open(runner.config.configFolder.toPath().resolve(path).toFile());
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



}
