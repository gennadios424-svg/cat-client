package com.astral.catclient.render;

import net.minecraft.client.gui.DrawContext;
import java.util.Random;

/** A tiny pixel-art cat falling through the menu. */
public class FallingCat {
    private static final int[] PALETTE = {
            0xFFFF6B78, // festive rose
            0xFF65D68A, // holly green
            0xFFF2F7FF, // snow white
            0xFF82CFFF, // icy blue
            0xFFC98BFF  // winter lavender
    };

    public double x, y, velocityY, drift, rotation, rotationSpeed;
    public int size, color;
    public boolean flipped;

    public FallingCat(Random random, int screenWidth, int baseSize, double speedMultiplier, double scaleMultiplier) {
        x = random.nextInt(Math.max(1, screenWidth));
        y = -32;
        velocityY = (0.6 + random.nextDouble() * 0.9) * speedMultiplier;
        drift = (random.nextDouble() - 0.5) * 0.4;
        rotation = random.nextDouble() * 360.0;
        rotationSpeed = (random.nextDouble() - 0.5) * 4.0;
        size = Math.max(6, (int) Math.round((baseSize + random.nextInt(10)) * scaleMultiplier));
        color = PALETTE[random.nextInt(PALETTE.length)];
        flipped = random.nextBoolean();
    }

    public boolean tick(int screenHeight) {
        y += velocityY * 4.0;
        x += drift;
        rotation += rotationSpeed;
        return y < screenHeight + 64;
    }

    public void render(DrawContext context) {
        int px = Math.max(1, size / 8);
        int baseX = (int) x;
        int baseY = (int) y;
        int dark = shade(color, 0.6f);
        int[][] grid = {
                {0, 1, 0, 0, 0, 0, 1, 0},
                {1, 2, 1, 0, 0, 1, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 0, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 0, 0, 1, 0, 0}
        };
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int value = grid[row][col];
                if (value == 0) continue;
                int drawCol = flipped ? grid[row].length - 1 - col : col;
                int colorToDraw = value == 2 ? dark : color;
                context.fill(baseX + drawCol * px, baseY + row * px,
                        baseX + (drawCol + 1) * px, baseY + (row + 1) * px, colorToDraw);
            }
        }
    }

    private static int shade(int argb, float factor) {
        int a = (argb >>> 24) & 0xFF;
        int r = (int) (((argb >> 16) & 0xFF) * factor);
        int g = (int) (((argb >> 8) & 0xFF) * factor);
        int b = (int) ((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
