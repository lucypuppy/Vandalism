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
import com.google.gson.JsonParser;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountFactory;
import de.nekosarekawaii.vandalism.util.common.StaticEncryptionUtil;
import net.minecraft.client.session.Session;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;

import java.util.Optional;

public abstract class AbstractMicrosoftAccount extends AbstractAccount {

    private final AccountFactory factory;
    private String tokenChain;
    private StepFullJavaSession.FullJavaSession session;

    public AbstractMicrosoftAccount(final String name, final AccountFactory factory) {
        super("Microsoft (" + name + ")"); //Java is bad, but we are worse
        this.factory = factory;
    }

    public AbstractMicrosoftAccount(final String name, final AccountFactory factory, final String tokenChain) {
        super("Microsoft (" + name + ")");
        this.factory = factory;
        this.tokenChain = tokenChain;
    }

    public abstract AbstractStep<?, StepFullJavaSession.FullJavaSession> getStep();

    @Override
    public void logIn0() throws Throwable {
        if (this.session != null) { //If we already got a session, we should use it right?
            if (this.tokenChain == null) {
                //Save the token chain if we don't have it yet
                this.tokenChain = this.getStep().toJson(this.session).toString();
            }
            final StepMCProfile.MCProfile profile = this.session.getMcProfile();
            this.updateSession(new Session(profile.getName(), profile.getId(), profile.getMcToken().getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        } else {
            //Get the token chain as a json object
            final JsonObject tokenChainNode = JsonParser.parseString(this.tokenChain).getAsJsonObject();
            //Refresh the token chain and get the new token chain
            this.initWithExistingSession(this.getStep().refresh(MinecraftAuth.createHttpClient(), this.getStep().fromJson(tokenChainNode)));
        }
    }

    public void initWithExistingSession(final StepFullJavaSession.FullJavaSession session) throws Throwable {
        this.session = session;
        Vandalism.getInstance().getAccountManager().logIn(this);
    }

    @Override
    public void save0(final JsonObject mainNode) throws Throwable {
        mainNode.addProperty("tokenChain", StaticEncryptionUtil.encrypt(this.getSession().getUsername(), this.tokenChain));
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
        this.tokenChain = StaticEncryptionUtil.decrypt(this.getSession().getUsername(), mainNode.get("tokenChain").getAsString());
    }

    @Override
    public String getDisplayName() {
        if (getSession() == null) {
            return "Unnamed Account";
        }
        return getSession().getUsername();
    }

    public String getTokenChain() {
        return this.tokenChain;
    }

    @Override
    public AccountFactory factory() {
        return this.factory;
    }

}
