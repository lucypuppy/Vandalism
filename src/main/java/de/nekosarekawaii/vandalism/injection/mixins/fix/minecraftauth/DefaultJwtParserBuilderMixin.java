/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraftauth;

import io.jsonwebtoken.gson.io.GsonDeserializer;
import io.jsonwebtoken.impl.DefaultJwtParserBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DefaultJwtParserBuilder.class, remap = false)
public abstract class DefaultJwtParserBuilderMixin {

    @Redirect(method = "build()Lio/jsonwebtoken/JwtParser;", at = @At(value = "INVOKE", target = "Lio/jsonwebtoken/impl/lang/Services;loadFirst(Ljava/lang/Class;)Ljava/lang/Object;"))
    public Object removeServicesSupport(Class<?> spi) {
        return new GsonDeserializer<>();
    }

}