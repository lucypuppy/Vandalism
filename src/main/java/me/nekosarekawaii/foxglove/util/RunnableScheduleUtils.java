package me.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;

public class RunnableScheduleUtils {

    public static void scheduleRunnableWithDelay(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void scheduleRunnableWithDelayForMinecraftThread(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                MinecraftClient.getInstance().execute(runnable);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
