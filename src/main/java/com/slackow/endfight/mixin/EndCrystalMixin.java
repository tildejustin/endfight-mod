package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalMixin extends Entity {
    @Unique
    private static char state = 'c';

    public EndCrystalMixin(World world) {
        super(world);
    }

    /**
     * @author Slackow
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void damage(DamageSource damage, int par2, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (!world.isClient && BigConfig.getSelectedConfig().dGodCrystals) {

                // noinspection unchecked
                List<PlayerEntity> list = this.world.playerEntities;
                PlayerEntity player = list.get(0);
                player.method_3331("§" + (state = (char) ('c' + '4' - state)) + "*BOOM*");
                world.spawnEntity(new EndCrystalEntity(world, x, y, z));
            }
        }
    }
}
