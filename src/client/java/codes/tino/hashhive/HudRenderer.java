package codes.tino.hashhive;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class HudRenderer implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        // Check if mod is enabled
        if (!ModConfig.getInstance().isEnabled()) {
            return;
        }

        CountdownManager countdown = CountdownManager.getInstance();

        if (!countdown.isActive()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }

        // Don't render if HUD is hidden (F1 mode)
        if (!client.options.hudHidden) {
            String timeText = "ChatGame: " + countdown.getFormattedTime();

            int x = 100;
            int y = 3;

            // Draw black background for better visibility
           // int textWidth = client.textRenderer.getWidth(timeText);
           // drawContext.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);

            // Draw text with shadow for better visibility
            drawContext.drawTextWithShadow(client.textRenderer, timeText, x, y, 0xFFFFFFFF);
        }
    }
}
