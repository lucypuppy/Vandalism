package de.foxglovedevelopment.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.event.TickListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.util.click.ClickGenerator;
import de.foxglovedevelopment.foxglove.util.click.clickers.MSTimerClicker;
import de.foxglovedevelopment.foxglove.util.rotation.RotationPriority;
import de.foxglovedevelopment.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
            if (entity instanceof PlayerEntity && player().distanceTo(entity) < 6 && entity != player()) {
                entities.add(entity);
            }
        });
        if (entities.isEmpty()) {
            Foxglove.getInstance().getRotationListener().setRotation((Rotation) null, new Vec2f(10, 15), RotationPriority.HIGH);
            return;
        }
        final Entity target = entities.get(0);

        final Rotation rotation = Rotation.Builder.build(target, true, 3.5f, 0.1D);

        if (rotation != null) {
            Foxglove.getInstance().getRotationListener().setRotation(rotation, new Vec2f(80, 100), RotationPriority.HIGH);
        }

        if (this.clickGenerator instanceof final MSTimerClicker clicker) {
            clicker.setMinDelay(20);
            clicker.setMaxDelay(100);
        }

        this.clickGenerator.setClickAction(mc()::doAttack);
        this.clickGenerator.update();
    }

}
