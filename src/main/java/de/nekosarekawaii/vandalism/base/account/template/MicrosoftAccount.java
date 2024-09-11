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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.encryption.AESEncryptionUtil;
import lombok.Getter;
import net.minecraft.client.session.Session;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class MicrosoftAccount extends Account {

    private static final Gson GSON = new Gson();

    private final AccountFactory factory;

    private String tokenChain;

    private StepFullJavaSession.FullJavaSession session;

    @Getter
    private long tokenExpirationTime = -1;

    @Getter
    private int refreshAttempts = 0;

    public MicrosoftAccount(final String name, final AccountFactory factory) {
        super("Microsoft (" + name + ")"); // Java is bad, but we are worse
        this.factory = factory;
    }

    public MicrosoftAccount(final String name, final AccountFactory factory, final String tokenChain) {
        super("Microsoft (" + name + ")");
        this.factory = factory;
        this.tokenChain = tokenChain;
    }

    public abstract AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep();

    private void updateTokenExpirationTime() {
        if (this.tokenChain != null) {
            final JsonObject jsonObject = GSON.fromJson(this.tokenChain, JsonObject.class);
            if (jsonObject.has("mcProfile")) {
                final JsonObject mcProfile = jsonObject.getAsJsonObject("mcProfile");
                if (mcProfile.has("mcToken")) {
                    final JsonObject mcToken = mcProfile.getAsJsonObject("mcToken");
                    if (mcToken.has("expireTimeMs")) {
                        this.tokenExpirationTime = mcToken.get("expireTimeMs").getAsLong();
                    }
                }
            }
        }
    }

    public void increaseRefreshAttempts() {
        this.refreshAttempts++;
    }

    public void refresh() throws Throwable {
        // Fixup the session if it's unset
        if (this.session == null) {
            final JsonObject tokenChainNode = JsonParser.parseString(this.tokenChain).getAsJsonObject();
            this.session = this.getStep().refresh(MinecraftAuth.createHttpClient(), this.getStep().fromJson(tokenChainNode));
            return;
        }

        // Refresh the token chain
        this.session = this.getStep().refresh(MinecraftAuth.createHttpClient(), this.session);
        this.tokenChain = this.getStep().toJson(this.session).toString();
        this.updateTokenExpirationTime();

        // Log the refresh
        final StringBuilder tokenExpirationData = new StringBuilder();
        final long timeLeft = this.tokenExpirationTime - System.currentTimeMillis();
        tokenExpirationData.append("new token expires in: ");
        tokenExpirationData.append(TimeUnit.MILLISECONDS.toHours(timeLeft));
        tokenExpirationData.append(" hours, ");
        tokenExpirationData.append(TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60);
        tokenExpirationData.append(" minutes and ");
        tokenExpirationData.append(TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60);
        tokenExpirationData.append(" seconds");
        Vandalism.getInstance().getLogger().info("Refreshed microsoft account {} {}", this.getDisplayName(), tokenExpirationData);
    }

    @Override
    public void login0() throws Throwable {
        // Save token chain if it is not already saved
        if (this.session != null && this.tokenChain == null) {
            this.tokenChain = this.getStep().toJson(this.session).toString();
        } else {
            // Get the token chain as a json object
            final JsonObject tokenChainNode = JsonParser.parseString(this.tokenChain).getAsJsonObject();
            // Refresh the token chain and get the new token chain
            this.session = this.getStep().refresh(MinecraftAuth.createHttpClient(), this.getStep().fromJson(tokenChainNode));
        }
        final StepMCProfile.MCProfile profile = session.getMcProfile();
        this.updateSession(new Session(profile.getName(), profile.getId(), profile.getMcToken().getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        this.updateTokenExpirationTime();
    }

    public void initialLogin(final StepFullJavaSession.FullJavaSession session) {
        this.session = session;
        this.login(); // Recall login flow to update session, otherwise get from tokenChain
    }

    @Override
    public void save0(final JsonObject mainNode) throws Throwable {
        mainNode.addProperty("tokenChain", AESEncryptionUtil.encrypt(this.getSession().getUsername(), this.tokenChain));
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
        this.tokenChain = AESEncryptionUtil.decrypt(this.getSession().getUsername(), mainNode.get("tokenChain").getAsString());
        this.updateTokenExpirationTime();
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
        return this.factory;
    }

}
