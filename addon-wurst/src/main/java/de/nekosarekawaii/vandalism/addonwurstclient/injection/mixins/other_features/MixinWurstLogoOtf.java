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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.other_features;

import net.wurstclient.other_features.WurstLogoOtf;
import net.wurstclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WurstLogoOtf.class)
public abstract class MixinWurstLogoOtf {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/other_features/WurstLogoOtf;addSetting(Lnet/wurstclient/settings/Setting;)V", ordinal = 0), remap = false)
    private void removeWurstLogoBackgroundColorSetting(final WurstLogoOtf instance, final Setting setting) {
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/other_features/WurstLogoOtf;addSetting(Lnet/wurstclient/settings/Setting;)V", ordinal = 1), remap = false)
    private void removeWurstLogoTextColorSetting(final WurstLogoOtf instance, final Setting setting) {
    }

}
