package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @WrapWithCondition(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private boolean vandalism$dontClearChatHistory(ChatHud instance, boolean clearHistory) {
        return !Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.dontClearChatHistory.getValue();
    }

}
