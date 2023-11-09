package de.vandalismdevelopment.vandalism.injection.mixins.libs.imnbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(MainWindow.class)
public abstract class MixinMainWindow {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;menuItem(Ljava/lang/String;)Z"))
    private boolean redirectRenderCancelMenuItems(final String name) {
        if (name.equals("Exit") || name.equals("About")) {
            return false;
        }
        return ImGui.menuItem(name);
    }

    @Unique
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;chooseFile()V"))
    private void redirectRenderChooseFileInANewThread(final MainWindow instance) {
        this.executorService.submit(instance::chooseFile);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;saveFile()V"))
    private void redirectRenderSaveFileInANewThread(final MainWindow instance) {
        this.executorService.submit(instance::saveFile);
    }

}
