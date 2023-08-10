package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.RenderListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.gui.imgui.ImGuiUtil;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;

import java.util.List;

@ModuleInfo(name = "Head Up Display", description = "The In-game HUD of the Mod.", category = FeatureCategory.RENDER, isDefaultEnabled = true)
public class HeadUpDisplayModule extends Module implements RenderListener {

    private final Value<Boolean> enabledModulesList = new BooleanValue(
            "Enabled Modules List",
            "Displays all enabled modules.",
            this,
            true
    );

    private final Value<Boolean> infos = new BooleanValue(
            "Infos",
            "Shows general infos.",
            this,
            true
    );
    private final Value<Boolean> fps = new BooleanValue(
            "FPS",
            "Shows the current fps.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> username = new BooleanValue(
            "Username",
            "Shows the current username.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> position = new BooleanValue(
            "Position",
            "Shows the current position.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> serverBrand = new BooleanValue(
            "Server Brand",
            "Shows the current server brand.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> difficulty = new BooleanValue(
            "Difficulty",
            "Shows the current world difficulty.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> permissionsLevel = new BooleanValue(
            "Permissions Level",
            "Shows the current permissions level.",
            this,
            true
    ).visibleConsumer(this.infos::getValue);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(RenderListener.Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(RenderListener.Render2DEvent.ID, this);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (mc.currentScreen == null) this.render();
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (mc.currentScreen != null && (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof InventoryScreen)) {
            this.render();
        }
    }

    private final List<String> enabledModules = new ObjectArrayList<>();
    private boolean sort = false;

    public void sortEnabledModules() {
        this.sort = true;
    }

    private void render() {
        Foxglove.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (this.sort) {
                this.sort = false;
                this.enabledModules.clear();
                final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
                for (final Module module : modules) {
                    final boolean display = module.isEnabled() && module.isShowInModuleList();
                    final String toDisplay = module.getName();
                    if (display) this.enabledModules.add(toDisplay);
                }
                this.enabledModules.sort((s1, s2) -> Float.compare(ImGui.calcTextSize(s2).x, ImGui.calcTextSize(s1).x));
            }
            final ClientPlayerEntity player = mc.player;
            final ClientWorld world = mc.world;
            if (player == null || world == null || mc.options.debugEnabled || mc.options.hudHidden) return;
            final int windowFlags = ImGuiUtil.getInGameFlags(0);
            if (this.enabledModulesList.getValue()) {
                if (ImGui.begin("Enabled Modules List##headupdisplaymodule", windowFlags)) {
                    final boolean empty = this.enabledModules.isEmpty();
                    for (final String enabledModule : this.enabledModules) {
                        ImGui.text(enabledModule);
                    }
                    ImGui.setWindowSize(empty ? 100 : 0, empty ? 50 : 0);
                    ImGui.end();
                }
            }
            if (this.infos.getValue()) {
                if (ImGui.begin("Infos##headupdisplaymodule", windowFlags)) {
                    ImGui.setWindowSize(0, 0);
                    if (this.fps.getValue()) {
                        ImGui.text("FPS: " + MinecraftClient.getInstance().getCurrentFps());
                    }
                    if (this.username.getValue()) {
                        ImGui.text("Username: " + player.getGameProfile().getName());
                    }
                    if (this.position.getValue()) {
                        ImGui.text("Position: " + player.getBlockPos().toShortString());
                    }
                    if (this.serverBrand.getValue()) {
                        final String serverBrand = player.getServerBrand();
                        if (serverBrand != null) {
                            ImGui.text("Server Brand: " + serverBrand.replaceFirst("\\(.*?\\) ", ""));
                        }
                    }
                    if (this.difficulty.getValue()) {
                        ImGui.text("Difficulty: " + world.getDifficulty().getName());
                    }
                    if (this.permissionsLevel.getValue()) {
                        ImGui.text("Permissions Level: " + player.getPermissionLevel());
                    }
                    ImGui.end();
                }
            }
        });
    }

}
