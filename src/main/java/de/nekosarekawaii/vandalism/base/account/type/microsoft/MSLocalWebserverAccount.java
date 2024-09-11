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

package de.nekosarekawaii.vandalism.base.account.type.microsoft;

import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.template.MicrosoftAccount;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepLocalWebServer;

import java.util.concurrent.CompletableFuture;

import static de.nekosarekawaii.vandalism.integration.MinecraftAuthAccess.INGAME_ACCOUNT_SWITCHER;

public class MSLocalWebserverAccount extends MicrosoftAccount {

    private static final String OPEN_URL = "Please open the url: ";

    private static final AccountFactory FACTORY = new AccountFactory() {

        private String state;

        @Override
        public void displayFactory() {
            ImGui.textWrapped(this.state == null ? "Click the button below to get a device code." : this.state);
            ImGui.spacing();
            if (this.state != null && this.state.startsWith(OPEN_URL)) {
                final String[] split = this.state.split(OPEN_URL);
                if (split.length == 2) {
                    final String url = split[1];
                    if (ImGui.button("Open URL", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                        Util.getOperatingSystem().open(url);
                    }
                    if (ImGui.button("Copy URL", ImGui.getColumnWidth() - 4f, ImGui.getTextLineHeightWithSpacing())) {
                        mc.keyboard.setClipboard(url);
                    }
                }
            }
        }

        @Override
        public CompletableFuture<Account> make() {
            this.state = "";
            return CompletableFuture.supplyAsync(() -> {
                try {
                    final StepFullJavaSession.FullJavaSession javaSession = INGAME_ACCOUNT_SWITCHER.getFromInput(MinecraftAuth.createHttpClient(), new StepLocalWebServer.LocalWebServerCallback(localWebServer -> {
                        final String url = localWebServer.getAuthenticationUrl();
                        this.state = OPEN_URL + url;
                        Util.getOperatingSystem().open(url);
                    }));
                    this.state = null;
                    final MSLocalWebserverAccount account = new MSLocalWebserverAccount();
                    account.initialLogin(javaSession);
                    return account;
                } catch (Throwable t) {
                    this.state = "Failed to log into account: " + t.getMessage();
                    return null;
                }
            });

        }

    };

    public MSLocalWebserverAccount() {
        super("local-webserver", FACTORY);
    }

    public MSLocalWebserverAccount(final String tokenChain) {
        super("local-webserver", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return INGAME_ACCOUNT_SWITCHER;
    }

}
