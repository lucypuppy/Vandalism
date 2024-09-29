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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.IllegalInteractionModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Unique
    private ActionResult makeInteractionIllegal(final ActionResult currentResult) {
        if (ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_8)) {
            final IllegalInteractionModule illegalInteractionModule = Vandalism.getInstance().getModuleManager().getIllegalInteractionModule();
            if (illegalInteractionModule.isActive() && illegalInteractionModule.viaVersionBug.getValue()) {
                if (currentResult == ActionResult.FAIL) {
                    return ActionResult.SUCCESS;
                }
            }
        }
        return currentResult;
    }

    @WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 0))
    private ActionResult hookIllegalInteraction1(final ItemStack instance, final ItemUsageContext context, final Operation<ActionResult> original) {
        return this.makeInteractionIllegal(original.call(instance, context));
    }

    @WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 1))
    private ActionResult hookIllegalInteraction2(final ItemStack instance, final ItemUsageContext context, final Operation<ActionResult> original) {
        return this.makeInteractionIllegal(original.call(instance, context));
    }

}
