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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.IllegalInteractionModule;
import de.nekosarekawaii.vandalism.integration.viafabricplus.ViaFabricPlusAccess;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult hookIllegalBlockPlace(final ItemStack instance, final ItemUsageContext context) {
        ActionResult actionResult = instance.useOnBlock(context);
        if (ViaFabricPlusAccess.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            final IllegalInteractionModule illegalInteractionModule = Vandalism.getInstance().getModuleManager().getIllegalInteractionModule();
            if (illegalInteractionModule.isActive() && illegalInteractionModule.viaVersionBug.getValue()) {
                if (actionResult == ActionResult.FAIL) {
                    actionResult = ActionResult.SUCCESS;
                }
            }
        }
        return actionResult;
    }

}
