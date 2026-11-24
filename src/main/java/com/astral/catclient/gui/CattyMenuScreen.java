package com.astral.catclient.gui;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CattyMenuScreen extends Screen {
    public CattyMenuScreen() {
        super(Text.literal("Kitty Menu"));
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = this.height / 4;
        addDrawableChild(ButtonWidget.builder(toggleLabel("HUD", CatConfig.INSTANCE.showHud), b -> toggle("hud"))
                .dimensions(center - 100, y, 200, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleLabel("FPS", CatConfig.INSTANCE.showFps), b -> toggle("fps"))
                .dimensions(center - 100, y, 200, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleLabel("Keystrokes", CatConfig.INSTANCE.showKeystrokes), b -> toggle("keys"))
                .dimensions(center - 100, y, 200, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleLabel("CPS", CatConfig.INSTANCE.showCps), b -> toggle("cps"))
                .dimensions(center - 100, y, 200, 20).build());
        y += 32;
        addDrawableChild(ButtonWidget.builder(Text.literal("Done ♡"), b -> close())
                .dimensions(center - 100, y, 200, 20).build());
    }

    private Text toggleLabel(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    private void toggle(String option) {
        CatConfig cfg = CatConfig.INSTANCE;
        switch (option) {
            case "hud" -> cfg.showHud = !cfg.showHud;
            case "fps" -> cfg.showFps = !cfg.showFps;
            case "keys" -> cfg.showKeystrokes = !cfg.showKeystrokes;
            case "cps" -> cfg.showCps = !cfg.showCps;
        }
        cfg.save();
        if (client != null) client.setScreen(new CattyMenuScreen());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0x552B1838);
        String title = "♡ KITTY MENU ♡";
        context.drawText(textRenderer, title, (width - textRenderer.getWidth(title)) / 2,
                height / 4 - 28, 0xFFFFCBE5, true);
        String hint = "Right Shift  •  Cat Client 1.21.1";
        context.drawText(textRenderer, hint, (width - textRenderer.getWidth(hint)) / 2,
                height - 24, 0xFFEAF8FF, true);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
