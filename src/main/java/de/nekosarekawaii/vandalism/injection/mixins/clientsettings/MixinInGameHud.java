package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @WrapWithCondition(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private boolean vandalism$dontClearChatHistory(ChatHud instance, boolean clearHistory) {
        return !Vandalism.getInstance().getClientSettings().getChatSettings().dontClearChatHistory.getValue();
    }

}
