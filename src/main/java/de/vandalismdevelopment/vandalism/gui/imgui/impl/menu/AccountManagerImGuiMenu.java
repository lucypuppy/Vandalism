package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.florianmichael.rclasses.common.object.ObjectTypeChecker;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.CrackedAccount;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.MicrosoftAccount;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.java.StepMCProfile;
import net.raphimc.mcauth.step.msa.StepMsaDeviceCode;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountManagerImGuiMenu extends ImGuiMenu {

    private final static ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (!Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) && imGuiInputTextCallbackData.getEventChar() != '_' && imGuiInputTextCallbackData.getEventChar() != '§') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString username, uuid;

    private String statusLine1, statusLine2;
    private final ExecutorService executor;

    public AccountManagerImGuiMenu() {
        super("Account Manager");
        this.uuid = new ImString();
        this.username = new ImString(16);
        this.resetStatus();
        this.executor = Executors.newSingleThreadExecutor();
    }

    private void resetStatus() {
        this.statusLine1 = "Waiting for input...";
        this.statusLine2 = "";
    }

    private void delayedResetStatus() {
        try {
            Thread.sleep(10000);
        }
        catch (final InterruptedException ignored) {}
        this.resetStatus();
    }

    private void renderCurrentData() {
        final Session session = MinecraftClient.getInstance().getSession();
        ImGui.text("Current Account");
        if (ImGui.beginListBox("##currentAccountData", 340, 75)) {
            ImGui.text("Username: " + session.getUsername());
            final UUID uuid = session.getUuidOrNull();
            if (uuid != null) ImGui.text("UUID: " + uuid);
            ImGui.text("Type: " + (session.getAccessToken().equals("FabricMC") ? "Cracked" : "Premium"));
            ImGui.endListBox();
        }
        ImGui.text("Status");
        if (ImGui.beginListBox("##currentAccountLoginStatus", 560, 75)) {
            ImGui.text(this.statusLine1);
            ImGui.text(this.statusLine2);
            ImGui.endListBox();
        }
    }

    @Override
    public void render() {
        final List<Account> accounts = Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts();
        if (ImGui.begin("Account Manager", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("##accountmanagertabbar")) {
                if (ImGui.beginTabItem("List##accountmanager")) {
                    this.renderCurrentData();
                    ImGui.newLine();
                    ImGui.text("Accounts");
                    if (ImGui.beginListBox("##accountList", 340, 0)) {
                        for (final Account account : accounts) {
                            ImGui.text(account.getUsername() + " | " + account.getType());
                            ImGui.sameLine();
                            ImGui.setCursorPosX(212);
                            if (ImGui.button("login##" + account.getUsername())) this.executor.submit(() -> {
                                try {
                                    account.login();
                                    this.statusLine1 = "Successfully logged into the " + account.getType() + " account:";
                                    this.statusLine2 = account.getUsername();
                                }
                                catch (final Throwable throwable) {
                                    Vandalism.getInstance().getLogger().error(
                                            "Failed to log into the " + account.getType() + " account: " + account.getUsername(),
                                            throwable
                                    );
                                    this.statusLine1 = "Failed to log into the " + account.getType() + " account: " + account.getUsername();
                                    this.statusLine2 = throwable.toString();
                                }
                                this.delayedResetStatus();
                            });
                            ImGui.sameLine();
                            if (ImGui.button("remove##" + account.getUsername())) {
                                accounts.remove(account);
                                Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                            }
                        }
                        ImGui.endListBox();
                    }
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Add##accountmanager")) {
                    this.renderCurrentData();
                    ImGui.newLine();
                    ImGui.text("Microsoft");
                    if (ImGui.button("Add Microsoft (Device Code)##accountmanager")) {
                        this.executor.submit(() -> {
                            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                                final StepMCProfile.MCProfile mcProfile = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(
                                        httpClient,
                                        new StepMsaDeviceCode.MsaDeviceCodeCallback(
                                                msaDeviceCode -> {
                                                    this.statusLine1 = "Please enter the code \"" + msaDeviceCode.userCode() + "\" at \"" + msaDeviceCode.verificationUri() + "\".";
                                                    this.statusLine2 = "The code has been copied to your clipboard and the login page has been opened.";
                                                    Util.getOperatingSystem().open(msaDeviceCode.verificationUri());
                                                    keyboard().setClipboard(msaDeviceCode.userCode());
                                                }
                                        )
                                );
                                accounts.add(
                                        new MicrosoftAccount(
                                                mcProfile.toJson().toString(),
                                                mcProfile.id(),
                                                mcProfile.name()
                                        )
                                );
                                Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                                this.statusLine1 = "Successfully added the microsoft account to your account list:";
                                this.statusLine2 = mcProfile.name();
                            }
                            catch (final Throwable throwable) {
                                Vandalism.getInstance().getLogger().error("Failed to log into a microsoft account.", throwable);
                                this.statusLine1 = "Failed to add the microsoft account to your account list.";
                                this.statusLine2 = throwable.toString();
                            }
                            this.delayedResetStatus();
                        });
                    }
                    ImGui.newLine();
                    ImGui.text("Cracked");
                    ImGui.inputText("Username##accountmanager", this.username,
                            ImGuiInputTextFlags.CallbackCharFilter,
                            USERNAME_NAME_FILTER
                    );
                    ImGui.inputText("UUID##accountmanager", this.uuid);
                    final String usernameValue = this.username.get();
                    if (!usernameValue.isBlank()) {
                        if (ImGui.button("Add Cracked (Direct)##accountmanager")) {
                            this.executor.submit(() -> {
                                boolean contains = false;
                                String uuidValue = this.uuid.get();
                                if (uuidValue.isBlank()) {
                                    uuidValue = Uuids.getOfflinePlayerUuid(usernameValue).toString();
                                }
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
                                    this.statusLine1 = "Successfully added the cracked account to your account list:";
                                    this.statusLine2 = usernameValue;
                                }
                                else {
                                    this.statusLine1 = "Failed to add the cracked account to your account list.";
                                    this.statusLine2 = "The UUID is invalid or already in use.";
                                }
                                this.delayedResetStatus();
                            });
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
