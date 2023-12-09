package de.vandalismdevelopment.vandalism.account_v2.gui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import de.vandalismdevelopment.vandalism.account_v2.AccountManager;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenu;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

public class AccountsImWindow extends ImGuiMenu {
    private static final float ACCOUNT_ENTRY_CONTENT_WIDTH = 64F;
    private static final float ACCOUNT_ENTRY_CONTENT_HEIGHT = 64F;

    public AccountsImWindow() {
        super("Accounts");
    }

    protected void renderMenuBar(final AccountManager accountManager) {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                ImGui.button("Import Accounts", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing());
                ImGui.button("Export Accounts", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing());
                ImGui.button("Cleanup", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing()); // Valid checker
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Add Account")) {
                for (AbstractAccount type : AccountManager.ACCOUNT_TYPES) {
                    if (ImGui.beginMenu(type.getName())) {
                        type.factory().displayFactory();
                        if (ImGui.button("Add", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                            accountManager.add(type.factory().make());
                        }
                        ImGui.endMenu();
                    }
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Current Account")) {
                ImGui.text("Account type: Session");
                ImGui.text("Account name: GommeHD");
                ImGui.text("Account UUID: <uuid>");
                if (ImGui.button("Copy", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                }
                if (ImGui.button("Logout", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
    }

    protected void renderAccountPopup() {
        if (ImGui.beginPopupContextItem("account-popup")) {
            ImGui.text("UWU GIRL");
            ImGui.endPopup();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        final AccountManager accountManager = Vandalism.getInstance().getAccountManager();

        ImGui.begin(getName(), ImGuiWindowFlags.MenuBar);
        renderMenuBar(accountManager);

        for (AbstractAccount account : accountManager.getList()) {
            if (ImGui.imageButton(account.getPlayerSkin().getGlId(), ACCOUNT_ENTRY_CONTENT_WIDTH, ACCOUNT_ENTRY_CONTENT_HEIGHT)) {
            }
            ImGui.sameLine();
            if (ImGui.button(account.getDisplayName(), ImGui.getColumnWidth(), ACCOUNT_ENTRY_CONTENT_HEIGHT + 10)) {
            }
            if (ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                ImGui.openPopup("account-popup");
            }
        }
        final long round = Util.getMeasuringTimeMs() / 300L % 4;
        if (round == 0) {
            ImGui.text("Loading");
        } else if (round == 1) {
            ImGui.text("Loading.");
        } else {
            ImGui.text("Loading..");
        }

        renderAccountPopup();
        ImGui.end();
    }

}
