package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins;

import net.minecraft.client.option.KeyBinding;
import net.wurstclient.WurstClient;
import net.wurstclient.analytics.WurstAnalytics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;

@Mixin(value = WurstClient.class, remap = false)
public abstract class MixinWurstClient {

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/keybinding/v1/KeyBindingHelper;registerKeyBinding(Lnet/minecraft/client/option/KeyBinding;)Lnet/minecraft/client/option/KeyBinding;"))
    private KeyBinding cancelWurstZoomKeybinding(final KeyBinding keyBinding) {
        // Prevent the FAPI from loading the keybinding as we are loading the WurstClient to late for the FAPI
        // Since we don't need this keybinding anyway, we can just get rid of it
        return keyBinding;
    }

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/wurstclient/analytics/WurstAnalytics;trackPageView(Ljava/lang/String;Ljava/lang/String;)V", remap = false))
    private void ignoreTrackPageView(final WurstAnalytics instance, final String url, final String title) {
        // Prevents the client from crashing...
    }

}
