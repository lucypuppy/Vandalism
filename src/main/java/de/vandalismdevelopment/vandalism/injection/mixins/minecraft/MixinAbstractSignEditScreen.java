package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.main.VisualCategory;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
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

    @Inject(method = "tick", at = @At("RETURN"))
    private void injectTick(final CallbackInfo ci) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.hideSignText.getValue()) {
            this.messages[3] = VisualCategory.SIGN_HIDE_SECRET;
        } else this.messages[3] = "";
    }

    @Redirect(method = "setCurrentRowMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;setText(Lnet/minecraft/block/entity/SignText;Z)Z"))
    private boolean redirectSetCurrentRowMessage(final SignBlockEntity instance, final SignText text, final boolean front) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.hideSignText.getValue()) {
            return false;
        }
        return instance.setText(text, front);
    }

    @Redirect(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    private int injectRenderSignTextDrawText(final DrawContext instance, final TextRenderer textRenderer, final String text, final int x, final int y, final int color, final boolean shadow) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.hideSignText.getValue()) {
            return instance.drawText(textRenderer, "*".repeat(text.length()), x, y, color, shadow);
        }
        return instance.drawText(textRenderer, text, x, y, color, shadow);
    }

    @Redirect(method = "renderSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
    private int injectRenderSignTextGetWidth(final TextRenderer instance, final String text) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.hideSignText.getValue()) {
            return instance.getWidth("*".repeat(text.length()));
        }
        return instance.getWidth(text);
    }

}
