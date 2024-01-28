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

package de.nekosarekawaii.vandalism.base.account.template;

import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.common.StaticEncryptionUtil;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractTokenBasedAccount extends AbstractAccount {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    private final String redeemUrl;
    private String token;

    public AbstractTokenBasedAccount(final String name, final String redeemUrl) {
        super(name);
        this.redeemUrl = redeemUrl;
    }

    public AbstractTokenBasedAccount(final String name, final String redeemUrl, final String token) {
        this(name, redeemUrl);
        this.token = token;
        if (token != null && this.getEnvironment() == YggdrasilEnvironment.PROD.getEnvironment()) {
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
                ImGui.inputText("Token", this.token, ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.Password);
            }

            @Override
            public CompletableFuture<AbstractAccount> make() {
                return CompletableFuture.supplyAsync(() -> create(this.token.get()));
            }

        };
    }

    @Override
    public void logIn0() throws Throwable {
        if (this.session != null) {
            //If we are already logged in, we don't need to do anything except reloading the session
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
        mainNode.addProperty("token", StaticEncryptionUtil.encrypt(this.getSession().getUsername(), this.token));
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
        this.token = StaticEncryptionUtil.decrypt(this.getSession().getUsername(), mainNode.get("token").getAsString());
    }

}
