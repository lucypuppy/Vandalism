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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
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

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen implements MinecraftWrapper {

    @Shadow
    private Screen parent;

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

}
