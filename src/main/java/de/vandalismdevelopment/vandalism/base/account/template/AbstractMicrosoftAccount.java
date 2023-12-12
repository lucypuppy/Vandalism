package de.vandalismdevelopment.vandalism.base.account.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.florianmichael.rclasses.io.encryption.EncryptionUtils;
import de.vandalismdevelopment.vandalism.base.account.AbstractAccount;
import de.vandalismdevelopment.vandalism.base.account.AccountFactory;
import net.minecraft.client.session.Session;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Optional;

public abstract class AbstractMicrosoftAccount extends AbstractAccount {

    private final AccountFactory factory;
    private String tokenChain;
    private StepFullJavaSession.FullJavaSession session;

    public AbstractMicrosoftAccount(String name, AccountFactory factory) {
        super("Microsoft (" + name + ")"); // Java is bad, but we are worse
        this.factory = factory;
    }

    public AbstractMicrosoftAccount(String name, AccountFactory factory, String tokenChain) {
        super("Microsoft (" + name + ")");
        this.factory = factory;

        this.tokenChain = tokenChain;
    }

    public abstract AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep();

    @Override
    public void logIn0() throws Throwable {
        if (this.session != null) { // If we already got a session, we should use it right?
            if (this.tokenChain == null) {
                // Save the token chain if we don't have it yet
                this.tokenChain = getStep().toJson(session).toString();
            }
            final StepMCProfile.MCProfile profile = session.getMcProfile();
            updateSession(new Session(profile.getName(), profile.getId(), profile.getMcToken().getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        } else {
            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                // Get the token chain as a json object
                final JsonObject tokenChainNode = JsonParser.parseString(this.tokenChain).getAsJsonObject();

                // Refresh the token chain and get the new token chain
                final StepFullJavaSession.FullJavaSession fullJavaSession = getStep().refresh(httpClient, getStep().fromJson(tokenChainNode));
                initWithExistingSession(fullJavaSession);
            }
        }
    }

    public void initWithExistingSession(final StepFullJavaSession.FullJavaSession session) throws Throwable {
        this.session = session;
        logIn();
    }

    @Override
    public void save0(JsonObject mainNode) throws Throwable {
        mainNode.addProperty("tokenChain", EncryptionUtils.aes(EncryptionUtils.fromString(getDisplayName())).encrypt(tokenChain));
    }

    @Override
    public void load0(JsonObject mainNode) throws Throwable {
        tokenChain = EncryptionUtils.aes(EncryptionUtils.fromString(getDisplayName())).decrypt(mainNode.get("tokenChain").getAsString());
    }

    @Override
    public String getDisplayName() {
        if (getSession() == null) {
            return "Unnamed Account";
        }
        return getSession().getUsername();
    }

    public String getTokenChain() {
        return this.tokenChain;
    }

    @Override
    public AccountFactory factory() {
        return this.factory;
    }

}
