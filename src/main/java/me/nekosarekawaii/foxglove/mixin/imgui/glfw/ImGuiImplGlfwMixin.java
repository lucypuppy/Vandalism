package me.nekosarekawaii.foxglove.mixin.imgui.glfw;

import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImGuiImplGlfw.class)
public class ImGuiImplGlfwMixin {

    @Inject(method = "keyCallback", at = @At(value = "INVOKE", target = "Limgui/ImGui;getIO()Limgui/ImGuiIO;", shift = At.Shift.AFTER), cancellable = true, remap = false)
    public void injectKeyCallback(final long windowId, final int key, final int scancode, final int action, final int mods, final CallbackInfo ci) {
        if (MinecraftClient.getInstance().mouse.isCursorLocked())
            ci.cancel();
    }

}