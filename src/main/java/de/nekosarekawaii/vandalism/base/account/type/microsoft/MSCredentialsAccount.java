package de.nekosarekawaii.vandalism.base.account.type.microsoft;

import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepCredentialsMsaCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

public class MSCredentialsAccount extends AbstractMicrosoftAccount {
    private static final AccountFactory FACTORY = new AccountFactory() {
        private String state;

        private final ImString email = new ImString();
        private final ImString password = new ImString();

        @Override
        public void displayFactory() {
            ImGui.text(this.state == null ? "Please enter your credentials." : state);

            ImGui.inputText("Email", email, ImGuiInputTextFlags.CallbackResize);
            ImGui.inputText("Password", password, ImGuiInputTextFlags.CallbackResize);
        }

        @Override
        public CompletableFuture<AbstractAccount> make() {
            return CompletableFuture.supplyAsync(() -> {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = MinecraftAuth.JAVA_CREDENTIALS_LOGIN.getFromInput(httpClient, new StepCredentialsMsaCode.MsaCredentials(this.email.get(), this.password.get()));

                    final var account = new MSCredentialsAccount();
                    account.initWithExistingSession(javaSession);

                    return account;
                } catch (Throwable e) {
                    this.state = "Failed to login: " + e.getMessage();
                    return null;
                }
            });
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
