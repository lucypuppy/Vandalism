package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.nbteditor;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.nbteditor.NbtManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class NbtEditortImGuiMenu extends ImGuiMenu {

    private final NbtManager nbtManager;

    public NbtEditortImGuiMenu() {
        super("Nbt Editor");
        this.nbtManager = new NbtManager();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final Window nbtRendererWindow = this.nbtManager.getWindow();
        if (nbtRendererWindow != null) {
            if (ImGui.begin(
                    "Nbt Editor##nbteditor",
                    Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags() |
                            ImGuiWindowFlags.MenuBar
            )) {
                nbtRendererWindow.render();
                ImGui.end();
            }
        } else {
            this.nbtManager.showWindow(this.nbtManager.getMainWindow());
        }
        final Popup<?> nbtRendererPopup = this.nbtManager.getPopup();
        if (nbtRendererPopup != null) {
            nbtRendererPopup.open();
            nbtRendererPopup.render(this.nbtManager);
        }
    }

    public void displayNbt(final String name, final NbtCompound nbt) {
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(stream);
            NbtIo.write(nbt, out);
            this.nbtManager.getMainWindow().dragAndDrop(new File(name), stream.toByteArray());
            out.close();
            stream.close();
            final NbtEditortImGuiMenu nbtEditortImGuiMenu = Vandalism
                    .getInstance()
                    .getImGuiHandler()
                    .getImGuiMenuCategoryRegistry()
                    .getImGuiMenuByClass(NbtEditortImGuiMenu.class);
            if (!nbtEditortImGuiMenu.getState()) {
                nbtEditortImGuiMenu.toggle();
            }
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException ignored) {
                }
                this.mc().execute(() -> Vandalism.getInstance().getImGuiHandler().toggleScreen());
            });
        } catch (final IOException io) {
            Vandalism.getInstance().getLogger().error("Failed to display nbt.", io);
        }
    }

}
