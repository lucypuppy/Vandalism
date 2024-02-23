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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerDataUtil;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerPingerWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Shadow @Final private MultiplayerScreen screen;

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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    private int renderAddressAsDefaultServerName(final DrawContext instance, final TextRenderer textRenderer, String text, final int x, final int y, final int color, final boolean shadow) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            if (enhancedServerListSettings.renderAddressAsDefaultServerName.getValue()) {
                if (this.server.name.isEmpty()) {
                    text = this.server.address;
                }
            }
        }
        return instance.drawText(textRenderer, text, x, y, color, shadow);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private int applyAdditionalServerInformation(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color, final boolean shadow) {
        instance.drawText(textRenderer, text, x, y, color, shadow);
        if (this.server.ping >= 0) {
            final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
            if (enhancedServerListSettings.enhancedServerList.getValue()) {
                if (enhancedServerListSettings.multiplayerScreenServerInformation.getValue()) {
                    final int textX = x + textRenderer.getWidth(text) + 22;
                    instance.drawTextWithShadow(textRenderer, vandalism$VERSION_TEXT + ServerDataUtil.fixVersionName(this.server.version.getString()), textX, y, -1);
                    instance.drawTextWithShadow(
                            textRenderer,
                            vandalism$PROTOCOL_TEXT + ProtocolVersion.getProtocol(this.server.protocolVersion),
                            textX,
                            y + textRenderer.fontHeight,
                            -1
                    );
                }
            }
        }
        return x;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;size()I"))
    private int return0WhileUsingServerPingerWidget(final ServerList instance) {
        if (ServerPingerWidget.IN_USE) {
            return 0;
        }
        if (instance == null) {
            return 0;
        }
        return instance.size();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void dontRenderWhileUsingServerPingerWidget(final DrawContext instance, final int x1, final int y1, final int x2, final int y2, final int color) {
        if (ServerPingerWidget.IN_USE) {
            return;
        }
        instance.fill(x1, y1, x2, y2, color);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void disableKeyPressingWhileUsingServerPingerWidget(final int keyCode, final int scanCode, final int modifiers, final CallbackInfoReturnable<Boolean> cir) {
        if (ServerPingerWidget.IN_USE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canConnect", at = @At("HEAD"), cancellable = true)
    private void disableConnectingWhileUsingServerPingerWidget(final CallbackInfoReturnable<Boolean> cir) {
        if (ServerPingerWidget.IN_USE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void disableMouseClickingWhileUsingServerPingerWidget(final double mouseX, final double mouseY, final int button, final CallbackInfoReturnable<Boolean> cir) {
        if (ServerPingerWidget.IN_USE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "saveFile", at = @At("HEAD"), cancellable = true)
    private void disableSavingWhileUsingServerPingerWidget(final CallbackInfo ci) {
        if (ServerPingerWidget.IN_USE) {
            ci.cancel();
            return;
        }
        if (this.screen.getServerList() == null) {
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V", ordinal = 0))
    private void attachAdditionalTooltipData(final MultiplayerScreen instance, final List<Text> tooltip) {
        instance.setMultiplayerScreenTooltip(ServerDataUtil.attachAdditionalTooltipData(new ArrayList<>(tooltip), this.server));
    }

}
