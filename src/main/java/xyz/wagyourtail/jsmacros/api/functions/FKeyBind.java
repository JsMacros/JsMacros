package xyz.wagyourtail.jsmacros.api.functions;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import xyz.wagyourtail.jsmacros.extensionbase.Functions;

import java.util.*;

/**
 * 
 * Functions for getting and modifying key pressed states.
 * 
 * An instance of this class is passed to scripts as the {@code keybind} variable.
 * 
 * @author Wagyourtail
 *
 */
public class FKeyBind extends Functions {
    /**
     * Don't modify
     */
    public static final Set<String> pressedKeys = new HashSet<>();
    
    
    public FKeyBind(String libName) {
        super(libName);
    }
    
    public FKeyBind(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    /**
     * Dont use this one... get the raw minecraft keycode class.
     * 
     * @param keyName
     * @return the raw minecraft keycode class
     */
    public Key getKeyCode(String keyName) {
        try {
            return InputUtil.fromTranslationKey(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEY;
        }
    }
    
    /**
     * @since 1.2.2
     * 
     * @return A {@link java.util.Map Map} of all the minecraft keybinds.
     */
    public Map<String, String> getKeyBindings() {
        Map<String, String> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.options.keysAll)) {
            keys.put(key.getTranslationKey(), key.getBoundKeyTranslationKey());
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
    public void setKeyBind(String bind, String key) {
        for (KeyBinding keybind : mc.options.keysAll) {
            if (keybind.getBoundKeyTranslationKey().equals(bind)) {
                keybind.setBoundKey(InputUtil.fromTranslationKey(key));
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
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    /**
     * Don't use this one... set the key-state using the raw minecraft keycode class.
     * 
     * @param keyBind
     * @param keyState
     */
    public void key(Key keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind);
        KeyBinding.setKeyPressed(keyBind, keyState);
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
            if (key.getBoundKeyTranslationKey().equals(keyBind)) {
                if (keyState) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()));
                key.setPressed(keyState);
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
    public void key(KeyBinding keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(keyBind.getBoundKeyTranslationKey()));
        keyBind.setPressed(keyState);
    }
    
    /**
     * @since 1.2.6
     * 
     * @return a list of currently pressed keys.
     */
    public List<String> getPressedKeys() {
        synchronized (pressedKeys) {
            return new ArrayList<>(pressedKeys);
        }
    }
}
