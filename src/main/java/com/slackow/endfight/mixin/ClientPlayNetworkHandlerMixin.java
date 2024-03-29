package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.*;
import net.minecraft.network.class_690;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_469.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onJoin(class_690 gameJoinS2CPacket, CallbackInfo ci) {
        Minecraft.getMinecraft().playerEntity.method_1262("End Fight Mod Enabled");
        if (EndFightMod.SRIGT_LOADED) {
            Medium.onGameJoinIGT();
        }
    }
}
