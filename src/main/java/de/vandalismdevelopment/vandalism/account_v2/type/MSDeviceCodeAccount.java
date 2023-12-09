package de.vandalismdevelopment.vandalism.account_v2.type;

import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import de.vandalismdevelopment.vandalism.account_v2.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class MSDeviceCodeAccount extends AbstractMicrosoftAccount {
    private static final AccountFactory factory = new AccountFactory() {
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

                    account.setTokenChain(MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession).getAsString());
                    account.logIn();
                } catch (Throwable e) {
                    account.setStatus("Failed to login: " + e.getMessage());
                }
            }

            return account;
        }
    };

    public MSDeviceCodeAccount() {
        super("device-code", factory);
    }

    public MSDeviceCodeAccount(String tokenChain) {
        super("device-code", factory, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return MinecraftAuth.JAVA_DEVICE_CODE_LOGIN;
    }
}
