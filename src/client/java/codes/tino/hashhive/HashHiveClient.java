package codes.tino.hashhive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

public class HashHiveClient implements ClientModInitializer {
	public static final String MOD_ID = "hashhive";
	private final AtomicBoolean expectingEquation = new AtomicBoolean(false);
	private static final int AUTO_SEND_DELAY_TICKS = 30; // 1.5 seconds = 30 ticks
	private int autoSendCountdown = 0;
	private String pendingSolution = null;

	@Override
	public void onInitializeClient() {
		// Register HUD element after boss bar (renders on top of main HUD elements)
		HudElementRegistry.attachElementAfter(
			VanillaHudElements.BOSS_BAR,
			Identifier.of(MOD_ID, "countdown"),
			this::renderHud
		);

		// Register client tick event to update countdown
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CountdownManager.getInstance().tick();

			// Handle auto-send countdown
			if (autoSendCountdown > 0) {
				autoSendCountdown--;
				if (autoSendCountdown == 0 && pendingSolution != null) {
					sendSolutionToServer(client, pendingSolution);
					pendingSolution = null;
				}
			}
		});

		// Register commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			ToggleCommand.register(dispatcher);
			AutoSendToggleCommand.register(dispatcher);
		});

		// Register chat message listener
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			handleGameMessage(message.getString());
		});

		// Register disconnect handler to cleanup pending tasks when leaving a server
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			autoSendCountdown = 0;
			pendingSolution = null;
			expectingEquation.set(false);
			CountdownManager.getInstance().stop();
		});
	}

	private void handleGameMessage(String message) {
		// Check if mod is enabled
		if (!ModConfig.getInstance().isEnabled()) {
			return;
		}

		// Check for ChatGame Event start
		if (message.contains("ChatGame Event")) {
			expectingEquation.set(true);
			CountdownManager.getInstance().startCountdown();
			return;
		}

		// Check for the prompt message
		if (expectingEquation.get() && message.contains("Solve this equation and get rewards!")) {
			return;
		}

		// Check for the actual equation
		if (expectingEquation.get() && message.contains("Equation:")) {
			expectingEquation.set(false);
			String solution = EquationSolver.solveFromMessage(message);

			if (solution != null) {
				MinecraftClient client = MinecraftClient.getInstance();
				// Display solution in red bold text locally
				Text solutionText = Text.literal("§c§l[HashHive] Solution: " + solution);
				client.execute(() -> {
					if (client.player != null) {
						client.player.sendMessage(solutionText, false);
					}
				});

				// Check if auto-submit is enabled
				if (ModConfig.getInstance().isAutoSubmitEnabled()) {
					pendingSolution = solution;
					autoSendCountdown = AUTO_SEND_DELAY_TICKS;
				}
			}
		}
	}

	private void sendSolutionToServer(MinecraftClient client, String solution) {
		if (client.player != null && client.player.networkHandler != null) {
			client.player.networkHandler.sendChatMessage(solution);
			Text sentMessage = Text.literal("§a[HashHive] Sent solution to the server!");
			client.player.sendMessage(sentMessage, false);
		}
	}

	private void renderHud(DrawContext drawContext, RenderTickCounter tickCounter) {
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
			if (ModConfig.getInstance().isAutoSubmitEnabled()) {
				timeText += " §a[Auto]";
			}

			int x = 100;
			int y = 3;

			// Draw black background for better visibility
			int textWidth = client.textRenderer.getWidth(timeText);
			drawContext.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);

			// Draw text with shadow for better visibility
			drawContext.drawTextWithShadow(client.textRenderer, timeText, x, y, 0xFFFFFFFF);
		}
	}
}
