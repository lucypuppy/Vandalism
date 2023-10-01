package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.florianmichael.rclasses.common.object.ObjectTypeChecker;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.Account;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type.CrackedAccount;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type.MicrosoftAccount;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;
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
        this.email = new ImString();
        this.password = new ImString();
        this.uuid = new ImString();
        this.username = new ImString(16);
        this.executor = Executors.newSingleThreadExecutor();
        this.authenticator = new MicrosoftAuthenticator();
    }

    private void renderCurrentAccount() {
        final Session session = mc().session;
        ImGui.text("Current Account");
        if (ImGui.beginListBox("##currentAccountData", 340, 75)) {
            ImGui.text("Username: " + session.getUsername());
            final UUID uuid = session.getUuidOrNull();
            if (uuid != null) ImGui.text("UUID: " + uuid);
            ImGui.text("Type: " + (session.getAccessToken().equals("-") ? "Cracked" : "Premium"));
            ImGui.endListBox();
        }
    }

    @Override
    public void render() {
        if (ImGui.begin("Alt Manager", ImGuiWindowFlags.NoCollapse)) {

            if (ImGui.beginTabBar("##altmanagertabbar")) {
                if (ImGui.beginTabItem("List##altmanager")) {
                    renderCurrentAccount();
                    ImGui.newLine();
                    ImGui.text("Accounts");
                    if (ImGui.beginListBox("##accountList", 340, 0)) {
                        for (final Account account : Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts()) {
                            ImGui.text(account.getUsername() + " | " + account.getType());

                            ImGui.sameLine();
                            ImGui.setCursorPosX(212);

                            if (ImGui.button("login##" + account.getUsername())) {
                                this.executor.submit(account::login);
                            }

                            ImGui.sameLine();

                            //TODO: Fix crash
                            if (ImGui.button("remove##" + account.getUsername())) {
                                Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts().remove(account);
                                Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                            }
                        }
                        ImGui.endListBox();
                    }

                    ImGui.endTabItem();
                }

                if (ImGui.beginTabItem("Add##altmanager")) {
                    renderCurrentAccount();
                    ImGui.inputText("E-Mail##altmanager", this.email);
                    ImGui.inputText("Password##altmanager", this.password, ImGuiInputTextFlags.Password);

                    if (ImGui.button("Add Microsoft##altmanager")) {
                        final String emailValue = this.email.get().replace(" ", ""),
                                passwordValue = this.password.get().replace(" ", "");
                        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
                            this.executor.submit(() -> {
                                try {
                                    final MicrosoftAuthResult result = this.authenticator.loginWithCredentials(emailValue, passwordValue);

                                    this.email.clear();
                                    this.password.clear();

                                    Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts().add(
                                            new MicrosoftAccount(
                                                    result.getRefreshToken(),
                                                    UUID.fromString(result.getProfile().getId()),
                                                    result.getProfile().getName()
                                            )
                                    );
                                    Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                                } catch (final MicrosoftAuthenticationException e) {
                                    Vandalism.getInstance().getLogger().error("Microsoft Account login failed!", e);
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
                            final List<Account> accounts = Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts();
                            boolean contains = false;
                            String uuidValue = this.uuid.get();
                            if (uuidValue.isEmpty()) uuidValue = Uuids.getOfflinePlayerUuid(usernameValue).toString();
                            for (final Account account : accounts) {
                                if (account instanceof final CrackedAccount crackedAccount && crackedAccount.getUuid().toString().equals(uuidValue)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains && ObjectTypeChecker.isUUID(uuidValue)) {
                                final UUID realUUID = UUID.fromString(uuidValue);
                                accounts.add(new CrackedAccount(usernameValue, realUUID));
                                Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
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
