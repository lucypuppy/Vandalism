package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.impl.command.CommandRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
                final String value = clickEvent.getValue(), secret = CommandRegistry.COMMAND_SECRET;
                if (value.startsWith(secret)) {
                    try {
                        Vandalism.getInstance().getCommandRegistry().commandDispatch(value.replaceFirst(secret, ""));
                        cir.setReturnValue(true);
                    } catch (final CommandSyntaxException e) {
                        Vandalism.getInstance().getLogger().error("Failed to run command.", e);
                    }
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE))
    private void injectRenderPre(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, mouseX, mouseY, delta, false));
        context.getMatrices().pop();
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void injectRenderPost(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, mouseX, mouseY, delta, true));
        context.getMatrices().pop();
    }

}
