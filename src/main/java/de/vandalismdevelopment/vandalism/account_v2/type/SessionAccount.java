package de.vandalismdevelopment.vandalism.account_v2.type;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.array.ObjectTypeChecker;
import de.vandalismdevelopment.vandalism.account_v2.AbstractAccount;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class SessionAccount extends AbstractAccount {

    private String name;
    private String uuid;
    private String accessToken;
    private String xuid;
    private String clientId;

    public SessionAccount() {
        super("Session");
    }

    public SessionAccount(String name, String uuid, String accessToken, String xuid, String clientId) {
        this();
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.xuid = xuid;
        this.clientId = clientId;

        logIn0();
    }

    @Override
    public void logIn0() {
        if (!ObjectTypeChecker.isUUID(this.uuid)) {
            this.uuid = UUID.randomUUID().toString();
        }
        updateSession(new Session(name, UUID.fromString(uuid), accessToken, Optional.of(xuid), Optional.of(clientId), Session.AccountType.LEGACY));
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public void save0(JsonObject mainNode) {
        // No Op
    }

    @Override
    public void load0(JsonObject mainNode) {
        // No Op
    }

    @Override
    public AccountFactory factory() {
        return new AccountFactory() {

            private final ImString name = new ImString();
            private final ImString uuid = new ImString();
            private final ImString accessToken = new ImString();
            private final ImString xuid = new ImString();
            private final ImString clientId = new ImString();

            @Override
            public void displayFactory() {
                ImGui.inputText("Name", name, ImGuiInputTextFlags.CallbackResize);
                ImGui.inputText("UUID", uuid, ImGuiInputTextFlags.CallbackResize);
                ImGui.inputText("Access Token", accessToken, ImGuiInputTextFlags.CallbackResize);
                ImGui.inputText("XUID", xuid, ImGuiInputTextFlags.CallbackResize);
                ImGui.inputText("Client ID", clientId, ImGuiInputTextFlags.CallbackResize);
            }

            @Override
            public AbstractAccount make() {
                return new SessionAccount(name.get(), uuid.get(), accessToken.get(), xuid.get(), clientId.get());
            }

        };
    }

}
