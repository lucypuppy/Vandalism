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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.injection.access.IChatHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {

    @Shadow
    protected abstract int getMessageLineIndex(double chatLineX, double chatLineY);

    @Shadow
    @Final
    public List<ChatHudLine> messages;

    @Unique
    private ChatHudLine lastHoveredLine;

    @Redirect(method = "getTextStyleAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getMessageLineIndex(DD)I"))
    private int modifyRotationYaw(ChatHud instance, double chatLineX, double chatLineY) {
        final int index = getMessageLineIndex(chatLineX, chatLineY);

        if (index >= 0 && index < this.messages.size()) {
            this.lastHoveredLine = this.messages.get(index);
        }

        return index;
    }

    @Override
    public ChatHudLine vandalism$getLastHovered() {
        return this.lastHoveredLine;
    }

    @Override
    public void vandalism$resetLastHovered() {
        this.lastHoveredLine = null;
    }

}
