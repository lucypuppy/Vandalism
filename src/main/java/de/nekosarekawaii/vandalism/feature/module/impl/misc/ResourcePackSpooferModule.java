/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.google.common.hash.HashCode;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Downloader;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackSpooferModule extends Module implements IncomingPacketListener {

    private final BooleanValue logStatus = new BooleanValue(
            this,
            "Log Resource Pack Status",
            "Logs resource pack status to the chat.",
            true
    );

    private final BooleanValue logResourcePackUrl = new BooleanValue(
            this,
            "Log Resource Pack URL",
            "Logs the Resource Pack URL to the chat.",
            true
    );

    private final List<PackEntry> packEntries;
    private ClientConnection connection;

    public ResourcePackSpooferModule() {
        super(
                "Resource Pack Spoofer",
                "Allows you to spoof the Resource Pack.",
                Category.MISC
        );
        this.packEntries = new ArrayList<>();
        this.connection = null;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final ResourcePackSendS2CPacket packet) {
            event.cancel();
            final String resourcePackUrl = packet.url();
            final UUID id = packet.id();
            final URL url = ClientCommonNetworkHandler.getParsedResourcePackUrl(resourcePackUrl);
            final String hash = packet.hash();

            if (url == null) {
                event.connection.send(new ResourcePackStatusC2SPacket(id, ResourcePackStatusC2SPacket.Status.INVALID_URL));
                if (this.logStatus.getValue()) {
                    ChatUtil.warningChatMessage(Text.literal("Received invalid resource pack URL from server: ").append(Text.literal(resourcePackUrl).formatted(Formatting.DARK_AQUA)), true);
                }
                return;
            }

            if (this.logResourcePackUrl.getValue()) {
                ChatUtil.infoChatMessage(Text.literal("Spoofed incoming resource pack").append(Text.literal(": ").formatted(Formatting.GRAY)).append(Text.literal(resourcePackUrl).styled(style -> style.withFormatting(Formatting.DARK_AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, resourcePackUrl)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click here to open the resource pack url.").formatted(Formatting.YELLOW))))));
            }

            this.add(id, new PackEntry(id, url, ServerResourcePackLoader.toHashCode(hash)));
        } else if (event.packet instanceof final ResourcePackRemoveS2CPacket packet) {
            event.cancel();
            packet.id().ifPresentOrElse(this::remove, this::removeAll);
        }
    }

    public final void remove(final UUID id) {
        final PackEntry packEntry = this.get(id);
        if (packEntry != null) {
            packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
            this.onPackChanged();
        }

    }

    public void removeAll() {
        for (final PackEntry packEntry : this.packEntries) {
            packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
        }

        this.onPackChanged();
    }

    @Nullable
    private PackEntry get(final UUID id) {
        return this.packEntries.stream().filter(packEntry -> !packEntry.isDiscarded() && packEntry.id.equals(id)).findFirst().orElse(null);
    }

    public final List<PackEntry> getActivatedPacks() {
        return this.packEntries.stream().filter(packEntry -> packEntry.status == ServerResourcePackManager.Status.ACTIVE && packEntry.translations != null).toList();
    }

    public void init(final ClientConnection connection) {
        this.connection = connection;
    }

    public void clear() {
        this.connection = null;
        this.packEntries.clear();
    }

    private void markReplaced(final UUID id) {
        this.packEntries.stream().filter(entry -> entry.id.equals(id)).forEach(entry -> entry.discard(ServerResourcePackManager.DiscardReason.SERVER_REPLACED));
    }

    private void add(final UUID id, final PackEntry pack) {
        this.markReplaced(id);
        this.packEntries.add(pack);
        this.accept(pack);

        this.onPackChanged();
    }

    private void accept(final PackEntry pack) {
        this.stateChanged(pack.id, PackStateChangeCallback.State.ACCEPTED);
        pack.accepted = true;
    }

    private void update() {
        final boolean unfinished = this.enqueueDownloads();
        if (!unfinished) {
            this.applyDownloadedPacks();
        }

        this.removeInactivePacks();
    }

    private void removeInactivePacks() {
        this.packEntries.removeIf((pack) -> {
            if (pack.status != ServerResourcePackManager.Status.INACTIVE) {
                return false;
            }

            if (pack.discardReason != null) {
                final PackStateChangeCallback.FinishState finishState = pack.discardReason.state;
                if (finishState != null) {
                    this.finish(pack.id, finishState);
                }

                return true;
            }

            return false;
        });
    }

    private void applyDownloadedPacks() {
        boolean reload = false;
        final List<PackEntry> availablePacks = new ArrayList<>();
        final List<PackEntry> unavailablePacks = new ArrayList<>();

        for (final PackEntry packEntry : this.packEntries) {
            if (packEntry.status == ServerResourcePackManager.Status.PENDING) {
                return;
            }

            boolean available = packEntry.accepted && packEntry.loadStatus == ServerResourcePackManager.LoadStatus.DONE && !packEntry.isDiscarded();
            if (available && packEntry.status == ServerResourcePackManager.Status.INACTIVE) {
                availablePacks.add(packEntry);
                reload = true;
            }

            if (packEntry.status == ServerResourcePackManager.Status.ACTIVE) {
                if (!available) {
                    reload = true;
                    unavailablePacks.add(packEntry);
                } else {
                    availablePacks.add(packEntry);
                }
            }
        }

        if (reload) {
            for (final PackEntry packEntry : availablePacks) {
                if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
                    packEntry.status = ServerResourcePackManager.Status.PENDING;
                }
            }

            for (final PackEntry packEntry : unavailablePacks) {
                packEntry.status = ServerResourcePackManager.Status.PENDING;
            }

            this.reload(availablePacks, unavailablePacks);
        }
    }

    private void reload(final List<PackEntry> availablePacks, final List<PackEntry> unavailablePacks) {
        boolean valid = true;

        if (!ResourcePackSpooferModule.valid(availablePacks.stream().map((pack) -> new ReloadScheduler.PackInfo(pack.id, pack.path)).toList())) {
            this.reloadFail(availablePacks);
            if (!ResourcePackSpooferModule.valid(availablePacks.stream().map((pack) -> new ReloadScheduler.PackInfo(pack.id, pack.path)).toList())) {
                valid = false;
            }
        }

        if (valid) {
            for (final PackEntry packEntry : availablePacks) {
                final Path path = packEntry.path;

                try (final ZipFile zipFile = new ZipFile(path.toFile())) {
                    final Map<String, Map<String, String>> translations = new HashMap<>();
                    final Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                    while (enumeration.hasMoreElements()) {
                        final ZipEntry entry = enumeration.nextElement();
                        String name = entry.getName();
                        if (name.startsWith("/")) {
                            name = name.substring(1);
                        }

                        final String[] split = name.split("/");

                        if (split.length != 4) {
                            continue;
                        }

                        final String file = split[3];
                        final String extension = ".json";

                        if (!split[0].equals("assets") || !split[2].equals("lang") || !file.endsWith(extension)) {
                            continue;
                        }

                        final String code = file.substring(0, file.length() - extension.length());

                        final Map<String, String> map = translations.computeIfAbsent(code, string -> new HashMap<>());
                        Language.load(zipFile.getInputStream(entry), map::put);
                    }

                    packEntry.translations = translations;
                } catch (final IOException ignored) { }
            }
        }

        this.reloadSuccess(availablePacks, unavailablePacks);
    }

    private void reloadSuccess(final List<PackEntry> availablePacks, final List<PackEntry> unavailablePacks) {
        for (final PackEntry packEntry : availablePacks) {
            packEntry.status = ServerResourcePackManager.Status.ACTIVE;
            if (packEntry.discardReason == null) {
                this.finish(packEntry.id, PackStateChangeCallback.FinishState.APPLIED);
            }
        }

        for (final PackEntry packEntry : unavailablePacks) {
            packEntry.status = ServerResourcePackManager.Status.INACTIVE;
        }

        this.onPackChanged();
    }

    private void reloadFail(final List<PackEntry> availablePacks) {
        availablePacks.clear();

        for (final PackEntry packEntry : this.packEntries) {
            switch (packEntry.status) {
                case INACTIVE -> packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
                case PENDING -> {
                    packEntry.status = ServerResourcePackManager.Status.INACTIVE;
                    packEntry.discard(ServerResourcePackManager.DiscardReason.ACTIVATION_FAILED);
                }
                case ACTIVE -> availablePacks.add(packEntry);
            }
        }

        this.onPackChanged();
    }

    private static boolean valid(final List<ReloadScheduler.PackInfo> info) {
        return mc.getServerResourcePackProvider().toProfiles(info) != null;
    }

    private boolean enqueueDownloads() {
        final List<PackEntry> list = new ArrayList<>();
        boolean incomplete = false;

        for (final PackEntry packEntry : this.packEntries) {
            if (!packEntry.isDiscarded() && packEntry.accepted) {
                if (packEntry.loadStatus != ServerResourcePackManager.LoadStatus.DONE) {
                    incomplete = true;
                }

                if (packEntry.loadStatus == ServerResourcePackManager.LoadStatus.REQUESTED) {
                    packEntry.loadStatus = ServerResourcePackManager.LoadStatus.PENDING;
                    list.add(packEntry);
                }
            }
        }

        if (!list.isEmpty()) {
            final Map<UUID, Downloader.DownloadEntry> map = new HashMap<>();

            for (final PackEntry packEntry : list) {
                map.put(packEntry.id, new Downloader.DownloadEntry(packEntry.url, packEntry.hashCode));
            }

            MinecraftClient.getInstance().getServerResourcePackProvider().manager.queuer.enqueue(map, (result) -> {
                this.onDownload(list, result);
            });
        }

        return incomplete;
    }

    private void onDownload(Collection<PackEntry> packs, Downloader.DownloadResult result) {
        if (!result.failed().isEmpty()) {
            for (final PackEntry packEntry : this.packEntries) {
                if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
                    if (result.failed().contains(packEntry.id)) {
                        packEntry.discard(ServerResourcePackManager.DiscardReason.DOWNLOAD_FAILED);
                    } else {
                        packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
                    }
                }
            }
        }

        for (final PackEntry packEntry : packs) {
            Path path = result.downloaded().get(packEntry.id);
            if (path != null) {
                packEntry.loadStatus = ServerResourcePackManager.LoadStatus.DONE;
                packEntry.path = path;
                if (!packEntry.isDiscarded()) {
                    this.stateChanged(packEntry.id, PackStateChangeCallback.State.DOWNLOADED);
                }
            }
        }

        this.onPackChanged();
    }

    private boolean currentlyRunning;
    private boolean shouldKeepRunning;

    public void onPackChanged() {
        this.shouldKeepRunning = true;
        if (!this.currentlyRunning) {
            this.currentlyRunning = true;
            MinecraftClient.getInstance().send(() -> {
                while (this.shouldKeepRunning) {
                    this.shouldKeepRunning = false;
                    ResourcePackSpooferModule.this.update();
                }

                this.currentlyRunning = false;
            });
        }

    }

    public void stateChanged(final UUID id, final PackStateChangeCallback.State state) {
        if (this.connection == null) {
            return; // Might happen if closed
        }

        this.connection.send(new ResourcePackStatusC2SPacket(id, switch (state) {
            case ACCEPTED -> ResourcePackStatusC2SPacket.Status.ACCEPTED;
            case DOWNLOADED -> ResourcePackStatusC2SPacket.Status.DOWNLOADED;
        }));
    }

    public void finish(final UUID id, final PackStateChangeCallback.FinishState state) {
        if (this.connection == null) {
            return; // Might happen if closed
        }

        this.connection.send(new ResourcePackStatusC2SPacket(id, switch (state) {
            case APPLIED -> ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED;
            case DOWNLOAD_FAILED -> ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD;
            case DECLINED -> ResourcePackStatusC2SPacket.Status.DECLINED;
            case DISCARDED -> ResourcePackStatusC2SPacket.Status.DISCARDED;
            case ACTIVATION_FAILED -> ResourcePackStatusC2SPacket.Status.FAILED_RELOAD;
        }));
    }

    public static class PackEntry {
        public final UUID id;
        public final URL url;
        @Nullable
        public final HashCode hashCode;
        @Nullable
        public Path path;
        @Nullable
        public ServerResourcePackManager.DiscardReason discardReason;
        public ServerResourcePackManager.LoadStatus loadStatus;
        public ServerResourcePackManager.Status status;
        public Map<String, Map<String, String>> translations;
        boolean accepted;

        PackEntry(UUID id, URL url, @Nullable HashCode hashCode) {
            this.loadStatus = ServerResourcePackManager.LoadStatus.REQUESTED;
            this.status = ServerResourcePackManager.Status.INACTIVE;
            this.id = id;
            this.url = url;
            this.hashCode = hashCode;
        }

        public void discard(ServerResourcePackManager.DiscardReason reason) {
            if (this.discardReason == null) {
                this.discardReason = reason;
            }

        }

        public boolean isDiscarded() {
            return this.discardReason != null;
        }
    }
}
