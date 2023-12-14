package de.nekosarekawaii.vandalism.base.account;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.base.account.config.AccountsConfig;
import de.nekosarekawaii.vandalism.base.account.gui.AccountsImWindow;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.gui.ImGuiManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccountManager extends Storage<AbstractAccount> {

    public static final Map<AbstractAccount, AccountFactory> ACCOUNT_TYPES = new LinkedHashMap<>();

    public AccountManager(final ConfigManager configManager, final ImGuiManager imGuiManager) {
        Arrays.asList(
                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount(),

                new SessionAccount(),
                new EasyMCAccount()
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));

        configManager.add(new AccountsConfig(this));
        imGuiManager.add(new AccountsImWindow(this));
    }

    private AbstractAccount firstAccount;
    private AbstractAccount currentAccount;

    @Override
    public void init() {
        final Session session = MinecraftClient.getInstance().getSession();
        firstAccount = currentAccount = new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );
    }

    public void logIn(final AbstractAccount account) throws Throwable {
        account.logIn();
        currentAccount = account;
    }

    public AbstractAccount getCurrentAccount() {
        return currentAccount;
    }

    public void logOut() throws Throwable {
        logIn(firstAccount);
    }

}
