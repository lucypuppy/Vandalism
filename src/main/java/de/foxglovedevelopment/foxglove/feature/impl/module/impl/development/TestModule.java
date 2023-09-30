package de.foxglovedevelopment.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.event.RenderListener;
import de.foxglovedevelopment.foxglove.event.TickListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import de.foxglovedevelopment.foxglove.util.click.ClickGenerator;
import de.foxglovedevelopment.foxglove.util.click.clickers.BoxMuellerClicker;
import de.foxglovedevelopment.foxglove.util.rotation.RotationPriority;
import de.foxglovedevelopment.foxglove.util.rotation.rotationtypes.Rotation;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.values.number.slider.SliderFloatValue;
import de.foxglovedevelopment.foxglove.value.values.number.slider.SliderIntegerValue;
import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class TestModule extends Module implements TickListener, RenderListener {

    private final ClickGenerator clickGenerator;

    private final Value<Float> mean = new SliderFloatValue(
            "Mean",
            "mean",
            this,
            15.0F,
            0.0F,
            20.0F
    );
    private final Value<Float> std = new SliderFloatValue(
            "std",
            "std",
            this,
            2.0F,
            0.0F,
            10.0F
    );

    private final Value<Integer> updatePossibility = new SliderIntegerValue(
            "updatePossibility",
            "updatePossibility",
            this,
            50,
            0,
            100
    );

    public TestModule() {
        super(
                "Test",
                "Just for development purposes.",
                FeatureCategory.DEVELOPMENT,
                true,
                false
        );
        this.clickGenerator = new BoxMuellerClicker();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);

        Foxglove.getInstance().getRotationListener().resetRotation();
    }

    @Override
    public void onTick() {
        if (world() == null || player() == null) return;
        final List<Entity> entities = new ArrayList<>();
        world().getEntities().forEach(entity -> {
            if (entity instanceof PlayerEntity && player().distanceTo(entity) < 6 && entity != player()) {
                entities.add(entity);
            }
        });
        if (entities.isEmpty()) {
            Foxglove.getInstance().getRotationListener().resetRotation();
            return;
        }
        final Entity target = entities.get(0);

        final Rotation rotation = Rotation.Builder.build(target, true, 3.5f, 0.1D);

        if (rotation != null) {
            Foxglove.getInstance().getRotationListener().setRotation(rotation, new Vec2f(20, 30), RotationPriority.HIGH);
        }

        if (this.clickGenerator instanceof final BoxMuellerClicker clicker) {
            clicker.setMean(this.mean.getValue());
            clicker.setStd(this.std.getValue());
            clicker.setUpdatePossibility(this.updatePossibility.getValue());
        }

        this.clickGenerator.setClickAction(mc()::doAttack);
        this.clickGenerator.update();
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        Foxglove.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (ImGui.begin("Graph")) {
                if (this.clickGenerator instanceof final BoxMuellerClicker clicker) {
                    final int size = clicker.getDelays().getNormalList().size();

                    if(size > 5) {
                        final Long[] xAxis = new Long[size];
                        final Long[] yAxis = new Long[size];
                        final Long[] yAxis2 = new Long[size];

                        for (int i = 0; i < size; i++) {
                            xAxis[i] = (long) i;
                            yAxis[i] = clicker.getDelays().getNormalList().get(i).getLeft();
                            yAxis2[i] = clicker.getDelays().getNormalList().get(i).getRight().longValue() * 20L;
                        }

                        if (ImPlot.beginPlot("CPSGraph")) {
                            ImPlot.plotLine("Delay", xAxis, yAxis);
                            ImPlot.plotLine("CPS", xAxis, yAxis2);
                            ImPlot.endPlot();
                        }
                    }
                }

                ImGui.end();
            }
        });
    }

}
