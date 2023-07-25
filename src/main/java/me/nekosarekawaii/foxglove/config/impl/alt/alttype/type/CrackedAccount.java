package me.nekosarekawaii.foxglove.config.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.util.minecraft.SessionUtil;
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

    @Override
    public void onConfigSave(final JsonObject jsonObject) {}

}
