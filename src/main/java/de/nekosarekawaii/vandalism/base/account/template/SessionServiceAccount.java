/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class SessionServiceAccount extends Account {

    private final String token;
    private final String serviceUrl;

    public SessionServiceAccount(final String name, final String token, final String serviceUrl) {
        super(name);
        this.token = token;
        if (token != null && this.getEnvironment() == YggdrasilEnvironment.PROD.getEnvironment()) {
            throw new RuntimeException("You are using the production environment. This is not allowed.");
        }
        this.serviceUrl = serviceUrl;
    }

    public abstract SessionServiceAccount create(final String token);

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
                    Util.getOperatingSystem().open(SessionServiceAccount.this.serviceUrl);
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
        final WaybackAuthLib auth = new WaybackAuthLib(this.getEnvironment().servicesHost());
        auth.setUsername(this.token);
        auth.setPassword(" ");
        auth.logIn();
        // TODO: Fix skin rendering caused by service
        this.updateSession(new Session(
                auth.getCurrentProfile().getName(),
                auth.getCurrentProfile().getId(),
                auth.getAccessToken(),
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.MOJANG
        ));
    }

}
