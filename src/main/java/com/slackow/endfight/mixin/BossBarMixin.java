package com.slackow.endfight.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.slackow.endfight.config.BigConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameHud.class)
public class BossBarMixin {
    @ModifyVariable(method = "renderBossBar", at = @At("STORE"))
    private String addSpecificText(String text, @Local EnderDragonEntity dragon) {
        // get name, get health, get max health
        if (BigConfig.getSelectedConfig().specificHealthBar) {
            // method_2904 -> getHealth
            // method_2599 -> getMaxHealth
            return text + ": " + dragon.method_2600() + "/" + dragon.method_2599();
        }
        return text;
    }
}
