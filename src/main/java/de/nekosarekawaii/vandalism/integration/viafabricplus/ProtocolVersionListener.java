/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.viafabricplus;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.common.MSTimer;

/**
 * Feel free to improve this.
 */
public class ProtocolVersionListener extends Thread {

    private final MSTimer checkTimer = new MSTimer();

    public ProtocolVersionListener() {
        Vandalism.getInstance().setTargetVersion(ViaFabricPlusAccess.getTargetVersion());

        // Cancer fix.
        if (Vandalism.getInstance().getTargetVersion() == null) {
            Vandalism.getInstance().setTargetVersion(ProtocolVersion.v1_20_5);
            interrupt();
        }
    }

    @Override
    public void run() {
        // Let's recheck every second or so.
        if (checkTimer.hasReached(1000, true)) {
            Vandalism.getInstance().setTargetVersion(ViaFabricPlusAccess.getTargetVersion());
        }
    }
}
