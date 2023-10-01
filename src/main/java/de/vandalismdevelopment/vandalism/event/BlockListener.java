package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface BlockListener {

    void onCollisionShapeGet(final BlockEvent event);

    class BlockEvent extends AbstractEvent<BlockListener> {

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
        public void call(final BlockListener listener) {
            listener.onCollisionShapeGet(this);
        }

    }

}
