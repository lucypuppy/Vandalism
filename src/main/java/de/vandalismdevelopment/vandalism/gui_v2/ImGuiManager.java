package de.vandalismdevelopment.vandalism.gui_v2;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import de.vandalismdevelopment.vandalism.gui_v2.base.ImGuiScreen;
import de.vandalismdevelopment.vandalism.gui_v2.loader.ImLoader;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiManager extends Storage<ImWindow> implements RenderListener, InputListener, MinecraftWrapper {

    public ImGuiManager(final File runDirectory) {
        DietrichEvents2.global().subscribe(this, KeyboardEvent.ID, Render2DEvent.ID);

        ImLoader.init(runDirectory);
    }

    @Override
    public void init() {
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN) return;
        if (this.mc.currentScreen instanceof ConnectScreen || this.mc.currentScreen instanceof LevelLoadingScreen) return;

        if (key == Vandalism.getInstance().getClientSettings().getMenuSettings().menuKey.getValue().getKeyCode()) {
            final var screen = MinecraftClient.getInstance().currentScreen;
            if (screen instanceof ChatScreen || screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen || screen instanceof ImGuiScreen) {
                return;
            }

            MinecraftClient.getInstance().setScreen(new ImGuiScreen(screen));
        }
    }

}
