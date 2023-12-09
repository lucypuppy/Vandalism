package de.vandalismdevelopment.vandalism.account_v2.type;

import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.net.URI;
import java.util.Optional;

public class EasyMCAccount extends AbstractAccount {

    private String token;

    public EasyMCAccount() {
        super("easymc");
    }

    public EasyMCAccount(final String token) throws Throwable {
        this();
        this.token = token;

        logIn();
    }

    @Override
    public Environment getEnvironment() {
        return new Environment("https://api.mojang.com", "https://sessionserver.easymc.io", "https://api.minecraftservices.com", "EasyMC");
    }

    @Override
    public void logIn0() throws Throwable {
        final WaybackAuthLib authenticator = new WaybackAuthLib(WaybackAuthLib.YGG_PROD, "", MinecraftClient.getInstance().getNetworkProxy());

        authenticator.logIn();
        if (authenticator.isLoggedIn()) {
            updateSession(new Session(authenticator.getCurrentProfile().getName(), authenticator.getCurrentProfile().getId(), authenticator.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
        }
    }

    @Override
    public void save0(JsonObject mainNode) throws Throwable {
        mainNode.addProperty("token", token);
    }

    @Override
    public void load0(JsonObject mainNode) throws Throwable {
        token = mainNode.get("token").getAsString();
    }

    @Override
    public String getDisplayName() {
        if (getSession() == null) {
            return "Unnamed Account";
        }
        return getSession().getUsername();
    }

    @Override
    public AccountFactory factory() {
        return new AccountFactory() {
            private final ImString token = new ImString();

            @Override
            public void displayFactory() {
                ImGui.inputText("Token", token, ImGuiInputTextFlags.CallbackResize);
            }

            @Override
            public AbstractAccount make() throws Throwable {
                return new EasyMCAccount(token.get());
            }
        };
    }
}
