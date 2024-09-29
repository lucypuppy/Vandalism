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

package de.nekosarekawaii.vandalism.injection.transformer;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.IllegalInteractionModule;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;

@CTransformer(name = "de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network.MixinClientPlayerInteractionManager")
public class TransformerMixinClientPlayerInteractionManager {

    @CInject(method = "interactBlock1_12_2", target = @CTarget("HEAD"), cancellable = true)
    public void fixIllegalInteractionModule(final InjectionCallback callback) {
        if (ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_8)) {
            final IllegalInteractionModule illegalInteractionModule = Vandalism.getInstance().getModuleManager().getIllegalInteractionModule();
            if (illegalInteractionModule.isActive() && illegalInteractionModule.viaVersionBug.getValue()) {
                callback.setCancelled(true);
            }
        }
    }

}
