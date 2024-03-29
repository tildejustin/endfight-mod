package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.mob.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {
    @SuppressWarnings("ConstantValue")
    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    public void a(CallbackInfoReturnable<Boolean> cir) {
        if (BigConfig.getSelectedConfig().enderMan == 0 && ((Object) this) instanceof EndermanEntity) {
            cir.setReturnValue(false);
        }
    }
}
