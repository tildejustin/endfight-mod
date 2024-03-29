package com.slackow.endfight.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "method_3333", at = @At("HEAD"), cancellable = true)
    private void bypassCheats(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
