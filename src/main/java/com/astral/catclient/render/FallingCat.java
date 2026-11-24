package com.astral.catclient.render;

import net.minecraft.client.gui.DrawContext;

import java.util.Random;

/**
 * A single falling cat: a small blocky pixel-art sprite drawn with flat-colored
 * rectangles, so the mod needs no external texture file. Purely decorative —
 * it has no interaction with the world, the player, or the server.
 */
public class FallingCat {

    private static final int[] PALETTE = {
            0xFFE8A33D, // orange tabby
            0xFF4A4A4A, // grey
            0xFFF2F2F2, // white
            0xFF2B2B2B, // black
            0xFFC97B4A  // ginger
    };

    public double x;
    public double y;
    public double velocityY;
    public double drift;
    public double rotation;
    public double rotationSpeed;
    public int size;
    public int color;
    public boolean flipped;

    public FallingCat(Random random, int screenWidth, int baseSize, double speedMultiplier, double scaleMultiplier) {
        this.x = random.nextInt(Math.max(1, screenWidth));
        this.y = -32;
        this.velocityY = (0.6 + random.nextDouble() * 0.9) * speedMultiplier;
        this.drift = (random.nextDouble() - 0.5) * 0.4;
        this.rotation = random.nextDouble() * 360.0;
        this.rotationSpeed = (random.nextDouble() - 0.5) * 4.0;
        this.size = Math.max(6, (int) Math.round((baseSize + random.nextInt(10)) * scaleMultiplier));
        this.color = PALETTE[random.nextInt(PALETTE.length)];
        this.flipped = random.nextBoolean();
    }

    /** Advance one frame. Returns false once the cat has fallen off-screen and should be removed. */
    public boolean tick(int screenHeight) {
        y += velocityY * 4.0;
        x += drift;
        rotation += rotationSpeed;
        return y < screenHeight + 64;
    }

    /**
     * Draws a simple 8x8-grid cat silhouette (ears, head, body) scaled up to {@code size}.
     * No image assets required — everything is flat-colored rectangles via DrawContext.
     */
    public void render(DrawContext context) {
        int px = size / 8; // one "pixel" of the sprite grid, in real screen pixels
        if (px < 1) px = 1;
        int baseX = (int) x;
        int baseY = (int) y;
        int c = color;
        int dark = shade(color, 0.6f);

        // Grid layout (8 wide x 7 tall), 1 = body color, 2 = darker shade (inner ear / stripe)
        int[][] grid = {
                {0, 1, 0, 0, 0, 0, 1, 0},
                {1, 2, 1, 0, 0, 1, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 0, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 0, 0, 1, 0, 0},
        };

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int v = grid[row][col];
                if (v == 0) continue;
                int drawCol = flipped ? (grid[row].length - 1 - col) : col;
                int rectX = baseX + drawCol * px;
                int rectY = baseY + row * px;
                int fillColor = (v == 2) ? dark : c;
                context.fill(rectX, rectY, rectX + px, rectY + px, fillColor);
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
