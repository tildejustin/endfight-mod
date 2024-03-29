package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonEntityRenderer.class)
public class RenderDragonMixin {
    @Inject(method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;DDDFF)V", at = @At("TAIL"))
    private void render(EnderDragonEntity d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
        int a = BigConfig.getSelectedConfig().deathBox;
        if (a == 2) {
            double dx = e - d.x;
            double dy = f - d.y;
            double dz = g - d.z;
            Medium.drawBox(0xff0000, d.partHead.boundingBox.expand(1.0D, 1.0D, 1.0D).offset(dx, dy, dz));
        }
        if (BigConfig.getSelectedConfig().dSeeTargetBlock) {
            double dx = e - d.x;
            double dy = f - d.y;
            double dz = g - d.z;
            Medium.drawBox(0x00ff00, Box.of(Medium.targetX, Medium.targetY, Medium.targetZ, Medium.targetX, Medium.targetY, Medium.targetZ)
                    .expand(0.5, 0.5, 0.5).offset(dx, dy, dz));
        }
    }
}
