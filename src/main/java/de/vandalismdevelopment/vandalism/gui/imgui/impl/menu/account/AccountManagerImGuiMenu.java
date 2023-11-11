package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.account;

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
import imgui.flag.ImGuiTableFlags;
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
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '_' &&
                            imGuiInputTextCallbackData.getEventChar() != '§'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString crackedUsername, crackedUUID, state, currentAccountData;

    private final ExecutorService executor;

    public AccountManagerImGuiMenu() {
        super("Account Manager");
        this.crackedUsername = new ImString(16);
        this.crackedUUID = new ImString();
        this.state = new ImString(200);
        this.currentAccountData = new ImString();
        this.resetState();
        this.executor = Executors.newSingleThreadExecutor();
    }

    private void resetState() {
        this.state.set("Waiting for input...");
    }

    private void delayedResetState() {
        try {
            Thread.sleep(10000);
        } catch (final InterruptedException ignored) {
        }
        this.resetState();
    }

    @Override
    public void render() {
        final float width = 1220, height = 630;
        ImGui.setNextWindowSizeConstraints(width - 700, height, width, height);
        if (ImGui.begin(
                "Account Manager##accountmanager",
                Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags()
        )) {
            final Session session = MinecraftClient.getInstance().getSession();
            ImGui.text("Current Account");
            ImGui.inputTextMultiline("##currentAccountData", this.currentAccountData, -1, 60, ImGuiInputTextFlags.ReadOnly);
            this.currentAccountData.set("Username: " + session.getUsername() + "\n" +
                    (session.getUuidOrNull() != null ? "UUID: " + session.getUuidOrNull() + "\n" : "") +
                    "Type: " + (session.getAccessToken().equals(CrackedAccount.ACCESSTOKEN) ? "Cracked" : "Premium")
            );
            ImGui.separator();
            ImGui.text("Accounts");
            final List<Account> accounts = Vandalism.getInstance().getConfigManager().getAccountConfig().getAccounts();
            final AccountsTableColumn[] accountsTableColumns = AccountsTableColumn.values();
            final int maxTableColumns = accountsTableColumns.length;
            ImGui.beginChild("##accountstablechild", -1, 200, true);
            if (ImGui.beginTable("accounts##accountstable", maxTableColumns,
                    ImGuiTableFlags.Borders |
                            ImGuiTableFlags.Resizable |
                            ImGuiTableFlags.RowBg |
                            ImGuiTableFlags.ContextMenuInBody
            )) {
                for (final AccountsTableColumn accountsTableColumn : accountsTableColumns) {
                    ImGui.tableSetupColumn(accountsTableColumn.normalName());
                }
                ImGui.tableHeadersRow();
                for (final Account account : accounts) {
                    ImGui.tableNextRow();
                    for (int i = 0; i < maxTableColumns; i++) {
                        ImGui.tableSetColumnIndex(i);
                        final AccountsTableColumn accountsTableColumn = accountsTableColumns[i];
                        switch (accountsTableColumn) {
                            case USERNAME -> {
                                ImGui.textWrapped(account.getUsername());
                            }
                            case UUID -> ImGui.textWrapped(account.getUuid().toString());
                            case TYPE -> ImGui.textWrapped(account.getType());
                            case ACTIONS -> {
                                ImGui.spacing();
                                final int buttonWidth = 0, buttonHeight = 28;
                                final String identifier = "##" + account.getUsername() + account.getUuid().toString() + i;
                                if (ImGui.button("login" + identifier + "login", buttonWidth, buttonHeight))
                                    this.executor.submit(() -> {
                                        try {
                                            account.login();
                                            this.state.set("Successfully logged into the " + account.getType() + " account: " + account.getUsername());
                                        } catch (final Throwable throwable) {
                                            Vandalism.getInstance().getLogger().error(
                                                    "Failed to log into the " + account.getType() + " account: " + account.getUsername(),
                                                    throwable
                                            );
                                            this.state.set(
                                                    "Failed to log into the " + account.getType() + " account: " + account.getUsername() + "\n" +
                                                            throwable
                                            );
                                        }
                                        this.delayedResetState();
                                    });
                                ImGui.sameLine();
                                if (ImGui.button("remove" + identifier + "remove", buttonWidth, buttonHeight)) {
                                    accounts.remove(account);
                                    Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                                }
                                ImGui.spacing();
                            }
                            default -> {
                            }
                        }
                    }
                }
                ImGui.endTable();
            }
            ImGui.endChild();
            ImGui.newLine();
            ImGui.separator();
            ImGui.text("Add Account");
            ImGui.setNextItemWidth(-300);
            ImGui.inputText("Cracked Username##accountmanagercrackedusername", this.crackedUsername,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    USERNAME_NAME_FILTER
            );
            ImGui.setNextItemWidth(-300);
            ImGui.inputText("Cracked UUID##accountmanagercrackeduuid", this.crackedUUID);
            final String usernameValue = this.crackedUsername.get();
            if (!usernameValue.isBlank() && usernameValue.length() > 2 && usernameValue.length() < 17) {
                final String originUUID = this.crackedUUID.get(), uuidValue;
                if (originUUID.isBlank()) {
                    uuidValue = Uuids.getOfflinePlayerUuid(usernameValue).toString();
                } else uuidValue = originUUID;
                if (ObjectTypeChecker.isUUID(uuidValue)) {
                    boolean contains = false;
                    for (final Account account : accounts) {
                        if (account.getUuid().toString().equalsIgnoreCase(uuidValue)) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        if (ImGui.button("Add Cracked Account##accountmanageraddcrackedaccount")) {
                            this.executor.submit(() -> {
                                final UUID realUUID = UUID.fromString(uuidValue);
                                accounts.add(new CrackedAccount(usernameValue, realUUID));
                                Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
                                this.crackedUsername.clear();
                                this.crackedUUID.clear();
                                this.state.set("Successfully added the cracked account to your account list: " + usernameValue);
                                this.delayedResetState();
                            });
                        }
                        ImGui.sameLine();
                    }
                }
            }
            if (ImGui.button("Add Microsoft Account##accountmanageraddmicrosoftaccount")) {
                this.executor.submit(() -> {
                    try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                        final StepMCProfile.MCProfile mcProfile = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(
                                httpClient,
                                new StepMsaDeviceCode.MsaDeviceCodeCallback(
                                        msaDeviceCode -> {
                                            this.state.set("Please enter the code " + msaDeviceCode.userCode() + " at " + msaDeviceCode.verificationUri() +
                                                    "\nThe code has been copied to your clipboard and the login page has been opened."
                                            );
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
                        this.state.set("Successfully added the microsoft account to your account list: " + mcProfile.name());
                    } catch (final Throwable throwable) {
                        Vandalism.getInstance().getLogger().error("Failed to log into a microsoft account.", throwable);
                        this.state.set("Failed to add the microsoft account to your account list.\n" + throwable);
                    }
                    this.delayedResetState();
                });
            }
            ImGui.separator();
            ImGui.text("State");
            ImGui.inputTextMultiline("##currentAccountLoginState", this.state, -1, 50, ImGuiInputTextFlags.ReadOnly);
            ImGui.end();
        }
    }

}
