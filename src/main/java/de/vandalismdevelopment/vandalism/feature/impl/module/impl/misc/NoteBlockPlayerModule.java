package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.event.WorldListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.gui.imgui.RenderInterface;
import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.EnumValue;
import de.vandalismdevelopment.vandalism.value.impl.RenderingValue;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.SongFormat;
import net.raphimc.noteblocklib.format.nbs.NbsDefinitions;
import net.raphimc.noteblocklib.format.nbs.model.NbsNote;
import net.raphimc.noteblocklib.model.Note;
import net.raphimc.noteblocklib.model.Song;
import net.raphimc.noteblocklib.player.ISongPlayerCallback;
import net.raphimc.noteblocklib.player.SongPlayer;
import net.raphimc.noteblocklib.util.Instrument;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class NoteBlockPlayerModule extends Module implements TickListener, RenderListener, WorldListener {

    private final EnumValue<InstrumentMode> instrumentMode = new EnumValue<>("Instrument Mode", "Select the instrument mode.", this, InstrumentMode.EXACT, InstrumentMode.ANY);

    private final EnumValue<InstrumentDetectMode> instrumentDetectMode = new EnumValue<>("Instrument Detect Mode", "Select an instrument detect mode. " + "Can be useful when server has a plugin that modifies note block states.", this, InstrumentDetectMode.BLOCK_STATE, InstrumentDetectMode.BELOW_BLOCK);

    private enum InstrumentMode implements EnumNameNormalizer {

        EXACT, ANY;

        private final String normalName;

        InstrumentMode() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

    }

    private interface InstrumentDetectFunction {

        net.minecraft.block.enums.Instrument detectInstrument(final BlockState noteBlock, final BlockPos pos);

    }

    private enum InstrumentDetectMode implements EnumNameNormalizer {

        BLOCK_STATE(((noteBlock, pos) -> noteBlock.get(NoteBlock.INSTRUMENT))), BELOW_BLOCK(((noteBlock, pos) -> {
            if (MinecraftClient.getInstance().world == null) return null;
            return MinecraftClient.getInstance().world.getBlockState(pos.down()).getInstrument();
        }));

        private final String normalName;

        private final InstrumentDetectFunction instrumentDetectFunction;

        InstrumentDetectMode(final InstrumentDetectFunction instrumentDetectFunction) {
            this.normalName = this.normalizeName(this.name());
            this.instrumentDetectFunction = instrumentDetectFunction;
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

        public InstrumentDetectFunction getInstrumentDetectFunction() {
            return this.instrumentDetectFunction;
        }

    }

    private enum ShapeMode implements EnumNameNormalizer {

        LINES, SIDES, BOTH;

        private final String normalName;

        ShapeMode() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

        public boolean lines() {
            return this == LINES || this == BOTH;
        }

        public boolean sides() {
            return this == LINES || this == BOTH;
        }

    }

    private enum Stage implements EnumNameNormalizer {

        NONE, LOADING_SONG, SETUP, TUNE, WAITING_TO_CHECK_NOTE_BLOCKS, PLAYING;

        private final String normalName;

        Stage() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

    }

    private enum PlayingMode implements EnumNameNormalizer {

        NONE, PREVIEW, NOTE_BLOCKS;

        private final String normalName;

        PlayingMode() {
            this.normalName = this.normalizeName(this.name());
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

    }

    private final ConcurrentHashMap<Pair<Byte, Byte>, BlockPos> scannedNoteBlocks;
    private File currentSongFile;
    private SongPlayer songPlayer;
    private boolean play;

    private final static File NOTE_BLOCK_SONGS_DIR = new File(Vandalism.getInstance().getDir(), "note-block-songs");
    private final ImString searchText = new ImString();
    private File searchFile;

    private void renderSongFile(final File dir, final File file) {
        final SongFormat songFormat = NoteBlockLib.getFormat(file.toPath());
        if (songFormat == null) return;
        final String identifier = dir.getName() + "-" + file.getName() + "noteblocksong";
        if (this.currentSongFile == null || !this.currentSongFile.getAbsolutePath().equals(file.getAbsolutePath())) {
            ImGui.separator();
            ImGui.text(file.getName());
            ImGui.separator();
            if (this.player() != null) {
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

    private final Value<RenderInterface> songSelector = new RenderingValue("Song Selector", "Select a song to play.", this, io -> {
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

    public NoteBlockPlayerModule() {
        super("Note Block Player", "Plays note block songs.", FeatureCategory.MISC, false, false);
        this.scannedNoteBlocks = new ConcurrentHashMap<>();
        this.reset();
        this.searchFile = null;
    }

    private void playClientSideSound(final SoundEvent soundEvent, final float volume, final float pitch) {
        this.mc().execute(() -> this.player().playSound(soundEvent, volume, pitch));
    }

    private void startBreakBlock(final byte instrument, final byte pitch) {
        this.mc().execute(() -> this.networkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.scannedNoteBlocks.get(new Pair<>(instrument, pitch)), Direction.UP)));
    }

    private void play(final File songFile, final boolean play) throws Exception {
        final Song<?, ?, ?> song = NoteBlockLib.readSong(songFile);
        this.reset();
        this.play = play;
        this.songPlayer = new SongPlayer(song.getView(), new ISongPlayerCallback() {

            @Override
            public void playNote(final Note note) {
                if (NoteBlockPlayerModule.this.networkHandler() == null || NoteBlockPlayerModule.this.player() == null) {
                    NoteBlockPlayerModule.this.disable();
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
                    if (soundEvent.get() != null) {
                        float pitch;
                        if (note instanceof final NbsNote nbsNote) {
                            final float keysPerOctave = NbsDefinitions.KEYS_PER_OCTAVE;
                            //TODO: 0.15F is some kind of constant value, but this should definitely be replaced.
                            pitch = (float) Math.pow(2.0F, ((nbsNote.getKey() + nbsNote.getPitch()) - keysPerOctave) / keysPerOctave) * 0.15F;
                        } else {
                            Vandalism.getInstance().getLogger().warn("Failed to handle note format: " + note.getClass().getSimpleName());
                            return;
                        }
                        if (play) {
                            NoteBlockPlayerModule.this.startBreakBlock(note.getInstrument(), (byte) pitch);
                        } else {
                            NoteBlockPlayerModule.this.playClientSideSound(soundEvent.get(), nbsNote.getVolume() / 100F, pitch);
                        }
                    } else {
                        Vandalism.getInstance().getLogger().warn("Failed to find mc instrument: " + instrument.name());
                    }
                } else {
                    Vandalism.getInstance().getLogger().warn("Failed to find instrument: " + note.getInstrument());
                }
            }

            @Override
            public void onFinished() {
                NoteBlockPlayerModule.this.reset();
            }

        });
        this.currentSongFile = songFile;
        this.enable();
    }

    private void reset() {
        this.scannedNoteBlocks.clear();
        this.currentSongFile = null;
        if (this.songPlayer != null) {
            this.songPlayer.stop();
        }
        this.songPlayer = null;
        this.play = false;
    }

    private void scanForNoteBlocks() {
        final int min = (int) (-this.interactionManager().getReachDistance()) - 2, max = (int) this.interactionManager().getReachDistance() + 2;
        for (int y = min; y < max; y++) {
            for (int x = min; x < max; x++) {
                for (int z = min; z < max; z++) {
                    final BlockPos pos = this.player().getBlockPos().add(x, y + 1, z);
                    final BlockState blockState = this.world().getBlockState(pos);
                    if (blockState.getBlock() != Blocks.NOTE_BLOCK) continue;
                    final Vec3d vec3d2 = Vec3d.ofCenter(pos);
                    final double sqDist = this.player().getEyePos().squaredDistanceTo(vec3d2);
                    if (sqDist > ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE) continue;
                    if (!this.world().getBlockState(pos.up()).isAir()) continue;
                    final net.minecraft.block.enums.Instrument instrument;
                    switch (this.instrumentMode.getValue()) {
                        case EXACT -> {
                            instrument = this.instrumentDetectMode.getValue().getInstrumentDetectFunction().detectInstrument(blockState, pos);
                        }
                        case ANY -> instrument = net.minecraft.block.enums.Instrument.HARP;
                        default -> instrument = null;
                    }
                    if (instrument == null) continue;
                    final byte pitch = blockState.get(NoteBlock.NOTE).byteValue();
                    this.scannedNoteBlocks.put(new Pair<>((byte) instrument.ordinal(), pitch), pos);
                }
            }
        }
    }

    @Override
    public void onTick() {
        if (this.songPlayer == null || this.player() == null) {
            this.disable();
            return;
        }
        if (this.play) {
            if (this.scannedNoteBlocks.isEmpty()) {
                this.scanForNoteBlocks();
                return;
            } else {
                //TODO: Finish this.
                for (final List<? extends Note> value : this.songPlayer.getSongView().getNotes().values()) {

                }
                for (final Map.Entry<Pair<Byte, Byte>, BlockPos> entry : this.scannedNoteBlocks.entrySet()) {

                }
            }
        }
        if (!this.songPlayer.isRunning()) {
            this.songPlayer.play();
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (this.songPlayer != null && this.currentSongFile != null) {
                int windowFlags = Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags() | ImGuiWindowFlags.AlwaysAutoResize;
                if (this.currentScreen() == null) {
                    windowFlags |= ImGuiWindowFlags.NoMove;
                    windowFlags |= ImGuiWindowFlags.NoInputs;
                }
                if (ImGui.begin("Note Block Player##noteblockplayerprogress", windowFlags)) {
                    ImGui.text((this.play ? "Playing" : "Previewing") + " Song");
                    ImGui.separator();
                    ImGui.text(this.currentSongFile.getName());
                    ImGui.separator();
                    ImGui.text("Progress");
                    final int current = this.songPlayer.getTick(), max = this.songPlayer.getSongView().getLength();
                    ImGui.progressBar((float) current / max);
                    ImGui.text(current + " / " + max);
                    ImGui.spacing();
                    if (!this.songPlayer.isPaused()) {
                        if (ImGui.button("Pause##pausenoteblocksong")) {
                            this.songPlayer.setPaused(true);
                        }
                    } else {
                        if (ImGui.button("Resume##resumenoteblocksong")) {
                            this.songPlayer.setPaused(false);
                        }
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Stop##stopnoteblocksong")) {
                        this.reset();
                    }
                    ImGui.end();
                }
            }
        });
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
        DietrichEvents2.global().subscribe(WorldLoadEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        DietrichEvents2.global().unsubscribe(WorldLoadEvent.ID, this);
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPreWorldLoad() {
        this.disable();
    }

}