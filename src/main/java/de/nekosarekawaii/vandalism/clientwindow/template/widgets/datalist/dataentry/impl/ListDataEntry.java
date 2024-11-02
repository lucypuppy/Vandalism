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

package de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl;

import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import lombok.Getter;
import net.minecraft.util.Pair;

import java.util.concurrent.CopyOnWriteArrayList;

@Deprecated
@Getter
public class ListDataEntry extends DataEntry {

    private final CopyOnWriteArrayList<Pair<String, String>> list;

    public ListDataEntry(final CopyOnWriteArrayList<Pair<String, String>> list) {
        this.list = list;
    }

    public Pair<String, String> getFirst() {
        if (this.list.isEmpty()) {
            return null;
        }
        return this.list.getFirst();
    }

    public Pair<String, String> getSecond() {
        if (this.list.size() < 2) {
            return null;
        }
        return this.list.get(1);
    }

    public Pair<String, String> getThird() {
        if (this.list.size() < 3) {
            return null;
        }
        return this.list.get(2);
    }

    public Pair<String, String> getFourth() {
        if (this.list.size() < 4) {
            return null;
        }
        return this.list.get(3);
    }

    @Override
    public String getData() {
        final StringBuilder builder = new StringBuilder();
        for (final Pair<String, String> entry : this.list) {
            builder.append(entry.getLeft()).append(": ").append(entry.getRight()).append("\n");
        }
        String data = builder.toString();
        if (data.endsWith("\n")) {
            data = data.substring(0, data.length() - 1);
        }
        return data;
    }

}
