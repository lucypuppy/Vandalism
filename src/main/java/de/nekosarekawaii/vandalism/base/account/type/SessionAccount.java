/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.base.account.type;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.array.ObjectTypeChecker;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.common.StaticEncryptionUtil;
import de.nekosarekawaii.vandalism.util.common.UUIDUtil;
import de.nekosarekawaii.vandalism.util.game.NameGenerationUtil;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SessionAccount extends AbstractAccount {

    private static final ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '_' &&
                            imGuiInputTextCallbackData.getEventChar() != '§'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private String name;
    private String uuid;
    private String accessToken;
    private String xuid;
    private String clientId;

    public SessionAccount() {
        super("Session");
    }

    public SessionAccount(final String name, final String uuid, final String accessToken, final String xuid, final String clientId) {
        this();
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.xuid = xuid;
        this.clientId = clientId;
        this.logIn0();
    }

    @Override
    public void logIn0() {
        if (!ObjectTypeChecker.isUUID(this.uuid)) {
            String uuid = "";
            if (!this.name.isBlank()) {
                try {
                    uuid = UUIDUtil.getUUIDFromName(this.name);
                } catch (Exception e) {
                    Vandalism.getInstance().getLogger().error("Failed to get UUID from username: \"" + this.name + "\"", e);
                }
            }
            if (!ObjectTypeChecker.isUUID(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            this.uuid = uuid;
        }
        this.updateSession(new Session(this.name, UUID.fromString(this.uuid), this.accessToken, Optional.of(this.xuid), Optional.of(this.clientId), Session.AccountType.LEGACY));
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public void save0(final JsonObject mainNode) throws Throwable {
        //Every account stores the last input from updateSession(), but we still have to save and load our own account data
        mainNode.addProperty("name", this.name);
        mainNode.addProperty("uuid", this.uuid);
        mainNode.addProperty("accessToken", StaticEncryptionUtil.encrypt(this.name, this.accessToken));
        mainNode.addProperty("xuid", this.xuid);
        mainNode.addProperty("clientId", this.clientId);
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
        //Every account stores the last input from updateSession(), but we still have to save and load our own account data
        this.name = mainNode.get("name").getAsString();
        this.uuid = mainNode.get("uuid").getAsString();
        this.accessToken = StaticEncryptionUtil.decrypt(this.name, mainNode.get("accessToken").getAsString());
        this.xuid = mainNode.get("xuid").getAsString();
        this.clientId = mainNode.get("clientId").getAsString();
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
                ImGui.text("Name");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##sessionAccountName", this.name, ImGuiInputTextFlags.CallbackCharFilter, USERNAME_NAME_FILTER);
                if (ImGui.button("Use Random Name", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                    this.name.set(NameGenerationUtil.generateUsername());
                }
                ImGui.text("UUID");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##sessionAccountUUID", this.uuid, ImGuiInputTextFlags.CallbackResize);
                final String name = this.name.get();
                if (!name.isEmpty()) {
                    if (ImGui.button("Use Offline UUID", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                        this.uuid.set(Uuids.getOfflinePlayerUuid(name).toString());
                    }
                }
                ImGui.text("Access Token");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##sessionAccountAccess Token", this.accessToken, ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.Password);
                ImGui.text("XUID");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##sessionAccountXUID", this.xuid, ImGuiInputTextFlags.CallbackResize);
                ImGui.text("Client ID");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##sessionAccountClient ID", this.clientId, ImGuiInputTextFlags.CallbackResize);
            }

            @Override
            public CompletableFuture<AbstractAccount> make() {
                return CompletableFuture.completedFuture(new SessionAccount(this.name.get(), this.uuid.get(), this.accessToken.get(), this.xuid.get(), this.clientId.get()));
            }

        };

    }

}
