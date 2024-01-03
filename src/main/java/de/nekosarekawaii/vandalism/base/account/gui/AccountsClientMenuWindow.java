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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.util.render.PlayerSkinRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.session.Session;

import static de.nekosarekawaii.vandalism.util.imgui.ImUtils.subButton;

public class AccountsClientMenuWindow extends ClientMenuWindow {
    private static final float ACCOUNT_ENTRY_CONTENT_WIDTH = 64F;
    private static final float ACCOUNT_ENTRY_CONTENT_HEIGHT = 64F;

    private final AccountManager accountManager;

    public AccountsClientMenuWindow(final AccountManager accountManager) {
        super("Accounts", Category.CONFIGURATION);

        this.accountManager = accountManager;
    }

    protected void renderMenuBar(final AccountManager accountManager) {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Add Account")) {
                AccountManager.ACCOUNT_TYPES.forEach((account, factory) -> {
                    if (ImGui.beginMenu(account.getName())) {
                        factory.displayFactory();
                        if (ImGui.button("Add", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                            factory.make().whenComplete((abstractAccount, throwable) -> {
                                if (abstractAccount == null) {
                                    Vandalism.getInstance().getLogger().error("Failed to create account");
                                    return;
                                }
                                if (throwable != null) {
                                    Vandalism.getInstance().getLogger().error("Failed to create account", throwable);
                                } else {
                                    accountManager.add(abstractAccount);
                                }
                            });
                        }
                        ImGui.endMenu();
                    }
                });
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Current Account")) {
                final AbstractAccount currentAccount = accountManager.getCurrentAccount();
                ImGui.text("Account type: " + currentAccount.getName());
                ImGui.text("Account name: " + currentAccount.getDisplayName());
                if (currentAccount.getSession().getUuidOrNull() != null) {
                    ImGui.text("Account UUID: " + currentAccount.getSession().getUuidOrNull());
                }
                if (ImGui.button("Copy", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    final var name = currentAccount.getDisplayName();
                    final var uuid = currentAccount.getSession().getUuidOrNull().toString();
                    final var accessToken = currentAccount.getSession().getAccessToken();
                    final var xuid = currentAccount.getSession().getXuid().orElse("Not available");
                    final var clientId = currentAccount.getSession().getClientId().orElse("Not available");

                    mc.keyboard.setClipboard("Name: " + name + "\n" +
                            "UUID: " + uuid + "\n" +
                            "Access Token: " + accessToken + "\n" +
                            "XUID: " + xuid + "\n" +
                            "Client ID: " + clientId + "\n");
                }
                if (subButton("Logout")) {
                    try {
                        accountManager.logOut();
                    } catch (Throwable t) {
                        Vandalism.getInstance().getLogger().error("Failed to logout", t);
                    }
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
    }

    private AbstractAccount hoveredAccount;

    protected void renderAccountPopup() {
        if (ImGui.beginPopupContextItem("account-popup")) {
            ImGui.setNextItemWidth(400F); // Just some magic value to make the popup look good
            if (subButton("Delete account")) {
                ImGui.closeCurrentPopup();
                Vandalism.getInstance().getAccountManager().remove(hoveredAccount);
                this.hoveredAccount = null;
            }
            if (this.hoveredAccount != null) {
                if (subButton("Copy Name")) {
                    this.mc.keyboard.setClipboard(this.hoveredAccount.getDisplayName());
                }
                final Session session = this.hoveredAccount.getSession();
                if (session != null) {
                    if (session.getUuidOrNull() != null && subButton("Copy UUID")) {
                        this.mc.keyboard.setClipboard(session.getUuidOrNull().toString());
                    }
                    if (subButton("Copy Access token")) {
                        this.mc.keyboard.setClipboard(session.getAccessToken());
                    }
                    ImGui.text("Account type: " + this.hoveredAccount.getName());
                    if (this.hoveredAccount.getLastLogin() != null) {
                        ImGui.text("Last login: " + this.hoveredAccount.getLastLogin());
                    }
                    if (this.hoveredAccount.getSession().getUuidOrNull() != null) {
                        ImGui.text("Account UUID: " + session.getUuidOrNull());
                    }
                }
            }
            ImGui.endPopup();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ImGui.begin(getName(), ImGuiWindowFlags.MenuBar);
        renderMenuBar(accountManager);

        for (AbstractAccount account : accountManager.getList()) {
            if (account == null) continue;

            final PlayerSkinRenderer accountPlayerSkin = account.getPlayerSkin();
            if (accountPlayerSkin != null && accountPlayerSkin.getGlId() != -1) {
                //Those are not some magic values these are the values to render exactly the face from the skin.
                ImGui.image(accountPlayerSkin.getGlId(), ACCOUNT_ENTRY_CONTENT_WIDTH, ACCOUNT_ENTRY_CONTENT_HEIGHT, 0.125f, 0.1f, 0.25f, 0.250f);
                ImGui.sameLine();
            }

            if (ImGui.button(account.getDisplayName() + " (" + (account.getStatus() == null ? "Idle" : account.getStatus()) + ")", ImGui.getColumnWidth(), ACCOUNT_ENTRY_CONTENT_HEIGHT + 1f)) {
                try {
                    accountManager.logIn(account);
                    account.setStatus("Logged in");
                } catch (Throwable throwable) {
                    account.setStatus("Error: " + throwable.getMessage());
                }
            }
            if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                hoveredAccount = account;
                ImGui.openPopup("account-popup");
            }
        }
        renderAccountPopup();
        ImGui.end();
    }

}
