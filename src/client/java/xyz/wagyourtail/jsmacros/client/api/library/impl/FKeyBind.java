package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Functions for getting and modifying key pressed states.
 * <p>
 * An instance of this class is passed to scripts as the {@code KeyBind} variable.
 *
 * @author Wagyourtail
 */
@Library("KeyBind")
@SuppressWarnings("unused")
public class FKeyBind extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FKeyBind(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * Dont use this one... get the raw minecraft keycode class.
     *
     * @param keyName
     * @return the raw minecraft keycode class
     */
    @DocletReplaceParams("keyName: Key")
    public Key getKeyCode(String keyName) {
        try {
            return InputUtil.fromTranslationKey(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEY;
        }
    }

    /**
     * @return A {@link Map Map} of all the minecraft keybinds.
     * @since 1.2.2
     */
    @DocletReplaceReturn("JavaMap<Bind, Key>")
    public Map<String, String> getKeyBindings() {
        Map<String, String> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.options.allKeys)) {
            keys.put(key.getTranslationKey(), key.getBoundKeyTranslationKey());
        }
        return keys;
    }

    /**
     * Sets a minecraft keybind to the specified key.
     *
     * @param bind
     * @param key
     * @since 1.2.2
     */
    @DocletReplaceParams("bind: Bind, key: Key | null")
    public void setKeyBind(String bind, @Nullable String key) {
        for (KeyBinding keybind : mc.options.allKeys) {
            if (keybind.getTranslationKey().equals(bind)) {
                keybind.setBoundKey(key != null ? InputUtil.fromTranslationKey(key) : InputUtil.UNKNOWN_KEY);
                KeyBinding.updateKeysByCode();
                return;
            }
        }
    }

    /**
     * Set a key-state for a key.
     *
     * @param keyName
     * @param keyState
     */
    @DocletReplaceParams("keyName: Key, keyState: boolean")
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }

    /**
     * Calls {@link #key(String, boolean)} with keyState set to true.
     *
     * @param keyName the name of the key to press
     * @since 1.8.4
     */
    @DocletReplaceParams("keyName: Key")
    public void pressKey(String keyName) {
        key(keyName, true);
    }

    /**
     * Calls {@link #key(String, boolean)} with keyState set to false.
     *
     * @param keyName the name of the key to release
     * @since 1.8.4
     */
    @DocletReplaceParams("keyName: Key")
    public void releaseKey(String keyName) {
        key(keyName, false);
    }

    /**
     * Don't use this one... set the key-state using the raw minecraft keycode class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(Key keyBind, boolean keyState) {
        if (MinecraftClient.getInstance().currentScreen != null) return;
        KeyBinding.setKeyPressed(keyBind, keyState);
        if (keyState) {
            KeyBinding.onKeyPressed(keyBind);
        }

        // add to pressed keys list
        if (keyState) {
            KeyTracker.press(keyBind);
        } else {
            KeyTracker.unpress(keyBind);
        }
    }

    /**
     * Set a key-state using the name of the keybind rather than the name of the key.
     * <p>
     * This is probably the one you should use.
     *
     * @param keyBind
     * @param keyState
     * @since 1.2.2
     */
    @DocletReplaceParams("keyBind: Bind, keyState: boolean")
    public void keyBind(String keyBind, boolean keyState) {
        if (MinecraftClient.getInstance().currentScreen != null) return;
        for (KeyBinding key : mc.options.allKeys) {
            if (key.getTranslationKey().equals(keyBind)) {
                key.setPressed(keyState);
                if (keyState) {
                    KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()));
                }

                // add to pressed keys list
                if (keyState) {
                    KeyTracker.press(key);
                } else {
                    KeyTracker.unpress(key);
                }
                return;
            }
        }
    }

    /**
     * Calls {@link #keyBind(String, boolean)} with keyState set to true.
     *
     * @param keyBind the name of the keybinding to press
     * @since 1.8.4
     */
    @DocletReplaceParams("keyBind: Bind")
    public void pressKeyBind(String keyBind) {
        keyBind(keyBind, true);
    }

    /**
     * Calls {@link #keyBind(String, boolean)} with keyState set to false.
     *
     * @param keyBind the name of the keybinding to release
     * @since 1.8.4
     */
    @DocletReplaceParams("keyBind: Bind")
    public void releaseKeyBind(String keyBind) {
        keyBind(keyBind, false);
    }

    /**
     * Don't use this one... set the key-state using the raw minecraft keybind class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(KeyBinding keyBind, boolean keyState) {
        if (MinecraftClient.getInstance().currentScreen != null) return;
        keyBind.setPressed(keyState);
        if (keyState) {
            KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(keyBind.getBoundKeyTranslationKey()));
        }

        // add to pressed keys list
        if (keyState) {
            KeyTracker.press(keyBind);
        } else {
            KeyTracker.unpress(keyBind);
        }
    }

    /**
     * @return a set of currently pressed keys.
     * @since 1.2.6 (turned into set instead of list in 1.6.5)
     */

    @DocletReplaceReturn("JavaSet<Key>")
    public Set<String> getPressedKeys() {
        return KeyTracker.getPressedKeys();
    }

    public static class KeyTracker {
        private static final Set<String> pressedKeys = new HashSet<>();

        public synchronized static void press(Key key) {
            String translationKey = key.getTranslationKey();
            if (translationKey != null) {
                pressedKeys.add(translationKey);
            }
        }

        public synchronized static void press(KeyBinding bind) {
            String translationKey = bind.getBoundKeyTranslationKey();
            if (translationKey != null) {
                pressedKeys.add(translationKey);
            }
        }

        public synchronized static void unpress(Key key) {
            String translationKey = key.getTranslationKey();
            if (translationKey != null) {
                pressedKeys.remove(translationKey);
            }
        }

        public synchronized static void unpress(KeyBinding bind) {
            String translationKey = bind.getBoundKeyTranslationKey();
            if (translationKey != null) {
                pressedKeys.remove(translationKey);
            }
        }

        public static synchronized Set<String> getPressedKeys() {
            return ImmutableSet.copyOf(pressedKeys);
        }

    }

}
