package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.util.minecraft.inventory.InventoryUtil;
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
    private final int rows = 3;

    public MixinShulkerBoxScreen(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        if (Foxglove.getInstance().getConfigManager().getMainConfig().manageContainerButtons.getValue()) {
            addDrawableChild(ButtonWidget
                    .builder(Text.literal("Steal"), b -> InventoryUtil.quickMoveInventory(this, 0, rows * 9))
                    .dimensions(x + backgroundWidth - 108, y - 14, 50, 12).build());

            addDrawableChild(ButtonWidget
                    .builder(Text.literal("Store"), b -> InventoryUtil.quickMoveInventory(this, rows * 9, rows * 9 + 44))
                    .dimensions(x + backgroundWidth - 56, y - 14, 50, 12).build());
        }
    }

}
