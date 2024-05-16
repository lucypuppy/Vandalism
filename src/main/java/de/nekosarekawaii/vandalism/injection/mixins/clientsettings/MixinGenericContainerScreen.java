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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.inventory.InventoryUtil;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GenericContainerScreen.class)
public abstract class MixinGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {

    @Shadow
    @Final
    private int rows;

    public MixinGenericContainerScreen(final GenericContainerScreenHandler container, final PlayerInventory playerInventory, final Text name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void init() {
        super.init();
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().manageContainerButtons.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Steal"), b -> InventoryUtil.quickMoveInventory(this, 0, this.rows * 9)).dimensions(this.x + this.backgroundWidth - 108, this.y - 14, 50, 12).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Store"), b -> InventoryUtil.quickMoveInventory(this, this.rows * 9, this.rows * 9 + 44)).dimensions(this.x + this.backgroundWidth - 56, this.y - 14, 50, 12).build());
        }
    }

}