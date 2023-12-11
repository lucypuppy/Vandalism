package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.StringValue;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.text.Text;

public class NetworkingSettings extends ValueGroup {

    public NetworkingSettings(final ClientSettings parent) {
        super(parent, "Networking", "Networking related configs.");
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
                    if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().forceDisconnectKeybind.getValue()) {
                        if (nativeEvent.getKeyCode() == 3663) {
                            if (NetworkingSettings.this.mc.getNetworkHandler() != null) {
                                NetworkingSettings.this.mc.getNetworkHandler().getConnection().disconnect(Text.literal("Manual force disconnect."));
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
            this,
            "Change Brand",
            "Changes the Brand when connecting to a Server.",
            true
    );

    public final Value<String> brand = new StringValue(
            this,
            "Brand",
            "The Brand that will used.",
            ClientBrandRetriever.VANILLA
    ).visibleCondition(this.changeBrand::getValue);

    public final Value<Boolean> forceDisconnectKeybind = new BooleanValue(
            this,
            "Force Disconnect Keybind",
            "Enables that you can disconnect with the key END even if the Game is frozen.",
            true
    );

    public final Value<Boolean> spoofIsCreativeLevelTwoOp = new BooleanValue(
            this,
            "Spoof Is Creative Level Two Op",
            "Makes the Game think you are a in Creative Mode and you have Level Two Op.",
            true
    );

    public final Value<Boolean> antiTelemetry = new BooleanValue(
            this,
            "Anti Telemetry",
            "Blocks the Telemetry of the Game.",
            true
    );

    public final Value<Boolean> antiServerBlockList = new BooleanValue(
            this,
            "Anti Server Block List",
            "Blocks the Server Block List from the Game.",
            true
    );

    public final Value<Boolean> antiTimeoutKick = new BooleanValue(
            this,
            "Anti Timeout Kick",
            "Prevents the Game from disconnecting if the server doesn't response for some time.",
            true
    );

    public final Value<Boolean> eliminateHitDelay = new BooleanValue(
            this,
            "Eliminate Hit Delay",
            "Eliminates the Hit Delay of the Game.",
            false
    );
    
}
