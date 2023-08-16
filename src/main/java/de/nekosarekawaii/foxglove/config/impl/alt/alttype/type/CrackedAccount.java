package de.nekosarekawaii.foxglove.config.impl.alt.alttype.type;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import de.nekosarekawaii.foxglove.util.minecraft.SessionUtil;
import net.minecraft.client.util.Session;

import java.util.Optional;
import java.util.UUID;

public class CrackedAccount extends Account {

    public CrackedAccount(final String username, final UUID uuid) {
        super("cracked", username, uuid.toString());
    }

    @Override
    public void login() {
        SessionUtil.setSession(new Session(this.getUsername(), this.getUuid(), "-", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
        Foxglove.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }
}
