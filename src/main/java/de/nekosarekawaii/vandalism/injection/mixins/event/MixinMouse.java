package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse implements MinecraftWrapper {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void callMouseEvent_Button(final long window, final int button, final int action, final int mods, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            DietrichEvents2.global().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(button, action, mods));
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void callMouseEvent_Scroll(final long window, final double horizontal, final double vertical, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            DietrichEvents2.global().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(true, horizontal, vertical));
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void callMouseEvent_Pos(final long window, final double x, final double y, final CallbackInfo ci) {
        if (this.mc.getWindow().getHandle() == window) {
            DietrichEvents2.global().postInternal(MouseInputListener.MouseEvent.ID, new MouseInputListener.MouseEvent(false, x, y));
        }
    }

}
