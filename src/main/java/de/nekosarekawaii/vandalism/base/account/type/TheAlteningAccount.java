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

package de.nekosarekawaii.vandalism.base.account.type;

import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import de.nekosarekawaii.vandalism.base.account.template.SessionServiceAccount;

public class TheAlteningAccount extends SessionServiceAccount {

    public TheAlteningAccount() { // Java is bad, we are worse
        this(null);
    }

    public TheAlteningAccount(final String token) {
        super(
                "The Altening",
                token,
                "https://thealtening.com/free/free-minecraft-alts"
        );
    }

    @Override
    public SessionServiceAccount create(final String token) {
        return new TheAlteningAccount(token);
    }

    @Override
    public Environment getEnvironment() {
        return new Environment(
                "http://sessionserver.thealtening.com",
                "http://authserver.thealtening.com",
                this.getType()
        );
    }

    @Override
    public void save0(final JsonObject mainNode) throws Throwable {
    }

    @Override
    public void load0(final JsonObject mainNode) throws Throwable {
    }

}
