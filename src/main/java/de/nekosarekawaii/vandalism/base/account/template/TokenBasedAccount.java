/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.base.account.template;

import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.encryption.AESEncryptionUtil;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public abstract class TokenBasedAccount extends Account {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    private final String serviceUrl;
    private final String redeemUrl;

    private String token;

    public TokenBasedAccount(final String name, final String serviceUrl, final String redeemUrl) {
        super(name);

        this.serviceUrl = serviceUrl;
        this.redeemUrl = redeemUrl;
    }

    public TokenBasedAccount(final String name, final String serviceUrl, final String redeemUrl, final String token) {
        this(name, serviceUrl, redeemUrl);

        this.token = token;
        if (token != null && this.getEnvironment() == YggdrasilEnvironment.PROD.getEnvironment()) {
            throw new RuntimeException("You are using the production environment. This is not allowed.");
        }
    }

    public abstract Session fromResponse(final String response);

    public abstract TokenBasedAccount create(final String token);

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
                ImGui.text("Token");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() - 4f);
                ImGui.inputText("##accountToken", this.token, ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.Password);
                if (ImGui.button("Open Service", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                    Util.getOperatingSystem().open(TokenBasedAccount.this.serviceUrl);
                }
            }

            @Override
            public CompletableFuture<Account> make() {
                return CompletableFuture.supplyAsync(() -> create(this.token.get()));
            }

        };
    }

    @Override
    public void login0() throws Throwable {
        if (this.session != null) {
            // If we are already logged in, we don't need to do anything except reloading the session
            this.updateSession(this.session);
            return;
        }
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.redeemUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"token\":\"" + this.token + "\"}"))
                .build();
        this.updateSession(this.fromResponse(REQUESTER.send(request, HttpResponse.BodyHandlers.ofString()).body()));
    }

    @Override
    public void save0(final JsonObject mainNode) throws Throwable {
        mainNode.addProperty("token", AESEncryptionUtil.encrypt(this.getSession().getUsername(), this.token));
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
        this.token = AESEncryptionUtil.decrypt(this.getSession().getUsername(), mainNode.get("token").getAsString());
    }

}
