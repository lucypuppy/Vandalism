package de.vandalismdevelopment.vandalism.account_v2.type.microsoft;

import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import de.vandalismdevelopment.vandalism.account_v2.AccountFactory;
import de.vandalismdevelopment.vandalism.account_v2.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepCredentialsMsaCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

public class MSCredentialsAccount extends AbstractMicrosoftAccount {
    private static final AccountFactory FACTORY = new AccountFactory() {
        private final MSCredentialsAccount account = new MSCredentialsAccount();

        private final ImString email = new ImString();
        private final ImString password = new ImString();

        @Override
        public void displayFactory() {
            ImGui.inputText("Email", email, ImGuiInputTextFlags.CallbackResize);
            ImGui.inputText("Password", password, ImGuiInputTextFlags.CallbackResize);
        }

        @Override
        public AbstractAccount make() {
            if (account.getTokenChain() == null) {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = MinecraftAuth.JAVA_CREDENTIALS_LOGIN.
                            getFromInput(httpClient, new StepCredentialsMsaCode.MsaCredentials(this.email.get(), this.password.get()));

                    account.updateSessionAndTokenChain(javaSession);
                } catch (Throwable e) {
                    account.setStatus("Failed to login: " + e.getMessage());
                }
            }

            return account;
        }
    };

    public MSCredentialsAccount() {
        super("credentials", FACTORY);
    }

    public MSCredentialsAccount(String tokenChain) {
        super("credentials", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return MinecraftAuth.JAVA_CREDENTIALS_LOGIN;
    }
}
