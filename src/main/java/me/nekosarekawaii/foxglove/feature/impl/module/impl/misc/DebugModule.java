package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.Render2DListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

@ModuleInfo(name = "Debug Module", description = "Debug some stuff about the client.", category = FeatureCategory.MISC)
public class DebugModule extends Module implements Render2DListener {

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        Foxglove.getInstance().imGuiRenderer.addRenderInterface(io -> {
            if (ImGui.begin("asdas")) {

                ImGui.end();
            }
        });
    }

}
