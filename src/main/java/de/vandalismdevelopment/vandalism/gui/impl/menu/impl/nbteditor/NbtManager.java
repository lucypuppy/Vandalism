package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.nbteditor;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
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

public class NbtManager implements ImNbtDrawer, MinecraftWrapper {

    private NativeImageBackedTexture icons;
    private final MainWindow mainWindow;
    private final AboutWindow aboutWindow;
    private final DiffWindow diffWindow;

    private Window window;
    private Popup<?> popup;

    public NbtManager() {
        try {
            this.icons = new NativeImageBackedTexture(NativeImage.read(
                    Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(
                            "imnbt/icons.png"
                    ))
            ));
        } catch (final IOException e) {
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
        return true;
    }

    @Override
    public void setClipboard(final @NotNull NamedTag tag) {
        this.keyboard().setClipboard(
                tag.getName() +
                        ": " +
                        tag.getTag().toString()
        );
    }

    @Nullable
    @Override
    public NamedTag getClipboard() {
        return null;
    }

}
