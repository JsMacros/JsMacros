package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.*;

/**
 *
 * Functions for getting and modifying key pressed states.
 * 
 * An instance of this class is passed to scripts as the {@code KeyBind} variable.
 * 
 * @author Wagyourtail
 */
 @Library("KeyBind")
 @SuppressWarnings("unused")
 public class FKeyBind extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    /**
     * @since 1.2.2
     *
     * @return A {@link java.util.Map Map} of all the minecraft keybinds.
     */
    public Map<String, Integer> getKeyBindings() {
        Map<String, Integer> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.options.keysAll)) {
            keys.put(key.getTranslationKey(), key.getCode());
        }
        return keys;
    }

    /**
     * Sets a minecraft keybind to the specified key.
     *
     * @since 1.2.2
     *
     * @param bind
     * @param key
     */
    public void setKeyBind(String bind, int key) {
        for (KeyBinding keybind : mc.options.keysAll) {
            if (keybind.getTranslationKey().equals(bind)) {
                keybind.setCode(key);
                KeyBinding.updateKeysByCode();
                return;
            }
        }
    }

    /**
     * Set a key-state for a key.
     *
     * @param keyBind
     * @param keyState
     */
    public void key(int keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind);
        KeyBinding.setKeyPressed(keyBind, keyState);

        // add to pressed keys list
        if (keyState) KeyTracker.press(keyBind);
        else KeyTracker.unpress(keyBind);
    }
    
    /**
     * Set a key-state using the name of the keybind rather than the name of the key.
     *
     * This is probably the one you should use.
     *
     * @since 1.2.2
     *
     * @param keyBind
     * @param keyState
     */
    public void keyBind(String keyBind, boolean keyState) {
        for (KeyBinding key : mc.options.keysAll) {
            if (key.getTranslationKey().equals(keyBind)) {
                if (keyState) KeyBinding.onKeyPressed(key.getCode());
                KeyBinding.setKeyPressed(key.getCode(), keyState);

                // add to pressed keys list
                if (keyState) KeyTracker.press(key);
                else KeyTracker.unpress(key);
                return;
            }
        }
    }

    /**
     * Don't use this one... set the key-state using the raw minecraft keybind class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(KeyBinding keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind.getCode());
        KeyBinding.setKeyPressed(keyBind.getCode(), keyState);

        // add to pressed keys list
        if (keyState) KeyTracker.press(keyBind);
        else KeyTracker.unpress(keyBind);
    }

    /**
     * @since 1.2.6 (turned into set instead of list in 1.6.5)
     * 
     * @return a set of currently pressed keys.
     */
    public Set<Integer> getPressedKeys() {
        return KeyTracker.getPressedKeys();
    }

    public static class KeyTracker {
        private static final Set<Integer> pressedKeys = new HashSet<>();

        public synchronized static void press(int key) {
             pressedKeys.add(key);
        }

        public synchronized static void press(KeyBinding bind) {
            pressedKeys.add(bind.getCode());
        }

        public synchronized static void unpress(int key) {
             pressedKeys.remove(key);
        }

        public synchronized static void unpress(KeyBinding bind) {
            pressedKeys.remove(bind.getCode());
        }

        public static synchronized Set<Integer> getPressedKeys() {
            return ImmutableSet.copyOf(pressedKeys);
        }
    }
}
