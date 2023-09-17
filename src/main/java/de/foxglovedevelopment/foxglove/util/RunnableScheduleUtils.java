package de.foxglovedevelopment.foxglove.util;

import de.foxglovedevelopment.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;

public class RunnableScheduleUtils {

    public static void scheduleRunnableWithDelay(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (final InterruptedException e) {
                Foxglove.getInstance().getLogger().error("Failed to schedule runnable thread.", e);
            }
        });
    }

    public static void scheduleRunnableWithDelayForMinecraftThread(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                MinecraftClient.getInstance().execute(runnable);
            } catch (final InterruptedException e) {
                Foxglove.getInstance().getLogger().error("Failed to schedule runnable into minecraft thread.", e);
            }
        });
    }

}
