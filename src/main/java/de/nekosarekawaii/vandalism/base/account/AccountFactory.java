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

package de.nekosarekawaii.vandalism.base.account;

import java.util.concurrent.CompletableFuture;

/**
 * Creates a new account and displays the interface for it.
 */
public interface AccountFactory {

    /**
     * Should be used to draw a ImGui based interface for the account.
     */
    void displayFactory();

    /**
     * Creates a new account based on the input from the interface.
     *
     * @return The created account.
     */
    CompletableFuture<Account> make();

}
