package de.vandalismdevelopment.vandalism.base.account.type.microsoft;

import de.vandalismdevelopment.vandalism.base.account.AbstractAccount;
import de.vandalismdevelopment.vandalism.base.account.AccountFactory;
import de.vandalismdevelopment.vandalism.base.account.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class MSDeviceCodeAccount extends AbstractMicrosoftAccount {
    private static final AccountFactory FACTORY = new AccountFactory() {
        private final MSDeviceCodeAccount account = new MSDeviceCodeAccount();

        private String state = "Click the button below to get a device code.";

        @Override
        public void displayFactory() {
            ImGui.text(state);
        }

        @Override
        public AbstractAccount make() {
            if (account.getTokenChain() == null) {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                        final String url = msaDeviceCode.getDirectVerificationUri();
                        this.state = "Please open the url: " + url;
                        Util.getOperatingSystem().open(url);
                    }));

                    account.initWithExistingSession(javaSession);
                } catch (Throwable e) {
                    account.setStatus("Failed to login: " + e.getMessage());
                }
            }

            return account;
        }
    };

    public MSDeviceCodeAccount() {
        super("device-code", FACTORY);
    }

    public MSDeviceCodeAccount(String tokenChain) {
        super("device-code", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return MinecraftAuth.JAVA_DEVICE_CODE_LOGIN;
    }
}
