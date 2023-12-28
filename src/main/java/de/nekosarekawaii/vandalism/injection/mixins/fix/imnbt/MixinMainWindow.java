package de.nekosarekawaii.vandalism.injection.mixins.fix.imnbt;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor.NbtEditorClientMenuWindow;
import imgui.ImGui;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = MainWindow.class, remap = false)
public abstract class MixinMainWindow {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;menuItem(Ljava/lang/String;)Z"))
    private boolean cancelRenderMenuItems(final String name) {
        if (name.equals("About")) {
            return false;
        } else if (name.equals("Exit")) {
            if (ImGui.menuItem("Exit")) {
                Vandalism.getInstance().getClientMenuManager().getByClass(NbtEditorClientMenuWindow.class).setActive(false);
            }
            return false;
        }
        return ImGui.menuItem(name);
    }

    @Unique
    private static final ExecutorService vandalism$EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;chooseFile()V"))
    private void chooseFileInANewThread(final MainWindow instance) {
        vandalism$EXECUTOR_SERVICE.submit(instance::chooseFile);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;saveFile()V"))
    private void saveFileInANewThread(final MainWindow instance) {
        vandalism$EXECUTOR_SERVICE.submit(instance::saveFile);
    }

}
