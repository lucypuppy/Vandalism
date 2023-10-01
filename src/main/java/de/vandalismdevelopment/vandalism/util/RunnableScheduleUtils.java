package de.vandalismdevelopment.vandalism.util;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;

public class RunnableScheduleUtils {

    public static void scheduleRunnableWithDelay(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (final InterruptedException e) {
                Vandalism.getInstance().getLogger().error("Failed to schedule runnable thread.", e);
            }
        });
    }

    public static void scheduleRunnableWithDelayForMinecraftThread(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                MinecraftClient.getInstance().execute(runnable);
            } catch (final InterruptedException e) {
                Vandalism.getInstance().getLogger().error("Failed to schedule runnable into minecraft thread.", e);
            }
        });
    }

}
