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
import com.google.gson.JsonParser;
import com.mojang.authlib.Environment;
import de.nekosarekawaii.vandalism.base.account.template.AbstractTokenBasedAccount;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;

public class EasyMCAccount extends AbstractTokenBasedAccount {

    public EasyMCAccount() { //Java is bad, we are worse
        this(null);
    }

    public EasyMCAccount(final String token) {
        super("EasyMC", "https://api.easymc.io/v1/token/redeem", token);
    }

    @Override
    public AbstractTokenBasedAccount create(final String token) {
        return new EasyMCAccount(token);
    }

    @Override
    public Environment getEnvironment() {
        return new Environment("https://sessionserver.easymc.io", "https://api.minecraftservices.com", this.getType());
    }

    @Override
    public Session fromResponse(final String response) {
        final JsonObject responseNode = JsonParser.parseString(response).getAsJsonObject();
        return new Session(
                responseNode.get("mcName").getAsString(),
                UUID.fromString(responseNode.get("uuid").getAsString()),
                responseNode.get("session").getAsString(),
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.LEGACY
        );
    }

    @Override
    protected void extraRender() {
        if (ImUtils.subButton("Get")) {
            Util.getOperatingSystem().open("https://easymc.io/get");
        }
    }

}
