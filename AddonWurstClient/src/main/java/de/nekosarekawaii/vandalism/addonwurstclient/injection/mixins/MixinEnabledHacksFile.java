package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins;

import de.nekosarekawaii.vandalism.addonwurstclient.AddonWurstClient;
import net.wurstclient.hack.EnabledHacksFile;
import net.wurstclient.hack.Hack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(value = EnabledHacksFile.class, remap = false)
public abstract class MixinEnabledHacksFile {

    @Redirect(method = "createJson", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0))
    public <T extends Hack> Stream<T> useStoredEnabledData(Stream<T> instance, Predicate<? super T> predicate) {
        if (AddonWurstClient.enabledHacks != null) {
            // If the client is disabled and stored enabled hacks, we save
            // all enabled hacks we previously stored instead of checking the current state
            return instance.filter(module -> AddonWurstClient.enabledHacks.contains(module.getName()));
        } else {
            // Otherwise, do the normal stuff
            return instance.filter(predicate);
        }
    }

}
