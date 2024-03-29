package com.slackow.endfight.mixin;
// the necessity of this class is quite frankly hilarious

import com.redlimerl.speedrunigt.timer.*;
import com.redlimerl.speedrunigt.timer.category.RunCategory;
import com.slackow.endfight.speedrunigt.EndFightCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ControllablePlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = InGameTimer.class, remap = false)
public abstract class InGameTimerMixin {
    @Shadow
    public abstract void setStatus(@NotNull TimerStatus status);

    @Inject(method = "setCategory", at = @At("HEAD"))
    public void setCategory(RunCategory category, boolean canSendPacket, CallbackInfo ci) {
        if (category == EndFightCategory.END_FIGHT_CATEGORY) {
            setStatus(TimerStatus.NONE);
            ControllablePlayerEntity player = Minecraft.getMinecraft().playerEntity;
            // canSendPacket only true if done through GUI
            if (player != null && canSendPacket) {
                player.method_3331("Timer will start on execution of /reset");
            }
        }
    }
}
