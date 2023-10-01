package de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.Account;
import de.vandalismdevelopment.vandalism.util.SessionUtil;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class CrackedAccount extends Account {

    public CrackedAccount(final String username, final UUID uuid) {
        super("cracked", username, uuid);
    }

    @Override
    public void login() {
        SessionUtil.setSession(new Session(this.getUsername(), this.getUuid(), "-", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
        Vandalism.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }

}
