package me.nekosarekawaii.foxglove.gui.imgui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.internal.ImGui;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.impl.KeyboardListener;
import me.nekosarekawaii.foxglove.event.impl.Render2DListener;
import me.nekosarekawaii.foxglove.gui.imgui.impl.AltManagerMenu;
import me.nekosarekawaii.foxglove.gui.imgui.impl.MainMenu;
import me.nekosarekawaii.foxglove.gui.imgui.impl.NBTEditMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiHandler implements KeyboardListener, Render2DListener {

    private final ImGuiRenderer imGuiRenderer;

    private boolean renderBar = false, renderMainMenu = false, renderAltManagerMenu = false;

    private final NBTEditMenu nbtEditMenu;

    public ImGuiHandler(final File dir) {
        this.imGuiRenderer = new ImGuiRenderer(dir);
        this.nbtEditMenu = new NBTEditMenu();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    private void renderImGuiContext() {
        this.imGuiRenderer.addRenderInterface(io -> {
            if (this.renderBar) {
                if (ImGui.beginMainMenuBar()) {
                    if (ImGui.button("Alt Manager")) {
                        this.renderAltManagerMenu = !this.renderAltManagerMenu;
                    }
                    ImGui.endMainMenuBar();
                }
                AltManagerMenu.render();
            }
            if (this.renderAltManagerMenu) {
                AltManagerMenu.render();
            }
            if (this.renderMainMenu) {
                MainMenu.render();
            }
            this.nbtEditMenu.render();
        });
        this.imGuiRenderer.render();
    }

    public ImGuiRenderer getImGuiRenderer() {
        return this.imGuiRenderer;
    }

    public NBTEditMenu getNbtEditMenu() {
        return this.nbtEditMenu;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        if (key == GLFW.GLFW_KEY_MENU) this.renderBar = !this.renderBar;
        else if (key == Foxglove.getInstance().getConfigManager().getMainConfig().mainMenuKeyCode.getValue()) {
            this.renderMainMenu = !this.renderMainMenu;
        }
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (MinecraftClient.getInstance().currentScreen != null) {
            this.renderImGuiContext();
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (MinecraftClient.getInstance().currentScreen == null) {
            this.renderImGuiContext();
        }
    }

}
