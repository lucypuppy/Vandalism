package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.ListValue;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.ArrayList;
import java.util.List;

public class HeadUpDisplayModule extends Module implements RenderListener {

    private final Value<Boolean> inventoryScreenBringToFront = new BooleanValue(
            "Inventory Screen bring to front",
            "Renders the hud over inventory screens.",
            this,
            false
    );

    private final Value<Boolean> gameMenuScreenBringToFront = new BooleanValue(
            "Game Menu Screen bring to front",
            "Renders the hud over the game menu screen.",
            this,
            false
    );

    private final Value<Boolean> transparent = new BooleanValue(
            "Transparent",
            "Makes the background and the title bar from the HUD elements invisible.",
            this,
            false
    );

    private final Value<Boolean> enabledModulesList = new BooleanValue(
            "Enabled Modules List",
            "Displays all enabled modules.",
            this,
            true
    );

    private final Value<String> enabledModulesListSortDirection = new ListValue(
            "Enabled Modules List Sort Direction",
            "Change the direction which the enabled modules are sorted to.",
            this,
            "Up",
            "Down"
    ).visibleConsumer(this.enabledModulesList::getValue).valueChangeConsumer((newValue) -> this.sortEnabledModules());

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
            this.infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> username = new BooleanValue(
            "Username",
            "Shows the current username.",
            this.infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> position = new BooleanValue(
            "Position",
            "Shows the current position.",
            this.infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> serverBrand = new BooleanValue(
            "Server Brand",
            "Shows the current server brand.",
            this.infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> difficulty = new BooleanValue(
            "Difficulty",
            "Shows the current world difficulty.",
            this.infoElements,
            true
    ).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> permissionsLevel = new BooleanValue(
            "Permissions Level",
            "Shows the current permissions level.",
            this.infoElements,
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
    public void onRender2DInGame(final DrawContext context, final float delta) {
        if (currentScreen() == null) this.render();
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (currentScreen() instanceof ChatScreen ||
                (currentScreen() instanceof InventoryScreen && this.inventoryScreenBringToFront.getValue()) ||
                (currentScreen() instanceof GameMenuScreen && this.gameMenuScreenBringToFront.getValue())
        ) render();
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
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {

            // sort enabled modules
            if (this.sort) {
                this.sort = false;
                this.enabledModules.clear();
                final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();

                for (final Module module : modules) {
                    if (module.isEnabled() && module.isShowInModuleList()) {
                        this.enabledModules.add(module.getName());
                    }
                }

                this.enabledModules.sort((s1, s2) -> {
                    final int compare;
                    switch (this.enabledModulesListSortDirection.getValue()) {
                        case "Up" -> compare = Float.compare(ImGui.calcTextSize(s2).x, ImGui.calcTextSize(s1).x);
                        case "Down" -> compare = Float.compare(ImGui.calcTextSize(s1).x, ImGui.calcTextSize(s2).x);
                        default -> compare = 0;
                    }
                    return compare;
                });
            }

            if (player() == null || world() == null || !mc().inGameHud.getDebugHud().shouldShowDebugHud()) {
                return;
            }

            final int windowFlags = this.transparent.getValue() ? ImGuiUtil.getInGameFlags(0) : (mouse().isCursorLocked() ? ImGuiWindowFlags.NoCollapse : 0);

            // Render watermark
            if (this.watermark.getValue()) {
                if (ImGui.begin("Watermark##headupdisplaymodule", windowFlags | ImGuiWindowFlags.NoResize)) {
                    ImGui.setWindowSize(0, 0);
                    ImGui.text(Vandalism.getInstance().getName() + "\tv" + Vandalism.getInstance().getVersion());
                    ImGui.text("Made by " + Vandalism.getInstance().getAuthor());
                    ImGui.end();
                }
            }

            // Render enabled modules list
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

                    if (this.fps.getValue()) {
                        ImGui.text("FPS: " + mc().getCurrentFps());
                    }

                    if (this.username.getValue()) {
                        ImGui.text("Username: " + player().getGameProfile().getName());
                    }

                    if (this.position.getValue()) {
                        ImGui.text("Position: " + player().getBlockPos().toShortString());
                    }

                    if (this.serverBrand.getValue()) {
                        final String serverBrand = networkHandler().getBrand();
                        if (serverBrand != null) {
                            ImGui.text("Server Brand: " + serverBrand.replaceFirst("\\(.*?\\) ", ""));
                        }
                    }

                    if (this.difficulty.getValue()) {
                        ImGui.text("Difficulty: " + world().getDifficulty().getName());
                    }

                    if (this.permissionsLevel.getValue()) {
                        ImGui.text("Permissions Level: " + player().getPermissionLevel());
                    }

                    ImGui.end();
                }
            }

        });
    }

}
