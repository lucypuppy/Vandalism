/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.minigames.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.integration.minigames.Minigame;
import de.nekosarekawaii.vandalism.integration.minigames.MinigamesManager;
import de.nekosarekawaii.vandalism.util.imgui.FontAwesomeIcons;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.render.gl.utils.TemporaryValues;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class MinigamesClientWindow extends ClientWindow {

    private final SimpleFramebuffer gameWindowFrameBuffer;
    private final MinigamesManager minigamesManager;

    public MinigamesClientWindow(final MinigamesManager minigamesManager) {
        super("Minigames", Category.MISC, 690f, 590f, ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
        this.minigamesManager = minigamesManager;
        this.gameWindowFrameBuffer = new SimpleFramebuffer(1, 1, true, MinecraftClient.IS_SYSTEM_MAC);
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
        final Minigame currentMinigame = this.minigamesManager.getCurrentMinigame();
        if (currentMinigame != null) {
            currentMinigame.mouseClicked(mouseX, mouseY, button, release);
        }
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        final Minigame currentMinigame = this.minigamesManager.getCurrentMinigame();
        if (currentMinigame != null) {
            currentMinigame.keyPressed(key, scanCode, modifiers, release);
        }
        return super.keyPressed(key, scanCode, modifiers, release);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.setNextWindowSizeConstraints(ImUtils.modulateDimension(this.width), ImUtils.modulateDimension(this.height), ImUtils.modulateDimension(this.width), ImUtils.modulateDimension(this.height));
        ImGui.begin(this.getName(), this.minigamesManager.getCurrentMinigame() != null ? this.windowFlags | ImGuiWindowFlags.NoMove : this.windowFlags);
        this.onRender(context, mouseX, mouseY, delta);
        ImGui.end();
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        final Minigame currentMinigame = this.minigamesManager.getCurrentMinigame();
        if (currentMinigame == null) {
            for (final Minigame minigame : Vandalism.getInstance().getMinigamesManager().getList()) {
                if (ImGui.button(FontAwesomeIcons.VolumeUp + id + "minigame" + minigame.getName(), ImUtils.modulateDimension(ImGui.getColumnWidth() / 9), ImUtils.modulateDimension(64f))) {
                    if (!minigame.isPlaying()) {
                        minigame.startPlaying();
                    }
                }
                ImGui.sameLine();
                if (ImGui.button(id + "minigame" + minigame.getName(), ImGui.getColumnWidth(), ImUtils.modulateDimension(64f))) {
                    this.minigamesManager.setCurrentMinigame(minigame);
                }
                ImGui.sameLine(ImUtils.modulateDimension(ImGui.getColumnWidth() / 6));
                ImGui.textWrapped("Name: " + minigame.getName() + "\n" + "Description: " + minigame.getDescription() + "\n" + "Author: " + minigame.getAuthor());
            }
            return;
        }
        ImGui.beginChild(id + "mingameContainer", ImGui.getColumnWidth(), -ImGui.getTextLineHeightWithSpacing() - ImUtils.modulateDimension(10), true);
        final int width = (int) (ImGui.getWindowWidth() - ImUtils.modulateDimension(10));
        final int height = (int) (ImGui.getWindowHeight() - ImUtils.modulateDimension(60));
        if (this.gameWindowFrameBuffer.textureWidth != width || this.gameWindowFrameBuffer.textureHeight != height) {
            this.gameWindowFrameBuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
        this.gameWindowFrameBuffer.beginWrite(true);
        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        final Matrix4f oldProjection = RenderSystem.getProjectionMatrix();
        final VertexSorter oldVertexSorter = RenderSystem.getVertexSorting();
        final Matrix4f oldModelViewMatrix = RenderSystem.modelViewMatrix;
        RenderSystem.setProjectionMatrix(new Matrix4f().identity().ortho(0f, width, 0f, height, 0f, 1f), oldVertexSorter);
        RenderSystem.modelViewMatrix = TemporaryValues.IDENTITY_MATRIX4F;
        RenderSystem.disableCull();
        final float contentRegionWidth = ImGui.getWindowContentRegionMaxX() - ImGui.getWindowContentRegionMinX();
        final float contentRegionHeight = ImGui.getWindowContentRegionMaxY() - ImGui.getWindowContentRegionMinY();
        final float startX = ImGui.getCursorScreenPosX() + contentRegionWidth / 2f - width / 2f;
        final float startY = ImGui.getCursorScreenPosY() + contentRegionHeight / 2f - height / 2f;
        final float endX = ImGui.getCursorScreenPosX() + contentRegionWidth / 2f + width / 2f;
        final float endY = ImGui.getCursorScreenPosY() + contentRegionHeight / 2f + height / 2f;
        final int guiScale = this.mc.options.getGuiScale().getValue();
        currentMinigame.onRender(context, mouseX * guiScale, mouseY * guiScale, startX, startY, endX, endY, width, height);
        RenderSystem.setProjectionMatrix(oldProjection, oldVertexSorter);
        RenderSystem.modelViewMatrix = oldModelViewMatrix;
        this.mc.getFramebuffer().beginWrite(true);
        final ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addImage(this.gameWindowFrameBuffer.getColorAttachment(), startX, startY, endX, endY);
        this.mc.getFramebuffer().beginWrite(true);
        ImGui.endChild();
        if (ImUtils.subButton("Close " + currentMinigame.getName() + id + "closeMinigame")) {
            this.minigamesManager.setCurrentMinigame(null);
        }
    }

}
