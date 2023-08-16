package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.elytraflight.CreativeModuleMode;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.list.ModuleModeValue;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@ModuleInfo(name = "Elytra Flight", description = "Uses some exploits in elytras to fly longer.", category = FeatureCategory.MOVEMENT)
public class ElytraFlightModule extends Module implements TickListener {

    private final Value<String> mode = new ModuleModeValue<>("Mode", "The current elytra flight mode.", this,
            new CreativeModuleMode(this)
    );

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
        final var player = mc.player;
        if (player == null)
            return;

        final ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() != Items.ELYTRA || !ElytraItem.isUsable(itemStack)) {
            ChatUtils.infoChatMessage(Text.literal("You need to equip an elytra to fly.").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            setState(false);
        }
    }

}
