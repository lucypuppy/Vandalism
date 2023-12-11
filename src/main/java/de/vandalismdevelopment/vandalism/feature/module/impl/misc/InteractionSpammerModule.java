package de.vandalismdevelopment.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.concurrent.CopyOnWriteArrayList;

public class InteractionSpammerModule extends AbstractModule implements TickListener {

    private final Value<Integer> maxXReach = new IntegerValue(
            "Max X Reach",
            "The max y reach.",
            this,
            3,
            0,
            5
    );

    private final Value<Integer> maxZReach = new IntegerValue(
            "Max Z Reach",
            "The max z reach.",
            this,
            3,
            0,
            5
    );

    private final Value<Integer> maxYReach = new IntegerValue(
            "Max Y Reach",
            "The max y reach.",
            this,
            3,
            0,
            5
    );

    private final Value<Integer> interactionListsDelay = new IntegerValue(
            "Interaction Lists Delay",
            "The delay between interaction lists.",
            this,
            1000,
            0,
            2000
    );

    private final Value<Integer> interactionDelay = new IntegerValue(
            "Interaction Delay",
            "The delay between interactions.",
            this,
            100,
            0,
            2000
    );

    private final CopyOnWriteArrayList<CopyOnWriteArrayList<BlockHitResult>> queue;

    private final MSTimer interactionListsTimer, interactionTimer;

    private CopyOnWriteArrayList<BlockHitResult> blockHitResults;

    public InteractionSpammerModule() {
        super(
                "Interaction Spammer",
                "Lets you spam interactions.",
                FeatureCategory.MISC,
                false,
                false
        );
        this.queue = new CopyOnWriteArrayList<>();
        this.interactionListsTimer = new MSTimer();
        this.interactionTimer = new MSTimer();
        this.blockHitResults = new CopyOnWriteArrayList<>();
    }

    private void clear() {
        this.queue.clear();
        this.blockHitResults.clear();
    }

    @Override
    public void onEnable() {
        this.clear();
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        this.clear();
    }

    @Override
    public void onTick() {
        final Entity cameraEntity = this.mc.getCameraEntity();
        if (this.mc.player == null || this.mc.world == null || this.mc.interactionManager == null || cameraEntity == null) {
            this.clear();
            return;
        }

        if (this.blockHitResults.isEmpty()) {
            if (!this.queue.isEmpty()) {
                if (this.interactionListsTimer.hasReached(this.interactionListsDelay.getValue(), true)) {
                    this.blockHitResults = this.queue.get(0);
                    this.queue.remove(this.blockHitResults);
                }
            }
        } else {
            for (final BlockHitResult blockHitResult : this.blockHitResults) {
                if (this.interactionTimer.hasReached(this.interactionDelay.getValue(), true)) {
                    this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, blockHitResult);
                    this.blockHitResults.remove(blockHitResult);
                }
            }
        }

        final HitResult hitResult = cameraEntity.raycast(this.mc.interactionManager.getReachDistance(), 0, false);
        if (!(hitResult instanceof final BlockHitResult blockHitResult)) return;

        final Block block = this.mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
        if (!(block instanceof AirBlock || block instanceof FluidBlock)) {
            if (this.mc.options.useKey.isPressed()) {
                this.interactionListsTimer.reset();
                this.interactionTimer.reset();

                final CopyOnWriteArrayList<BlockHitResult> blockHitResults = new CopyOnWriteArrayList<>();
                for (int y = 0; y < this.maxYReach.getValue(); y++) {
                    for (int x = 0; x < this.maxXReach.getValue(); x++) {
                        for (int z = 0; z < this.maxZReach.getValue(); z++) {
                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(-x, y, z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(-x, y, z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(x, y, -z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(x, y, -z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(x, y, z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(x, y, z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(-x, y, -z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(-x, y, -z),
                                    blockHitResult.isInsideBlock()
                            ));
                        }
                    }
                }

                this.queue.add(blockHitResults);
            }
        }
    }

}
