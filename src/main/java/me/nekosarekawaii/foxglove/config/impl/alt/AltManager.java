package me.nekosarekawaii.foxglove.config.impl.alt;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.CrackedAccount;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.MicrosoftAccount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltManager {

    private final static ImString email = new ImString(), password = new ImString(), username = new ImString(16), uuid = new ImString();

    private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static boolean render = false;

    private static void renderCurrentAccount() {
        final Session session = MinecraftClient.getInstance().getSession();
        ImGui.text("Current Account:");
        ImGui.text("Username: " + session.getUsername());
        ImGui.text("UUID: " + session.getUuid());
        ImGui.text("Type: " + (session.getAccessToken().equals("-") ? "Cracked" : "Premium"));
        ImGui.newLine();
    }

    public static void render(final ImGuiIO io) {
        ImGui.setNextWindowSizeConstraints(320, 0, 320, 400);

        if (ImGui.begin("Alt Manager", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize)) {
            ImGui.setWindowSize(0, 0);

            if (ImGui.beginTabBar("")) {
                if (ImGui.beginTabItem("List")) {
                    renderCurrentAccount();
                    for (final Account account : Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts()) {
                        ImGui.text(account.getUsername() + " | " + account.getType());

                        ImGui.sameLine();
                        ImGui.setCursorPosX(320 - 108); //Hardcode lol rofl

                        if (ImGui.button("login##" + account.getUsername())) {
                            executor.submit(account::login);
                        }

                        ImGui.sameLine();

                        if (ImGui.button("remove##" + account.getUsername())) {
                            Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts().remove(account);

                            Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAltsConfig());
                        }
                    }

                    ImGui.endTabItem();
                }

                if (ImGui.beginTabItem("Add")) {
                    renderCurrentAccount();
                    ImGui.inputText("E-Mail", email);
                    ImGui.inputText("Password", password, ImGuiInputTextFlags.Password);

                    if (ImGui.button("Add Microsoft")) {
                        final String emailValue = email.get().replace(" ", ""), passwordValue = password.get().replace(" ", "");
                        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
                            final ObjectArrayList<Account> accounts = Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts();
                            boolean contains = false;
                            for (final Account account : accounts) {
                                if (account instanceof final MicrosoftAccount microsoftAccount) {
                                    if (microsoftAccount.getEmail().equals(emailValue)) {
                                        contains = true;
                                        break;
                                    }
                                }
                            }
                            if (!contains) {
                                email.clear();
                                password.clear();
                                Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts().add(new MicrosoftAccount(emailValue, passwordValue));
                                Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAltsConfig());
                            }
                        }
                    }

                    if (ImGui.button("Add Microsoft (Browser)")) {
                        executor.submit(() -> {
                            final MicrosoftAccount account = new MicrosoftAccount();
                            account.loginWithBrowser();
                            if (!account.getUsername().isEmpty()) {
                                Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts().add(account);
                                Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAltsConfig());
                            }
                        });
                    }

                    ImGui.newLine();
                    ImGui.inputText("Username", username);
                    ImGui.inputText("UUID", uuid);

                    if (ImGui.button("Add Cracked")) {
                        final String usernameValue = username.get().replace(" ", "");
                        if (!usernameValue.isEmpty()) {
                            final ObjectArrayList<Account> accounts = Foxglove.getInstance().getConfigManager().getAltsConfig().getAccounts();
                            boolean contains = false;
                            String uuidValue = uuid.get();
                            if (uuidValue.isEmpty()) uuidValue = Uuids.getOfflinePlayerUuid(usernameValue).toString();
                            for (final Account account : accounts) {
                                if (account instanceof final CrackedAccount crackedAccount && crackedAccount.getUuidString().equals(uuidValue)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                try {
                                    final UUID realUUID = UUID.fromString(uuidValue);
                                    accounts.add(new CrackedAccount(usernameValue, realUUID));
                                    Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAltsConfig());
                                    username.clear();
                                    uuid.clear();
                                } catch (final IllegalArgumentException ignored) {
                                }
                            }
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
