package com.astral.catclient;

import com.astral.catclient.config.CatConfig;
import com.astral.catclient.render.CatRainOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Astral Cat Client — a purely cosmetic Fabric mod.
 *
 * It rains little pixel-art cats down the title screen and the in-game pause
 * menu. That is the entire feature set: no automation, no server interaction,
 * no account access, no macros. One keybind toggles it on/off.
 */
public class AstralCatClient implements ClientModInitializer {

    private final CatRainOverlay overlay = new CatRainOverlay();
    private KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        CatConfig.INSTANCE = CatConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.astralcatclient.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA,
                "key.category.astralcatclient"
        ));

        // Poll the toggle key once per client tick, regardless of which screen (if any) is open.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                CatConfig.INSTANCE.enabled = !CatConfig.INSTANCE.enabled;
                CatConfig.INSTANCE.save();
                if (!CatConfig.INSTANCE.enabled) {
                    overlay.clear();
                }
            }
        });

        // Hook every screen as it's created; only draw on the ones we care about.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isSupportedScreen(screen)) {
                return;
            }
            ScreenEvents.afterRender(screen).register((s, context, mouseX, mouseY, tickDelta) -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                overlay.renderAndTick(context, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
            });
            ScreenEvents.remove(screen).register(s -> overlay.clear());
        });
    }

    private boolean isSupportedScreen(Screen screen) {
        CatConfig cfg = CatConfig.INSTANCE;
        if (screen instanceof TitleScreen) {
            return cfg.onTitleScreen;
        }
        if (screen instanceof GameMenuScreen) {
            return cfg.onPauseScreen;
        }
        return false;
    }
}
