package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import de.nekosarekawaii.vandalism.Vandalism;
import net.wurstclient.hack.Hack;
import net.wurstclient.hud.HackListHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.regex.Pattern;

@Mixin(value = HackListHUD.class, remap = false)
public abstract class MixinHackListHUD {

    @Unique
    private static final HashMap<String, String> vandalism$NORMAL_HACK_NAMES = new HashMap<>();

    @Unique
    private static final Pattern vandalism$NORMALIZE_PATTERN = Pattern.compile("([a-z])([A-Z])");

    @Unique
    public String vandalism$normalizeHackName(final Hack hack) {
        String result = vandalism$NORMALIZE_PATTERN.matcher(hack.getName()).replaceAll("$1 $2");
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }

    @Inject(method = "updateState", at = @At("HEAD"), cancellable = true)
    private void redirectWurstHackListEntry(final Hack hack, final CallbackInfo ci) {
        final String name;
        if (vandalism$NORMAL_HACK_NAMES.containsKey(hack.getName())) {
            name = vandalism$NORMAL_HACK_NAMES.get(hack.getName());
        }
        else vandalism$NORMAL_HACK_NAMES.put(hack.getName(), name = vandalism$normalizeHackName(hack));
        if (hack.isEnabled()) {
            Vandalism.getInstance().getHudManager().moduleListHUDElement.addExternalModule("Wurst", name);
        } else {
            Vandalism.getInstance().getHudManager().moduleListHUDElement.removeExternalModule("Wurst", name);
        }
        ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void disableWurstHackListUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

}
