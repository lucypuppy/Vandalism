package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.InputListener;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse implements MinecraftWrapper {


    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void vandalism$callMouseEvent1(final long window, final int button, final int action, final int mods, final CallbackInfo ci) {
        if (this.window().getHandle() == window) {
            DietrichEvents2.global().postInternal(InputListener.MouseEvent.ID, new InputListener.MouseEvent(button, action, mods));
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void vandalism$callMouseEvent2(final long window, final double horizontal, final double vertical, final CallbackInfo ci) {
        if (this.window().getHandle() == window) {
            DietrichEvents2.global().postInternal(InputListener.MouseEvent.ID, new InputListener.MouseEvent(true, horizontal, vertical));
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void vandalism$callMouseEvent3(final long window, final double x, final double y, final CallbackInfo ci) {
        if (this.window().getHandle() == window) {
            DietrichEvents2.global().postInternal(InputListener.MouseEvent.ID, new InputListener.MouseEvent(false, x, y));
        }
    }

}
