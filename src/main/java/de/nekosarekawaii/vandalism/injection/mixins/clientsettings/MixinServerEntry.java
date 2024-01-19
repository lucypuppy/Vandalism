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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.ServerUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Inject(method = "protocolVersionMatches", at = @At(value = "RETURN"), cancellable = true)
    private void forceProtocolVersionMatches(final CallbackInfoReturnable<Boolean> cir) {
        if (!Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            return;
        }
        if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().multiplayerScreenServerInformation.getValue()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static final String vandalism$VERSION_TEXT = Formatting.GOLD + Formatting.BOLD.toString() + "Version" + Formatting.DARK_GRAY + Formatting.BOLD + "> " + Formatting.GRAY;

    @Unique
    private static final String vandalism$PROTOCOL_TEXT = Formatting.DARK_AQUA + Formatting.BOLD.toString() + "Protocol" + Formatting.DARK_GRAY + Formatting.BOLD + "> " + Formatting.AQUA;

    @Unique
    private static final String vandalism$INCOMPATIBLE_PROTOCOL_TEXT = Formatting.DARK_GRAY + Formatting.BOLD.toString() + "(" + Formatting.RED + Formatting.BOLD + "Incompatible Protocol!" + Formatting.DARK_GRAY + Formatting.BOLD + ")";

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private int vandalism$applyAdditionalServerInformation(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color, final boolean shadow) {
        instance.drawText(textRenderer, text, x, y, color, shadow);
        if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().multiplayerScreenServerInformation.getValue()) {
                final int textX = x + textRenderer.getWidth(text) + 22;
                instance.drawTextWithShadow(textRenderer, vandalism$VERSION_TEXT + ServerUtil.fixVersionName(this.server.version.getString()), textX, y, -1);
                instance.drawTextWithShadow(
                        textRenderer,
                        vandalism$PROTOCOL_TEXT + this.server.protocolVersion,
                        textX,
                        y + textRenderer.fontHeight,
                        -1
                );
                if (this.server.protocolVersion != ProtocolHack.getTargetVersion().getVersion()) {
                    instance.drawTextWithShadow(
                            textRenderer,
                            vandalism$INCOMPATIBLE_PROTOCOL_TEXT,
                            textX,
                            y + (textRenderer.fontHeight * 2),
                            -1
                    );
                }
            }
        }
        return x;
    }

}
