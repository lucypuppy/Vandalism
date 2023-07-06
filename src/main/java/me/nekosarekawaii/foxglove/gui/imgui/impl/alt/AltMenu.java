package me.nekosarekawaii.foxglove.gui.imgui.impl.alt;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type.CrackedAccount;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type.MicrosoftAccount;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltMenu extends ImGuiMenu {

    @Override
    public void init() {
    }

    private final ImString email = new ImString();
    private final ImString password = new ImString();
    private final ImString username = new ImString();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void render(final ImGuiIO imGuiIO) {
        if (ImGui.begin("Alt Menu")) {
            ImGui.setWindowSize(400, 0);

            if (ImGui.beginTabBar("")) {
                if (ImGui.beginTabItem("List")) {
                    for (final Account account : Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts()) {
                        ImGui.text(account.getUsername());

                        ImGui.sameLine();

                        if (ImGui.button("login##" + account.getUsername())) {
                            this.executor.submit(account::login);
                        }

                        ImGui.sameLine();

                        if (ImGui.button("remove##" + account.getUsername())) {
                            Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().remove(account);

                            Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                        }
                    }

                    ImGui.endTabItem();
                }

                if (ImGui.beginTabItem("Add")) {
                    ImGui.inputText("email", this.email);
                    ImGui.inputText("password", this.password, ImGuiInputTextFlags.Password);

                    if (ImGui.button("Add Microsoft")) {
                        if (!this.email.isEmpty() && !this.password.isEmpty()) {
                            Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(
                                    new MicrosoftAccount(this.email.get(), this.password.get()));

                            Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                        }
                    }

                    if (ImGui.button("Add Microsoft (Browser)")) {
                        this.executor.submit(() -> {
                            final MicrosoftAccount account = new MicrosoftAccount();
                            account.loginWithBrowser();
                            if (!account.isBrowserSession()) {
                                Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(account);
                                Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                            }
                        });
                    }

                    ImGui.newLine();
                    ImGui.inputText("username", this.username);

                    if (ImGui.button("Add Cracked")) {
                        if (!this.username.isEmpty()) {
                            Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(
                                    new CrackedAccount(this.username.get()));

                            Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                        }
                    }

                    ImGui.endTabItem();
                }

                ImGui.endTabBar();
            }

            ImGui.end();
        }
    }

    @Override
    public void tick() {
        if (!(mc().currentScreen instanceof MultiplayerScreen || mc().currentScreen instanceof DirectConnectScreen)) {
            Foxglove.getInstance().setCurrentImGuiMenu(null);
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }

    @Override
    public void close() {
    }

}
