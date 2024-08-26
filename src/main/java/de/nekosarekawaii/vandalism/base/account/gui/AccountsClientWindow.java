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

package de.nekosarekawaii.vandalism.base.account.gui;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.account.template.AbstractMicrosoftAccount;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.integration.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.SessionUtil;
import de.nekosarekawaii.vandalism.util.game.NameGenerationUtil;
import de.nekosarekawaii.vandalism.util.render.util.PlayerSkinRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.session.Session;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AccountsClientWindow extends ClientWindow {

    private static final float HEAD_ENTRY_DIMENSION = 64F;

    private final AccountManager accountManager;

    private AbstractAccount hoveredAccount;

    public AccountsClientWindow(final AccountManager accountManager) {
        super("Accounts", Category.CONFIG, 500f, 400f);
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

    private void renderHoveredAccountPopup(final boolean allowDelete) {
        if (this.hoveredAccount == null) return;
        if (ImGui.beginPopupContextItem("account-popup")) {
            ImGui.setNextItemWidth(400f);
            ImGui.text(this.hoveredAccount.getDisplayName());
            ImGui.separator();
            if (allowDelete) {
                if (ImUtils.subButton("Delete")) {
                    ImGui.closeCurrentPopup();
                    this.accountManager.remove(this.hoveredAccount);
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

    private void renderAccount(final String id, final AbstractAccount account, final boolean isEntry) {
        if (account == null) return;
        final Session session = account.getSession();
        if (session == null) return;
        final UUID playerUuid = session.getUuidOrNull();
        final PlayerSkinRenderer accountPlayerSkin = account.getPlayerSkin();
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
        final String playerName = account.getDisplayName();
        final GameProfile gameProfile = this.mc.getGameProfile();
        final boolean isCurrentAccount = gameProfile.getName().equals(playerName) && gameProfile.getId().equals(playerUuid);
        if (isCurrentAccount) {
            final float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
        }
        if (ImGui.button(id + "account" + playerName + account.getType(), ImGui.getColumnWidth(), ImUtils.modulateDimension(HEAD_ENTRY_DIMENSION + 5f))) {
            if (isEntry) {
                try {
                    account.login();
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
        ImGui.sameLine(ImUtils.modulateDimension(92));
        final StringBuilder data = new StringBuilder();
        data.append("Name: ");
        data.append(playerName);
        data.append("\n");
        data.append("Type: ");
        data.append(account.getType());
        data.append("\n");
        data.append("Status: ");
        data.append(account.getStatus() == null ? "Idle" : account.getStatus());
        if (account instanceof final AbstractMicrosoftAccount microsoftAccount) {
            final long tokenExpiration = microsoftAccount.getTokenExpirationTime();
            if (tokenExpiration != -1) {
                data.append("\n");
                final long timeLeft = tokenExpiration - System.currentTimeMillis();
                if (TimeUnit.MILLISECONDS.toHours(timeLeft) <= 0) {
                    data.append("Token expired");
                } else {
                    data.append("Token expires in: ");
                    data.append(TimeUnit.MILLISECONDS.toHours(timeLeft));
                    data.append(" hours, ");
                    data.append(TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60);
                    data.append(" minutes and ");
                    data.append(TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60);
                    data.append(" seconds");
                }
            }
        }
        ImGui.textWrapped(data.toString());
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        if (ImGui.beginTabBar(id + "tabBar")) {
            if (ImGui.beginTabItem("Current Account")) {
                final AbstractAccount currentAccount = this.accountManager.getCurrentAccount();
                if (currentAccount != null) {
                    this.renderAccount(id, currentAccount, false);
                    this.renderHoveredAccountPopup(false);
                    if (ImUtils.subButton("Logout")) {
                        this.accountManager.getFirstAccount().login();
                    }
                }
                ImGui.endTabItem();
            }
            final List<AbstractAccount> list = this.accountManager.getList();
            if (!list.isEmpty()) {
                if (ImGui.beginTabItem("Accounts")) {
                    for (final AbstractAccount account : list) {
                        this.renderAccount(id, account, true);
                    }
                    this.renderHoveredAccountPopup(true);
                    ImGui.endTabItem();
                }
            }
            if (ImGui.beginTabItem("Add Account")) {
                AccountManager.ACCOUNT_TYPES.forEach((account, factory) -> {
                    if (!(account instanceof EasyMCAccount)) {
                        if (ImGui.treeNodeEx(account.getType() + id + account.getType() + "addAccount")) {
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
                    if (ImGui.treeNodeEx(account.getType() + id + account.getType() + "directLoginAccount")) {
                        factory.displayFactory();
                        if (ImGui.button("Login", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                            this.recallAccount(factory, AbstractAccount::login);
                        }
                        ImGui.treePop();
                    }
                });
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Quick Login")) {
                this.renderAccount(id, this.accountManager.getCurrentAccount(), false);
                if (ImGui.button("Login with random username", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                    SessionUtil.setSessionAsync(NameGenerationUtil.generateUsername(), "");
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

}
