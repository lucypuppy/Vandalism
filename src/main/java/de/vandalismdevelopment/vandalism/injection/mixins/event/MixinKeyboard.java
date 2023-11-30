package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", shift = At.Shift.BEFORE, ordinal = 0))
    private void vandalism$callKeyboardKeyEvent(final long window, final int key, final int scanCode, final int action, final int modifiers, final CallbackInfo callbackInfo) {
        DietrichEvents2.global().postInternal(KeyboardListener.KeyboardEvent.ID,
                new KeyboardListener.KeyboardEvent(
                        KeyboardListener.KeyboardEventType.KEY,
                        window,
                        key,
                        -1,
                        scanCode,
                        action,
                        modifiers
                )
        );
    }

    @Inject(method = "onChar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", shift = At.Shift.BEFORE))
    private void vandalism$callKeyboardCharEvent(final long window, final int codePoint, final int modifiers, final CallbackInfo callbackInfo) {
        DietrichEvents2.global().postInternal(KeyboardListener.KeyboardEvent.ID,
                new KeyboardListener.KeyboardEvent(
                        KeyboardListener.KeyboardEventType.CHAR,
                        window,
                        -1,
                        codePoint,
                        -1,
                        -1,
                        modifiers
                )
        );
    }

}
