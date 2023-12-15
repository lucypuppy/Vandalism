package de.nekosarekawaii.vandalism.base.account.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.StaticEncryptionUtil;
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
                initWithExistingSession(getStep().refresh(httpClient, getStep().fromJson(tokenChainNode)));
            }
        }
    }

    public void initWithExistingSession(final StepFullJavaSession.FullJavaSession session) throws Throwable {
        this.session = session;
        Vandalism.getInstance().getAccountManager().logIn(this);
    }

    @Override
    public void save0(JsonObject mainNode) throws Throwable {
        mainNode.addProperty("tokenChain", StaticEncryptionUtil.encrypt(getDisplayName(), tokenChain));
    }

    @Override
    public void load0(JsonObject mainNode) throws Throwable {
        tokenChain = StaticEncryptionUtil.decrypt(getDisplayName(), mainNode.get("tokenChain").getAsString());
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
