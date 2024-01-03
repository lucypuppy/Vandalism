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

package de.nekosarekawaii.vandalism.addonwurstclient.injection;

import net.lenni0451.reflect.stream.RStream;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WurstPatcher implements IMixinConfigPlugin {

    private static final String WURST_MIXIN_PACKAGE = "net.wurstclient.mixin.";

    private void disableMixins(final String... mixinClasses) {
        final RStream loadedMixinsStream = RStream.of("org.spongepowered.asm.mixin.transformer.MixinConfig");
        final Set<String> loadedMixins = loadedMixinsStream.fields().by("globalMixinList").get();
        for (final String mixinClass : mixinClasses) {
            loadedMixins.add(WURST_MIXIN_PACKAGE + mixinClass);
        }
    }

    @Override
    public void onLoad(final String mixinPackage) {
        System.out.println("Vandalism fries the wurst...");
        this.disableMixins(
                "GameMenuScreenMixin",
                "ShulkerBoxScreenMixin",
                "GenericContainerScreenMixin",
                "MultiplayerScreenMixin",
                "TitleScreenMixin",
                "DisconnectedScreenMixin",
                "DisconnectedRealmsScreenMixin",
                "StatsScreenMixin",
                "PlayerSkinProviderMixin",
                "EntityRendererMixin"
        );
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
