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

package de.nekosarekawaii.vandalism.integration.friends.gui;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.integration.friends.FriendsManager;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import de.nekosarekawaii.vandalism.util.render.util.PlayerSkinRenderer;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

import java.util.UUID;

public class FriendsClientWindow extends ClientWindow {

    private static final ImGuiInputTextCallback NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            final int eventCharInt = imGuiInputTextCallbackData.getEventChar();
            if (eventCharInt == 0) return;
            final char eventChar = (char) eventCharInt;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '_') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private static final float HEAD_ENTRY_DIMENSION = 64F;

    private final FriendsManager friendsManager;

    private Friend hoveredFriend;

    private final ImString friendName = new ImString(MinecraftConstants.MAX_USERNAME_LENGTH);
    private final ImString friendAlias = new ImString(MinecraftConstants.MAX_USERNAME_LENGTH);
    private final ImString updatedFriendAlias = new ImString(MinecraftConstants.MAX_USERNAME_LENGTH);

    public FriendsClientWindow(final FriendsManager friendsManager) {
        super("Friends", Category.CONFIG, 450f, 500f);
        this.friendsManager = friendsManager;
    }

    private void renderHoveredFriendPopup(final String id) {
        if (this.hoveredFriend == null) return;
        if (ImGui.beginPopupContextItem("friend-popup")) {
            ImGui.setNextItemWidth(400f);
            ImGui.text(this.hoveredFriend.getName());
            ImGui.separator();
            if (this.hoveredFriend != null) {
                ImGui.text("Alias");
                ImGui.setNextItemWidth(500f);
                ImGui.inputText(id + "updatedFriendAlias", this.updatedFriendAlias, ImGuiInputTextFlags.CallbackCharFilter, NAME_FILTER);
                final String updatedFriendAliasValue = this.updatedFriendAlias.get();
                if ((!updatedFriendAliasValue.equals(this.hoveredFriend.getAlias()) || this.updatedFriendAlias.isEmpty()) && MathUtil.isBetween(updatedFriendAliasValue.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
                    if (ImUtils.subButton("Update Alias")) {
                        this.hoveredFriend.setAlias(updatedFriendAliasValue.isEmpty() ? this.hoveredFriend.getName() : updatedFriendAliasValue);
                        this.updatedFriendAlias.set(updatedFriendAliasValue);
                    }
                } else if (!updatedFriendAliasValue.equals(this.hoveredFriend.getName())) {
                    if (ImUtils.subButton("Reset Alias")) {
                        this.hoveredFriend.setAlias(this.hoveredFriend.getName());
                        this.updatedFriendAlias.set(this.hoveredFriend.getName());
                    }
                }
            }
            if (ImUtils.subButton("Remove Friend")) {
                ImGui.closeCurrentPopup();
                this.friendsManager.removeFriend(this.hoveredFriend);
                this.hoveredFriend = null;
            }
            if (this.hoveredFriend != null) {
                if (ImUtils.subButton("Copy Name")) {
                    mc.keyboard.setClipboard(this.hoveredFriend.getName());
                }
                final UUID uuid = this.hoveredFriend.getUuid();
                if (uuid != null) {
                    if (ImUtils.subButton("Copy UUID")) {
                        mc.keyboard.setClipboard(uuid.toString());
                    }
                }
                if (ImUtils.subButton("Copy Alias")) {
                    mc.keyboard.setClipboard(this.hoveredFriend.getAlias());
                }
            }
            ImGui.endPopup();
        }
    }

    private void renderFriend(final String id, final Friend friend) {
        if (friend == null) return;
        final UUID playerUuid = friend.getUuid();
        final PlayerSkinRenderer accountPlayerSkin = friend.getPlayerSkin();
        if (accountPlayerSkin != null) {
            final int playerSkinId = accountPlayerSkin.getGlId();
            if (playerSkinId != -1) {
                final float modulatedDimension = ImUtils.modulateDimension(HEAD_ENTRY_DIMENSION);
                ImUtils.texture(
                        playerSkinId,
                        modulatedDimension,
                        modulatedDimension,
                        ImUtils.modulateDimension(8f),
                        ImUtils.modulateDimension(8f),
                        ImUtils.modulateDimension(15.5f),
                        ImUtils.modulateDimension(15f)
                );
                ImGui.sameLine(15);
                ImUtils.texture(
                        playerSkinId,
                        modulatedDimension,
                        modulatedDimension,
                        ImUtils.modulateDimension(39.5f),
                        ImUtils.modulateDimension(8f),
                        ImUtils.modulateDimension(47.1f),
                        ImUtils.modulateDimension(14.8f)
                );
                ImGui.sameLine();
            }
        }
        final String playerName = friend.getName();
        final GameProfile gameProfile = mc.getGameProfile();
        final boolean isCurrentPlayer = gameProfile.getName().equals(playerName) && gameProfile.getId().equals(playerUuid);
        if (isCurrentPlayer) {
            final float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
        }
        ImGui.button(id + "friend" + playerName, ImGui.getColumnWidth(), ImUtils.modulateDimension(HEAD_ENTRY_DIMENSION));
        if (isCurrentPlayer) {
            ImGui.popStyleColor(3);
        }
        if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
            this.hoveredFriend = friend;
            this.updatedFriendAlias.set(this.hoveredFriend.getAlias());
            ImGui.openPopup("friend-popup");
        }
        ImGui.sameLine(ImUtils.modulateDimension(92));
        ImGui.textWrapped("Name: " + friend.getName() + "\n" + "UUID: " + friend.getUuid() + "\n" + "Alias: " + friend.getAlias());
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        if (ImGui.beginTabBar(id + "tabBar")) {
            if (ImGui.beginTabItem("Friends")) {
                for (final Friend friend : this.friendsManager.getList()) {
                    this.renderFriend(id, friend);
                }
                this.renderHoveredFriendPopup(id);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Add Friend")) {
                ImGui.text("Name");
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(id + "friendName", this.friendName, ImGuiInputTextFlags.CallbackCharFilter, NAME_FILTER);
                ImGui.text("Alias");
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(id + "friendAlias", this.friendAlias, ImGuiInputTextFlags.CallbackCharFilter, NAME_FILTER);
                ImGui.spacing();
                final String friendNameValue = this.friendName.get();
                if (!friendNameValue.isBlank() && MathUtil.isBetween(friendNameValue.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
                    if (this.friendAlias.isEmpty() || MathUtil.isBetween(this.friendAlias.get().length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
                        if (ImUtils.subButton("Add")) {
                            this.friendsManager.addFriend(this.friendName.get(), this.friendAlias.isEmpty() ? this.friendName.get() : this.friendAlias.get());
                            this.friendName.set("");
                            this.friendAlias.set("");
                        }
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

}
