package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.impl.Render2DListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.imgui.ImGuiUtil;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;

@ModuleInfo(name = "HUD", description = "The In-game Overlay of the Mod.", category = FeatureCategory.RENDER, isDefaultEnabled = true)
public class HUDModule extends Module implements Render2DListener {

    private final Value<Boolean> moduleList = new BooleanValue("Module List", "Shows the Module List.", this, true);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DListener.Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DListener.Render2DEvent.ID, this);
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

    private void render() {
        if (mc.player == null)
            return;

        Foxglove.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (mc.options.debugEnabled || mc.options.hudHidden)
                return;

            if (this.moduleList.getValue()) {
                if (ImGui.begin("Module List", ImGuiUtil.getInGameFlags(0))) {
                    ImGui.setWindowSize(0, 0);

                    final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
                    for (final Module module : modules) {
                        if (module != this && module.isEnabled()) {
                            ImGui.text(module.getName());
                        }
                    }

                    ImGui.end();
                }
            }
        });
    }

}
