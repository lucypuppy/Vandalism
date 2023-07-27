package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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
            addDrawableChild(ButtonWidget
                    .builder(Text.literal("Steal"), b -> shiftClickSlots(0, rows * 9))
                    .dimensions(x + backgroundWidth - 108, y - 14, 50, 12).build());

            addDrawableChild(ButtonWidget
                    .builder(Text.literal("Store"), b -> shiftClickSlots(rows * 9, rows * 9 + 44))
                    .dimensions(x + backgroundWidth - 56, y - 14, 50, 12).build());
        }
    }

    @Unique
    private void shiftClickSlots(final int from, final int to) {
        for (int i = from; i < to; i++) {
            if (handler.slots.size() <= i || client.currentScreen == null)
                break;

            final Slot slot = handler.slots.get(i);

            if (slot.getStack().isEmpty())
                continue;

            onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
        }
    }

}