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

package de.nekosarekawaii.vandalism.base.account.gui;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.render.PlayerSkinRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.session.Session;

import java.util.UUID;
import java.util.function.Consumer;

public class AccountsClientMenuWindow extends ClientMenuWindow {

    private static final float ACCOUNT_ENTRY_CONTENT_WIDTH = 64f;
    private static final float ACCOUNT_ENTRY_CONTENT_HEIGHT = 64f;

    private final AccountManager accountManager;

    public AccountsClientMenuWindow(final AccountManager accountManager) {
        super("Accounts", Category.CONFIG);
        this.accountManager = accountManager;
    }

    private void recallAccount(final AccountFactory factory, final Consumer<AbstractAccount> account) {
        factory.make().whenComplete((abstractAccount, throwable) -> {
            if (abstractAccount == null) {
                Vandalism.getInstance().getLogger().error("Failed to create account.");
                return;
            }
            if (throwable != null) {
                Vandalism.getInstance().getLogger().error("Failed to create account.", throwable);
            } else {
                account.accept(abstractAccount);
            }
        });
    }

    private AbstractAccount hoveredAccount;

    private void renderHoveredAccountPopup(final boolean allowDelete) {
        if (this.hoveredAccount == null) return;
        if (ImGui.beginPopupContextItem("account-popup")) {
            ImGui.setNextItemWidth(400f);
            if (allowDelete) {
                if (ImUtils.subButton("Delete")) {
                    ImGui.closeCurrentPopup();
                    Vandalism.getInstance().getAccountManager().remove(this.hoveredAccount);
                    this.hoveredAccount = null;
                }
            }
            if (this.hoveredAccount != null) {
                if (ImUtils.subButton("Copy Name")) {
                    this.mc.keyboard.setClipboard(this.hoveredAccount.getDisplayName());
                }
                final Session session = this.hoveredAccount.getSession();
                if (session != null) {
                    final UUID uuid = session.getUuidOrNull();
                    final boolean uuidAvailable = uuid != null;
                    if (uuidAvailable) {
                        if (ImUtils.subButton("Copy UUID")) {
                            this.mc.keyboard.setClipboard(uuid.toString());
                        }
                    }
                    if (ImUtils.subButton("Copy Access token")) {
                        this.mc.keyboard.setClipboard(session.getAccessToken());
                    }
                    ImGui.text("Type: " + this.hoveredAccount.getType());
                    final String lastLogin = this.hoveredAccount.getLastLogin();
                    if (lastLogin != null) {
                        ImGui.text("Last Login: " + lastLogin);
                    }
                    if (uuidAvailable) {
                        ImGui.text("UUID: " + uuid);
                    }
                }
            }
            ImGui.endPopup();
        }
    }

    private void renderAccount(final AbstractAccount account, final boolean isEntry) {
        if (account == null) return;
        final Session session = account.getSession();
        if (session == null) return;
        final UUID playerUuid = session.getUuidOrNull();
        final PlayerSkinRenderer accountPlayerSkin = account.getPlayerSkin();
        if (accountPlayerSkin != null) {
            final int playerSkinId = accountPlayerSkin.getGlId();
            if (playerSkinId != -1) {
                ImUtils.texture(playerSkinId, ACCOUNT_ENTRY_CONTENT_WIDTH, ACCOUNT_ENTRY_CONTENT_HEIGHT, 8f, 8f, 15.5f, 15f);
                ImGui.sameLine(15);
                ImUtils.texture(playerSkinId, ACCOUNT_ENTRY_CONTENT_WIDTH, ACCOUNT_ENTRY_CONTENT_HEIGHT, 39.5f, 8f, 47.1f, 14.8f);
                ImGui.sameLine();
            }
        }
        final String playerName = account.getDisplayName();
        final GameProfile gameProfile = this.mc.getGameProfile();
        final boolean isCurrentAccount = gameProfile.getName().equals(playerName) && gameProfile.getId().equals(playerUuid);
        if (isCurrentAccount) {
            final float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
        }
        if (ImGui.button("##account" + playerName + account.getType(), ImGui.getColumnWidth(), ACCOUNT_ENTRY_CONTENT_HEIGHT)) {
            if (isEntry) {
                try {
                    account.logIn();
                    account.setStatus("Logged in");
                } catch (Throwable throwable) {
                    account.setStatus("Error: " + throwable.getMessage());
                }
            }
        }
        if (isCurrentAccount) {
            ImGui.popStyleColor(3);
        }
        if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
            this.hoveredAccount = account;
            ImGui.openPopup("account-popup");
        }
        ImGui.sameLine(95);
        final StringBuilder data = new StringBuilder();
        data.append("Name: ");
        data.append(account.getDisplayName());
        data.append("\n");
        data.append("Type: ");
        data.append(account.getType());
        data.append("\n");
        data.append("Status: ");
        data.append(account.getStatus() == null ? "Idle" : account.getStatus());
        ImGui.text(data.toString());
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(this.getName());
        if (ImGui.beginTabBar("##accountsTabBar")) {
            if (ImGui.beginTabItem("Current Account")) {
                final AbstractAccount currentAccount = this.accountManager.getCurrentAccount();
                if (currentAccount != null) {
                    this.renderAccount(currentAccount, false);
                    this.renderHoveredAccountPopup(false);
                    if (ImUtils.subButton("Logout")) {
                        try {
                            this.accountManager.logOut();
                        } catch (Throwable t) {
                            Vandalism.getInstance().getLogger().error("Failed to logout from account.", t);
                        }
                    }
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Accounts")) {
                for (final AbstractAccount account : this.accountManager.getList()) {
                    this.renderAccount(account, true);
                }
                this.renderHoveredAccountPopup(true);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Add Account")) {
                AccountManager.ACCOUNT_TYPES.forEach((account, factory) -> {
                    if (!(account instanceof EasyMCAccount)) {
                        if (ImGui.treeNodeEx(account.getType() + "##" + account.getType() + "AddAccount")) {
                            factory.displayFactory();
                            if (ImGui.button("Add", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                                this.recallAccount(factory, this.accountManager::add);
                            }
                            ImGui.treePop();
                        }
                    }
                });
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Direct Login")) {
                AccountManager.ACCOUNT_TYPES.forEach((account, factory) -> {
                    if (ImGui.treeNodeEx(account.getType() + "##" + account.getType() + "DirectLoginAccount")) {
                        factory.displayFactory();
                        if (ImGui.button("Login", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                            this.recallAccount(factory, AbstractAccount::logIn);
                        }
                        ImGui.treePop();
                    }
                });
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

}
