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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = PlayerListHud.class)
public abstract class MixinPlayerListHud implements MinecraftWrapper {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private PlayerListEntry vandalism$trackedEntry;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2, shift = At.Shift.BEFORE))
    public void trackEntry(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci, @Local(ordinal = 0) List<PlayerListEntry> list, @Local(ordinal = 15) int w) {
        vandalism$trackedEntry = list.get(w);
    }

    @ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
    private long hookBetterTabListModule(final long count) {
        final var betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();

        if (betterTabListModule.isActive()) {
            return betterTabListModule.tabSize.getValue();
        } else {
            return count;
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    public void hookBetterTabListModule(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        final var betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        final GameProfile profile = vandalism$trackedEntry.getProfile();
        if (betterTabListModule.isActive()) {
            if (betterTabListModule.highlightSelf.getValue() && this.mc.player != null && profile.getId().equals(this.mc.player.getGameProfile().getId())) {
                color = betterTabListModule.selfColor.getColor().getRGB();
            }
            for (final Friend friend : Vandalism.getInstance().getFriendManager().getList()) {
                if (betterTabListModule.highlightFriends.getValue() && profile.getName().equalsIgnoreCase(friend.getName())) {
                    color = betterTabListModule.friendsColor.getColor().getRGB();
                    break;
                }
            }
        }

        original.call(instance, x1, y1, x2, y2, color);
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void hookBetterTabListModule(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        final var betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();

        if (betterTabListModule.isActive() && betterTabListModule.moreInfo.getValue()) {
            final var color = betterTabListModule.getColorFromGameMode(entry.getGameMode().getId());

            cir.setReturnValue(Text.literal("").append(
                    Text.literal(
                            Formatting.DARK_GRAY + "[" + Formatting.RESET + entry.getGameMode().getId() + Formatting.DARK_GRAY + "] " + Formatting.RESET
                    ).withColor(color)
            ).append(cir.getReturnValue()));
        }
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    public void hookBetterTabListModule(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        final var betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        if (betterTabListModule.isActive() && betterTabListModule.moreInfo.getValue()) {
            final var matrices = context.getMatrices();
            matrices.push();
            matrices.translate(x + width - 6, y + client.textRenderer.fontHeight / 2f, 0);
            matrices.scale(0.5f, 0.5f, 1f);

            final var color = betterTabListModule.getColorFromPing(entry.getLatency());
            context.drawText(client.textRenderer, String.valueOf(entry.getLatency()), -client.textRenderer.getWidth(String.valueOf(entry.getLatency())) / 2, -client.textRenderer.fontHeight / 2, color, true);

            matrices.pop();
            ci.cancel();
        }
    }

}
