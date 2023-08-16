package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import de.nekosarekawaii.foxglove.config.impl.alt.alttype.type.CrackedAccount;
import de.nekosarekawaii.foxglove.config.impl.alt.alttype.type.MicrosoftAccount;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import de.nekosarekawaii.foxglove.util.ValidatorUtils;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltManagerImGuiMenu extends ImGuiMenu {

    private final ImString email, password, username, uuid;

    private final ExecutorService executor;

    private final MicrosoftAuthenticator authenticator;

    public AltManagerImGuiMenu() {
        super("Alt Manager");
        this.email = this.password = this.uuid = new ImString();
        this.username = new ImString(16);
        this.executor = Executors.newSingleThreadExecutor();
        this.authenticator = new MicrosoftAuthenticator();
    }

    private static void renderCurrentAccount() {
        final Session session = MinecraftClient.getInstance().getSession();
        ImGui.text("Current Account:");
        ImGui.text("Username: " + session.getUsername());
        ImGui.text("UUID: " + session.getUuid());
        ImGui.text("Type: " + (session.getAccessToken().equals("-") ? "Cracked" : "Premium"));
        ImGui.newLine();
    }

    @Override
    public void render() {
        ImGui.setNextWindowSizeConstraints(320, 0, 320, 400);

        if (ImGui.begin("Alt Manager")) {

            if (ImGui.beginTabBar("")) {
                if (ImGui.beginTabItem("List##altmanager")) {
                    renderCurrentAccount();
                    for (final Account account : Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts()) {
                        ImGui.text(account.getUsername() + " | " + account.getType());

                        ImGui.sameLine();
                        ImGui.setCursorPosX(212);

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

                if (ImGui.beginTabItem("Add##altmanager")) {
                    renderCurrentAccount();
                    ImGui.inputText("E-Mail##altmanager", this.email);
                    ImGui.inputText("Password##altmanager", this.password, ImGuiInputTextFlags.Password);

                    if (ImGui.button("Add Microsoft##altmanager")) {
                        final String emailValue = this.email.get().replace(" ", ""), passwordValue = this.password.get().replace(" ", "");
                        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
                            this.executor.submit(() -> {
                                try {
                                    final MicrosoftAuthResult result = this.authenticator.loginWithCredentials(emailValue, passwordValue);

                                    this.email.clear();
                                    this.password.clear();

                                    Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts().add(new MicrosoftAccount(result.getRefreshToken(), result.getProfile().getId(), result.getProfile().getName()));
                                    Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                                } catch (final MicrosoftAuthenticationException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }

                    ImGui.newLine();
                    ImGui.inputText("Username##altmanager", this.username);
                    ImGui.inputText("UUID##altmanager", this.uuid);

                    if (ImGui.button("Add Cracked##altmanager")) {
                        final String usernameValue = this.username.get().replace(" ", "");
                        if (!usernameValue.isEmpty()) {
                            final List<Account> accounts = Foxglove.getInstance().getConfigManager().getAccountConfig().getAccounts();
                            boolean contains = false;
                            String uuidValue = this.uuid.get();
                            if (uuidValue.isEmpty()) uuidValue = Uuids.getOfflinePlayerUuid(usernameValue).toString();
                            for (final Account account : accounts) {
                                if (account instanceof final CrackedAccount crackedAccount && crackedAccount.getUuid().equals(uuidValue)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains && ValidatorUtils.isUUID(uuidValue)) {
                                final UUID realUUID = UUID.fromString(uuidValue);
                                accounts.add(new CrackedAccount(usernameValue, realUUID));
                                Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
                                this.username.clear();
                                this.uuid.clear();
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
