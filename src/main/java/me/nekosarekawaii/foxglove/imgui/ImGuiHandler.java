package me.nekosarekawaii.foxglove.imgui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.internal.ImGui;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.KeyboardListener;
import me.nekosarekawaii.foxglove.event.Render2DListener;
import me.nekosarekawaii.foxglove.imgui.impl.menu.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiHandler implements KeyboardListener, Render2DListener {

    private final ImGuiRenderer imGuiRenderer;

    private boolean
            renderBar = false,
            renderConfigMenu = false,
            renderModulesMenu = false,
            renderAltManagerMenu = false,
            renderNameHistoryMenu = false,
            renderServerPingerMenu = false;

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
                    if (ImGui.button("Config")) {
                        this.renderConfigMenu = !this.renderConfigMenu;
                    }
                    if (ImGui.button("Modules")) {
                        this.renderModulesMenu = !this.renderModulesMenu;
                    }
                    if (ImGui.button("Alt Manager")) {
                        this.renderAltManagerMenu = !this.renderAltManagerMenu;
                    }
                    if (ImGui.button("Name History")) {
                        this.renderNameHistoryMenu = !this.renderNameHistoryMenu;
                    }
                    if (ImGui.button("Server Pinger")) {
                        this.renderServerPingerMenu = !this.renderServerPingerMenu;
                    }
                    ImGui.endMainMenuBar();
                }
            }
            if (this.renderNameHistoryMenu) {
                NameHistoryMenu.render();
            }
            if (this.renderAltManagerMenu) {
                AltManagerMenu.render();
            }
            if (this.renderConfigMenu) {
                ConfigMenu.render();
            }
            if (this.renderModulesMenu) {
                ModulesMenu.render();
            }
            if (this.renderServerPingerMenu) {
                ServerPingerMenu.render();
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
        if (key == Foxglove.getInstance().getConfigManager().getMainConfig().menuBarKey.getValue().left()) {
            this.renderBar = !this.renderBar;
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
