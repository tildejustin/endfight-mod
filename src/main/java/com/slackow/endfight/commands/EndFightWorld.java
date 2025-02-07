package com.slackow.endfight.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.*;
import net.minecraft.world.level.LevelInfo;

public class EndFightWorld extends MultiServerWorld {
    private static long seed;

    public EndFightWorld(long seed, MinecraftServer server, SaveHandler saveHandler, String levelName, int i, LevelInfo levelInfo, ServerWorld overWorld, Profiler profiler) {
        super(g(server, seed), saveHandler, levelName, i, levelInfo, overWorld, profiler);
    }

    // awesome hack to run code before super
    private static MinecraftServer g(MinecraftServer server, long seed) {
        EndFightWorld.seed = seed;
        return server;
    }

    @Override
    public long getSeed() {
        return seed;
    }
}
