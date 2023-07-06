package me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.Account;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.util.Optional;

public class CrackedAccount extends Account {

    public CrackedAccount(final String username) {
        super("cracked", username);
    }

    @Override
    public boolean login() {
        MinecraftClient.getInstance().session = new Session(this.getUsername(), this.getUsername(), "-",
                Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);

        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        System.out.println("Logged in with " + this.getUsername());
        return true;
    }

}
