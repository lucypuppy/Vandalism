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

package de.nekosarekawaii.vandalism.base;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.Feature;
import net.fabricmc.loader.api.FabricLoader;
import net.lenni0451.reflect.Enums;

import java.util.function.Consumer;

/**
 * Integration interface for Vandalism addons to be launched after Vandalism has been initialized.
 */
public interface VandalismAddonLauncher {

    /**
     * Launches the addon before Vandalism has been initialized.
     *
     * @param vandalism The Vandalism instance.
     */
    default void onPreLaunch(final Vandalism vandalism) {
    }

    /**
     * Launches the addon after Vandalism has been initialized and before the config files are loaded.
     *
     * @param vandalism The Vandalism instance.
     */
    void onLaunch(final Vandalism vandalism);

    /**
     * Launches the addon after both Vandalism and the config files have been loaded.
     *
     * @param vandalism The Vandalism instance.
     */
    default void onLateLaunch(final Vandalism vandalism) {
    }

    /**
     * Invokes the given consumer for all registered addons that implement this interface.
     *
     * @param consumer The consumer to invoke.
     */
    static void call(final Consumer<VandalismAddonLauncher> consumer) {
        for (final VandalismAddonLauncher entrypoint : FabricLoader.getInstance().getEntrypoints("vandalism:onLaunch", VandalismAddonLauncher.class)) {
            consumer.accept(entrypoint);
        }
    }

    default Feature.Category createFeatureCategory(final String name) {
        final Feature.Category category = Enums.newInstance(Feature.Category.class, name.toUpperCase(), Feature.Category.values().length,
                new Class[]{String.class}, new Object[]{name});
        Enums.addEnumInstance(Feature.Category.class, category);

        return category;
    }

}
