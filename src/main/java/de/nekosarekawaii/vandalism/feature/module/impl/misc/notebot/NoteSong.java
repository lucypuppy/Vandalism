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

package de.nekosarekawaii.vandalism.feature.module.impl.misc.notebot;

import lombok.Getter;
import net.raphimc.noteblocklib.format.nbs.model.NbsData;
import net.raphimc.noteblocklib.format.nbs.model.NbsHeader;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.model.SongView;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class NoteSong {

    /**
     * This class might be able to do some usage easier later. Don't remove it.
     */

    // the actual song
    private final Song<NbsHeader, NbsData, NbsNote> song;

    private final File file;

    // Needed note blocks and instruments
    private final Set<NbsNote> requirements = new HashSet<>();

    /**
     * Initializes a new song.
     *
     * @param song   the song instance from NoteBlockLib
     * @param file the file of the song
     */
    public NoteSong(final Song<NbsHeader, NbsData, NbsNote> song, final File file) {
        this.song = song;
        this.file = file;
        requirements.addAll(song.getView().getNotes().values().stream().flatMap(Collection::stream).distinct().toList());
    }

    /**
     * Gets the total notes of the song.
     *
     * @return total notes
     */
    public int getTotalNotes() {
        return this.song.getView().getNotes().values().
                stream().
                mapToInt(List::size).
                sum();
    }

    public NbsHeader getHeader() {
        return this.song.getHeader();
    }

    public SongView<NbsNote> getView() {
        return this.song.getView();
    }

}
