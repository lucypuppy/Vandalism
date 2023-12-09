package de.vandalismdevelopment.vandalism.account_v2.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import de.vandalismdevelopment.vandalism.util.EncryptionUtil;
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

    public AbstractMicrosoftAccount(String name, AccountFactory factory) {
        super(name);
        this.factory = factory;
    }

    public AbstractMicrosoftAccount(String name, AccountFactory factory, String tokenChain) {
        super(name);
        this.factory = factory;

        this.tokenChain = tokenChain;
    }

    public abstract AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep();

    @Override
    public void logIn0() throws Throwable {
        try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
            // Get the token chain as a json object
            final JsonObject tokenChainNode = JsonParser.parseString(this.tokenChain).getAsJsonObject();

            // Refresh the token chain and get the new token chain
            final StepFullJavaSession.FullJavaSession fullJavaSession = getStep().refresh(httpClient, getStep().fromJson(tokenChainNode));

            // Save the new token chain as a string
            this.tokenChain = getStep().toJson(fullJavaSession).getAsString();

            final StepMCProfile.MCProfile profile = fullJavaSession.getMcProfile();
            updateSession(new Session(profile.getName(), profile.getId(), profile.getMcToken().getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        }
    }

    @Override
    public void save0(JsonObject mainNode) throws Throwable {
        mainNode.addProperty("tokenChain", EncryptionUtil.encrypt(this.tokenChain, EncryptionUtil.getKeyFromPassword(this.getDisplayName())));
    }

    @Override
    public void load0(JsonObject mainNode) throws Throwable {
        tokenChain = EncryptionUtil.decrypt(mainNode.get("tokenChain").getAsString(), EncryptionUtil.getKeyFromPassword(getDisplayName()));
    }

    @Override
    public String getDisplayName() {
        if (getSession() == null) {
            return "Unnamed Account";
        }
        return getSession().getUsername();
    }

    public String getTokenChain() {
        return tokenChain;
    }

    public void setTokenChain(String tokenChain) {
        this.tokenChain = tokenChain;
    }

    @Override
    public AccountFactory factory() {
        return this.factory;
    }

}
