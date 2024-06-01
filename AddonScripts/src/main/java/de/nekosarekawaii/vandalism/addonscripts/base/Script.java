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

package de.nekosarekawaii.vandalism.addonscripts.base;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.feature.Feature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Script extends Feature implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final UUID uuid;
    private final File file;
    private final String version;
    private final String author;
    private final KeyBindValue keyBind;

    public Script(String name, String description, Category category, File file, String version, String author) {
        super(name, description, category);
        this.uuid = UUID.randomUUID();
        this.file = file;
        this.version = version;
        this.author = author;
        this.keyBind = new KeyBindValue(
                this,
                "Key Bind",
                "The key bind of this script."
        );
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public File getFile() {
        return this.file;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public KeyBindValue getKeyBind() {
        return this.keyBind;
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
