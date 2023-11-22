package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessibilityOnboardingScreen.class)
public abstract class MixinAccessibilityOnboardingScreen {

    @Inject(method = "tickNarratorPrompt", at = @At(value = "INVOKE", target = "Lcom/mojang/text2speech/Narrator;say(Ljava/lang/String;Z)V", remap = false), cancellable = true)
    private void injectTickNarratorPrompt(final CallbackInfo ci) {
        ci.cancel();
    }

}
