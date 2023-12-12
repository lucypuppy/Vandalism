package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.MinecraftClient;

public interface MinecraftBoostrapListener {

    void onBootstrapGame(final MinecraftClient mc);

    class MinecraftBootstrapEvent extends AbstractEvent<MinecraftBoostrapListener> {

        public static final int ID = 6;

        private final MinecraftClient mc;

        public MinecraftBootstrapEvent(final MinecraftClient mc) {
            this.mc = mc;
        }

        @Override
        public void call(MinecraftBoostrapListener listener) {
            listener.onBootstrapGame(this.mc);
        }
    }

}
