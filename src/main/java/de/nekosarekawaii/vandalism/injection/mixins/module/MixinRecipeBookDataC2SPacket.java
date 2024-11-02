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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.consolespammer.impl.IdentifierModuleMode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookDataC2SPacket.class)
public abstract class MixinRecipeBookDataC2SPacket {

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeIdentifier(Lnet/minecraft/util/Identifier;)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf hookConsoleSpammer(final PacketByteBuf instance, final Identifier id) {
        if (IdentifierModuleMode.IDENTIFIER.equals(id)) {
            final IdentifierModuleMode identifierModuleMode = (IdentifierModuleMode) Vandalism.getInstance().getModuleManager().getConsoleSpammerModule().mode.getValue();
            return instance.writeString(identifierModuleMode.consoleString());
        }
        return instance.writeIdentifier(id);
    }

}
