package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Redirect(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private void vandalism$dontClearChatHistory(final ChatHud instance, final boolean clearHistory) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.dontClearChatHistory.getValue())
            return;
        instance.clear(clearHistory);
    }

}
