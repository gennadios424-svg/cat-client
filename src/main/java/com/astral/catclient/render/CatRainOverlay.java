package com.astral.catclient.render;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cosmetic winter overlay for the title and pause screens.
 * Draws a cool winter-sky tint, falling cats, snow, and cat-ear button frames.
 */
public class CatRainOverlay {

    private static final Random RANDOM = new Random();
    private final List<FallingCat> cats = new ArrayList<>();
    private long frame;

    public void renderAndTick(DrawContext context, Screen screen, int screenWidth, int screenHeight) {
        CatConfig cfg = CatConfig.INSTANCE;
        if (!cfg.enabled) return;

        frame++;
        drawWinterSky(context, screenWidth, screenHeight);
        maybeSpawn(screenWidth, cfg);
        cats.removeIf(cat -> !cat.tick(screenHeight));
        for (FallingCat cat : cats) cat.render(context);

        drawSnow(context, screenWidth, screenHeight);
        drawCatButtons(context, screen);
    }

    private void drawWinterSky(DrawContext context, int width, int height) {
        // Soft transparent blue veil keeps the original Minecraft screen visible.
        context.fill(0, 0, width, height, 0x180B2C55);
        context.fill(0, 0, width, Math.max(2, height / 5), 0x100D4774);
    }

    private void drawSnow(DrawContext context, int width, int height) {
        for (int i = 0; i < 42; i++) {
            int x = Math.floorMod(i * 97 + (int) (frame * (1 + i % 3)), Math.max(1, width));
            int y = Math.floorMod(i * 53 + (int) (frame * (2 + i % 2)), Math.max(1, height));
            int s = (i % 5 == 0) ? 3 : 2;
            int color = (i % 4 == 0) ? 0xDFFFFFFF : 0xAFFFFFFF;
            context.fill(x, y, x + s, y + s, color);
            if (i % 3 == 0) {
                context.fill(x - 2, y + 1, x + s + 2, y + 2, color);
                context.fill(x + 1, y - 2, x + 2, y + s + 2, color);
            }
        }
    }

    private void drawCatButtons(DrawContext context, Screen screen) {
        for (var child : screen.children()) {
            if (!(child instanceof ButtonWidget button)) continue;

            int x = button.getX();
            int y = button.getY();
            int w = button.getWidth();
            int h = button.getHeight();
            if (w < 12 || h < 8) continue;

            // Icy outline with festive top trim.
            context.fill(x - 2, y - 2, x + w + 2, y, 0xFFEAF8FF);
            context.fill(x - 2, y + h, x + w + 2, y + h + 2, 0xFFEAF8FF);
            context.fill(x - 2, y, x, y + h, 0xFFEAF8FF);
            context.fill(x + w, y, x + w + 2, y + h, 0xFFEAF8FF);
            context.fill(x + 1, y - 2, x + Math.max(2, w / 2), y, 0xFFFF6B78);
            context.fill(x + Math.max(2, w / 2), y - 2, x + w - 1, y, 0xFF65D68A);

            // Tiny pixel cat ears on the upper corners.
            context.fill(x + 3, y - 5, x + 7, y - 2, 0xFFFFE6EF);
            context.fill(x + 4, y - 7, x + 6, y - 4, 0xFFFFE6EF);
            context.fill(x + w - 7, y - 5, x + w - 3, y - 2, 0xFFFFE6EF);
            context.fill(x + w - 6, y - 7, x + w - 4, y - 4, 0xFFFFE6EF);
        }
    }

    private void maybeSpawn(int screenWidth, CatConfig cfg) {
        if (cats.size() >= cfg.maxCats) return;
        double rate = Math.max(0.0, cfg.spawnRate);
        int guaranteed = (int) Math.floor(rate);
        double remainder = rate - guaranteed;
        for (int i = 0; i < guaranteed && cats.size() < cfg.maxCats; i++) spawnOne(screenWidth, cfg);
        if (RANDOM.nextDouble() < remainder && cats.size() < cfg.maxCats) spawnOne(screenWidth, cfg);
    }

    private void spawnOne(int screenWidth, CatConfig cfg) {
        cats.add(new FallingCat(RANDOM, screenWidth, 24, cfg.fallSpeed, cfg.catScale));
    }

    public void clear() {
        cats.clear();
        frame = 0;
    }

    public int count() {
        return cats.size();
    }
}
