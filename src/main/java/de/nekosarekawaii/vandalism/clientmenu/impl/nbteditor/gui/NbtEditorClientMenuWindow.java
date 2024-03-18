/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor.NbtEditorManager;
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

    private final NbtEditorManager nbtEditorManager;

    public NbtEditorClientMenuWindow() {
        super("Nbt Editor", Category.MISC);
        this.nbtEditorManager = new NbtEditorManager();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final Window nbtRendererWindow = this.nbtEditorManager.getWindow();
        if (nbtRendererWindow != null) {
            ImGui.begin("Nbt Editor##nbteditor", ImGuiWindowFlags.MenuBar);
            nbtRendererWindow.render();
            ImGui.end();
        } else {
            this.nbtEditorManager.showWindow(this.nbtEditorManager.getMainWindow());
        }
        final Popup<?> nbtRendererPopup = this.nbtEditorManager.getPopup();
        if (nbtRendererPopup != null) {
            nbtRendererPopup.open();
            nbtRendererPopup.render(this.nbtEditorManager);
        }
    }

    public void displayNbt(final String name, final NbtCompound nbt) {
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(stream);
            NbtIo.write(nbt, out);
            this.nbtEditorManager.getMainWindow().dragAndDrop(new File(name), stream.toByteArray());
            out.close();
            stream.close();
            setActive(true);
            Vandalism.getInstance().getClientMenuManager().openScreen();
        } catch (IOException io) {
            Vandalism.getInstance().getLogger().error("Failed to display nbt.", io);
        }
    }

}
