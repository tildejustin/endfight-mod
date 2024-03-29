package com.slackow.endfight.commands;

import com.redlimerl.speedrunigt.timer.*;
import com.slackow.endfight.*;
import com.slackow.endfight.config.*;
import com.slackow.endfight.gui.config.ConfigGUI;
import com.slackow.endfight.speedrunigt.EndFightCategory;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.Minecraft;
import net.minecraft.command.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.server.world.*;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class ResetCommand extends EndFightCommand {
    @Override
    public String getCommandName() {
        return "reset";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "/reset [out|options]";
    }

    @Override
    public List<String> method_3276(CommandSource source, String[] args) {
        if (args.length == 1) {
            return Stream.of("options", "out")
                    .filter(option -> option.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length > 0 && "options".equals(args[0])) {
            Medium.commandMap.forEach(command ->
                    source.method_3331("§c" + command.getUsageTranslationKey(source)));
            return;
        }
        boolean twice = args.length == 0 || !args[0].contains("o");
        if (source instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source;
            MinecraftServer server = MinecraftServer.getServer();
            for (Object objP : server.getPlayerManager().players) {
                if (objP instanceof PlayerEntity) {
                    PlayerEntity p = (PlayerEntity) objP;
                    if (p.dimension == 1) {
                        Arrays.fill(p.inventory.main, null);
                        Arrays.fill(p.inventory.armor, null);
                        // set creative mode
                        p.method_3170(GameMode.CREATIVE);
                        if (!twice) {
                            this.teleportToDimension((ServerPlayerEntity) p, 1);
                        }
                        this.teleportToDimension((ServerPlayerEntity) p, 0);
                    }
                }
            }
            File dim1 = new File(Minecraft.getMinecraft().runDirectory, "saves/" + server.getLevelName() + "/DIM1");
            boolean endExists = dim1.exists();
            if (endExists) {
                ServerWorld end = server.getWorld(1);
                // delete it then
                end.close();
                server.worlds = ArrayUtils.remove(server.worlds, 2);
                server.field_3858 = ArrayUtils.remove(server.field_3858, 2);
                end.close();
                try {
                    FileUtils.forceDelete(dim1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (dim1.exists() && !FileUtils.deleteQuietly(dim1)) {
                    player.method_3331("§c" + "Failed to remove End Dimension :(");
                }
            }
            if (twice || !endExists) {
                ServerWorld overWorld = server.worlds[0];
                LevelProperties oldInfo = overWorld.getLevelProperties();
                LevelInfo levelInfo = new LevelInfo(oldInfo.getSeed(),
                        oldInfo.getGamemode(),
                        oldInfo.hasStructures(),
                        oldInfo.isHardcore(),
                        oldInfo.getGeneratorType());
                long seed;
                Config cfg = BigConfig.getSelectedConfig();
                if (cfg.selectedIsland == -2) {
                    seed = new Random().nextLong();
                } else if (cfg.selectedIsland == -1) {
                    seed = overWorld.getSeed();
                } else {
                    seed = cfg.islands.get(cfg.selectedIsland).getSeed();
                }


                ServerWorld newEnd = new EndFightWorld(seed, server, overWorld.getSaveHandler(), server.getLevelName(), 1, levelInfo, overWorld, server.profiler);
                // copy difficulty
                newEnd.difficulty = overWorld.difficulty;

                server.worlds = ArrayUtils.add(server.worlds, newEnd);
                server.field_3858 = ArrayUtils.add(server.field_3858, new long[100]);
                newEnd.addListener(new ServerWorldManager(server, newEnd));
                heal(player);
                // method_2643 -> clearStatusEffects
                player.method_2643();
                if (cfg.dGodPlayer) {
                    player.method_2654(new StatusEffectInstance(11, 100000, 255));
                }
                // set Gamemode
                player.method_3170(cfg.gamemode);
                EndFightMod.giveInventory(player, cfg.inventory);
                // method_3197 -> teleportToDimension
                this.teleportToDimension((ServerPlayerEntity) player, 1);

                if (cfg.showSettings) {
                    String[] three = {"§c", "§e", "§a"};
                    player.method_3331("");
                    player.method_3331("Selected Profile: " + "§e" + "'" + cfg.getName() + "§e" + "'");
                    player.method_3331("Island Type: " + three[Math.max(0, -cfg.selectedIsland)] + "[" + (cfg.selectedIslandName()) + "]");
                    player.method_3331("Endermen: " + three[cfg.enderMan] + "[" + ConfigGUI.enderManNames[cfg.enderMan] + "]");
                    if (cfg.dGodPlayer) {
                        player.method_3331("§c" + "You are in god mode");
                    }
                    if (cfg.dGodDragon) {
                        player.method_3331("§c" + "The dragon is in god mode");
                    }
                    if (cfg.dGodCrystals) {
                        player.method_3331("§c" + "The crystals are in god mode");
                    }
                }


                player.method_3331("Sent to End");
                EndFightMod.time = System.currentTimeMillis();
                if (EndFightMod.SRIGT_LOADED) {
                    if (InGameTimer.getInstance().getCategory() == EndFightCategory.END_FIGHT_CATEGORY) {
                        InGameTimer.reset();
                        InGameTimer.getInstance().setCategory(EndFightCategory.END_FIGHT_CATEGORY, false);
                        InGameTimer.getInstance().setStatus(TimerStatus.RUNNING);
                        InGameTimer.getInstance().setStartTime(EndFightMod.time);
                    }
                }
            }
        }
    }

    private void teleportToDimension(ServerPlayerEntity player, int dimension) {
        Minecraft minecraft = Minecraft.getMinecraft();
        PlayerManager manager = minecraft.getServer().getPlayerManager();
        manager.teleportToDimension(player, dimension);
    }
}
