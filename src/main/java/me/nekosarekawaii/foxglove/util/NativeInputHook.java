package me.nekosarekawaii.foxglove.util;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import me.nekosarekawaii.foxglove.Foxglove;

public class NativeInputHook {

    public NativeInputHook() {
        try {
            GlobalScreen.registerNativeHook();
            Foxglove.getInstance().getLogger().info("Successfully registered native input hook.");
        } catch (final NativeHookException e) {
            Foxglove.getInstance().getLogger().error("Failed to register native input hook.", e);
        }
    }

    public void registerKeyListener(final NativeKeyListener listener) {
        GlobalScreen.addNativeKeyListener(listener);
    }

    public void unregisterKeyListener(final NativeKeyListener listener) {
        GlobalScreen.removeNativeKeyListener(listener);
    }

}
