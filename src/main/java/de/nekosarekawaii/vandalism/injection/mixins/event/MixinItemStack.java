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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.render.TooltipDrawListener;
import de.nekosarekawaii.vandalism.util.tooltip.impl.CompoundTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Optional;

@Mixin(value = ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
    private void getTooltipData(final CallbackInfoReturnable<Optional<TooltipData>> info) {
        final ArrayList<TooltipData> tooltipData = new ArrayList<>();
        info.getReturnValue().ifPresent(tooltipData::add);

        Vandalism.getInstance().getEventSystem().callExceptionally(
                TooltipDrawListener.TooltipDrawEvent.ID,
                new TooltipDrawListener.TooltipDrawEvent((ItemStack) (Object) this, tooltipData));

        if (tooltipData.size() == 1) {
            info.setReturnValue(Optional.of(tooltipData.get(0)));
        } else if (tooltipData.size() > 1) {
            final CompoundTooltipComponent comp = new CompoundTooltipComponent();

            for (final TooltipData data : tooltipData) {
                comp.addComponent(TooltipComponent.of(data));
            }

            info.setReturnValue(Optional.of(comp));
        }
    }

}
