package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE))
    private void callRender2DListener_Pre(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, mouseX, mouseY, delta, false));
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void callRender2DListener_Post(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, mouseX, mouseY, delta, true));
    }

}
