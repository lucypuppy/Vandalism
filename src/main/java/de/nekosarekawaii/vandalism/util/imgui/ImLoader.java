package de.nekosarekawaii.vandalism.util.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiCol;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.MinecraftClient;

import java.io.File;

public class ImLoader {
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

    public static void init(final File runDirectory) {
        ImGui.createContext();
        ImPlot.createContext();

        final var io = ImGui.getIO();
        io.setFontGlobalScale(1f);
        io.setIniFilename(runDirectory.getName() + "/imgui.ini");

        loadFonts(io);
        pushStyle();

        imGuiImplGlfw.init(MinecraftClient.getInstance().getWindow().getHandle(), true);
        imGuiImplGl3.init();
    }

    public static void draw(final Runnable runnable) {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        runnable.run();

        ImGui.endFrame();
        ImGui.render();

        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    protected static void loadFonts(final ImGuiIO io) {
        final var atlas = io.getFonts();
        final var fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);

        final var rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesCyrillic());
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesJapanese());

        io.setFontDefault(ImUtils.loadFont("roboto-regular", 16, atlas, fontConfig, rangesBuilder.buildRanges()));

        atlas.build();
        fontConfig.destroy();
    }

    protected static void pushStyle() {
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

}
