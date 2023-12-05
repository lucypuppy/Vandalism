package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface WorldListener {

    default void onPreWorldLoad() {
    }

    default void onPostWorldLoad() {
    }

    enum State {
        PRE, POST
    }

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 7;

        private final State state;

        public WorldLoadEvent(final State state) {
            this.state = state;
        }

        @Override
        public void call(final WorldListener listener) {
            if (this.state == State.PRE) listener.onPreWorldLoad();
            else listener.onPostWorldLoad();
        }

    }

    default void onCollisionShapeGet(final BlockEvent event) {
    }

    class BlockEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 13;

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
