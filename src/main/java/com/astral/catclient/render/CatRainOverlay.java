package com.astral.catclient.render;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Owns the list of currently-falling cats and advances/draws them each frame.
 * Entirely self-contained: no network, world, or account access of any kind.
 */
public class CatRainOverlay {

    private static final Random RANDOM = new Random();
    private final List<FallingCat> cats = new ArrayList<>();

    /** Call once per rendered frame while a supported screen is open. */
    public void renderAndTick(DrawContext context, int screenWidth, int screenHeight) {
        CatConfig cfg = CatConfig.INSTANCE;
        if (!cfg.enabled) {
            return;
        }

        maybeSpawn(screenWidth, cfg);

        cats.removeIf(cat -> !cat.tick(screenHeight));

        for (FallingCat cat : cats) {
            cat.render(context);
        }
    }

    private void maybeSpawn(int screenWidth, CatConfig cfg) {
        if (cats.size() >= cfg.maxCats) {
            return;
        }
        // spawnRate is an average number of cats per tick; handle rates above 1 by
        // spawning multiple, and rates below 1 via random chance.
        double rate = Math.max(0.0, cfg.spawnRate);
        int guaranteed = (int) Math.floor(rate);
        double remainder = rate - guaranteed;

        for (int i = 0; i < guaranteed && cats.size() < cfg.maxCats; i++) {
            spawnOne(screenWidth, cfg);
        }
        if (RANDOM.nextDouble() < remainder && cats.size() < cfg.maxCats) {
            spawnOne(screenWidth, cfg);
        }
    }

    private void spawnOne(int screenWidth, CatConfig cfg) {
        cats.add(new FallingCat(RANDOM, screenWidth, 24, cfg.fallSpeed, cfg.catScale));
    }

    /** Clears all cats, e.g. when leaving the screen, so they don't linger into gameplay. */
    public void clear() {
        cats.clear();
    }

    public int count() {
        return cats.size();
    }
}
