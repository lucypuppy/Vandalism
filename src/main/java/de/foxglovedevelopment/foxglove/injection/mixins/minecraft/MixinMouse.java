package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.foxglovedevelopment.foxglove.Foxglove;
import imgui.ImGui;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (ImGui.isItemClicked() || Foxglove.getInstance().getImGuiHandler().isHovered())
            ci.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double xoffset, double yoffset, CallbackInfo ci) {
        if (ImGui.isItemClicked() || Foxglove.getInstance().getImGuiHandler().isHovered())
            ci.cancel();
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        if (ImGui.isItemClicked() || Foxglove.getInstance().getImGuiHandler().isHovered())
            ci.cancel();
    }

}
