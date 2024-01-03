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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.common;

import net.wurstclient.other_feature.OtfList;
import net.wurstclient.other_features.DisableOtf;
import net.wurstclient.other_features.HackListOtf;
import net.wurstclient.other_features.WurstCapesOtf;
import net.wurstclient.other_features.ZoomOtf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.TreeMap;

@Mixin(value = OtfList.class, remap = false)
public abstract class MixinOtfList {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeSomeWurstOtfs(TreeMap instance, Object key, Object value) {
        if (value.getClass().equals(HackListOtf.class) || value.getClass().equals(DisableOtf.class) || value.getClass().equals(WurstCapesOtf.class) || value.getClass().equals(ZoomOtf.class)) {
            return value;
        }
        return instance.put(key, value);
    }

}
