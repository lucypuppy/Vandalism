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

package de.nekosarekawaii.vandalism.base.account.type.microsoft;

import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.base.account.template.AbstractMicrosoftAccount;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepCredentialsMsaCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

public class MSCredentialsAccount extends AbstractMicrosoftAccount {

    private static final AccountFactory FACTORY = new AccountFactory() {

        private String state;

        private final ImString email = new ImString();
        private final ImString password = new ImString();

        @Override
        public void displayFactory() {
            ImGui.text(this.state == null ? "Please enter your credentials." : this.state);
            ImGui.inputText("Email", this.email, ImGuiInputTextFlags.CallbackResize);
            ImGui.inputText("Password", this.password, ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.Password);
        }

        @Override
        public CompletableFuture<AbstractAccount> make() {
            return CompletableFuture.supplyAsync(() -> {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final StepFullJavaSession.FullJavaSession javaSession = MinecraftAuth.JAVA_CREDENTIALS_LOGIN.getFromInput(httpClient, new StepCredentialsMsaCode.MsaCredentials(this.email.get(), this.password.get()));
                    final MSCredentialsAccount account = new MSCredentialsAccount();
                    account.initWithExistingSession(javaSession);
                    return account;
                } catch (Throwable t) {
                    this.state = "Failed to log into account: " + t.getMessage();
                    return null;
                }
            });
        }

    };

    public MSCredentialsAccount() {
        super("credentials", FACTORY);
    }

    public MSCredentialsAccount(final String tokenChain) {
        super("credentials", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return MinecraftAuth.JAVA_CREDENTIALS_LOGIN;
    }

}
