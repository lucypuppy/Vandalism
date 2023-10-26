package de.vandalismdevelopment.vandalism.injection.mixins.libs;

import de.vandalismdevelopment.vandalism.gui.minecraft.ImGuiScreen;
import de.vandalismdevelopment.vandalism.injection.access.IImGuiImplGlfw;
import imgui.flag.ImGuiMouseCursor;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ImGuiImplGlfw.class, remap = false)
public abstract class MixinImGuiImplGlfw implements IImGuiImplGlfw {

    @Shadow
    @Final
    private long[] mouseCursors;

    @Inject(method = "charCallback", at = @At(value = "INVOKE", target = "Limgui/ImGui;getIO()Limgui/ImGuiIO;", shift = At.Shift.BEFORE), cancellable = true)
    public void injectCharCallback(final long windowId, final int c, final CallbackInfo ci) {
        if (!(MinecraftClient.getInstance().currentScreen instanceof ImGuiScreen)) {
            ci.cancel();
        }
    }

    @Override
    public void forceUpdateMouseCursor() {
        final long windowPtr = MinecraftClient.getInstance().getWindow().getHandle();
        GLFW.glfwSetCursor(windowPtr, mouseCursors[ImGuiMouseCursor.Arrow]);
        GLFW.glfwSetInputMode(windowPtr, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

}
