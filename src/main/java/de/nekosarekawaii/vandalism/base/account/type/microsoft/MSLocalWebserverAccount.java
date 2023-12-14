package de.nekosarekawaii.vandalism.base.account.type.microsoft;

import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepLocalWebServer;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

public class MSLocalWebserverAccount extends AbstractMicrosoftAccount {

    public static final AbstractStep<?, StepFullJavaSession.FullJavaSession> JAVA_LOCAL_WEBSERVER_LOGIN = MinecraftAuth.builder()
            .withClientId(MicrosoftConstants.JAVA_TITLE_ID).withScope(MicrosoftConstants.SCOPE_TITLE_AUTH)
            .localWebServer()
            .withDeviceToken("Win32")
            .sisuTitleAuthentication(MicrosoftConstants.JAVA_XSTS_RELYING_PARTY)
            .buildMinecraftJavaProfileStep(true);

    private static final AccountFactory FACTORY = new AccountFactory() {
        private String state;

        @Override
        public void displayFactory() {
            ImGui.text(state == null ? "Click the button below to get a device code." : state);
        }

        @Override
        public CompletableFuture<AbstractAccount> make() {
            return CompletableFuture.supplyAsync(() -> {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = JAVA_LOCAL_WEBSERVER_LOGIN.getFromInput(httpClient, new StepLocalWebServer.LocalWebServerCallback(localWebServer -> {
                        final String url = localWebServer.getAuthenticationUrl();
                        this.state = "Please open the url: " + url;
                        Util.getOperatingSystem().open(url);
                    }));

                    this.state = null;
                    final var account = new MSLocalWebserverAccount();
                    account.initWithExistingSession(javaSession);

                    return account;
                } catch (Throwable e) {
                    this.state = "Failed to login: " + e.getMessage();
                    return null;
                }
            });
        }
    };

    public MSLocalWebserverAccount() {
        super("local-webserver", FACTORY);
    }

    public MSLocalWebserverAccount(String tokenChain) {
        super("local-webserver", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return JAVA_LOCAL_WEBSERVER_LOGIN;
    }
}
