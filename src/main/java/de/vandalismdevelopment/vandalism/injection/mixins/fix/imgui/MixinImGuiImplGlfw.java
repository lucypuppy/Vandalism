package de.vandalismdevelopment.vandalism.injection.mixins.fix.imgui;

import de.vandalismdevelopment.vandalism.gui.base.ImGuiScreen;
import de.vandalismdevelopment.vandalism.injection.access.IImGuiImplGlfw;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import imgui.flag.ImGuiMouseCursor;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ImGuiImplGlfw.class, remap = false)
public abstract class MixinImGuiImplGlfw implements IImGuiImplGlfw, MinecraftWrapper {

    @Shadow
    @Final
    private long[] mouseCursors;

    @Inject(method = "charCallback", at = @At(value = "INVOKE", target = "Limgui/ImGui;getIO()Limgui/ImGuiIO;", shift = At.Shift.BEFORE), cancellable = true)
    public void vandalism$cancelCharCallback(final long windowId, final int c, final CallbackInfo ci) {
        if (!(this.mc.currentScreen instanceof ImGuiScreen)) {
            ci.cancel();
        }
    }

    @Override
    public void vandalism$forceUpdateMouseCursor() {
        final long handle = mc.getWindow().getHandle();
        GLFW.glfwSetCursor(handle, this.mouseCursors[ImGuiMouseCursor.Arrow]);
        GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

}
