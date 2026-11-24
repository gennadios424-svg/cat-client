package com.astral.catclient.hud;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CattyHud {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean leftWasDown;
    private static boolean rightWasDown;
    private static long lastPoll;

    private CattyHud() {}

    public static void tick(MinecraftClient client) {
        if (client == null || client.getWindow() == null) return;
        long now = System.currentTimeMillis();
        long window = client.getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (left && !leftWasDown) LEFT_CLICKS.addLast(now);
        if (right && !rightWasDown) RIGHT_CLICKS.addLast(now);
        leftWasDown = left;
        rightWasDown = right;
        trim(LEFT_CLICKS, now);
        trim(RIGHT_CLICKS, now);
        lastPoll = now;
    }

    private static void trim(Deque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.removeFirst();
    }

    public static void render(MinecraftClient client, DrawContext context) {
        CatConfig cfg = CatConfig.INSTANCE;
        if (!cfg.showHud || client == null || client.options.hudHidden || client.currentScreen != null) return;

        int x = 7;
        int y = 7;
        int line = 12;
        context.fill(x - 4, y - 4, x + 112, y + 5 * line + 3, 0x88321F3D);
        context.fill(x - 4, y - 4, x + 112, y - 2, 0xFFFFB8D8);

        context.drawText(client.textRenderer, Text.literal("♡ CAT CLIENT ♡"), x, y, 0xFFFFCBE5, true);
        y += line + 2;
        if (cfg.showFps) {
            context.drawText(client.textRenderer, Text.literal("FPS  " + client.getCurrentFps()), x, y, 0xFFEAF8FF, true);
            y += line;
        }
        if (cfg.showKeystrokes) {
            String keys = key(client, GLFW.GLFW_KEY_W, "W") + "  "
                    + key(client, GLFW.GLFW_KEY_A, "A") + " "
                    + key(client, GLFW.GLFW_KEY_S, "S") + " "
                    + key(client, GLFW.GLFW_KEY_D, "D");
            context.drawText(client.textRenderer, Text.literal(keys), x, y, 0xFFFFD6EA, true);
            y += line;
            String extra = key(client, GLFW.GLFW_KEY_SPACE, "SPACE") + "  "
                    + key(client, GLFW.GLFW_KEY_LEFT_SHIFT, "SHIFT");
            context.drawText(client.textRenderer, Text.literal(extra), x, y, 0xFFEAF8FF, true);
            y += line;
        }
        if (cfg.showCps) {
            long now = System.currentTimeMillis();
            trim(LEFT_CLICKS, now);
            trim(RIGHT_CLICKS, now);
            context.drawText(client.textRenderer,
                    Text.literal("CPS  L " + LEFT_CLICKS.size() + "  R " + RIGHT_CLICKS.size()),
                    x, y, 0xFFFFE6A8, true);
        }
    }

    private static String key(MinecraftClient client, int key, String label) {
        boolean down = GLFW.glfwGetKey(client.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
        return down ? "[" + label + "]" : label;
    }
}
