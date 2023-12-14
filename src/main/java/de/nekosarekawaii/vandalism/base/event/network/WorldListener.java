package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

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

        public BlockState state;
        public BlockPos pos;
        public VoxelShape shape;
        public boolean shouldUpdate;

        public BlockEvent(final BlockState state, final BlockPos pos, final VoxelShape shape) {
            this.state = state;
            this.pos = pos;
            this.shape = shape;
            this.shouldUpdate = false;
        }

        @Override
        public void call(final WorldListener listener) {
            listener.onCollisionShapeGet(this);
        }

    }

}
