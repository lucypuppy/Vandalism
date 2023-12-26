package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.keybinds.Keybind;
import net.wurstclient.keybinds.KeybindList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(value = KeybindList.class, remap = false)
public abstract class MixinKeybindList {

    @Shadow
    private static void addKB(Set<Keybind> set, String key, String cmds) {
    }

    @Inject(method = "createDefaultKeybinds", at = @At("HEAD"), cancellable = true)
    private static void removeWurstDefaultKeybinds(final CallbackInfoReturnable<Set<Keybind>> cir) {
        final Set<Keybind> set = new LinkedHashSet<>();
        addKB(set, "insert", "navigator");
        addKB(set, "home", "clickgui");
        cir.setReturnValue(set);
    }

}
