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

package de.nekosarekawaii.vandalism.integration.minigames;

import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.integration.minigames.config.MinigamesConfig;
import de.nekosarekawaii.vandalism.integration.minigames.gui.MinigamesClientWindow;
import de.nekosarekawaii.vandalism.integration.minigames.impl.mittebekommttritte.MitteBekommtTritteMinigame;
import de.nekosarekawaii.vandalism.util.storage.NamedStorage;
import org.jetbrains.annotations.Nullable;

public class MinigamesManager extends NamedStorage<Minigame> {

    private Minigame currentMinigame;

    public MinigamesManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        configManager.add(new MinigamesConfig(this));
        clientWindowManager.add(new MinigamesClientWindow(this));
        this.currentMinigame = null;
    }

    @Override
    public void init() {
        this.add(
                new MitteBekommtTritteMinigame()
        );
    }

    public Minigame getCurrentMinigame() {
        return this.currentMinigame;
    }

    public void setCurrentMinigame(@Nullable final Minigame minigame) {
        if (this.currentMinigame != null) {
            this.currentMinigame.onClose();
        }
        this.currentMinigame = minigame;
        if (this.currentMinigame != null) {
            this.currentMinigame.onStart();
        }
    }

}
