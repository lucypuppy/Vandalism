package de.vandalismdevelopment.vandalism.injection.mixins.libs.imnbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.ContextMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContextMenu.class, remap = false)
public abstract class MixinContextMenu {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;beginMenu(Ljava/lang/String;)Z"))
    private boolean redirectRenderCancelTransformMenu(final String name) {
        if (name.equals("Transform")) {
            return false;
        }
        return ImGui.beginMenu(name);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;menuItem(Ljava/lang/String;)Z"))
    private boolean redirectRenderCancelPasteTagMenuItem(final String name) {
        if (name.equals("Paste Tag")) {
            return false;
        }
        return ImGui.menuItem(name);
    }

}
