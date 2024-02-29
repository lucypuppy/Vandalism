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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.google.common.io.Files;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.RenderingValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Formatting;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.SongFormat;
import net.raphimc.noteblocklib.format.nbs.NbsDefinitions;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.model.SongView;
import net.raphimc.noteblocklib.player.ISongPlayerCallback;
import net.raphimc.noteblocklib.player.SongPlayer;
import net.raphimc.noteblocklib.util.Instrument;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class NoteBotModule extends AbstractModule implements PlayerUpdateListener {

    private File currentSongFile;
    private SongPlayer songPlayer;

    private final static File NOTE_BLOCK_SONGS_DIR = new File(Vandalism.getInstance().getRunDirectory(), "note-block-songs");
    private final ImString searchText = new ImString();
    private File searchFile;
    private boolean playing = false;

    private void renderSongFile(final File dir, final File file) {
        final SongFormat songFormat = NoteBlockLib.getFormat(file.toPath());
        if (songFormat == null) return;
        final String identifier = dir.getName() + "-" + file.getName() + "noteblocksong";
        if (this.currentSongFile == null || !this.currentSongFile.getAbsolutePath().equals(file.getAbsolutePath())) {
            ImGui.separator();
            ImGui.text(file.getName());
            ImGui.separator();
            if (this.mc.player != null) {
                if (ImGui.button("Play##" + identifier + "play")) {
                    try {
                        this.play(file, true);
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to play note block song: " + file.getName(), e);
                    }
                }
                ImGui.sameLine();
                if (ImGui.button("Preview##" + identifier + "preview")) {
                    try {
                        this.play(file, false);
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to preview note block song: " + file.getName(), e);
                    }
                }
            }
            ImGui.spacing();
        }
    }

    private void renderSongDir(final File dir) {
        if (!dir.isDirectory()) return;
        if (ImGui.treeNodeEx(dir.getName() + "##" + dir.getAbsolutePath() + "noteblocksongdirectory")) {
            final boolean searchIsEmpty = this.searchText.get().isBlank();
            final File[] files = dir.listFiles();
            if (files == null || files.length < 1) {
                ImGui.text("Empty directory.");
            } else if (files.length == 1 && files[0].isFile()) {
                final File file = files[0];
                if (this.currentSongFile != null) {
                    if (this.currentSongFile.getAbsoluteFile().equals(file.getAbsoluteFile())) {
                        ImGui.text(file.getName() + " is already playing.");
                    }
                } else if (searchIsEmpty || StringUtils.contains(file.getName(), this.searchText.get())) {
                    this.renderSongFile(dir, file);
                }
            } else {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        this.renderSongDir(file);
                        continue;
                    }
                    if (searchIsEmpty || StringUtils.contains(file.getName(), this.searchText.get())) {
                        this.renderSongFile(dir, file);
                    }
                }
            }
            ImGui.treePop();
        }
    }

    private void updateSearchFile(final File dir) {
        final File[] files = dir.listFiles();
        if (files == null || files.length < 1) return;
        for (final File file : files) {
            if (file.isDirectory()) {
                this.updateSearchFile(file);
                continue;
            }
            if (StringUtils.contains(file.getName(), this.searchText.get())) {
                this.searchFile = file;
                break;
            }
        }
    }

    private final EnumModeValue<Mode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "The mode to use.",
            Mode.COMMAND,
            Mode.values()
    );

    private enum Mode implements IName {

        COMMAND;

        private final String name;

        Mode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private final RenderingValue songSelector = new RenderingValue(this, "Song Selector", "Select a song to play.", io -> {
        if (NOTE_BLOCK_SONGS_DIR.exists()) {
            if (NOTE_BLOCK_SONGS_DIR.isFile()) {
                NOTE_BLOCK_SONGS_DIR.delete();
            }
        }
        if (!NOTE_BLOCK_SONGS_DIR.exists()) {
            NOTE_BLOCK_SONGS_DIR.mkdirs();
        }
        ImGui.inputText("##noteblocksongsearch", this.searchText);
        ImGui.separator();
        ImGui.spacing();
        if (this.searchText.get().isBlank()) {
            this.renderSongDir(NOTE_BLOCK_SONGS_DIR);
        } else if (this.searchFile != null) {
            if (StringUtils.contains(this.searchFile.getName(), this.searchText.get())) {
                this.renderSongDir(this.searchFile.getParentFile());
            } else this.searchFile = null;
        } else this.updateSearchFile(NOTE_BLOCK_SONGS_DIR);
    });

    public NoteBotModule() {
        super("Note Bot", "Plays note block songs.", Category.MISC);
        if (NOTE_BLOCK_SONGS_DIR.exists()) {
            if (NOTE_BLOCK_SONGS_DIR.isFile()) {
                NOTE_BLOCK_SONGS_DIR.delete();
            }
        }
        if (!NOTE_BLOCK_SONGS_DIR.exists()) {
            NOTE_BLOCK_SONGS_DIR.mkdirs();
        }
        this.deactivateAfterSessionDefault();
        this.reset();
        this.searchFile = null;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    private void play(final File songFile, final boolean play) throws Exception {
        this.playing = play;
        final Song<?, ?, ?> song = NoteBlockLib.readSong(songFile);
        this.reset();
        final SongView<?> view = song.getView();
        if (play) {
            final ClientPlayNetworkHandler networkHandler = this.mc.getNetworkHandler();
            if (networkHandler != null) {
                switch (this.mode.getValue()) {
                    case COMMAND -> networkHandler.sendChatCommand("bossbar add notebot \"Note Bot\"");
                    default -> {}
                }
            }
        }
        this.songPlayer = new SongPlayer(view, new ISongPlayerCallback() {

            @Override
            public void playNote(final Note note) {
                final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
                final ClientPlayerEntity player = mc.player;
                if (networkHandler == null || player == null) {
                    deactivate();
                    return;
                }
                final Instrument instrument = Instrument.fromNbsId(note.getInstrument());
                if (instrument != null) {
                    final AtomicReference<SoundEvent> soundEvent = new AtomicReference<>(null);
                    for (final net.minecraft.block.enums.Instrument mcInstrument : net.minecraft.block.enums.Instrument.values()) {
                        if (mcInstrument.ordinal() != instrument.mcId()) continue;
                        soundEvent.set(mcInstrument.getSound().value());
                        break;
                    }
                    final SoundEvent sound = soundEvent.get();
                    if (sound != null) {
                        final float pitch;
                        if (note instanceof final NbsNote nbsNote) {
                            final float keysPerOctave = NbsDefinitions.KEYS_PER_OCTAVE;
                            // 0.15F is some kind of constant value, but this should definitely be replaced
                            pitch = Math.min(2.0F, (float) Math.pow(2.0F, ((nbsNote.getKey() + nbsNote.getPitch()) - keysPerOctave) / keysPerOctave) * 0.15F);
                        } else {
                            ChatUtil.errorChatMessage("Note format " + note.getClass().getSimpleName() + " is not supported.");
                            return;
                        }
                        if (play) {
                            switch (mode.getValue()) {
                                case COMMAND -> {
                                    networkHandler.sendChatCommand("bossbar set minecraft:notebot players @a");
                                    final String[] styles = new String[]{ "notched_10", "notched_12", "notched_20", "notched_6", "progress" };
                                    networkHandler.sendChatCommand("bossbar set minecraft:notebot style " + styles[ThreadLocalRandom.current().nextInt(styles.length)]);
                                    networkHandler.sendChatCommand("playsound " + sound.getId().toString() + " master @a ~ ~ ~ " + 1 + " " + pitch);
                                    final int currentTick = songPlayer.getTick();
                                    final int maxTicks = view.getLength();
                                    final int progress = (int) ((currentTick / (float) maxTicks) * 100);
                                    networkHandler.sendChatCommand("bossbar set minecraft:notebot value " + progress);
                                    final String title = songFile.getName();
                                    if (!title.isEmpty()) {
                                        networkHandler.sendChatCommand(
                                                "bossbar set minecraft:notebot name \"" +
                                                        Formatting.GREEN + "Currently playing " +
                                                        Formatting.DARK_AQUA + Files.getNameWithoutExtension(title) + " " +
                                                        Formatting.YELLOW + ((currentTick * 50) / 1000) + "s" +
                                                        Formatting.GRAY + "/" +
                                                        Formatting.YELLOW + ((maxTicks * 50) / 1000) + "s\""
                                        );
                                    }
                                    final String color;
                                    if (progress < 50) {
                                        color = "green";
                                    }
                                    else if (progress < 75) {
                                        color = "yellow";
                                    }
                                    else {
                                        color = "red";
                                    }
                                    networkHandler.sendChatCommand("bossbar set minecraft:notebot color " + color);
                                }
                                default -> {}
                            }
                        } else {
                            mc.execute(() -> player.playSound(sound, nbsNote.getVolume() / 100F, pitch));
                        }
                    } else {
                        ChatUtil.errorChatMessage("Failed to find mc instrument: " + instrument.name());
                    }
                } else {
                    ChatUtil.errorChatMessage("Failed to find instrument: " + note.getInstrument());
                }
            }

            @Override
            public void onFinished() {
                reset();
            }

        });
        this.currentSongFile = songFile;
        this.activate();
    }

    private void reset() {
        this.currentSongFile = null;
        if (this.songPlayer != null) {
            this.songPlayer.stop();
        }
        this.songPlayer = null;
        if (this.playing) {
            final ClientPlayNetworkHandler networkHandler = this.mc.getNetworkHandler();
            switch (this.mode.getValue()) {
                case COMMAND -> {
                    if (networkHandler != null && this.mc.player != null) {
                        networkHandler.sendChatCommand("bossbar remove minecraft:notebot");
                    }
                }
                default -> {}
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.songPlayer == null || this.mc.player == null) {
            this.deactivate();
            return;
        }
        if (!this.songPlayer.isRunning()) {
            this.songPlayer.play();
        }
    }

}