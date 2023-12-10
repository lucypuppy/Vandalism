package de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.modes.elytraflight.CreativeModuleMode;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.list.ModuleModeValue;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ElytraFlightModule extends Module implements TickListener {

    private final Value<String> mode = new ModuleModeValue<>(
            "Mode",
            "The current elytra flight mode.",
            this,
            new CreativeModuleMode(this)
    );

    public ElytraFlightModule() {
        super(
                "Elytra Flight",
                "Lets you take control when flying with an elytra.",
                FeatureCategory.MOVEMENT,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;

        final ItemStack itemStack = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() != Items.ELYTRA || !ElytraItem.isUsable(itemStack)) {
            ChatUtil.errorChatMessage(Text.literal("You need to equip an elytra to fly."));
            this.disable();
        }
    }

}
