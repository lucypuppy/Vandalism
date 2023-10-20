package de.vandalismdevelopment.vandalism.config.impl.main;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import net.minecraft.text.Text;

public class AccessibilityCategory extends ValueCategory {

    public AccessibilityCategory(final MainConfig parent) {
        super("Accessibility", "Accessibility related settings.", parent);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
                    if (Vandalism.getInstance().getConfigManager().getMainConfig().accessibilityCategory.forceDisconnectKeybind.getValue()) {
                        if (nativeEvent.getKeyCode() == 3663) { // END Key
                            if (networkHandler() != null) {
                                networkHandler().getConnection().disconnect(Text.literal("Manual force disconnect."));
                            }
                        }
                    }
                }

            });
            Vandalism.getInstance().getLogger().info("Successfully registered native input hook disconnect listener.");
        } catch (final NativeHookException e) {
            Vandalism.getInstance().getLogger().error("Failed to register native input hook disconnect listener.", e);
        }
    }

    public final Value<Boolean> forceDisconnectKeybind = new BooleanValue(
            "Force Disconnect Keybind",
            "Enables that you can disconnect with the key END even if the Game is frozen.",
            this,
            true
    );

    public final Value<Boolean> spoofIsCreativeLevelTwoOp = new BooleanValue(
            "Spoof Is Creative Level Two Op",
            "Makes the Game think you are a in Creative Mode and you have Level Two Op.",
            this,
            true
    );

    public final Value<Boolean> antiTelemetry = new BooleanValue(
            "Anti Telemetry",
            "Blocks the Telemetry of the Game.",
            this,
            true
    );

    public final Value<Boolean> antiServerBlockList = new BooleanValue(
            "Anti Server Block List",
            "Blocks the Server Block List from the Game.",
            this,
            true
    );

    public final Value<Boolean> antiTimeoutKick = new BooleanValue(
            "Anti Timeout Kick",
            "Prevents the Game from disconnecting after 30 seconds if the server doesn't response.",
            this,
            true
    );

    public final Value<Boolean> eliminateHitDelay = new BooleanValue(
            "Eliminate Hit Delay",
            "Eliminates the Hit Delay of the Game.",
            this,
            false
    );
    
}
