package de.vandalismdevelopment.vandalism.base;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.game.MinecraftBoostrapListener;
import de.vandalismdevelopment.vandalism.base.event.game.ShutdownProcessListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.util.Identifier;

public class FabricBootstrap implements ClientModInitializer {

    public static String MOD_ID, MOD_NAME, MOD_AUTHORS, MOD_VERSION;

    public static Identifier MOD_ICON;
    public static String WINDOW_TITLE;

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(MOD_ID = "vandalism").ifPresent(modContainer -> {
            FabricBootstrap.MOD_NAME = modContainer.getMetadata().getName();
            FabricBootstrap.MOD_AUTHORS = String.join(", ", modContainer.getMetadata().getAuthors().stream().map(Person::getName).toList());
            FabricBootstrap.MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
        });

        FabricBootstrap.WINDOW_TITLE = String.format("%s %s made by %s", FabricBootstrap.MOD_NAME, FabricBootstrap.MOD_VERSION, FabricBootstrap.MOD_AUTHORS);
        FabricBootstrap.MOD_ICON = new Identifier(FabricBootstrap.MOD_ID, "textures/logo.png");

        DietrichEvents2.global().subscribe(Vandalism.getInstance(),
                MinecraftBoostrapListener.MinecraftBootstrapEvent.ID,
                ShutdownProcessListener.ShutdownProcessEvent.ID
        );
    }

}
