package de.vandalismdevelopment.vandalism.account_v2.type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Environment;
import de.vandalismdevelopment.vandalism.account_v2.template.AbstractTokenBasedAccount;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class EasyMCAccount extends AbstractTokenBasedAccount {

    public EasyMCAccount() { // Java is bad, we are worse
        this(null);
    }

    public EasyMCAccount(String token) {
        super("EasyMC", "https://api.easymc.io/v1/token/redeem", token);
    }

    @Override
    public AbstractTokenBasedAccount create(String token) {
        return new EasyMCAccount(token);
    }

    @Override
    public Environment getEnvironment() {
        return new Environment("https://api.mojang.com", "https://sessionserver.easymc.io", "https://api.minecraftservices.com", this.getName());
    }

    @Override
    public Session fromResponse(String response) {
        final JsonObject responseNode = JsonParser.parseString(response).getAsJsonObject();

        return new Session(
                responseNode.get("mcName").getAsString(),
                UUID.fromString(responseNode.get("uuid").getAsString()),
                responseNode.get("session").getAsString(),
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.LEGACY
        );
    }

}
