package de.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.FeatureList;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiUtil;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.ValueCategory;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;

import java.util.ArrayList;
import java.util.List;

public class HeadUpDisplayModule extends Module implements RenderListener {

    private final Value<Boolean> transparent = new BooleanValue(
            "Transparent",
            "Makes the background and the titlebar from the HUD elements invisible.",
            this,
            false
    );

    private final Value<Boolean> enabledModulesList = new BooleanValue(
            "Enabled Modules List",
            "Displays all enabled modules.",
            this,
            true
    );

    private final Value<Boolean> watermark = new BooleanValue(
            "Watermark",
            "Shows the watermark.",
            this,
            true
    );

    private final Value<Boolean> infos = new BooleanValue(
            "Infos",
            "Shows general infos.",
            this,
            true
    );

    private final ValueCategory infoElements = new ValueCategory(
            "Info Elements",
            "Elements that are shown in the infos category.",
            this
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> fps = new BooleanValue(
            "FPS",
            "Shows the current fps.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> username = new BooleanValue(
            "Username",
            "Shows the current username.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> position = new BooleanValue(
            "Position",
            "Shows the current position.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> serverBrand = new BooleanValue(
            "Server Brand",
            "Shows the current server brand.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> difficulty = new BooleanValue(
            "Difficulty",
            "Shows the current world difficulty.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> permissionsLevel = new BooleanValue(
            "Permissions Level",
            "Shows the current permissions level.",
            infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (currentScreen() == null) this.render();
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (currentScreen() != null) {
            this.render();
        }
    }

    private final List<String> enabledModules;
    private boolean sort;

    public HeadUpDisplayModule() {
        super(
                "Head Up Display",
                "Shows various infos from the game and the mod in game.",
                FeatureCategory.RENDER,
                false,
                true
        );
        this.enabledModules = new ArrayList<>();
        this.sort = false;
    }

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

            if (player() == null || world() == null || options().debugEnabled || options().hudHidden || currentScreen() instanceof InventoryScreen)
                return;

            final int windowFlags = this.transparent.getValue() ? ImGuiUtil.getInGameFlags(0) : (mouse().isCursorLocked() ? ImGuiWindowFlags.NoCollapse : 0);

            // Render Watermark
            if (watermark.getValue()) {
                if (ImGui.begin("Watermark##headupdisplaymodule", windowFlags | ImGuiWindowFlags.NoResize)) {
                    ImGui.setWindowSize(0, 0);
                    ImGui.text(Foxglove.getInstance().getName() + "\tv" + Foxglove.getInstance().getVersion());
                    ImGui.text("Made by " + Foxglove.getInstance().getAuthorsAsString());
                    ImGui.end();
                }
            }

            // Render module list
            if (this.enabledModulesList.getValue()) {
                if (ImGui.begin("Enabled Modules##headupdisplaymodule", windowFlags)) {
                    final boolean empty = this.enabledModules.isEmpty();

                    for (final String enabledModule : this.enabledModules) {
                        ImGui.text(enabledModule);
                    }

                    ImGui.setWindowSize(empty ? 100 : 0, empty ? 50 : 0);
                    ImGui.end();
                }
            }

            // Render infos
            if (this.infos.getValue()) {
                if (ImGui.begin("Infos##headupdisplaymodule", windowFlags)) {
                    ImGui.setWindowSize(0, 0);

                    if (this.fps.getValue())
                        ImGui.text("FPS: " + mc().getCurrentFps());

                    if (this.username.getValue())
                        ImGui.text("Username: " + player().getGameProfile().getName());

                    if (this.position.getValue())
                        ImGui.text("Position: " + player().getBlockPos().toShortString());

                    if (this.serverBrand.getValue()) {
                        final String serverBrand = player().getServerBrand();
                        if (serverBrand != null)
                            ImGui.text("Server Brand: " + serverBrand.replaceFirst("\\(.*?\\) ", ""));
                    }

                    if (this.difficulty.getValue())
                        ImGui.text("Difficulty: " + world().getDifficulty().getName());

                    if (this.permissionsLevel.getValue())
                        ImGui.text("Permissions Level: " + player().getPermissionLevel());

                    ImGui.end();
                }
            }
        });
    }

}
