package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TargetSelectorModule extends AbstractModule implements TickGameListener {

    private final ValueGroup targetSelectionGroup = new ValueGroup(
            this,
            "Target Selection",
            "Settings for the target selection."
    );

    private final Value<Boolean> players = new BooleanValue(
            this.targetSelectionGroup,
            "Players",
            "Whether players should be attacked.",
            true
    );

    private final Value<Boolean> hostile = new BooleanValue(
            this.targetSelectionGroup,
            "Hostile",
            "Whether hostile mobs should be attacked.",
            false
    );

    private final Value<Boolean> animals = new BooleanValue(
            this.targetSelectionGroup,
            "Animals",
            "Whether animals should be attacked.",
            false
    );

    private final Value<Boolean> isAlive = new BooleanValue(
            this.targetSelectionGroup,
            "Alive",
            "Checks if the entity is alive.",
            true
    );

    private final ValueGroup antiBot = new ValueGroup(
            this,
            "Anti Bot",
            "Settings for the anti bot."
    );

    private final List<LivingEntity> targets = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TargetSelectorModule() {
        super(
                "Target Selector",
                "This module gets all possible targets.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
        targets.clear();
    }

    @Override
    public void onTick() {
        if (mc.world == null)
            return;

        executorService.submit(() -> {
            targets.clear();

            mc.world.getEntities().forEach(entity -> {
                if (entity instanceof final LivingEntity livingEntity
                        && livingEntity != mc.player
                        && (livingEntity.isAlive() || !isAlive.getValue()) &&
                        ((livingEntity instanceof PlayerEntity && players.getValue())
                                || (livingEntity instanceof HostileEntity && hostile.getValue())
                                || (livingEntity instanceof AnimalEntity && animals.getValue()))) {
                    targets.add(livingEntity);
                }
            });
        });
    }

    public List<LivingEntity> getTargets() {
        return targets;
    }

    public List<LivingEntity> getTargets(final double range) {
        return targets.stream()
                .filter(entity -> mc.player.distanceTo(entity) < range)
                .collect(Collectors.toList());
    }

}
