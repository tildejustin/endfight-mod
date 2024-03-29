package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.mob.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EndermanEntity.class)
public abstract class EndermanMixin extends HostileEntity {
    public EndermanMixin(World world) {
        super(world);
    }

    @ModifyVariable(method = "isPlayerStaring", at = @At("STORE"), ordinal = 0)
    public Vec3d isStaring(Vec3d value) {
        if (BigConfig.getSelectedConfig().enderMan == 1) {
            // Replace player direction with looking straight down
            return Vec3d.method_603().getOrCreate(0, -1, 0);
        } else {
            return value;
        }
    }
}
