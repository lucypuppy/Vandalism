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

package de.nekosarekawaii.vandalism.feature;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public abstract class Feature implements IName, MinecraftWrapper {

    private final String name;
    private final String description;
    private final Category category;
    private final VersionRange supportedVersions;
    private boolean experimental;

    public Feature(String name, String description, Category category) {
        this(name, description, category, null);
    }

    public Feature(String name, String description, Category category, VersionRange supportedVersions) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.supportedVersions = supportedVersions;
    }

    public enum Category {

        DEVELOPMENT, COMBAT, EXPLOIT, MOVEMENT, RENDER, MISC;

        public String getName() {
            return StringUtils.normalizeEnumName(name());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public void markExperimental() {
        this.experimental = true;
    }

    public VersionRange getSupportedVersions() {
        return this.supportedVersions;
    }

    public boolean isSupportedVersion(final VersionEnum version) {
        //Using null as a wildcard instead of VersionRange.all() is faster
        if (this.supportedVersions == null) {
            return true;
        }
        return this.supportedVersions.contains(version);
    }

}
