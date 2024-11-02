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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.nekosarekawaii.vandalism.Vandalism;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Config<T extends JsonElement> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private int cachedConfigHash = 0;

    private final Class<T> nodeType;

    @Getter
    private final File file;

    public Config(final Class<T> nodeType, final String name) {
        this.nodeType = nodeType;
        this.file = new File(Vandalism.getInstance().getRunDirectory(), name + ".json");
    }

    public void save() {
        this.save(this.file);
    }

    public boolean save(final File file) {
        final String fileName = file.getName();
        try {
            file.delete();
            file.createNewFile();
            try (final FileWriter fw = new FileWriter(file)) {
                final String currentConfig = this.asString();
                this.cachedConfigHash = currentConfig.hashCode();
                fw.write(currentConfig);
                fw.flush();
                return true;
            } catch (final Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to save config {}", fileName, e);
            }
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to create config {}", fileName, e);
        }
        return false;
    }

    public void load() {
        this.load(this.file);
    }

    public boolean load(final File file) {
        if (file.exists()) {
            try (final FileReader fr = new FileReader(file)) {
                this.load0(GSON.fromJson(fr, this.nodeType));
                this.cachedConfigHash = this.asString().hashCode();
                return true;
            } catch (final Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load config {}", file.getName(), e);
            }
        }
        return false;
    }

    public abstract T save0();

    public abstract void load0(final T mainNode);

    public String asString() {
        return GSON.toJson(this.save0());
    }

    public boolean isModified() {
        return this.cachedConfigHash != this.asString().hashCode();
    }

}
