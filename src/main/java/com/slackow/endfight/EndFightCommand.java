package com.slackow.endfight;

import net.minecraft.command.*;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public abstract class EndFightCommand extends AbstractCommand {
    public static void heal(PlayerEntity player) {
        player.method_2668(20);
        player.extinguish();
        player.getHungerManager().add(20, 1);
        player.getHungerManager().add(1, -8); // -16
        player.fallDistance = 0;
    }

    @Override
    public boolean isAccessible(CommandSource source) {
        return true;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof EndFightCommand) {
            return getCommandName().compareTo(((EndFightCommand) o).getCommandName());
        }
        return 1;
    }
}
