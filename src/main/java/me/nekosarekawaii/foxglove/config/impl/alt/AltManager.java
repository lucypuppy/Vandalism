package me.nekosarekawaii.foxglove.config.impl.alt;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.CrackedAccount;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.MicrosoftAccount;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltManager {

    private final static ImString email = new ImString();
    private final static ImString password = new ImString();
    private final static ImString username = new ImString();

    private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static boolean render = false;

    public static void render(final ImGuiIO io) {
        if (ImGui.begin("Alt Manager")) {
            ImGui.setWindowSize(400, 0);

            if (ImGui.beginTabBar("")) {
                if (ImGui.beginTabItem("List")) {
                    ImGui.text("Current Account: " + MinecraftClient.getInstance().getSession().getUsername());
                    for (final Account account : Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts()) {
                        ImGui.text(account.getUsername());

                        ImGui.sameLine();

                        if (ImGui.button("login##" + account.getUsername())) {
                            executor.submit(account::login);
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
                    ImGui.text("Current Account: " + MinecraftClient.getInstance().getSession().getUsername());
                    ImGui.inputText("E-Mail", email);
                    ImGui.inputText("Password", password, ImGuiInputTextFlags.Password);

                    if (ImGui.button("Add Microsoft")) {
                        if (!email.isEmpty() && !password.isEmpty()) {
                            Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(
                                    new MicrosoftAccount(email.get(), password.get()));

                            Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                        }
                    }

                    if (ImGui.button("Add Microsoft (Browser)")) {
                        executor.submit(() -> {
                            final MicrosoftAccount account = new MicrosoftAccount();
                            account.loginWithBrowser();
                            if (!account.isBrowserSession()) {
                                Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(account);
                                Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                            }
                        });
                    }

                    ImGui.newLine();
                    ImGui.inputText("Username", username);

                    if (ImGui.button("Add Cracked")) {
                        if (!username.isEmpty()) {
                            Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(
                                    new CrackedAccount(username.get()));

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

}
