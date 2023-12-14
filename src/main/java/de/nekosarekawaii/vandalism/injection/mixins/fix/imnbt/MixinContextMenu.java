package de.nekosarekawaii.vandalism.injection.mixins.fix.imnbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.ContextMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContextMenu.class, remap = false)
public abstract class MixinContextMenu {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;beginMenu(Ljava/lang/String;)Z"))
    private boolean vandalism$cancelRenderTransformMenu(final String name) {
        if (name.equals("Transform")) {
            return false;
        }
        return ImGui.beginMenu(name);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;menuItem(Ljava/lang/String;)Z"))
    private boolean vandalism$cancelRenderPasteTagMenuItem(final String name) {
        if (name.equals("Paste Tag")) {
            return false;
        }
        return ImGui.menuItem(name);
    }

}
