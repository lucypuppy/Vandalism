package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import imgui.glfw.ImGuiImplGlfw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImGuiImplGlfw.class)
public abstract class MixinImGuiImplGlfw implements MinecraftWrapper {

    @Inject(method = "keyCallback", at = @At(value = "INVOKE", target = "Limgui/ImGui;getIO()Limgui/ImGuiIO;", shift = At.Shift.AFTER), cancellable = true, remap = false)
    public void injectKeyCallback(final long windowId, final int key, final int scancode, final int action, final int mods, final CallbackInfo ci) {
        if (mouse().isCursorLocked()) {
            ci.cancel();
        }
    }

}