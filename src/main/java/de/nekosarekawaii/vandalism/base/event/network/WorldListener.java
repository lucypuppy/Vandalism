package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public interface WorldListener {

    default void onPreWorldLoad() {
    }

    default void onPostWorldLoad() {
    }

    enum WorldEventState {
        PRE, POST
    }

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public static final int ID = 13;

        private final WorldEventState state;

        public WorldLoadEvent(final WorldEventState state) {
            this.state = state;
        }

        @Override
        public void call(final WorldListener listener) {
            if (this.state == WorldEventState.PRE) listener.onPreWorldLoad();
            else listener.onPostWorldLoad();
        }

    }

    default void onCollisionShapeGet(final BlockEvent event) {
    }

    class BlockEvent extends AbstractEvent<WorldListener> {

        public static final int ID = 14;

        public final Block block;
        public final BlockState state;
        public final BlockView world;
        public final BlockPos pos;
        public final ShapeContext context;
        public VoxelShape shape;

        public BlockEvent(final Block block, final BlockState state, final BlockView world, final BlockPos pos, final ShapeContext context, final VoxelShape shape) {
            this.block = block;
            this.state = state;
            this.world = world;
            this.pos = pos;
            this.context = context;
            this.shape = shape;
        }

        @Override
        public void call(final WorldListener listener) {
            listener.onCollisionShapeGet(this);
        }

    }

}
