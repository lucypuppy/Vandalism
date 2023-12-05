package de.vandalismdevelopment.vandalism.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.MovementListener;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.Clicker;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.impl.BoxMuellerClicker;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.Rotation;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationListener;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationPriority;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderFloatValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TestModule extends Module implements TickListener, RenderListener, MovementListener {

    private final Clicker clicker;

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
        this.clicker = new BoxMuellerClicker();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
        DietrichEvents2.global().subscribe(StrafeEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(SprintEvent.ID, this);
        DietrichEvents2.global().subscribe(MoveInputEvent.ID, this);
        DietrichEvents2.global().subscribe(MoveFlyingEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        DietrichEvents2.global().unsubscribe(StrafeEvent.ID, this);
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().unsubscribe(SprintEvent.ID, this);
        DietrichEvents2.global().unsubscribe(MoveInputEvent.ID, this);
        DietrichEvents2.global().unsubscribe(MoveFlyingEvent.ID, this);
        Vandalism.getInstance().getRotationListener().resetRotation();
    }

    @Override
    public void onTick() {
        if (this.world() == null || this.player() == null) return;
        final List<Entity> entities = new ArrayList<>();
        this.world().getEntities().forEach(entity -> {
            if (this.player().distanceTo(entity) < 6 && entity != this.player()) {
                entities.add(entity);
            }
        });
        if (entities.isEmpty()) {
            Vandalism.getInstance().getRotationListener().resetRotation();
            return;
        }
        final Entity target = entities.get(0);
        final Rotation rotation = Rotation.Builder.build(target, true, 3f, 1D / 32);

        // ChatUtil.chatMessage("" + h + " / " + g + " / " + f);
        if (rotation == null) { //sanity check, crashes if you sneak and have your reach set to 3.0
            return;
        }
        Vandalism.getInstance().getRotationListener().setRotation(rotation, new Vec2f(179, 180), RotationPriority.HIGH);
        //this.player().setYaw(rotation.getYaw());
        //this.player().setPitch(rotation.getPitch());
        this.handleAttack(true, target);

    }

    //TODO: Make a proper util
    private void handleAttack(final boolean legacyAttacking, Entity target) {
        if (legacyAttacking) {
            if (this.clicker instanceof final BoxMuellerClicker clicker) {
                clicker.setMean(this.mean.getValue());
                clicker.setStd(this.std.getValue());
                clicker.setUpdatePossibility(this.updatePossibility.getValue());
            }
            this.clicker.setClickAction(this.mc()::doAttack);
            this.clicker.update();
        } else {
            float baseAttackDamage = (float) player().getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float enchantmentBonus;
            if (target instanceof LivingEntity) {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(player().getMainHandStack(), ((LivingEntity) target).getGroup());
            } else {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(player().getMainHandStack(), EntityGroup.DEFAULT);
            }
            float attackCooldown = player().getAttackCooldownProgress(0.5F);
            baseAttackDamage *= 0.2F + attackCooldown * attackCooldown * 0.8F;
            enchantmentBonus *= attackCooldown;
            if (baseAttackDamage >= 1) {
                this.mc().doAttack();
            }
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        /*Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (ImGui.begin("Graph##testmodule", Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags())) {
                if (this.clicker instanceof final BoxMuellerClicker clicker) {
                    final int size = clicker.getDelays().getNormalList().size();
                    if (size > 5) {
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
        });*/
    }

    @Override
    public void onSprint(final SprintEvent event) {
    }

    @Override
    public void onMoveInput(final MoveInputEvent event) {
        /*final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation == null) return;
        float deltaYaw = player().getYaw() - rotation.getYaw();

        float x = event.movementSideways;
        float z = event.movementForward;

        float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
        float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

        event.movementSideways = Math.round(newX);
        event.movementForward = Math.round(newZ);*/
    }

    @Override
    public void onStrafe(final StrafeEvent event) {
        final RotationListener rotation = Vandalism.getInstance().getRotationListener();
        if (rotation.getRotation() == null || rotation.getTargetRotation() == null) return;
        event.yaw = rotation.getRotation().getYaw();
        float[] INPUTS = MovementUtil.getFixedMoveInputs(event.yaw);
        if (INPUTS[0] == 0f && INPUTS[1] == 0f) {
            return;
        }
        event.movementInput = new Vec3d(INPUTS[0], player().upwardSpeed, INPUTS[1]);
    }

    @Override
    public void onMoveFlying(final MoveFlyingEvent event) {
    }

}
