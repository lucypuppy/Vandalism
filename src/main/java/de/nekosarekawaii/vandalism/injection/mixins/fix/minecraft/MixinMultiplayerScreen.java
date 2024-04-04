/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen implements MinecraftWrapper {

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    private Screen parent;

    @Shadow
    public List<Text> multiplayerScreenTooltip;

    protected MixinMultiplayerScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fixInvalidParentScreen(final CallbackInfo ci) {
        if (this.parent instanceof GameMenuScreen && this.mc.player == null) {
            this.parent = new TitleScreen();
        }
    }

    @Redirect(method = "method_19916", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/I18n;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private String replaceDefaultServerNameWithEmptyString(final String key, final Object[] args) {
        return "";
    }

    @Redirect(method = "method_19914", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;getServer()Lnet/minecraft/client/network/ServerInfo;"))
    private ServerInfo fixServerName(final MultiplayerServerListWidget.ServerEntry instance) {
        final ServerInfo origin = instance.getServer();
        ServerInfo server = new ServerInfo(origin.name, origin.address, origin.getServerType());
        if (server.name.isEmpty()) {
            server.name = server.address;
        }
        return server;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // render xd
        context.fill(serverListWidget.getX(), 0, serverListWidget.getRight(), serverListWidget.getY(), Integer.MIN_VALUE);
        context.fill(serverListWidget.getX(), serverListWidget.getBottom(), serverListWidget.getRight(), MinecraftClient.getInstance().getWindow().getHeight(), Integer.MIN_VALUE);

        // NOW draw the buttons etc
        for (final Drawable drawable : drawables) {
            drawable.render(context, mouseX, mouseY, delta);
        }

        if (this.multiplayerScreenTooltip != null) {
            context.drawTooltip(this.textRenderer, this.multiplayerScreenTooltip, mouseX, mouseY);
            this.multiplayerScreenTooltip = null;
        }

        this.serverListWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);

        ci.cancel();
    }

}
