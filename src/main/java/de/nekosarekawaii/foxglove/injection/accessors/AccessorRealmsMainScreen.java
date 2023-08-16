package de.nekosarekawaii.foxglove.injection.accessors;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RealmsMainScreen.class)
public interface AccessorRealmsMainScreen {

    @Accessor
    @Mutable
    static void setCheckedClientCompatibility(final boolean checked) {
    }

    @Accessor
    @Mutable
    static void setRealmsGenericErrorScreen(final Screen screen) {
    }

}