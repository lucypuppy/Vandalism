package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.minecraft.client.option.KeyBinding;
import net.wurstclient.WurstClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WurstClient.class, remap = false)
public abstract class MixinWurstClient {

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/keybinding/v1/KeyBindingHelper;registerKeyBinding(Lnet/minecraft/client/option/KeyBinding;)Lnet/minecraft/client/option/KeyBinding;"))
    private KeyBinding cancelWurstZoomKeybinding(final KeyBinding keyBinding) {
        return keyBinding;
    }

}
