package de.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.util.click.ClickGenerator;
import de.nekosarekawaii.foxglove.util.click.clickers.MSTimerClicker;
import de.nekosarekawaii.foxglove.util.rotation.RotationPriority;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class TestModule extends Module implements TickListener {

    private final ClickGenerator clickGenerator;

    public TestModule() {
        super(
                "Test",
                "Just for development purposes.",
                FeatureCategory.DEVELOPMENT,
                true,
                false
        );
        this.clickGenerator = new MSTimerClicker();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        Foxglove.getInstance().getRotationListener().setRotation((Rotation) null, new Vec2f(10, 15), RotationPriority.HIGH);
    }

    @Override
    public void onTick() {
        if (world() == null || player() == null) return;
        final List<Entity> entities = new ArrayList<>();
        world().getEntities().forEach(entity -> {
            if (entity instanceof LivingEntity && entity instanceof VillagerEntity && player().distanceTo(entity) < 6 && entity != player()) {
                entities.add(entity);
            }
        });
        if (entities.isEmpty()) {
            Foxglove.getInstance().getRotationListener().setRotation((Rotation) null, new Vec2f(10, 15), RotationPriority.HIGH);
            return;
        }
        final Entity target = entities.get(0);

        Foxglove.getInstance().getRotationListener().setRotation(target, true, 3.5f, new Vec2f(15, 20), RotationPriority.HIGH);

        if (this.clickGenerator instanceof final MSTimerClicker clicker) {
            clicker.setMinDelay(20);
            clicker.setMaxDelay(100);
        }

        this.clickGenerator.setClickAction(mc()::doAttack);
        this.clickGenerator.update();
    }

}
