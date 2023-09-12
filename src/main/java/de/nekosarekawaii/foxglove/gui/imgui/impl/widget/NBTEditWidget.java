package de.nekosarekawaii.foxglove.gui.imgui.impl.widget;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.imnbt.ImNbtDrawer;
import net.lenni0451.imnbt.ui.types.Popup;
import net.lenni0451.imnbt.ui.types.Window;
import net.lenni0451.imnbt.ui.windows.AboutWindow;
import net.lenni0451.imnbt.ui.windows.DiffWindow;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.main.Main;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

//TODO: Implement this correct.
public class NBTEditWidget implements ImNbtDrawer {

    private static NativeImageBackedTexture icon = null;

    private final MainWindow mainWindow;
    private final AboutWindow aboutWindow;
    private final DiffWindow diffWindow;

    private Popup<?> popup;
    private Window window;

    private boolean show;

    public NBTEditWidget() {
        this.mainWindow = new MainWindow(this, null);
        this.aboutWindow = new AboutWindow(this);
        this.diffWindow = new DiffWindow(this);
        this.window = this.mainWindow;
        this.show = false;
        if (icon == null) {
            try {
                icon = new NativeImageBackedTexture(NativeImage.read(Main.class.getClassLoader().getResourceAsStream("assets/icons.png")));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void show() {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().mouse.isCursorLocked()) {
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ChatScreen("")));
        }
        this.show = true;
    }

    public void hide() {
        this.show = false;
    }

    @Override
    public int getLinesPerPage() {
        return 500;
    }

    @Override
    public int getIconsTexture() {
        return icon.getGlId();
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
        return null;
    }

    @Nullable
    @Override
    public String showSaveFileDialog(final String title) {
        return null;
    }

    public void render() {
        if (this.show) {
            if (ImGui.beginMainMenuBar()) {
                if (ImGui.button("Close NBT Edit")) {
                    this.hide();
                }
                ImGui.endMainMenuBar();
            }
            if (!MinecraftClient.getInstance().mouse.isCursorLocked()) {
                ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
                if (ImGui.begin("NBT Edit", ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse)) {
                    this.window.render();
                    ImGui.end();
                }
                if (this.popup != null) {
                    this.popup.open();
                    this.popup.render(this);
                }
                ImGui.popStyleVar();
            }
        }
    }

}
