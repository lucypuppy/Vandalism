package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.util.SignHideUtils;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen {

    @Shadow
    @Final
    private String[] messages;

    protected MixinAbstractSignEditScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void injectInit(final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().hideSignTextFeature.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(SignHideUtils.hideText ? "Hidden" : "Shown"),
                    button -> {
                        SignHideUtils.hideText = !SignHideUtils.hideText;
                        button.setMessage(Text.literal(SignHideUtils.hideText ? "Hidden" : "Shown"));
                    }
            ).dimensions(width - 74, 10, 70, 20).build());
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void injectTick(final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().hideSignTextFeature.getValue()) {
            if (SignHideUtils.hideText) this.messages[3] = SignHideUtils.secret;
            else this.messages[3] = "";
        }
    }

    @Redirect(method = "setCurrentRowMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;setText(Lnet/minecraft/block/entity/SignText;Z)Z"))
    private boolean redirectSetCurrentRowMessage(final SignBlockEntity instance, final SignText text, final boolean front) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().hideSignTextFeature.getValue()) {
            return false;
        }
        return instance.setText(text, front);
    }

    @Redirect(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    private int injectRenderSignTextDrawText(final DrawContext instance, final TextRenderer textRenderer, final String text, final int x, final int y, final int color, final boolean shadow) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().hideSignTextFeature.getValue()) {
            if (SignHideUtils.hideText) {
                return instance.drawText(textRenderer, "*".repeat(text.length()), x, y, color, shadow);
            }
        }
        return instance.drawText(textRenderer, text, x, y, color, shadow);
    }

    @Redirect(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
    private int injectRenderSignTextGetWidth(final TextRenderer instance, final String text) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().hideSignTextFeature.getValue()) {
            if (SignHideUtils.hideText) {
                return instance.getWidth("*".repeat(text.length()));
            }
        }
        return instance.getWidth(text);
    }

}
