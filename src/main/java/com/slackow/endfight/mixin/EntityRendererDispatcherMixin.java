package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererDispatcherMixin {
    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void render(Entity entity, double x, double y, double z, float yaw, float tickDelta, CallbackInfo ci) {
        if (entity instanceof EndCrystalEntity && BigConfig.getSelectedConfig().arrowHelp) {
            Minecraft client = Minecraft.getMinecraft();
            PlayerInventory inv = client.playerEntity.inventory;
            ItemStack itemStack = inv.main[inv.selectedSlot];
            if (itemStack != null && itemStack.getItem() instanceof BowItem && client.playerEntity.getItemUseTicks() > 0) {
                this.renderHitbox(entity, x, y, z, yaw, tickDelta);
            }
        }
    }

    @Unique
    private void renderHitbox(Entity entity, double d, double e, double f, float g, float h) {
        GL11.glDepthMask(false);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        float var10 = entity.width / 2.0F;
        Box var11 = Box.of(d - (double) var10, e, f - (double) var10, d + (double) var10, e + (double) entity.height, f + (double) var10);
        Medium.renderBox(var11, 16777215);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }
}
