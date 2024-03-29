package com.slackow.endfight;

import com.google.gson.*;
import com.redlimerl.speedrunigt.option.*;
import com.slackow.endfight.speedrunigt.EndFightCategory;
import com.slackow.endfight.util.Kit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class EndFightMod implements ModInitializer {

    public static long time = System.currentTimeMillis();
    public static boolean SRIGT_LOADED = false;

    public static void setInventory(PlayerEntity player, Kit kit) {
        kit.contents = Stream.concat(Arrays.stream(player.inventory.main), Arrays.stream(player.inventory.armor))
                .mapToInt(EndFightMod::itemToInt).toArray();
    }

    public static int itemToInt(ItemStack item) {
        if (item == null) return 0;
        return item.count << 24 | item.getData() << 12 | item.getItem().id;
    }

    public static ItemStack intToItem(int num) {
        if (num == 0) return null;
        return new ItemStack(Item.ITEMS[num & 0xFFF], num >>> 24, num >>> 12 & 0xFFF);
    }

    public static void giveInventory(PlayerEntity player, Kit kit) {
        ItemStack[] full = Arrays.stream(kit.contents)
                .mapToObj(EndFightMod::intToItem)
                .toArray(ItemStack[]::new);
        System.arraycopy(full, 0, player.inventory.main, 0, 36);
        System.arraycopy(full, 36, player.inventory.armor, 0, 4);
    }

    @Deprecated
    public static void giveInventory(PlayerEntity player) throws CommandException {
        Path path = getDataPath();
        if (Files.notExists(path)) {
            try {
                Files.write(path,
                        Collections.singleton("[16777483,16777473,16777571,16777477,1073741842,16777584,16777542," +
                                "16777491,83886446,16777274,16777571,16777571,16777571,16777571,16777571,16777489,402653446," +
                                "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]"));
            } catch (IOException e) {
                throw new CommandException("file.read.error");
            }
        }
        try {

            ItemStack[] full = StreamSupport.stream(new JsonParser().parse(Files.newBufferedReader(path)).getAsJsonArray().spliterator(), false)
                    .mapToInt(JsonElement::getAsInt)
                    .mapToObj(EndFightMod::intToItem).toArray(ItemStack[]::new);
            if (full.length < 40) {
                throw new CommandException("commands.generic.exception");
            }
            System.arraycopy(full, 0, player.inventory.main, 0, 36);
            System.arraycopy(full, 36, player.inventory.armor, 0, 4);
        } catch (IOException e) {
            throw new CommandException("commands.generic.exception");
        }
    }

    public static Path getDataPath() {
        return Minecraft.getMinecraft().runDirectory.toPath().resolve("end.txt");
    }

    @Override
    public void onInitialize() {
        // MinecraftClient.getInstance().options.debugEnabled = true;
        SRIGT_LOADED = FabricLoader.getInstance().isModLoaded("speedrunigt");
        if (SRIGT_LOADED) {
            SpeedRunOption.setOption(SpeedRunOptions.TIMER_CATEGORY, EndFightCategory.END_FIGHT_CATEGORY);
        }
    }
}
