package de.vandalismdevelopment.vandalism.base.account.template;

import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.florianmichael.rclasses.io.WebUtils;
import de.vandalismdevelopment.vandalism.base.account.AbstractAccount;
import de.vandalismdevelopment.vandalism.base.account.AccountFactory;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;

public abstract class AbstractTokenBasedAccount extends AbstractAccount {

    private final String redeemUrl;
    private String token;

    public AbstractTokenBasedAccount(String name, String redeemUrl) {
        super(name);
        this.redeemUrl = redeemUrl;
    }

    public AbstractTokenBasedAccount(String name, String redeemUrl, String token) {
        this(name, redeemUrl);
        this.token = token;

        if (token != null && getEnvironment() == YggdrasilEnvironment.PROD.getEnvironment()) {
            throw new RuntimeException("You are using the production environment. This is not allowed.");
        }
    }

    public abstract Session fromResponse(final String response);
    public abstract AbstractTokenBasedAccount create(final String token);

    @Override
    public String getDisplayName() {
        if (this.getSession() != null) {
            return this.getSession().getUsername();
        }
        return this.token == null ? "None" : this.token;
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
            public AbstractAccount make() {
                return create(token.get());
            }
        };
    }

    @Override
    public void logIn0() throws Throwable {
        if (this.session != null) {
            updateSession(session); // If we are already logged in, we don't need to do anything except reloading the session
            return;
        }
        final JsonObject request = new JsonObject();
        request.addProperty("token", this.token);

        WebUtils.DEFAULT.withHeader("Content-Type", "application/json");
        final String response = WebUtils.DEFAULT.post(this.redeemUrl, request.toString());
        updateSession(fromResponse(response));
    }

    @Override
    public void save0(JsonObject mainNode) throws Throwable {
        mainNode.addProperty("token", this.token);
    }

    @Override
    public void load0(JsonObject mainNode) throws Throwable {
        this.token = mainNode.get("token").getAsString();
    }

}
