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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.common;

import de.nekosarekawaii.vandalism.addonwurstclient.hack.WurstClientOptionsHack;
import net.wurstclient.hack.Hack;
import net.wurstclient.hack.HackList;
import net.wurstclient.hacks.AutoRespawnHack;
import net.wurstclient.hacks.AutoStealHack;
import net.wurstclient.hacks.HealthTagsHack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Mixin(value = HackList.class, remap = false)
public abstract class MixinHackList {

    @Shadow
    @Final
    private TreeMap<String, Hack> hax;

    @Unique
    private static final List<Class<? extends Hack>> vandalism$DISABLED_HACKS = Arrays.asList(
            HealthTagsHack.class,
            AutoStealHack.class,
            AutoRespawnHack.class
    );

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addSomeWurstHacks(final Path enabledHacksFile, final CallbackInfo ci) {
        this.hax.put("Options", new WurstClientOptionsHack());
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeSomeWurstHacks(TreeMap instance, Object key, Object value) {
        if (vandalism$DISABLED_HACKS.contains(value.getClass())) {
            return value;
        }
        return instance.put(key, value);
    }

}
