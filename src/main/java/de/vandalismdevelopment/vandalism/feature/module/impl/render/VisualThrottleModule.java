package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.util.ParticleTracker;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;

public class VisualThrottleModule extends AbstractModule {

    public final Value<Integer> minSodiumEntityAverageSideLength = new IntegerValue(
            "Min sodium entity average side length",
            "How long should a average side from an entity be to get always rendered to prevent sodium crash.",
            this,
            10,
            10,
            1000
    ).visibleCondition(() -> FabricLoader.getInstance().isModLoaded("sodium"));

    public final HashMap<String, ParticleTracker> particleTrackerMap = new HashMap<>();

    public final Value<Boolean> blockTooManyParticles = new BooleanValue(
            "Block too many particles",
            "Blocks particles when their count is too high.",
            this,
            false
    );

    public final Value<Integer> countToBlockParticles = new IntegerValue(
            "Count to block particles",
            "Set the count to block the particles (per particle type).",
            this,
            10,
            2,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final Value<Integer> particleBlockingCountResetDelay = new IntegerValue(
            "Particle blocking reset delay",
            "Set the delay to reset the particle blocking count (per particle type).",
            this,
            100,
            1,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final Value<Boolean> modifyDisplayNameLength = new BooleanValue(
            "Modify display name length",
            "If enabled allows you to modify the max display name length.",
            this,
            true
    );

    public final Value<Integer> maxDisplayNameLength = new IntegerValue(
            "Max display name length",
            "How long can a display name be.",
            this,
            250,
            3,
            500
    ).visibleCondition(this.modifyDisplayNameLength::getValue);

    public VisualThrottleModule() {
        super(
                "Visual Throttle",
                "Limits the game rendering to enhance performance or even prevent crashes.",
                FeatureCategory.RENDER,
                true,
                false
        );
    }

}
