package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ParticleTracker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;

public class VisualThrottleModule extends AbstractModule {

    public final IntegerValue minSodiumEntityAverageSideLength = new IntegerValue(
            this,
            "Min sodium entity average side length",
            "How long should a average side from an entity be to get always rendered to prevent sodium crash.",
            10,
            10,
            1000
    ).visibleCondition(() -> FabricLoader.getInstance().isModLoaded("sodium"));

    public final HashMap<String, ParticleTracker> particleTrackerMap = new HashMap<>();

    public final BooleanValue blockTooManyParticles = new BooleanValue(
            this,
            "Block too many particles",
            "Blocks particles when their count is too high.",
            false
    );

    public final IntegerValue countToBlockParticles = new IntegerValue(
            this,
            "Count to block particles",
            "Set the count to block the particles (per particle type).",
            10,
            2,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final IntegerValue particleBlockingCountResetDelay = new IntegerValue(
            this,
            "Particle blocking reset delay",
            "Set the delay to reset the particle blocking count (per particle type).",
            100,
            1,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final BooleanValue modifyDisplayNameLength = new BooleanValue(
            this,
            "Modify display name length",
            "If activated allows you to modify the max display name length.",
            true
    );

    public final IntegerValue maxDisplayNameLength = new IntegerValue(
            this,
            "Max display name length",
            "How long can a display name be.",
            250,
            3,
            500
    ).visibleCondition(this.modifyDisplayNameLength::getValue);

    public VisualThrottleModule() {
        super(
                "Visual Throttle",
                "Limits the game rendering to enhance performance or even prevent crashes.",
                Category.RENDER
        );
        this.markExperimental();
    }

}
