package de.vandalismdevelopment.vandalism.gui.imgui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.minecraft.ImGuiScreen;
import de.vandalismdevelopment.vandalism.injection.access.IImGuiImplGlfw;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImGuiRenderer implements MinecraftWrapper {

    private final ImGuiImplGl3 imGuiImplGl3;
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final List<RenderInterface> renderInterfaces;

    public ImGuiRenderer(final long handle, final File dir) {
        this.imGuiImplGl3 = new ImGuiImplGl3();
        this.imGuiImplGlfw = new ImGuiImplGlfw();
        this.renderInterfaces = new ArrayList<>();
        ImGui.createContext();
        ImPlot.createContext();
        final ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setFontGlobalScale(1f);
        imGuiIO.setIniFilename(dir.getName() + "/imgui.ini");
        this.loadFonts(imGuiIO);
        this.setStyle();
        this.imGuiImplGlfw.init(handle, true);
        this.imGuiImplGl3.init();
    }

    private void setStyle() {
        final ImGuiStyle style = ImGui.getStyle();
        style.setWindowPadding(15, 15);
        style.setWindowRounding(5.0f);
        style.setFramePadding(5, 5);
        style.setFrameRounding(4.0f);
        style.setItemSpacing(12, 8);
        style.setItemInnerSpacing(8, 6);
        style.setIndentSpacing(25.0f);
        style.setScrollbarSize(15.0f);
        style.setScrollbarRounding(9.0f);
        style.setGrabMinSize(5.0f);
        style.setChildBorderSize(0);
        style.setFrameBorderSize(0);
        style.setTabBorderSize(0);
        style.setWindowBorderSize(0);
        style.setGrabRounding(3.0f);
        final float[][] colors = style.getColors();
        colors[ImGuiCol.Text] = new float[]{0.80f, 0.80f, 0.83f, 1.00f};
        colors[ImGuiCol.TextDisabled] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.WindowBg] = new float[]{0.06f, 0.05f, 0.07f, 1.00f};
        colors[ImGuiCol.ChildBg] = new float[]{0.07f, 0.07f, 0.09f, 1.00f};
        colors[ImGuiCol.PopupBg] = new float[]{0.07f, 0.07f, 0.09f, 1.00f};
        colors[ImGuiCol.Border] = new float[]{0.80f, 0.80f, 0.83f, 0.88f};
        colors[ImGuiCol.BorderShadow] = new float[]{0.92f, 0.91f, 0.88f, 0.00f};
        colors[ImGuiCol.FrameBg] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.Tab] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.TabHovered] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.TabActive] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.TitleBg] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.07f, 0.07f, 0.09f, 1.00f};
        colors[ImGuiCol.MenuBarBg] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.80f, 0.80f, 0.83f, 0.31f};
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[]{0.56f, 0.56f, 0.58f, 1.00f};
        colors[ImGuiCol.ScrollbarGrabActive] = new float[]{0.06f, 0.05f, 0.07f, 1.00f};
        colors[ImGuiCol.CheckMark] = new float[]{0.80f, 0.80f, 0.83f, 0.31f};
        colors[ImGuiCol.SliderGrab] = new float[]{0.80f, 0.80f, 0.83f, 0.31f};
        colors[ImGuiCol.SliderGrabActive] = new float[]{0.06f, 0.05f, 0.07f, 1.00f};
        colors[ImGuiCol.Button] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.56f, 0.56f, 0.58f, 1.00f};
        colors[ImGuiCol.Header] = new float[]{0.10f, 0.09f, 0.12f, 1.00f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.24f, 0.23f, 0.29f, 1.00f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.06f, 0.05f, 0.07f, 1.00f};
        colors[ImGuiCol.ResizeGrip] = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.ResizeGripHovered] = new float[]{0.56f, 0.56f, 0.58f, 1.00f};
        colors[ImGuiCol.ResizeGripActive] = new float[]{0.06f, 0.05f, 0.07f, 1.00f};
        style.setColors(colors);
    }

    private void loadFonts(final ImGuiIO imGuiIO) {
        final ImFontAtlas atlas = imGuiIO.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesCyrillic());
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesJapanese());
        final short[] glyphRanges = rangesBuilder.buildRanges();
        final ImFont robotoRegular16 = loadFont("roboto-regular", 16, atlas, fontConfig, glyphRanges);
        if (robotoRegular16 != null) imGuiIO.setFontDefault(robotoRegular16);
        atlas.build();
        fontConfig.destroy();
    }

    private ImFont loadFont(final String fontName, final int size, final ImFontAtlas atlas, final ImFontConfig fontConfig, final short[] glyphRanges) {
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Vandalism.getInstance().getId());
        if (modContainer.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find mod container for mod " + Vandalism.getInstance().getId());
            return null;
        }
        final String pathString = "assets/" + Vandalism.getInstance().getId() + "/font/" + fontName + ".ttf";
        final Optional<Path> path = modContainer.get().findPath(pathString);
        if (path.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find font file: " + pathString);
            return null;
        }
        try {
            fontConfig.setName(fontName + " " + size + "px");
            return atlas.addFontFromMemoryTTF(IOUtils.toByteArray(Files.newInputStream(path.get())), size, fontConfig, glyphRanges);
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to load font: " + pathString, e);
        }
        return null;
    }

    private boolean hasSyncedStates;

    public void render() {
        if (this.renderInterfaces.isEmpty()) {
            if (!this.hasSyncedStates && !(this.mc().currentScreen instanceof ImGuiScreen)) {
                ((IImGuiImplGlfw) this.imGuiImplGlfw).vandalism$forceUpdateMouseCursor();
                if (this.player() != null && this.mc().currentScreen == null) {
                    this.mouse().unlockCursor();
                    this.mouse().lockCursor();
                }
                this.hasSyncedStates = true;
            }
            return;
        } else {
            this.hasSyncedStates = false;
        }
        this.imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        final ImGuiIO imGuiIO = ImGui.getIO();
        for (final RenderInterface renderInterface : this.renderInterfaces) {
            renderInterface.render(imGuiIO);
        }
        this.renderInterfaces.clear();
        ImGui.endFrame();
        ImGui.render();
        this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        if (imGuiIO.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public void addRenderInterface(final RenderInterface isWidget) {
        this.renderInterfaces.add(isWidget);
    }

    public int getGlobalWindowFlags() {
        return ImGuiWindowFlags.NoCollapse;
    }

}
