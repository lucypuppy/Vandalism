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

package de.nekosarekawaii.vandalism.feature.script.parse.info.impl;

import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.script.parse.info.IScriptInfo;

public class CategoryScriptInfo implements IScriptInfo<Feature.Category> {

    @Override
    public String tag() {
        return "category";
    }

    @Override
    public Feature.Category parse(final String line) throws Exception {
        try {
            return Feature.Category.valueOf(line.toUpperCase());
        } catch (Exception e) {
            throw new Exception("Invalid category!");
        }
    }

    @Override
    public Feature.Category defaultValue() {
        return Feature.Category.MISC;
    }

}
