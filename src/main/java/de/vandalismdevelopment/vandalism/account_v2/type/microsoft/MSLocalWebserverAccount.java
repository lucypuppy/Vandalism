package de.vandalismdevelopment.vandalism.account_v2.type.microsoft;

import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import de.vandalismdevelopment.vandalism.account_v2.AccountFactory;
import de.vandalismdevelopment.vandalism.account_v2.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepLocalWebServer;
import net.raphimc.minecraftauth.step.msa.StepLocalWebServerMsaCode;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class MSLocalWebserverAccount extends AbstractMicrosoftAccount {

    public static final AbstractStep<?, StepFullJavaSession.FullJavaSession> JAVA_LOCAL_WEBSERVER_LOGIN = MinecraftAuth.builder()
            .withClientId(MicrosoftConstants.JAVA_TITLE_ID).withScope(MicrosoftConstants.SCOPE_TITLE_AUTH)
            .localWebServer()
            .withDeviceToken("Win32")
            .sisuTitleAuthentication(MicrosoftConstants.JAVA_XSTS_RELYING_PARTY)
            .buildMinecraftJavaProfileStep(true);

    private static final AccountFactory FACTORY = new AccountFactory() {
        private final MSLocalWebserverAccount account = new MSLocalWebserverAccount();

        private String state = "Click the button below to get a device code.";

        @Override
        public void displayFactory() {
            ImGui.text(state);
        }

        @Override
        public AbstractAccount make() {
            if (account.getTokenChain() == null) {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = JAVA_LOCAL_WEBSERVER_LOGIN.getFromInput(httpClient, new StepLocalWebServer.LocalWebServerCallback(localWebServer -> {
                        final String url = localWebServer.getAuthenticationUrl();
                        this.state = "Please open the url: " + url;
                        Util.getOperatingSystem().open(url);
                    }));

                    account.updateSessionAndTokenChain(javaSession);
                } catch (Throwable e) {
                    account.setStatus("Failed to login: " + e.getMessage());
                }
            }

            return account;
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
