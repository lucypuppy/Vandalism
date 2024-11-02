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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import de.nekosarekawaii.vandalism.Vandalism;
import net.wurstclient.hack.Hack;
import net.wurstclient.hud.HackListHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.regex.Pattern;

@Mixin(value = HackListHUD.class, remap = false)
public abstract class MixinHackListHUD {

    @Unique
    private static final HashMap<String, String> vandalism$NORMAL_HACK_NAMES = new HashMap<>();

    @Unique
    private static final Pattern vandalism$NORMALIZE_PATTERN = Pattern.compile("([a-z])([A-Z])");

    @Unique
    public String vandalism$normalizeHackName(final Hack hack) {
        String result = vandalism$NORMALIZE_PATTERN.matcher(hack.getName()).replaceAll("$1 $2");
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }

    @Inject(method = "updateState", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectWurstHackListEntry(final Hack hack, final CallbackInfo ci) {
        final String name;
        if (vandalism$NORMAL_HACK_NAMES.containsKey(hack.getName())) {
            name = vandalism$NORMAL_HACK_NAMES.get(hack.getName());
        } else vandalism$NORMAL_HACK_NAMES.put(hack.getName(), name = vandalism$normalizeHackName(hack));
        if (hack.isEnabled()) {
            Vandalism.getInstance().getHudManager().moduleListHUDElement.addExternalModule("Wurst", name);
        } else {
            Vandalism.getInstance().getHudManager().moduleListHUDElement.removeExternalModule("Wurst", name);
        }
        ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void disableWurstHackListUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

}
