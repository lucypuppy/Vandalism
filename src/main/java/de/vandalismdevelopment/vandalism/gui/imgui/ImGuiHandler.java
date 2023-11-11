package de.vandalismdevelopment.vandalism.gui.imgui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.gui.minecraft.ImGuiScreen;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiHandler implements KeyboardListener, RenderListener, MinecraftWrapper {

    private final ImGuiRenderer imGuiRenderer;

    private final ImGuiMenuRegistry imGuiMenuRegistry;

    public ImGuiHandler(final File dir) {
        this.imGuiRenderer = new ImGuiRenderer(dir);
        this.imGuiMenuRegistry = new ImGuiMenuRegistry();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    public ImGuiRenderer getImGuiRenderer() {
        return this.imGuiRenderer;
    }

    public ImGuiMenuRegistry getImGuiMenuRegistry() {
        return this.imGuiMenuRegistry;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN) return;
        if (currentScreen() instanceof ConnectScreen || currentScreen() instanceof LevelLoadingScreen) return;
        if (key == Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.menuKey.getValue().getKeyCode()) {
            this.toggleScreen();
        }
    }

    public void toggleScreen() {
        Screen screen = currentScreen();
        if (screen != null) {
            if (screen instanceof ImGuiScreen) {
                return;
            } else if (screen instanceof HandledScreen<?> && !(screen instanceof InventoryScreen)) {
                screen = null;
            }
        }
        setScreen(new ImGuiScreen(screen));
    }

    @Override
    public void onRender2DOutGamePost(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.render();
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        this.render();
    }

    private void render() {
        this.imGuiRenderer.render();
    }

}
