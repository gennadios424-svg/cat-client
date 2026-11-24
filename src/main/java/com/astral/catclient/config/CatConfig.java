package com.astral.catclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain JSON-backed settings for the cat rain effect.
 * Entirely cosmetic: nothing here touches game state, accounts, or servers.
 */
public class CatConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("astralcatclient.json");

    public boolean enabled = true;
    public double spawnRate = 0.35;      // average new cats per tick (0.0 - 3.0)
    public double fallSpeed = 1.0;       // multiplier on base fall speed
    public double catScale = 1.0;        // multiplier on base cat size
    public int maxCats = 150;            // hard cap so it never tanks FPS
    public boolean onTitleScreen = true;
    public boolean onPauseScreen = true;

    public static CatConfig INSTANCE = load();

    public static CatConfig load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                CatConfig loaded = GSON.fromJson(reader, CatConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException e) {
                System.err.println("[AstralCatClient] Failed to read config, using defaults: " + e.getMessage());
            }
        }
        CatConfig fresh = new CatConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[AstralCatClient] Failed to save config: " + e.getMessage());
        }
    }
}
