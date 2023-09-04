package de.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.util.click.ClickGenerator;
import de.nekosarekawaii.foxglove.util.click.clickers.CooldownClicker;
import de.nekosarekawaii.foxglove.util.rotation.RotationPriority;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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
        this.clickGenerator = new CooldownClicker();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        Foxglove.getInstance().getRotationListener().setRotation((Rotation) null, 20, RotationPriority.HIGH);
    }

    @Override
    public void onTick() {
        if (world() == null || player() == null) return;
        final List<Entity> entities = new ArrayList<>();
        world().getEntities().forEach(entity -> {
            if (entity instanceof LivingEntity && player().distanceTo(entity) < 6 && entity != player()) {
                entities.add(entity);
            }
        });
        if (entities.isEmpty()) {
            Foxglove.getInstance().getRotationListener().setRotation((Rotation) null, 20, RotationPriority.HIGH);
            return;
        }
        final Entity target = entities.get(0);
        final Box box = target.getBoundingBox();
        final double y = box.minY + (box.maxY - box.minY) * 0.9;
        Foxglove.getInstance().getRotationListener().setRotation(new Vec3d(target.getX(), y, target.getZ()), 20, RotationPriority.HIGH);
        this.clickGenerator.setClickAction(mc()::doAttack);
        this.clickGenerator.update();
    }

}
