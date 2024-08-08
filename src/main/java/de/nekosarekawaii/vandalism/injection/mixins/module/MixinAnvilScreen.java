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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ComponentResolverContainer;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public abstract class MixinAnvilScreen {

    @Redirect(method = "onSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"))
    private Text hookExploitFixer1(final ItemStack instance) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (!exploitFixerModule.isActive() || !exploitFixerModule.packetSettings.componentResolveContainer.getValue()) {
            return instance.getName();
        }

        final Text r = ComponentResolverContainer.compute(instance.getName());

        System.out.println("ANVIL " + instance.getName().getString() + " | " + r.getString() + " | " + Text.Serialization.toJsonString(instance.getName(), DynamicRegistryManager.EMPTY));

        return r;
    }

    @Redirect(method = "onRenamed", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"))
    private Text hookExploitFixer2(final ItemStack instance) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (!exploitFixerModule.isActive() || !exploitFixerModule.packetSettings.componentResolveContainer.getValue()) {
            return instance.getName();
        }

        return ComponentResolverContainer.compute(instance.getName());
    }
}
