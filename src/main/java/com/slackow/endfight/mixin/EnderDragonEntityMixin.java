package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.class_956;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends class_956 {
    @Unique
    private static int a = 0;

    @Shadow
    public double field_3742;

    @Shadow
    public double field_3751;

    @Shadow
    public double field_3752;

    @Unique
    int setNewTargetCounter = 0; // increment this every time you call setNewTarget

    @Unique
    int lastSetNewTargetCount = 0;

    @Shadow
    private Entity target;
    @Unique
    private int bedDamaged = 0;

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "method_2896", at = @At("RETURN"))
    public void onDamage(EnderDragonPart enderDragonPart, DamageSource source, int amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (BigConfig.getSelectedConfig().damageInfo) {
                Minecraft.getMinecraft().playerEntity.method_3331("Dragon damaged by " + source.getName() + ": " + amount);
            }
            if (BigConfig.getSelectedConfig().dGodDragon) {
                // method_2668 -> setHealth
                // method_2599 -> getMaxHealth
                // method_2904 -> getHealth
                method_2668(method_2599() - amount);
            }
            if (method_2600() <= 0) {
                int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
                // this runs like four times for some reason, but the time will be really short on the subsequent runs
                if (seconds < 1) return;
                seconds = MathHelper.clamp(seconds, 0, 86399);
                Minecraft.getMinecraft().playerEntity.method_3331(
                        "Dragon Killed in about " + LocalTime.ofSecondOfDay(seconds)
                                .format(DateTimeFormatter.ofPattern("mm:ss")) + " [RTA]");
                EndFightMod.time = System.currentTimeMillis();
                if (EndFightMod.SRIGT_LOADED) {
                    Medium.completeTimerIfEndFight();
                }
            }
        }
    }

    @Unique
    private double myDistanceTo(double targetX, double targetY, double targetZ) {
        double o;
        double p;
        double q;
        double r;
        o = targetX - this.x;
        p = targetY - this.y;
        q = targetZ - this.z;
        r = o * o + p * p + q * q;
        return Math.sqrt(r);
    }

    @Inject(method = "method_2651", at = @At("HEAD"))
    public void onUpdates(CallbackInfo ci) {
        if (!world.isClient && BigConfig.getSelectedConfig().chaosTech > 0) {
            if (lastSetNewTargetCount != setNewTargetCounter) {
                lastSetNewTargetCount = setNewTargetCounter;
                // noinspection unchecked
                List<PlayerEntity> list = this.world.playerEntities;
                PlayerEntity player = list.get(0);
                double targetX = player.x;
                double targetZ = player.z;
                double s = targetX - this.x;
                double t = targetZ - this.z;
                double u = Math.sqrt(s * s + t * t);
                double v = 0.4000000059604645D + u / 80.0D - 1.0D;
                if (v > 10.0D) {
                    v = 10.0D;
                }
                double targetY = player.boundingBox.minY + v;
                double dist = this.myDistanceTo(targetX, targetY, targetZ);
                if (dist >= 10.0 && dist <= 150.0D && (BigConfig.getSelectedConfig().chaosTech == 1 || bedDamaged > 0)) {
                    // System.out.println("you got the strat");
                    player.method_3331("You got Chaos Tech");
                }
            }
            if (bedDamaged > 0) {
                bedDamaged--;
            }
        }
    }

    @Inject(method = "method_2896", at = @At("HEAD"))
    private void setAngry(EnderDragonPart damageSource, DamageSource angry, int par3, CallbackInfoReturnable<Boolean> cir) {
        if (angry == DamageSource.field_3138) {
            bedDamaged = 20;
        }
    }

    @Inject(method = "method_2906", at = @At("TAIL"))
    public void newTarget(CallbackInfo ci) {
        setNewTargetCounter++;
        if (!world.isClient && BigConfig.getSelectedConfig().dPrintDebugMessages) {
            int seconds = (int) ((System.currentTimeMillis() - EndFightMod.time) / 1000);
            seconds = MathHelper.clamp(seconds, 0, 186399);
            String format = LocalTime.ofSecondOfDay(seconds).format(DateTimeFormatter.ofPattern("h:mm:ss"));
            Minecraft.getMinecraft().playerEntity.method_3331("§" + (6 + ((a++) & 3)) +
                    "Rolled 50/50 at " + format + " targeted (" + (target != null ? "player" : "block") + ")");
//            System.out.println("------------------");
//            System.out.println(setNewTargetCounter);
//            System.out.println("dragon pos " + r(x) + " " + r(y) + " " + r(z) + " ");
//            System.out.println("target obj " + target);
//            System.out.println("target coords " + r(field_3742) + " " + r(field_3751) + " " + r(field_3752));
        }
    }

    @Inject(method = "method_2651", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (!world.isClient && BigConfig.getSelectedConfig().dSeeTargetBlock) {
            Medium.targetX = field_3742;
            Medium.targetY = field_3751;
            Medium.targetZ = field_3752;
        }
    }
}
