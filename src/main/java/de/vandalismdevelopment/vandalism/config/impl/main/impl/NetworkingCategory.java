package de.vandalismdevelopment.vandalism.config.impl.main.impl;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.main.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.StringValue;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.text.Text;

public class NetworkingCategory extends ValueCategory {

    public NetworkingCategory(final MainConfig parent) {
        super("Networking", "Networking related configs.", parent);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
                    if (Vandalism.getInstance().getConfigManager().getMainConfig().networkingCategory.forceDisconnectKeybind.getValue()) {
                        if (nativeEvent.getKeyCode() == 3663) {
                            if (NetworkingCategory.this.networkHandler() != null) {
                                NetworkingCategory.this.networkHandler().getConnection().disconnect(Text.literal("Manual force disconnect."));
                            }
                        }
                    }
                }

            });
        } catch (final NativeHookException e) {
            Vandalism.getInstance().getLogger().error("Failed to register native input hook disconnect listener.", e);
        }
    }

    public final Value<Boolean> changeBrand = new BooleanValue(
            "Change Brand",
            "Changes the Brand when connecting to a Server.",
            this,
            true
    );

    public final Value<String> brand = new StringValue(
            "Brand",
            "The Brand that will used.",
            this,
            ClientBrandRetriever.VANILLA
    ).visibleConsumer(this.changeBrand::getValue);

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
            "Prevents the Game from disconnecting if the server doesn't response for some time.",
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
