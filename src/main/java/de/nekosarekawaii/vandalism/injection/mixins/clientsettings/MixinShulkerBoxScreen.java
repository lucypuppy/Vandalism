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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShulkerBoxScreen.class)
public abstract class MixinShulkerBoxScreen extends HandledScreen<ShulkerBoxScreenHandler> implements ScreenHandlerProvider<ShulkerBoxScreenHandler> {

    @Unique
    private static final int vandalism$ROWS = 3;

    public MixinShulkerBoxScreen(final ShulkerBoxScreenHandler handler, final PlayerInventory inventory, final Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().manageContainerButtons.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Steal"), b -> InventoryUtil.quickMoveInventory(this, 0, vandalism$ROWS * 9)).dimensions(this.x + this.backgroundWidth - 108, this.y - 14, 50, 12).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Store"), b -> InventoryUtil.quickMoveInventory(this, vandalism$ROWS * 9, vandalism$ROWS * 9 + 44)).dimensions(this.x + this.backgroundWidth - 56, this.y - 14, 50, 12).build());
        }
    }

}
