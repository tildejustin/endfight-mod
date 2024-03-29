package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends MobEntity {
    public PlayerMixin(World world) {
        super(world);
    }

    @Inject(method = "damage", at = @At("TAIL"))
    public void onDamage(DamageSource source, int damage, CallbackInfoReturnable<Boolean> cir) {
        if (BigConfig.getSelectedConfig().dGodPlayer && !source.isOutOfWorld()) {
            this.method_2668(20);
        }
    }
}
