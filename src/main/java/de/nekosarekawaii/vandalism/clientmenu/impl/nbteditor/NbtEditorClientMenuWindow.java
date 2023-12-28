package de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
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

public class NbtEditorClientMenuWindow extends ClientMenuWindow {

    private final NbtManager nbtManager;

    public NbtEditorClientMenuWindow() {
        super("Nbt Editor", Category.MISC_UTILS);
        this.nbtManager = new NbtManager();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final Window nbtRendererWindow = this.nbtManager.getWindow();
        if (nbtRendererWindow != null) {
            if (ImGui.begin("Nbt Editor##nbteditor", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar)) {
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
            setActive(true);
            Vandalism.getInstance().getClientMenuManager().openScreen();
        } catch (IOException io) {
            Vandalism.getInstance().getLogger().error("Failed to display nbt.", io);
        }
    }

}
