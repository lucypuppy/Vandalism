package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.InventoryUtil;
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

    public MixinShulkerBoxScreen(final ShulkerBoxScreenHandler handler, final PlayerInventory inventory, final Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.manageContainerButtons.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Steal"), b -> InventoryUtil.quickMoveInventory(this, 0, this.rows * 9)).dimensions(this.x + this.backgroundWidth - 108, this.y - 14, 50, 12).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Store"), b -> InventoryUtil.quickMoveInventory(this, this.rows * 9, this.rows * 9 + 44)).dimensions(this.x + this.backgroundWidth - 56, this.y - 14, 50, 12).build());
        }
    }

}
