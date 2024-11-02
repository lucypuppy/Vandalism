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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Mixin(MultiplayerServerListWidget.class)
public abstract class MixinMultiplayerServerListWidget {

    @Mutable
    @Final
    @Shadow
    static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL;

    @Shadow
    public List<MultiplayerServerListWidget.ServerEntry> servers;

    @Unique
    private static final int vandalism$MAX_THREADS = 5; // Turned out to be stable in most scenarios

    @Unique
    private static boolean vandalism$initialized = false;

    @Inject(method = "updateEntries", at = @At("HEAD"))
    private void clearThreadPool(CallbackInfo ci) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (!enhancedServerListSettings.enhancedServerList.getValue() || !enhancedServerListSettings.handleThreadPoolOverloads.getValue()) {
            return;
        }
        if (!vandalism$initialized) {
            vandalism$initialized = true;
            vandalism$clearServerPingerThreadPool();
        }
        if (SERVER_PINGER_THREAD_POOL.getActiveCount() >= vandalism$MAX_THREADS) {
            vandalism$clearServerPingerThreadPool();
        }
    }

    @Unique
    private void vandalism$clearServerPingerThreadPool() {
        SERVER_PINGER_THREAD_POOL.shutdownNow();

        SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(servers.size() + vandalism$MAX_THREADS,
                new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
    }

}