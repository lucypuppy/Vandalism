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
import de.nekosarekawaii.vandalism.addonwurstclient.AddonWurstClient;
import de.nekosarekawaii.vandalism.feature.hud.impl.WatermarkHUDElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.wurstclient.hud.WurstLogo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WurstLogo.class)
public abstract class MixinWurstLogo {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/wurstclient/hud/WurstLogo;drawQuads(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFFF)V"))
    private void cancelWurstLogoQuad(final WurstLogo instance, final MatrixStack matrices, final int x1, final int y1, final int x2, final int y2, final float r, final float g, final float b, final float a) {
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void moveWurstLogoDrawTexture(final DrawContext instance, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int textureWidth, final int textureHeight) {
        final WatermarkHUDElement watermarkHUDElement = Vandalism.getInstance().getHudManager().watermarkHUDElement;
        if (!watermarkHUDElement.isActive()) return;

        final AddonWurstClient addonWurstClient = AddonWurstClient.getInstance();

        instance.drawTexture(
                texture,
                watermarkHUDElement.getX() + addonWurstClient.wurstOffsetX.getValue(),
                watermarkHUDElement.getY() + addonWurstClient.wurstOffsetY.getValue(),
                u, v,
                width, height,
                textureWidth, textureHeight
        );
    }

    @Inject(method = "getVersionString", at = @At("RETURN"), cancellable = true, remap = false)
    private void changeWurstLogoVersionString(final CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("");
    }

}
