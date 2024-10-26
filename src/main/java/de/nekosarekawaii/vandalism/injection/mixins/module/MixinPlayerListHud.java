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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.BetterTabListModule;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
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

    @ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
    private long hookBetterTabListModule(final long count) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();

        if (betterTabListModule.isActive()) {
            return betterTabListModule.tabSize.getValue();
        } else {
            return count;
        }
    }

    @Unique
    private int vandalism$index;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    public void hookBetterTabListModule(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original, @Local(ordinal = 0) List<PlayerListEntry> list) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        final GameProfile profile = list.get(vandalism$index).getProfile();
        if (betterTabListModule.isActive()) {
            if (betterTabListModule.highlightSelf.getValue() && this.mc.player != null && profile.getId().equals(this.mc.player.getGameProfile().getId())) {
                color = betterTabListModule.selfColor.getColor().getRGB();
            }
            else {
                for (final Friend friend : Vandalism.getInstance().getFriendsManager().getList()) {
                    if (betterTabListModule.highlightFriends.getValue() && profile.getName().equalsIgnoreCase(friend.getName())) {
                        color = betterTabListModule.friendsColor.getColor().getRGB();
                        break;
                    }
                }
            }
        }

        original.call(instance, x1, y1, x2, y2, color);

        vandalism$index++;
        if (vandalism$index >= list.size()) vandalism$index = 0;
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void hookBetterTabListModule(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();

        if (betterTabListModule.isActive() && betterTabListModule.showGameMode.getValue()) {
            final int color = betterTabListModule.getColorFromGameMode(entry.getGameMode().getId());

            cir.setReturnValue(Text.literal("").append(
                    Text.literal(
                            Formatting.DARK_GRAY + "[" + Formatting.RESET + entry.getGameMode().getId() + Formatting.DARK_GRAY + "] " + Formatting.RESET
                    ).withColor(color)
            ).append(cir.getReturnValue()));
        }
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    public void hookBetterTabListModule(final DrawContext context, final int width, final int x, final int y, final PlayerListEntry entry, final CallbackInfo ci) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        if (betterTabListModule.isActive() && betterTabListModule.showAccuratePing.getValue()) {
            final MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(x + width - 6, y + client.textRenderer.fontHeight / 2f, 0);
            matrices.scale(0.5f, 0.5f, 1f);
            long latency = entry.getLatency();
            final long maxPing = betterTabListModule.maxPing.getValue();
            if (latency > maxPing) latency = maxPing;
            else if (latency < -maxPing) latency = -maxPing;
            final String latencyString = String.valueOf(latency);
            final int color = betterTabListModule.getColorFromPing(latency);
            context.drawText(client.textRenderer, latencyString, -client.textRenderer.getWidth(latencyString) / 2, -client.textRenderer.fontHeight / 2, color, true);
            matrices.pop();
            ci.cancel();
        }
    }

}
