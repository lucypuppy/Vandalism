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

package de.nekosarekawaii.vandalism.util.render.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import lombok.Getter;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class PlayerSkinRenderer implements MinecraftWrapper {

    private static final ExecutorService SKIN_LOADER = Executors.newSingleThreadExecutor();

    private int glId;
    private Identifier skin;

    public PlayerSkinRenderer(final UUID uuid) {
        this.glId = -1;
        this.skin = null;
        CompletableFuture.supplyAsync(() -> {
            final ProfileResult result = mc.getSessionService().fetchProfile(uuid, false);
            if (result == null) return null;
            return result.profile();
        }, SKIN_LOADER).thenComposeAsync(profile -> {
            if (profile == null) return CompletableFuture.completedFuture(DefaultSkinHelper.getSkinTextures(uuid));
            return mc.getSkinProvider().fetchSkinTextures(profile);
        }, mc).thenAcceptAsync(skin -> this.glId = RenderUtil.getGlId(this.skin = skin.texture()), mc);
    }

}
