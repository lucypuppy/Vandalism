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

package de.nekosarekawaii.vandalism.feature.module.impl.misc.notebot;

import com.google.common.io.Files;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.RenderingValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.viafabricplus.ViaFabricPlusAccess;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
import de.nekosarekawaii.vandalism.util.common.RandomUtils;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.render.imgui.ImUtils;
import imgui.ImGui;
import imgui.type.ImString;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.SongFormat;
import net.raphimc.noteblocklib.format.nbs.model.NbsData;
import net.raphimc.noteblocklib.format.nbs.model.NbsHeader;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.SongPlayer;
import net.raphimc.noteblocklib.player.SongPlayerCallback;
import net.raphimc.noteblocklib.util.Instrument;
import net.raphimc.noteblocklib.util.MinecraftDefinitions;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class NoteBotModule extends AbstractModule implements PlayerUpdateListener, IncomingPacketListener {

    private final EnumModeValue<Mode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "The mode to use.",
            Mode.COMMAND,
            Mode.values()
    );

    private final EnumModeValue<TuneMode> tuneMode = new EnumModeValue<>(
            this,
            "Tune Mode",
            "The tune mode to use.",
            TuneMode.SINGLE,
            TuneMode.values()
    ).visibleCondition(() -> mode.getValue() == Mode.BLOCKS);

    private final BooleanValue loopSong = new BooleanValue(
            this,
            "Loop song",
            "Puts the song into a loop.",
            true
    );

    private final BooleanValue soundShuffler = new BooleanValue(
            this,
            "Sound Shuffler",
            "Shuffles sounds for note block songs.",
            false
    ).visibleCondition(() -> this.mode.getValue() == Mode.COMMAND);

    private static final String BOSS_BAR_NAME = "minecraft:note_bot";
    private static final File NOTE_BLOCK_SONGS_DIR = new File(Vandalism.getInstance().getRunDirectory(), "note-block-songs");

    private final Map<BlockPos, Integer> cachedPositions = new HashMap<>();
    private final Map<BlockPos, Note> tunableBlocks = new HashMap<>();
    private final MSTimer tuningDelay = new MSTimer();
    private final HashMap<String, String> customInstruments = new HashMap<>();
    private final ImString searchText = new ImString();

    private State state = State.DISCOVERING;
    private NoteSong song;
    private SongPlayer player;
    private File searchFile;

    private final SongPlayerCallback callback = n -> {
        if (!(n instanceof NbsNote note)) {
            return;
        }
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        final ClientPlayerEntity player = this.mc.player;
        if (networkHandler == null || player == null) {
            deactivate();
            return;
        }
        final Instrument instrument = note.getInstrument();
        if (instrument != null) {
            final AtomicReference<SoundEvent> soundEvent = new AtomicReference<>(null);
            for (final net.minecraft.block.enums.Instrument mcInstrument : net.minecraft.block.enums.Instrument.values()) {
                if (mcInstrument.ordinal() != instrument.mcId()) continue;
                soundEvent.set(mcInstrument.getSound().value());
                break;
            }
            final SoundEvent sound = soundEvent.get();
            if (sound != null) {
                final float pitch = Math.min(2.0F, MinecraftDefinitions.mcKeyToMcPitch(MinecraftDefinitions.nbsKeyToMcKey(note.getKey())));
                if (this.state == State.PLAYING) {
                    switch (mode.getValue()) {
                        case PREVIEW -> this.mc.execute(() -> player.playSound(sound, 100F, pitch));
                        case COMMAND -> {
                            final String[] styles = new String[]{"progress", "notched_6", "notched_10", "notched_12", "notched_20"};
                            String currentStyle = "progress";
                            if (pitch <= 0.4f) currentStyle = styles[4];
                            else if (pitch <= 0.8f) currentStyle = styles[3];
                            else if (pitch <= 1.2f) currentStyle = styles[2];
                            else if (pitch <= 1.6f) currentStyle = styles[1];
                            final int currentTick = this.player.getTick();
                            final int maxTicks = this.song.getView().getLength();
                            final int progress = (int) ((currentTick / (float) maxTicks) * 100);
                            final String title = this.song.getFile().getName();
                            final String bossBarColor;
                            if (progress < 50) bossBarColor = "green";
                            else if (progress < 75) bossBarColor = "yellow";
                            else bossBarColor = "red";
                            String id = sound.getId().toString();
                            if (this.soundShuffler.getValue()) {
                                id = this.customInstruments.getOrDefault(id, id);
                            }
                            final List<String> commands = Arrays.asList(
                                    "execute as @a run playsound " + id + " master @a ~ ~ ~ 100 " + pitch,
                                    "bossbar set " + BOSS_BAR_NAME + " players @a",
                                    "bossbar set " + BOSS_BAR_NAME + " style " + currentStyle,
                                    "bossbar set " + BOSS_BAR_NAME + " color " + bossBarColor,
                                    "bossbar set " + BOSS_BAR_NAME + " value " + progress,
                                    "bossbar set " + BOSS_BAR_NAME + " name \"Currently playing " + (title.isEmpty() ? "a Song" : Files.getNameWithoutExtension(title)) + " " + this.getProgress() + "\""
                            );
                            for (final String command : commands) {
                                networkHandler.sendChatCommand(command);
                            }
                        }
                        case BLOCKS -> {
                            final int key = MinecraftDefinitions.nbsKeyToMcKey(note.getKey());
                            final BlockPos pos = getNoteBlock(note.getInstrument(), key);
                            if (pos == null || this.mc.interactionManager == null) return;
                            this.mc.interactionManager.attackBlock(pos, Direction.UP);
                        }
                        default -> {
                        }
                    }
                }
            } else {
                ChatUtil.errorChatMessage("Failed to find mc instrument: " + instrument.name());
            }
        } else {
            ChatUtil.errorChatMessage("Failed to find instrument: " + note.getInstrument());
        }
    };

    private String formatSeconds(final int target) {
        final StringBuilder builder = new StringBuilder();
        int minutes = target / 60;
        int hours = minutes / 60;
        final int seconds = target % 60;
        if (hours > 0) builder.append(hours).append(":");
        builder.append(minutes).append(":").append(String.format("%02d", seconds));
        return builder.toString();
    }

    private void renderSongFile(final File dir, final File file) {
        final SongFormat songFormat = NoteBlockLib.getFormat(file.toPath());
        if (songFormat == null) return;
        final String identifier = dir.getName() + "-" + file.getName() + "noteblocksong";
        if (this.song == null || !this.song.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
            ImGui.separator();
            ImGui.text(file.getName());
            if (this.mc.player != null) {
                if (ImGui.button("Play##" + identifier + "play", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                    try {
                        this.deactivate();
                        // Okay, so we're going to read the song extract info we might need and then recreate it as a NBS. This will make the playing at the end less cancer.
                        final Song<?, ?, ?> song = NoteBlockLib.readSong(file);
                        // We recreate the song as NBS here
                        this.song = new NoteSong((Song<NbsHeader, NbsData, NbsNote>) NoteBlockLib.createSongFromView(song.getView(), SongFormat.NBS), file);
                        this.activate();
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to play note block song: {}", file.getName(), e);
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
                if (this.song != null) {
                    if (this.song.getFile().getAbsoluteFile().equals(file.getAbsoluteFile())) {
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

    private String getProgress() {
        if (this.player == null || this.song == null) return "0:00 / 0:00";
        return formatSeconds((int) (this.player.getTick() / (this.player.getSongView().getSpeed()))) + " / " + formatSeconds((int) (this.song.getView().getLength() / (this.player.getSongView().getSpeed())));
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
        ImGui.spacing();
        final NoteSong currentSong = this.song;
        if (currentSong != null && currentSong.getFile() != null) {
            if (ImUtils.subButton("Stop##noteblockstop")) {
                this.deactivate();
            }
            ImGui.separator();
            ImGui.text("Currently playing");
            final String title = currentSong.getFile().getName();
            if (!title.isEmpty()) {
                ImGui.textWrapped("Name: " + Files.getNameWithoutExtension(title));
            }
            if (this.player != null) {
                ImGui.textWrapped("Duration: " + this.getProgress());
            }
        }
        ImGui.separator();
        ImGui.text("Search for a song");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##noteblocksongsearch", this.searchText);
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
        this.deactivateAfterSessionDefault();
        this.deactivateOnWorldLoadDefault();
        if (NOTE_BLOCK_SONGS_DIR.exists()) {
            if (NOTE_BLOCK_SONGS_DIR.isFile()) {
                NOTE_BLOCK_SONGS_DIR.delete();
            }
        }
        if (!NOTE_BLOCK_SONGS_DIR.exists()) {
            NOTE_BLOCK_SONGS_DIR.mkdirs();
        }
        this.searchFile = null;
    }

    /**
     * Catches incoming block event packets for versions below 1.12.2 because ViaVersion doesn't map them correctly ( - RK_01 ).
     *
     * @param event the event
     */
    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.packet instanceof final BlockEventS2CPacket packet && ViaFabricPlusAccess.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            if (packet.getBlock() instanceof NoteBlock || mc.world.getBlockState(packet.getPos()).getBlock() instanceof NoteBlock) {
                this.cachedPositions.put(packet.getPos(), packet.getData());
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!isEverythingTuned() && state == State.PLAYING) {
            state = State.TUNING;
            player.stop();
        }

        switch (state) {
            case DISCOVERING -> {
                if (this.mode.getValue() == Mode.BLOCKS) {
                    if (tunableBlocks.isEmpty()) {
                        ChatUtil.errorChatMessage("No tunable blocks collected, please try to restart the module.");
                        this.deactivate();
                        return;
                    }
                    tunableBlocks.keySet().forEach(p -> mc.interactionManager.attackBlock(p, Direction.UP));
                    state = State.TUNING;
                } else {
                    state = State.PLAYING;
                }
            }
            case TUNING -> {
                if (this.mode.getValue() == Mode.BLOCKS) {
                    final List<BlockPos> nonTunedPositions = tunableBlocks.keySet()
                            .stream()
                            .filter(p -> tunableBlocks.get(p).key() != getNote(p)).toList();

                    if (nonTunedPositions.isEmpty()) {
                        state = State.PLAYING;
                        player.play();
                        return;
                    }

                    if (!tuningDelay.hasReached(70, true))
                        return;

                    final BlockPos pos = nonTunedPositions.getFirst();
                    final Note note = tunableBlocks.get(pos);

                    // tune mode start
                    switch (tuneMode.getValue()) {
                        case SINGLE -> mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                                new BlockHitResult(Vec3d.ofCenter(pos, 1.0f), Direction.DOWN, pos, false));
                        case BATCH -> {
                            final int neededNote = note.key() < getNote(pos) ? note.key() + 25 : note.key();
                            final int tuningBatch = Math.min(22, neededNote - getNote(pos));

                            for (int i = 0; i < tuningBatch; i++) {
                                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                                        new BlockHitResult(Vec3d.ofCenter(pos, 1), Direction.DOWN, pos, false));
                            }
                        }
                    }
                    // tune mode end
                } else {
                    state = State.PLAYING;
                }
            }
            case PLAYING -> {
                if (this.soundShuffler.getValue() && this.customInstruments.isEmpty()) {
                    final List<String> validSounds = Registries.SOUND_EVENT.stream().map(SoundEvent::getId).filter(id -> !id.toString().toLowerCase().contains("music")).map(Object::toString).toList();
                    for (final net.minecraft.block.enums.Instrument mcInstrument : net.minecraft.block.enums.Instrument.values()) {
                        final String input = mcInstrument.getSound().value().getId().toString();
                        final String output = validSounds.get(RandomUtils.randomInt(0, validSounds.size()));
                        this.customInstruments.put(input, output);
                    }
                }
                if ((int) (this.player.getTick() / (this.player.getSongView().getSpeed())) >= (int) (this.song.getView().getLength() / (this.player.getSongView().getSpeed()))) {
                    if (this.loopSong.getValue()) {
                        this.player.setTick(0);
                    } else {
                        this.state = State.DONE;
                        this.deactivate();
                        ChatUtil.infoChatMessage("Song has finished playing.");
                    }
                } else if (!this.player.isRunning()) {
                    this.player.play();
                }
            }
            case DONE -> this.deactivate();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivate() {
        tunableBlocks.clear();
        cachedPositions.clear();
        customInstruments.clear();

        this.state = State.DISCOVERING;

        try {
            if (this.mode.getValue() == Mode.BLOCKS) {
                // feel free to improve this while still having spaces, thanks!
                final List<BlockPos> noteBlocks = scanNoteBlocks();
                for (NbsNote requirement : this.song.getRequirements()) {
                    final Instrument requiredInstrument = requirement.getInstrument();

                    for (BlockPos noteBlock : noteBlocks) {
                        if (tunableBlocks.containsKey(noteBlock))
                            continue;

                        final Instrument instrument = getInstrument(noteBlock);
                        final int key = MinecraftDefinitions.nbsKeyToMcKey(requirement.getKey());
                        if (requiredInstrument == instrument) {
                            tunableBlocks.put(noteBlock, new Note(instrument, key));
                            break;
                        }
                    }
                }

                ChatUtil.infoChatMessage("%d tunable blocks found".formatted(tunableBlocks.size()));
                ChatUtil.infoChatMessage("%d total blocks found".formatted(noteBlocks.size()));
            } else if (this.mode.getValue() == Mode.COMMAND) {
                final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
                if (networkHandler != null) {
                    networkHandler.sendChatCommand("bossbar add " + BOSS_BAR_NAME + " \"Note Bot\"");
                }
            }

            if (this.song == null) {
                this.deactivate();
                return;
            }
            player = new SongPlayer(this.song.getView(), callback);
        } catch (final Exception e) {
            ChatUtil.errorChatMessage("Failed to read song: " + e);
        }

        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        if (player != null) player.stop();
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
        if (this.mode.getValue() == Mode.COMMAND) {
            final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            if (networkHandler != null) {
                networkHandler.sendChatCommand("bossbar remove " + BOSS_BAR_NAME);
            }
        }
        this.song = null;
    }

    /**
     * Gets the instrument from a block.
     *
     * @param pos the position
     * @return the instrument
     */
    private Instrument getInstrument(BlockPos pos) {
        if (isNotNoteBlock(pos)) return Instrument.HARP;
        Instrument instrument = blockToInstrument(mc.world.getBlockState(pos.down()));
        return instrument != null ? instrument : Instrument.HARP;
    }

    /**
     * converts a state to instrument yay
     *
     * @param block the block state
     * @return your mom
     */
    private Instrument blockToInstrument(final BlockState block) {
        return Instrument.fromMcId((byte) block.getBlock().getSettings().instrument.ordinal());
    }

    /**
     * Checks if the block is a playable note block.
     *
     * @param pos the position to check
     * @return if it's a playable note block
     */
    private boolean isNotNoteBlock(final BlockPos pos) {
        return !(mc.world.getBlockState(pos).getBlock() instanceof NoteBlock) || !mc.world.getBlockState(pos.up()).isAir();
    }

    /**
     * Gets the current note / key from a block.
     *
     * @param pos the position
     * @return the key
     */
    public int getNote(BlockPos pos) {
        if (ViaFabricPlusAccess.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return cachedPositions.getOrDefault(pos, 0);
        }

        return isNotNoteBlock(pos) ? -1 : mc.world.getBlockState(pos).get(NoteBlock.NOTE);
    }

    /**
     * Gets a block position from an instrument and note.
     *
     * @param instrument the instrument
     * @param note       the note/key
     * @return the pos
     */
    private BlockPos getNoteBlock(final Instrument instrument, final int note) {
        return tunableBlocks.entrySet().stream()
                .filter(entry -> entry.getValue().instrument() == instrument && entry.getValue().key() == note)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if all noteblocks are tuned.
     *
     * @return if they're tuned
     */
    private boolean isEverythingTuned() {
        return tunableBlocks.entrySet().stream().allMatch(match -> {
            int note = getNote(match.getKey());
            return note != -1 && note == match.getValue().key();
        });
    }

    /**
     * Scans for note blocks in your area.
     *
     * @return list of note block positions
     */
    private List<BlockPos> scanNoteBlocks() {
        final List<BlockPos> scannedBlocks = new ArrayList<>();

        if (mc.interactionManager == null || mc.world == null || mc.player == null)
            return scannedBlocks;

        // TODO make it adjustable
        int min = -6;
        int max = 6;

        for (int y = min; y < max; y++) {
            for (int x = min; x < max; x++) {
                for (int z = min; z < max; z++) {
                    BlockPos pos = mc.player.getBlockPos().add(x, y + 1, z);

                    if (isNotNoteBlock(pos))
                        continue;

                    scannedBlocks.add(pos);
                }
            }
        }

        return scannedBlocks;
    }

    private enum Mode implements IName {

        PREVIEW, COMMAND, BLOCKS;

        private final String name;

        Mode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    @Getter
    private enum TuneMode implements IName {
        SINGLE, BATCH;

        private final String name;

        TuneMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }
    }


    private enum State {
        DISCOVERING, TUNING, PLAYING, DONE
    }

}