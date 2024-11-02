/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.base.config;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.util.storage.Storage;

public class ConfigManager extends Storage<Config<?>> {

    private final Thread autoSaveThread;

    public ConfigManager() {
        this.autoSaveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException ignored) {
                }
                if (FabricBootstrap.INITIALIZED && !FabricBootstrap.SHUTTING_DOWN) {
                    for (final Config<?> config : this.getList()) {
                        if (config.isModified()) {
                            config.save();
                            Vandalism.getInstance().getLogger().info("Auto saved config {}", config.getFile().getName());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void init() {
        this.getList().forEach(Config::load);
        this.autoSaveThread.start();
    }

    public void save() {
        this.getList().forEach(Config::save);
    }

}
