package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE))
    private void vandalism$callPreOutGameRender2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, mouseX, mouseY, delta, false));
        context.getMatrices().pop();
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void vandalism$callPostOutGameRender2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, mouseX, mouseY, delta, true));
        context.getMatrices().pop();
    }

}
