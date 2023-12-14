package de.nekosarekawaii.vandalism.base.account.type.microsoft;

import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

public class MSDeviceCodeAccount extends AbstractMicrosoftAccount {

    private static final AccountFactory FACTORY = new AccountFactory() {
        private String state;

        @Override
        public void displayFactory() {
            ImGui.text(this.state == null ? "Click the button below to get a device code." : state);
        }

        @Override
        public CompletableFuture<AbstractAccount> make() {
            return CompletableFuture.supplyAsync(() -> {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final var javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                        final String url = msaDeviceCode.getDirectVerificationUri();
                        this.state = "Please open the url: " + url;
                        Util.getOperatingSystem().open(url);
                    }));

                    this.state = null;
                    final var account = new MSDeviceCodeAccount();
                    account.initWithExistingSession(javaSession);

                    return account;
                } catch (Throwable e) {
                    this.state = "Failed to login: " + e.getMessage();
                    return null;
                }
            });
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
