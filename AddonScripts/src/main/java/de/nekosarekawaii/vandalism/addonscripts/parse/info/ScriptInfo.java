/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonscripts.parse.info;

import de.nekosarekawaii.vandalism.addonscripts.parse.info.impl.CategoryScriptInfo;
import de.nekosarekawaii.vandalism.addonscripts.parse.info.impl.bool.ExperimentalScriptInfo;
import de.nekosarekawaii.vandalism.addonscripts.parse.info.impl.string.AuthorScriptInfo;
import de.nekosarekawaii.vandalism.addonscripts.parse.info.impl.string.DescriptionScriptInfo;
import de.nekosarekawaii.vandalism.addonscripts.parse.info.impl.string.VersionScriptInfo;

public enum ScriptInfo {

    VERSION(new VersionScriptInfo()),
    AUTHOR(new AuthorScriptInfo()),
    DESCRIPTION(new DescriptionScriptInfo()),
    CATEGORY(new CategoryScriptInfo()),
    EXPERIMENTAL(new ExperimentalScriptInfo());

    private final IScriptInfo<?> IScriptInfo;


    ScriptInfo(final IScriptInfo<?> IScriptInfo) {
        this.IScriptInfo = IScriptInfo;
    }

    public String getTag() {
        return this.IScriptInfo.tag();
    }

    public <T> T parseValue(final String line) throws Exception {
        return (T) this.IScriptInfo.parse(line);
    }

    public <T> T getDefaultValue() {
        return (T) this.IScriptInfo.defaultValue();
    }

    public IScriptInfo<?> get() {
        return this.IScriptInfo;
    }

}
