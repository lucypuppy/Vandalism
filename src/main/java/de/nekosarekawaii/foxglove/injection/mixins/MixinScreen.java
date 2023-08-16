package de.nekosarekawaii.foxglove.injection.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.RenderListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.TelemetryInfoScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Screen.class, priority = 9999)
public abstract class MixinScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1, remap = false), cancellable = true)
    private void injectHandleTextClick(final Style style, final CallbackInfoReturnable<Boolean> cir) {
        final ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null) {
            if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                final String value = clickEvent.getValue(), secret = Foxglove.getInstance().getCommandRegistry().getCommandSecret();
                if (value.startsWith(secret)) {
                    try {
                        Foxglove.getInstance().getCommandRegistry().commandDispatch(value.replaceFirst(secret, ""));
                        cir.setReturnValue(true);
                    } catch (final CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Inject(method = "renderBackgroundTexture", at = @At(value = "HEAD"), cancellable = true)
    private void injectRenderBackgroundTexture(final DrawContext context, final CallbackInfo ci) {
        if (this.client != null && this.client.player != null && this.client.world != null && !(this.client.currentScreen instanceof MessageScreen) && !(this.client.currentScreen instanceof TelemetryInfoScreen) && !(this.client.currentScreen instanceof PackScreen)) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void injectRender(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, mouseX, mouseY, delta));
        context.getMatrices().pop();
    }

}
