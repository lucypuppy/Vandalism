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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.settings.ExploitFixerRenderSettings;
import de.nekosarekawaii.vandalism.feature.module.impl.render.BetterTabListModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    protected abstract void renderHealthBar(final DrawContext context, final PlayerEntity player, final int x, final int y, final int lines, final int regeneratingHeartIndex, final float maxHealth, final int lastHealth, final int health, final int absorption, final boolean blinking);

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    private void hookExploitFixer(final InGameHud instance, final DrawContext context, final PlayerEntity player, final int x, final int y, final int lines, final int regeneratingHeartIndex, float maxHealth, final int lastHealth, int health, final int absorption, final boolean blinking) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        final ExploitFixerRenderSettings renderSettings = exploitFixerModule.renderSettings;
        if (exploitFixerModule.isActive() && renderSettings.blockTooHighHealthUpdates.getValue()) {
            final float maxHealthUpdateHealth = renderSettings.maxHealthUpdateHealth.getValue();
            if (maxHealth > maxHealthUpdateHealth) {
                maxHealth = maxHealthUpdateHealth;
            }
            if (health > maxHealth) {
                health = (int) maxHealth;
            }
        }
        this.renderHealthBar(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    @Redirect(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    private boolean hookBetterTabList(final KeyBinding instance) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        if (betterTabListModule.isActive() && betterTabListModule.toggleable.getValue()) {
            return betterTabListModule.toggleState;
        }
        return instance.isPressed();
    }

}