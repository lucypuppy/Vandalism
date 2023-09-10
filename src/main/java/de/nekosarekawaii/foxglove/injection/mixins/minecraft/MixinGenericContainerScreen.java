package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.util.inventory.InventoryUtil;
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
        if (Foxglove.getInstance().getConfigManager().getMainConfig().manageContainerButtons.getValue()) {
            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Steal"), b -> InventoryUtil.quickMoveInventory(
                                    this,
                                    0,
                                    this.rows * 9
                            ))
                            .dimensions(
                                    this.x + this.backgroundWidth - 108,
                                    this.y - 14,
                                    50,
                                    12
                            ).build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Store"), b ->
                                    InventoryUtil.quickMoveInventory(
                                            this,
                                            this.rows * 9,
                                            this.rows * 9 + 44
                                    )
                            )
                            .dimensions(
                                    this.x + this.backgroundWidth - 56,
                                    this.y - 14,
                                    50,
                                    12
                            )
                            .build()
            );
        }
    }

}