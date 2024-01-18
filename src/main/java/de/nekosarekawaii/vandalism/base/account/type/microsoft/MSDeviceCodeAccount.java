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
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.CompletableFuture;

public class MSDeviceCodeAccount extends AbstractMicrosoftAccount {

    private static final String OPEN_URL = "Please open the url: ";

    private static final AccountFactory FACTORY = new AccountFactory() {

        private String state;

        @Override
        public void displayFactory() {
            if (this.state != null && this.state.startsWith(OPEN_URL)) {
                final String[] split = this.state.split(OPEN_URL);
                if (split.length == 2) {
                    final String url = split[1];
                    if (ImUtils.subButton("Open URL")) {
                        Util.getOperatingSystem().open(url);
                    }
                    if (ImUtils.subButton("Copy URL")) {
                        mc.keyboard.setClipboard(url);
                    }
                }
            }
            ImGui.text(this.state == null ? "Click the button below to get a device code." : this.state);
        }

        @Override
        public CompletableFuture<AbstractAccount> make() {
            return CompletableFuture.supplyAsync(() -> {
                try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                    final StepFullJavaSession.FullJavaSession javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(httpClient, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
                        final String url = msaDeviceCode.getDirectVerificationUri();
                        this.state = OPEN_URL + url;
                        Util.getOperatingSystem().open(url);
                    }));
                    this.state = null;
                    final MSDeviceCodeAccount account = new MSDeviceCodeAccount();
                    account.initWithExistingSession(javaSession);
                    return account;
                } catch (Throwable t) {
                    this.state = "Failed to log into account: " + t.getMessage();
                    return null;
                }
            });
        }

    };

    public MSDeviceCodeAccount() {
        super("device-code", FACTORY);
    }

    public MSDeviceCodeAccount(final String tokenChain) {
        super("device-code", FACTORY, tokenChain);
    }

    @Override
    public AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep() {
        return MinecraftAuth.JAVA_DEVICE_CODE_LOGIN;
    }

}
