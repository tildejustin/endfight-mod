package com.slackow.endfight.util;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.slackow.endfight.EndFightCommand;
import com.slackow.endfight.speedrunigt.EndFightCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.player.ControllablePlayerEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * A Hacky way of transferring data between client and server, don't depend on anything useful actually being here
 * it's more of an "If it's present, take it" kinda deal. I'm not too experienced with not mixing these two, but
 * I know I shouldn't do it, so I figured maybe it'd be better if I just put it all in one place because I don't know
 * how proxies work lmao.
 */
public class Medium {
    public static double targetX;
    public static double targetY;
    public static double targetZ;
    public static List<EndFightCommand> commandMap;
    private static boolean switched = false;

    // method_4328
    // I didn't know where else to place this method it doesn't really fit here
    public static void drawBox(int color, Box box) {
        GL11.glDepthMask(false);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        Medium.renderBox(box, color);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    public static void renderBox(Box box, int color) {
        Tessellator var2 = Tessellator.INSTANCE;
        var2.begin(3);
        if (color != -1) {
            var2.color(color);
        }

        var2.vertex(box.minX, box.minY, box.minZ);
        var2.vertex(box.maxX, box.minY, box.minZ);
        var2.vertex(box.maxX, box.minY, box.maxZ);
        var2.vertex(box.minX, box.minY, box.maxZ);
        var2.vertex(box.minX, box.minY, box.minZ);
        var2.end();
        var2.begin(3);
        if (color != -1) {
            var2.color(color);
        }

        var2.vertex(box.minX, box.maxY, box.minZ);
        var2.vertex(box.maxX, box.maxY, box.minZ);
        var2.vertex(box.maxX, box.maxY, box.maxZ);
        var2.vertex(box.minX, box.maxY, box.maxZ);
        var2.vertex(box.minX, box.maxY, box.minZ);
        var2.end();
        var2.begin(1);
        if (color != -1) {
            var2.color(color);
        }

        var2.vertex(box.minX, box.minY, box.minZ);
        var2.vertex(box.minX, box.maxY, box.minZ);
        var2.vertex(box.maxX, box.minY, box.minZ);
        var2.vertex(box.maxX, box.maxY, box.minZ);
        var2.vertex(box.maxX, box.minY, box.maxZ);
        var2.vertex(box.maxX, box.maxY, box.maxZ);
        var2.vertex(box.minX, box.minY, box.maxZ);
        var2.vertex(box.minX, box.maxY, box.maxZ);
        var2.end();
    }


    /**
     * I Need to make one of these methods anytime I use SRIGT classes inside a mixin, or you get an error. :/
     */
    public static void completeTimerIfEndFight() {
        InGameTimer timer = InGameTimer.getInstance();
        if (timer.getCategory() == EndFightCategory.END_FIGHT_CATEGORY && timer.isPlaying()) {
            InGameTimer.complete();
        }
    }


    public static void onGameJoinIGT() {
        InGameTimer timer = InGameTimer.getInstance();
        ControllablePlayerEntity player = Minecraft.getMinecraft().playerEntity;

        if (!switched) {
            switched = true;
            timer.setCategory(EndFightCategory.END_FIGHT_CATEGORY, false);
        }
        if (timer.getCategory() == EndFightCategory.END_FIGHT_CATEGORY) {
            player.method_1262("Loaded End Fight Category w/ SpeedrunIGT");
        } else {
            player.method_1262("Warning: End Fight Category disabled in SpeedrunIGT");
        }
    }
}
