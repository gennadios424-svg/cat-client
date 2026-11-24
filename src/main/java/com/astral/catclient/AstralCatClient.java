package com.astral.catclient;

import com.astral.catclient.config.CatConfig;
import com.astral.catclient.gui.CattyMenuScreen;
import com.astral.catclient.render.CatRainOverlay;
import com.astral.catclient.hud.CattyHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AstralCatClient implements ClientModInitializer {
    private final CatRainOverlay overlay = new CatRainOverlay();
    private KeyBinding toggleKey;
    private KeyBinding kittyMenuKey;

    @Override
    public void onInitializeClient() {
        CatConfig.INSTANCE = CatConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.astralcatclient.toggle", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA, "key.category.astralcatclient"));
        kittyMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.astralcatclient.kitty_menu", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, "key.category.astralcatclient"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() != null) {
                client.getWindow().setTitle("Cat Client 1.21.1 | CattyCraft");
            }
            while (kittyMenuKey.wasPressed()) {
                if (client.currentScreen instanceof CattyMenuScreen) {
                    client.setScreen(null);
                } else if (client.currentScreen == null) {
                    client.setScreen(new CattyMenuScreen());
                }
            }
            while (toggleKey.wasPressed()) {
                CatConfig.INSTANCE.enabled = !CatConfig.INSTANCE.enabled;
                CatConfig.INSTANCE.save();
                if (!CatConfig.INSTANCE.enabled) overlay.clear();
            }
            CattyHud.tick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) ->
                CattyHud.render(MinecraftClient.getInstance(), context));

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isSupportedScreen(screen)) return;
            ScreenEvents.afterRender(screen).register((s, context, mouseX, mouseY, tickDelta) -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                overlay.renderAndTick(context, screen,
                        mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
                if (screen instanceof TitleScreen) {
                    String brand = "Cat Client 1.21.1  •  CattyCraft";
                    context.drawText(mc.textRenderer, Text.literal(brand),
                            (scaledWidth - mc.textRenderer.getWidth(brand)) / 2,
                            8, 0xFFFFD6EA, true);
                }
            });
            ScreenEvents.remove(screen).register(s -> overlay.clear());
        });
    }

    private boolean isSupportedScreen(Screen screen) {
        CatConfig cfg = CatConfig.INSTANCE;
        return (screen instanceof TitleScreen && cfg.onTitleScreen)
                || (screen instanceof GameMenuScreen && cfg.onPauseScreen);
    }
}
