package com.slackow.endfight.mixin;

import net.minecraft.client.gui.screen.CreditsScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsScreen.class)
public abstract class CreditsScreenMixin {
    @Shadow
    protected abstract void close();

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        close();
    }
}
