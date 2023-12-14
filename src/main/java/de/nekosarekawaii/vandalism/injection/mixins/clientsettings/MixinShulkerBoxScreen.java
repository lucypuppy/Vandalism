package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.minecraft.InventoryUtil;
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
