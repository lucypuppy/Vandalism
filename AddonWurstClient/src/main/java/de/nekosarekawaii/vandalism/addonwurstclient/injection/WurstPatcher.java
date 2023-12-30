package de.nekosarekawaii.vandalism.addonwurstclient.injection;

import net.lenni0451.reflect.stream.RStream;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WurstPatcher implements IMixinConfigPlugin {

    private static final String WURST_MIXIN_PACKAGE = "net.wurstclient.mixin.";

    private void disableMixins(final String... mixinClasses) {
        final RStream loadedMixinsStream = RStream.of("org.spongepowered.asm.mixin.transformer.MixinConfig");
        final Set<String> loadedMixins = loadedMixinsStream.fields().by("globalMixinList").get();
        for (final String mixinClass : mixinClasses) {
            loadedMixins.add(WURST_MIXIN_PACKAGE + mixinClass);
        }
    }

    @Override
    public void onLoad(final String mixinPackage) {
        System.out.println("Vandalism fries the wurst...");
        this.disableMixins(
                "GameMenuScreenMixin",
                "ShulkerBoxScreenMixin",
                "GenericContainerScreenMixin",
                "MultiplayerScreenMixin",
                "TitleScreenMixin",
                "DisconnectedScreenMixin",
                "DisconnectedRealmsScreenMixin",
                "StatsScreenMixin",
                "PlayerSkinProviderMixin",
                "EntityRendererMixin"
        );
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
