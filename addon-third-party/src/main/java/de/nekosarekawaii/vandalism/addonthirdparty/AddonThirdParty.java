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

package de.nekosarekawaii.vandalism.addonthirdparty;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.namehistory.gui.NameHistoryClientWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.player.PlayerDiscoveryClientWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.server.ServerDiscoveryClientWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.server.ServerInfoClientWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.addonthirdparty.spotify.gui.SpotifyClientWindow;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;
import lombok.Getter;


public class AddonThirdParty implements VandalismAddonLauncher {

    @Getter
    private static AddonThirdParty instance;

    @Getter
    private SpotifyManager spotifyManager;

    @Override
    public void onLaunch(final Vandalism vandalism) {
        instance = this;
        this.spotifyManager = new SpotifyManager(vandalism.getConfigManager(), vandalism.getHudManager());

        vandalism.getClientWindowManager().add(
                new ServerInfoClientWindow(),
                new ServerDiscoveryClientWindow(),
                new PlayerDiscoveryClientWindow(),
                new NameHistoryClientWindow(),
                new SpotifyClientWindow()
        );
    }

}
