package de.vandalismdevelopment.vandalism.feature.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.MovementListener;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.util.MovementUtil;
import de.vandalismdevelopment.vandalism.util.WorldUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.Clicker;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.impl.BoxMuellerClicker;
import de.vandalismdevelopment.vandalism.integration.rotation.Rotation;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationListener;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderFloatValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TestModule extends AbstractModule implements TickListener, RenderListener, MovementListener {

    private final Clicker clicker = new BoxMuellerClicker();

    private final Value<Float> mean = new SliderFloatValue("Mean", "mean", this, 15.0F, 0.0F, 20.0F);
    private final Value<Float> std = new SliderFloatValue("std", "std", this, 2.0F, 0.0F, 10.0F);
    private final Value<Integer> updatePossibility = new SliderIntegerValue("updatePossibility", "updatePossibility", this, 50, 0, 100);

    private PlayerEntity target;
    private Vec3d rotationVector;

    public TestModule() {
        super("Test", "Just for development purposes.", Category.DEVELOPMENT);
        setExperimental(true);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
        DietrichEvents2.global().subscribe(StrafeEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(MoveInputEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
        DietrichEvents2.global().unsubscribe(StrafeEvent.ID, this);
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().unsubscribe(MoveInputEvent.ID, this);
        Vandalism.getInstance().getRotationListener().resetRotation();
    }

    //TODO mouse event -> attack
    //TODO: frame event (entityrenderer set angles)-> rotate

    @Override
    public void onTick() {
        if (this.mc.world == null || this.mc.player == null) return;
        final List<PlayerEntity> entities = new ArrayList<>();
        this.mc.world.getPlayers().forEach(entity -> {
            if (this.mc.player.distanceTo(entity) < 6 && entity != this.mc.player) {
                entities.add(entity);
            }
        });

        if (entities.isEmpty()) {
            return;
        }
        target = entities.get(0);
        //   if(this.rotationVector != null) {
        if (!target.isBlocking() && this.rotationVector != null && WorldUtil.rayTraceRamge(mc.player.getYaw(), mc.player.getPitch()) <= 3)
            this.handleAttack(false, target);
        // }
    }

    //TODO: Make a proper util
    private void handleAttack(final boolean legacyAttacking, Entity target) {
        if (legacyAttacking) {
            if (this.clicker instanceof final BoxMuellerClicker clicker) {
                clicker.setMean(this.mean.getValue());
                clicker.setStd(this.std.getValue());
                clicker.setUpdatePossibility(this.updatePossibility.getValue());
            }
            this.clicker.setClickAction(this.mc::doAttack);
            this.clicker.update();
        } else {
            float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float enchantmentBonus;
            if (target instanceof LivingEntity) {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), ((LivingEntity) target).getGroup());
            } else {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), EntityGroup.DEFAULT);
            }
            float attackCooldown = mc.player.getAttackCooldownProgress(0F);
            baseAttackDamage *= 0.2F + attackCooldown * attackCooldown * 0.8F;
            enchantmentBonus *= attackCooldown;
            if (baseAttackDamage >= 0.98) {
                this.mc.doAttack();
            }
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        //ChatUtil.infoChatMessage(target.toString());
        if (target != null) {
            final Rotation rotation = Rotation.Builder.build(target, true, 6f, 1D / 32);
            if (rotation == null) { //sanity check, crashes if you sneak and have your reach set to 3.0
                this.rotationVector = null;
                return;
            }
            //   Vandalism.getInstance().getRotationListener().setRotation(rotation, new Vec2f(179, 180), RotationPriority.HIGH);
            mc.player.setYaw(rotation.getYaw());
            mc.player.setPitch(rotation.getPitch());
            this.rotationVector = new Vec3d(1, 1, 1);
        }
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
    public void onMoveInput(final MoveInputEvent event) {
        /*final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (rotation == null) return;
        float deltaYaw = mc.player.getYaw() - rotation.getYaw();

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
        event.movementInput = new Vec3d(INPUTS[0], mc.player.upwardSpeed, INPUTS[1]);
    }

}
