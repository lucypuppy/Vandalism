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

package de.nekosarekawaii.vandalism.addonthirdparty;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.player.PlayerDiscoveryClientMenuWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.server.ServerDiscoveryClientMenuWindow;
import de.nekosarekawaii.vandalism.addonthirdparty.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.addonthirdparty.spotify.gui.SpotifyClientMenuWindow;

public class AddonThirdParty implements VandalismAddonLauncher {

    private static AddonThirdParty instance;

    private SpotifyManager spotifyManager;

    @Override
    public void onLaunch(Vandalism vandalism) {
        instance = this;
        this.spotifyManager = new SpotifyManager(vandalism.getConfigManager(), vandalism.getHudManager());

        vandalism.getClientMenuManager().add(
                new ServerDiscoveryClientMenuWindow(),
                new PlayerDiscoveryClientMenuWindow(),
                new SpotifyClientMenuWindow()
        );
    }

    public static AddonThirdParty getInstance() {
        return instance;
    }

    public SpotifyManager getSpotifyManager() {
        return spotifyManager;
    }

}
