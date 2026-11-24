package com.astral.catclient.gui;

import com.astral.catclient.config.CatConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Compact, readable settings panel for Cat Client. */
public class CattyMenuScreen extends Screen {
    private static final int PANEL = 0xF0182038;
    private static final int PANEL_INNER = 0xFF202D49;
    private static final int PINK = 0xFFFFB8D8;
    private static final int CYAN = 0xFF9BE8F5;
    private static final int MUTED = 0xFFB7C5DA;

    public CattyMenuScreen() {
        super(Text.literal("Cat Client | Kitty Menu"));
    }

    @Override
    protected void init() {
        clearChildren();
        int panelWidth = Math.min(310, width - 24);
        int left = (width - panelWidth) / 2;
        int buttonWidth = Math.min(246, panelWidth - 36);
        int x = (width - buttonWidth) / 2;
        int y = Math.max(76, height / 2 - 72);

        addDrawableChild(toggle("FPS counter", CatConfig.INSTANCE.showFps,
                b -> change("fps")).dimensions(x, y, buttonWidth, 22).build());
        y += 28;
        addDrawableChild(toggle("Keystrokes", CatConfig.INSTANCE.showKeystrokes,
                b -> change("keys")).dimensions(x, y, buttonWidth, 22).build());
        y += 28;
        addDrawableChild(toggle("CPS counter", CatConfig.INSTANCE.showCps,
                b -> change("cps")).dimensions(x, y, buttonWidth, 22).build());
        y += 28;
        addDrawableChild(toggle("HUD overlay", CatConfig.INSTANCE.showHud,
                b -> change("hud")).dimensions(x, y, buttonWidth, 22).build());
        y += 36;
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x, y, buttonWidth, 22).build());
    }

    private ButtonWidget.Builder toggle(String label, boolean enabled,
                                        net.minecraft.client.gui.widget.ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label + "     " + (enabled ? "ON" : "OFF")), action);
    }

    private void change(String option) {
        CatConfig cfg = CatConfig.INSTANCE;
        switch (option) {
            case "fps" -> cfg.showFps = !cfg.showFps;
            case "keys" -> cfg.showKeystrokes = !cfg.showKeystrokes;
            case "cps" -> cfg.showCps = !cfg.showCps;
            case "hud" -> cfg.showHud = !cfg.showHud;
        }
        cfg.save();
        if (client != null) client.setScreen(new CattyMenuScreen());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int panelWidth = Math.min(310, width - 24);
        int panelHeight = 270;
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;

        context.fill(0, 0, width, height, 0x66071120);
        context.fill(left, top, left + panelWidth, top + panelHeight, PANEL);
        context.fill(left + 1, top + 1, left + panelWidth - 1, top + 3, PINK);
        context.fill(left + 1, top + 3, left + panelWidth - 1, top + 4, CYAN);
        context.fill(left + 10, top + 42, left + panelWidth - 10, top + 43, 0x665B789B);

        String title = "CAT CLIENT";
        context.drawText(textRenderer, title,
                (width - textRenderer.getWidth(title)) / 2, top + 15, PINK, true);
        String subtitle = "KITTY MENU  •  1.21.1";
        context.drawText(textRenderer, subtitle,
                (width - textRenderer.getWidth(subtitle)) / 2, top + 29, CYAN, false);
        super.render(context, mouseX, mouseY, delta);

        String footer = "Right Shift to open  •  Settings save automatically";
        context.drawText(textRenderer, footer,
                (width - textRenderer.getWidth(footer)) / 2,
                top + panelHeight - 18, MUTED, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
