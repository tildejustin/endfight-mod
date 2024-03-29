package com.slackow.endfight.mixin;

import net.minecraft.command.*;
import net.minecraft.server.command.CommandRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(CommandRegistry.class)
public class CommandRegistryMixin {
    @Redirect(method = "executeCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/Command;isAccessible(Lnet/minecraft/command/CommandSource;)Z"))
    public boolean makeAllAccessible(Command instance, CommandSource commandSource) {
        return true;
    }

    @Redirect(method = "method_3311", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/Command;isAccessible(Lnet/minecraft/command/CommandSource;)Z"))
    public boolean makeAllAccessible2(Command instance, CommandSource commandSource) {
        return true;
    }
}
