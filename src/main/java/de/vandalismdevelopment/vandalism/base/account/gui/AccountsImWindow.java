package de.vandalismdevelopment.vandalism.base.account.gui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.account.AbstractAccount;
import de.vandalismdevelopment.vandalism.base.account.AccountManager;
import de.vandalismdevelopment.vandalism.gui.base.ImWindow;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;

import static de.vandalismdevelopment.vandalism.gui.loader.ImUtils.subButton;

public class AccountsImWindow extends ImWindow {
    private static final float ACCOUNT_ENTRY_CONTENT_WIDTH = 64F;
    private static final float ACCOUNT_ENTRY_CONTENT_HEIGHT = 64F;

    private final AccountManager accountManager;

    public AccountsImWindow(final AccountManager accountManager) {
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
                            try {
                                accountManager.add(factory.make());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                    final var xuid = currentAccount.getSession().getXuid().orElse("");
                    final var clientId = currentAccount.getSession().getClientId().orElse("");

                    mc.keyboard.setClipboard("Name: " + name + "\n" +
                            "UUID: " + uuid + "\n" +
                            "Access Token: " + accessToken + "\n" +
                            "XUID: " + xuid + "\n" +
                            "Client ID: " + clientId + "\n");
                }
                if (subButton("Logout")) {
                    try {
                        accountManager.logOut();
                    } catch (Throwable e) {
                        e.printStackTrace();
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
                hoveredAccount = null;
                ImGui.endPopup(); // We force cancel all rendering actions by pushing the endPopup call here
                return;
            }

            if (subButton("Copy Name")) {
                mc.keyboard.setClipboard(hoveredAccount.getDisplayName());
            }
            if (hoveredAccount.getSession() != null) {
                if (hoveredAccount.getSession().getUuidOrNull() != null && ImGui.button("Copy UUID", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    mc.keyboard.setClipboard(hoveredAccount.getSession().getUuidOrNull().toString());
                }
                if (subButton("Copy Access token")) {
                    mc.keyboard.setClipboard(hoveredAccount.getSession().getAccessToken());
                }
                ImGui.text("Account type: " + hoveredAccount.getName());
                if (hoveredAccount.getLastLogin() != null) {
                    ImGui.text("Last login: " + hoveredAccount.getLastLogin());
                }
                if (hoveredAccount.getSession().getUuidOrNull() != null) {
                    ImGui.text("Account UUID: " + hoveredAccount.getSession().getUuidOrNull());
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
            if (account.getPlayerSkin() != null) {
                ImGui.image(account.getPlayerSkin().getGlId(), ACCOUNT_ENTRY_CONTENT_WIDTH, ACCOUNT_ENTRY_CONTENT_HEIGHT);
                ImGui.sameLine();
            }

            if (ImGui.button(account.getDisplayName() + " (" + (account.getStatus() == null ? "IDLE" : account.getStatus()) + ")", ImGui.getColumnWidth(), ACCOUNT_ENTRY_CONTENT_HEIGHT + 10)) {
                try {
                    accountManager.logIn(account);
                    account.setStatus("Logged in");
                } catch (Throwable throwable) {
                    account.setStatus("Error: " + throwable.getMessage());
                    throwable.printStackTrace();
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
