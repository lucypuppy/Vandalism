/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.imgui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.injection.access.IImGuiImplGlfw;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiCol;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.MinecraftClient;

import java.io.File;

public class ImLoader {

    private static final ImGuiImplGl3 IM_GUI_IMPL_GL_3 = new ImGuiImplGl3();
    private static final ImGuiImplGlfw IM_GUI_IMPL_GLFW = new ImGuiImplGlfw();

    public static void init(final File runDirectory, final int scale) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO io = ImGui.getIO();
        io.setFontGlobalScale(1f);
        io.setIniFilename(runDirectory.getName() + "/imgui.ini");

        loadFonts(io, scale);
        pushStyle();

        IM_GUI_IMPL_GLFW.init(MinecraftClient.getInstance().getWindow().getHandle(), true);
        IM_GUI_IMPL_GL_3.init();
    }

    public static void draw(final Runnable runnable) {
        IM_GUI_IMPL_GLFW.newFrame();
        ImGui.newFrame();

        runnable.run();

        ImGui.endFrame();
        ImGui.render();

        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.imguiGlowOutline.getValue()) {
            Shaders.getGlowOutlineEffect().configure(menuSettings.imguiGlowOutlineWidth.getValue(), menuSettings.imguiGlowOutlineAccuracy.getValue(), menuSettings.imguiGlowOutlineExponent.getValue());
            Shaders.getGlowOutlineEffect().bindMask();
            IM_GUI_IMPL_GL_3.renderDrawData(ImGui.getDrawData());
            Shaders.getGlowOutlineEffect().renderFullscreen(Shaders.getColorFillEffect().maskFramebuffer().get(), false);
            Shaders.getColorFillEffect().setColor(menuSettings.imguiGlowOutlineColor.getColor());
            Shaders.getColorFillEffect().renderFullscreen(MinecraftClient.getInstance().getFramebuffer(), false);
        }

        IM_GUI_IMPL_GL_3.renderDrawData(ImGui.getDrawData());
    }

    protected static void loadFonts(final ImGuiIO io, final int scale) {
        final ImFontAtlas atlas = io.getFonts();

        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(atlas.getGlyphRangesDefault());
        rangesBuilder.addRanges(atlas.getGlyphRangesCyrillic());
        rangesBuilder.addRanges(atlas.getGlyphRangesJapanese());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);
        fontConfig.setGlyphRanges(rangesBuilder.buildRanges());

        ImUtils.loadFont("roboto-regular", scale, atlas, fontConfig, fontConfig.getGlyphRanges());

        fontConfig.setMergeMode(true);
        ImUtils.loadFont("fa-solid-900", scale, atlas, fontConfig, fontConfig.getGlyphRanges());
        ImUtils.loadFont("fa-regular-400", scale, atlas, fontConfig, fontConfig.getGlyphRanges());

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

    public static void forceUpdateMouse() {
        ((IImGuiImplGlfw) IM_GUI_IMPL_GLFW).vandalism$forceUpdateMouseCursor();
    }

}
