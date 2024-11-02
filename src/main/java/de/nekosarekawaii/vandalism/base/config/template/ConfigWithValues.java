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

package de.nekosarekawaii.vandalism.base.config.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.Config;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;

import java.util.List;

public class ConfigWithValues extends Config<JsonObject> {

    private final List<? extends ValueParent> keys;

    public ConfigWithValues(final String name, final List<? extends ValueParent> keys) {
        super(JsonObject.class, name);
        this.keys = keys;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        for (final ValueParent key : this.keys) {
            final JsonObject keyNode = new JsonObject();
            saveValues(keyNode, key.getValues());
            mainNode.add(key.getName(), keyNode);
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        for (final ValueParent key : this.keys) {
            final String keyName = key.getName();
            if (!mainNode.has(keyName)) {
                continue;
            }
            final JsonObject keyNode = mainNode.getAsJsonObject(keyName);
            if (keyNode != null) {
                loadValues(keyNode, key.getValues());
            }
        }
    }

    public static void saveValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            value.save(targetNode);
        }
    }

    private static String getValueTree(final Value<?> value) {
        final ValueParent parent = value.getParent();
        if (parent instanceof final ValueGroup valueGroup) {
            return getValueTree(valueGroup) + "/" + value.getName();
        } else {
            return value.getName();
        }
    }

    public static void loadValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            if (!targetNode.has(value.getName())) {
                continue;
            }
            try {
                value.load(targetNode);
            }
            catch (Throwable t) {
                Vandalism.getInstance().getLogger().error("Failed to load value: {}", getValueTree(value), t);
            }
        }
    }

}
