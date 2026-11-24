package com.astral.catclient.render;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Subtle seasonal decoration for the title screen. */
public class CatRainOverlay {
    private static final Random RANDOM = new Random();
    private final List<FallingCat> cats = new ArrayList<>();
    private long frame;

    public void renderAndTick(DrawContext context, Screen screen, int screenWidth, int screenHeight) {
        CatConfig cfg = CatConfig.INSTANCE;
        if (!cfg.enabled || !(screen instanceof TitleScreen)) return;

        frame++;
        drawWinterSky(context, screenWidth, screenHeight);
        maybeSpawn(screenWidth, cfg);
        cats.removeIf(cat -> !cat.tick(screenHeight));
        for (FallingCat cat : cats) cat.render(context);
        drawSnow(context, screenWidth, screenHeight);
        drawGarland(context, screenWidth);
        drawTwinkleStars(context, screenWidth, screenHeight);
    }

    private void drawWinterSky(DrawContext context, int width, int height) {
        context.fill(0, 0, width, height, 0x100B2C55);
    }

    private void drawSnow(DrawContext context, int width, int height) {
        for (int i = 0; i < 14; i++) {
            int x = Math.floorMod(i * 137 + (int) (frame * (1 + i % 2)), Math.max(1, width));
            int y = Math.floorMod(i * 83 + (int) (frame * (1 + i % 2)), Math.max(1, height));
            context.fill(x, y, x + 2, y + 2, 0xAFFFFFFF);
        }
    }

    private void drawGarland(DrawContext context, int width) {
        for (int x = 0; x < width; x += 12) {
            int y = 5 + (int) (Math.sin((x + frame * 0.2) * 0.025) * 2.0);
            context.fill(x, y, x + 13, y + 2, 0xFF28784D);
            if (x % 36 == 0) {
                int[] bulbs = {0xFFFF8298, 0xFFFFD66B, 0xFF82CFFF};
                int color = bulbs[Math.floorMod(x / 36, bulbs.length)];
                context.fill(x + 5, y + 2, x + 8, y + 5, color);
            }
        }
    }

    private void drawTwinkleStars(DrawContext context, int width, int height) {
        for (int i = 0; i < 5; i++) {
            int x = Math.floorMod(i * 173 + 31, Math.max(1, width));
            int y = 24 + Math.floorMod(i * 97, Math.max(1, height - 24));
            if ((frame / 12 + i) % 3 == 0) {
                context.fill(x, y, x + 2, y + 2, 0xBFFFFFFF);
            }
        }
    }

    private void maybeSpawn(int screenWidth, CatConfig cfg) {
        // Clamp old saved settings too: at most three cats and roughly one new cat every 8 seconds.
        int cap = Math.max(0, Math.min(cfg.maxCats, 3));
        double rate = Math.max(0.0, Math.min(cfg.spawnRate, 0.0025));
        if (cats.size() < cap && RANDOM.nextDouble() < rate) {
            cats.add(new FallingCat(RANDOM, screenWidth, 24, cfg.fallSpeed, cfg.catScale));
        }
    }

    public void clear() {
        cats.clear();
        frame = 0;
    }

    public int count() {
        return cats.size();
    }
}
