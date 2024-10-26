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

package de.nekosarekawaii.vandalism.feature;

import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import lombok.Getter;
import net.raphimc.vialoader.util.VersionRange;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Feature implements IName, MinecraftWrapper {

    private final String name;

    @Getter
    private final @Nullable String description;

    @Getter
    private final Category category;

    @Getter
    private final VersionRange supportedVersions;

    @Getter
    private boolean experimental;

    public Feature(String name, @Nullable String description, Category category) {
        this(name, description, category, null);
    }

    public Feature(String name, @Nullable String description, Category category, VersionRange supportedVersions) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.supportedVersions = supportedVersions;
        if (this.description != null && this.description.trim().isEmpty()) {
            throw new IllegalStateException("Description cannot be empty, use null instead.");
        }
    }

    public enum Category {

        COMBAT, EXPLOIT, MOVEMENT, RENDER, MISC;

        public String getName() {
            return StringUtils.normalizeEnumName(name());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void markExperimental() {
        this.experimental = true;
    }

}
