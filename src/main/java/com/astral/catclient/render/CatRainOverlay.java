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
        drawGarland(context, screenWidth);
        drawTwinkleStars(context, screenWidth, screenHeight);
        drawPresentCorners(context, screenWidth, screenHeight);
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


    private void drawGarland(DrawContext context, int width) {
        // A slim holly garland hugs the very top edge so it avoids menu labels.
        for (int x = 0; x < width; x += 8) {
            int y = 7 + (int) (Math.sin((x + frame * 0.35) * 0.035) * 3.0);
            context.fill(x, y, x + 9, y + 2, 0xFF28784D);
            if (x % 24 == 0) {
                context.fill(x + 2, y - 2, x + 5, y + 1, 0xFF3FAF68);
                context.fill(x + 5, y - 2, x + 8, y + 1, 0xFF3FAF68);
                context.fill(x + 4, y - 4, x + 6, y - 1, 0xFFBCEBCE);
            }
        }

        // Alternating tiny bulbs gently pulse between warm holiday colours.
        int[] bulbs = {0xFFFF6B78, 0xFFFFD66B, 0xFF82CFFF, 0xFF65D68A};
        for (int x = 12; x < width; x += 22) {
            int y = 9 + (int) (Math.sin((x + frame * 0.35) * 0.035) * 3.0);
            int color = bulbs[Math.floorMod((x / 22) + (int) (frame / 8), bulbs.length)];
            context.fill(x, y + 2, x + 4, y + 6, color);
            context.fill(x + 1, y + 1, x + 3, y + 7, color);
        }
    }

    private void drawTwinkleStars(DrawContext context, int width, int height) {
        for (int i = 0; i < 14; i++) {
            int x = Math.floorMod(i * 137 + 31, Math.max(1, width));
            int y = 22 + Math.floorMod(i * 71, Math.max(1, height - 22));
            int pulse = (int) ((Math.sin(frame * 0.12 + i * 1.7) + 1.0) * 1.5);
            int center = Math.max(1, 2 + pulse);
            int color = (i % 2 == 0) ? 0xBFFFFFFF : 0xAFFFF0B5;
            context.fill(x - center, y, x + center + 1, y + 1, color);
            context.fill(x, y - center, x + 1, y + center + 1, color);
            if (pulse > 1) {
                context.fill(x - 1, y - 1, x + 2, y + 2, 0x70FFFFFF);
            }
        }
    }

    private void drawPresentCorners(DrawContext context, int width, int height) {
        // Small pixel presents sit in the bottom corners, clear of the menu.
        drawPresent(context, 8, height - 17, 0xFFCC4358, 0xFFFFD66B);
        drawPresent(context, Math.max(8, width - 24), height - 14, 0xFF28784D, 0xFFEAF8FF);
        // A soft snowbank along the bottom edge.
        context.fill(0, height - 2, width, height, 0xBFEAF8FF);
        context.fill(18, height - 4, 35, height - 2, 0xAFFFFFFF);
        context.fill(Math.max(0, width - 52), height - 5, Math.max(0, width - 29), height - 2, 0xAFFFFFFF);
    }

    private void drawPresent(DrawContext context, int x, int y, int box, int ribbon) {
        context.fill(x, y + 4, x + 15, y + 15, box);
        context.fill(x + 6, y + 4, x + 9, y + 15, ribbon);
        context.fill(x, y + 8, x + 15, y + 10, ribbon);
        context.fill(x + 3, y + 1, x + 7, y + 5, ribbon);
        context.fill(x + 8, y + 1, x + 12, y + 5, ribbon);
        context.fill(x + 6, y + 1, x + 9, y + 4, box);
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
