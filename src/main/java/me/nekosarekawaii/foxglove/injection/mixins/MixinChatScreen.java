package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatScreen.class, priority = 2000)
public abstract class MixinChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void injectInit(final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().customChatLength.getValue()) {
            this.chatField.setMaxLength(Foxglove.getInstance().getConfigManager().getMainConfig().maxChatLength.getValue());
        }
    }

}
