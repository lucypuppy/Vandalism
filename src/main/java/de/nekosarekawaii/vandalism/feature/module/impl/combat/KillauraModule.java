package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.base.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationListener;
import de.nekosarekawaii.vandalism.integration.rotation.RotationPriority;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;
import de.nekosarekawaii.vandalism.util.minecraft.WorldUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class KillauraModule extends AbstractModule implements TickGameListener, StrafeListener, Render2DListener, MoveInputListener, de.nekosarekawaii.vandalism.base.event.player.RotationListener {

    public final Value<Double> range =
            new DoubleValue(this, "Range", "Increases your maximum range to fist enemies", 3.0, 3.0, 6.0);

    private PlayerEntity target;
    private Vec3d rotationVector;
    private final RotationListener rotationListener;

    public KillauraModule() {
        super("Killaura", "A module that automatically kills entities in minecraft.", Category.DEVELOPMENT);
        this.rotationListener = Vandalism.getInstance().getRotationListener();

        setExperimental(true);
    }

    @Override
    public void onEnable() {
        Vandalism.getEventSystem().subscribe(TickGameEvent.ID, this);
        Vandalism.getEventSystem().subscribe(StrafeEvent.ID, this);
        Vandalism.getEventSystem().subscribe(Render2DEvent.ID, this);
        Vandalism.getEventSystem().subscribe(MoveInputEvent.ID, this);
        Vandalism.getEventSystem().subscribe(RotationEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getEventSystem().unsubscribe(TickGameEvent.ID, this);
        Vandalism.getEventSystem().unsubscribe(StrafeEvent.ID, this);
        Vandalism.getEventSystem().unsubscribe(Render2DEvent.ID, this);
        Vandalism.getEventSystem().unsubscribe(MoveInputEvent.ID, this);
        Vandalism.getEventSystem().unsubscribe(RotationEvent.ID, this);
        this.rotationListener.resetRotation();
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
        this.target = entities.get(0);

        if (this.rotationVector == null)
            return;

        double raytraceDistance = -1;
        //TODO: need serverside yaw/pitch or move into proper events.
        if (this.rotationListener.getTargetRotation() != null) {
            if (Float.isNaN(this.rotationListener.getTargetRotation().getYaw()) || Float.isNaN(this.rotationListener.getTargetRotation().getPitch()))
                return;
            raytraceDistance = WorldUtil.rayTraceRange(this.rotationListener.getTargetRotation().getYaw(), this.rotationListener.getTargetRotation().getPitch(), true);
        }
        // if(raytraceDistance > 3){
        // }
        if (!target.isBlocking() && raytraceDistance <= this.range.getValue() - 0.05 && raytraceDistance > 0)
            this.handleAttack();
    }

    //TODO: Make a proper util
    private void handleAttack() {
        float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            /*float enchantmentBonus;
            if (target instanceof LivingEntity) {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), ((LivingEntity) target).getGroup());
            } else {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), EntityGroup.DEFAULT);
            }*/
        float additionalBaseDelayOffset = 0;
        if (TimerHack.getSpeed() > 1) {
            additionalBaseDelayOffset = -(TimerHack.getSpeed() - 1);
        }
        float attackCooldown = mc.player.getAttackCooldownProgress(additionalBaseDelayOffset);
        baseAttackDamage *= 0.2F + attackCooldown * attackCooldown * 0.8F;
        // enchantmentBonus *= attackCooldown;
        if (baseAttackDamage >= 0.98) {
            this.mc.doAttack();
        } else {
            if (this.mc.player.getOffHandStack().isEmpty())
                return;
            if (this.mc.player.getOffHandStack().getItem().equals(Items.SHIELD)) {
                this.mc.doItemUse();
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
    public void onMoveInput(final MoveInputEvent event) {
        /*final Rotation rotation = this.rotationListener.getRotation();
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
       /* if (this.rotationListener.getRotation() == null || this.rotationListener.getTargetRotation() == null) return;
        event.yaw = this.rotationListener.getRotation().getYaw();
        float[] INPUTS = MovementUtil.getFixedMoveInputs(event.yaw);
        if (INPUTS[0] == 0f && INPUTS[1] == 0f) {
            return;
        }
        event.movementInput = new Vec3d(INPUTS[0], mc.player.upwardSpeed, INPUTS[1]);*/
    }

    @Override
    public void onRotation(RotationEvent event) {
        if (this.target != null) {
            final Rotation rotation = Rotation.Builder.build(this.target, true, 6f, 1D / 32);
            if (rotation == null) { //sanity check, crashes if you sneak and have your reach set to 3.0
                //   ChatUtil.infoChatMessage("hä");
                this.rotationVector = null;
                this.rotationListener.resetRotation();
                return;
            }
            this.rotationListener.setRotation(rotation, new Vec2f(179, 180), RotationPriority.HIGH);
            this.rotationVector = new Vec3d(1, 1, 1);
        } else {
            this.rotationListener.resetRotation();
        }
    }
}
