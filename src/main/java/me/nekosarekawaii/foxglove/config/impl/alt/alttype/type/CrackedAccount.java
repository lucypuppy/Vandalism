package me.nekosarekawaii.foxglove.config.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.util.Optional;
import java.util.UUID;

public class CrackedAccount extends Account {

    private final String uuidString;

    public CrackedAccount(final String username, final UUID uuid) {
        super("cracked", username);
        this.uuidString = uuid.toString();
    }

    @Override
    public void login() {
        MinecraftClient.getInstance().session = new Session(this.getUsername(), this.uuidString, "-", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        Foxglove.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }

    @Override
    public void onConfigSave(final JsonObject jsonObject) {
        jsonObject.addProperty("uuid", this.uuidString);
    }

    public String getUuidString() {
        return this.uuidString;
    }

}
