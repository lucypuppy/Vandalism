/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor.gui.NbtEditorClientMenuWindow;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.lenni0451.mcstructs.nbt.io.NamedTag;
import net.minecraft.client.main.Main;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.IOException;
import java.util.Objects;

public class NbtEditorManager implements ImNbtDrawer, MinecraftWrapper {

    private NativeImageBackedTexture icons;
    private final MainWindow mainWindow;
    private final AboutWindow aboutWindow;
    private final DiffWindow diffWindow;

    private Window window;
    private Popup<?> popup;

    private NamedTag clipboardTag;

    public NbtEditorManager() {
        try {
            this.icons = new NativeImageBackedTexture(NativeImage.read(
                    Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(
                            "imnbt/icons.png"
                    ))
            ));
        } catch (IOException e) {
            this.icons = null;
            Vandalism.getInstance().getLogger().error("Failed to load nbt editor icons!", e);
        }
        this.mainWindow = new MainWindow(this, null);
        this.aboutWindow = new AboutWindow(this);
        this.diffWindow = new DiffWindow(this);
    }

    public Window getWindow() {
        return this.window;
    }

    public Popup<?> getPopup() {
        return this.popup;
    }

    @Override
    public int getLinesPerPage() {
        return 500;
    }

    @Override
    public int getIconsTexture() {
        return this.icons.getGlId();
    }

    @Override
    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    @Override
    public AboutWindow getAboutWindow() {
        return this.aboutWindow;
    }

    @Override
    public DiffWindow getDiffWindow() {
        return this.diffWindow;
    }

    @Override
    public void openPopup(final @NotNull Popup<?> popup) {
        this.popup = popup;
    }

    @Override
    public void closePopup() {
        this.popup = null;
    }

    @Override
    public void showWindow(final @NotNull Window window) {
        this.window = window;
    }

    @Nullable
    @Override
    public String showOpenFileDialog(final String title) {
        final MemoryStack memoryStack = MemoryStack.stackPush();
        final PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
        pointerBuffer.put(memoryStack.UTF8("*.nbt")).flip();
        final String response = TinyFileDialogs.tinyfd_openFileDialog(
                title,
                null,
                pointerBuffer,
                "Nbt File",
                false
        );
        memoryStack.pop();
        return response;
    }

    @Nullable
    @Override
    public String showSaveFileDialog(final String title) {
        final MemoryStack memoryStack = MemoryStack.stackPush();
        final PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
        pointerBuffer.put(memoryStack.UTF8("*.nbt")).flip();
        final String response = TinyFileDialogs.tinyfd_saveFileDialog(
                title,
                null,
                pointerBuffer,
                "Nbt File"
        );
        memoryStack.pop();
        return response;
    }

    @Override
    public boolean hasClipboard() {
        return this.clipboardTag != null;
    }

    @Override
    public void setClipboard(final @NotNull NamedTag tag) {
        this.clipboardTag = tag;
    }

    @Nullable
    @Override
    public NamedTag getClipboard() {
        return this.clipboardTag;
    }

    @Override
    public void exit() {
        Vandalism.getInstance().getClientMenuManager().getByClass(NbtEditorClientMenuWindow.class).setActive(false);
    }

}
