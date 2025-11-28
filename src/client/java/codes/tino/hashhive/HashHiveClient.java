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

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class HashHiveClient implements ClientModInitializer {
	public static final String MOD_ID = "hashhive";
	private final AtomicBoolean expectingEquation = new AtomicBoolean(false);
	private static final int AUTO_SEND_MIN_TICKS = 30; // 1.5 seconds = 30 ticks
	private static final int AUTO_SEND_MAX_TICKS = 70; // 3.5 seconds = 70 ticks
	private final Random random = new Random();
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
			HashHiveCommand.register(dispatcher);
		});

		// Register chat message listener with ability to cancel messages
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
			return handleGameMessage(message.getString());
		});

		// Register disconnect handler to cleanup pending tasks when leaving a server
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			autoSendCountdown = 0;
			pendingSolution = null;
			expectingEquation.set(false);
			CountdownManager.getInstance().stop();
		});
	}

	private boolean handleGameMessage(String message) {
		// Suppress "You can't mine this block!" messages
		if (message.contains("You can't mine this block!")) {
			return false;
		}

		// Check if mod is enabled
		if (!ModConfig.getInstance().isEnabled()) {
			return true;
		}

		// Check for ChatGame Event start
		if (message.contains("ChatGame Event")) {
			expectingEquation.set(true);
			CountdownManager.getInstance().startCountdown();
			return true;
		}

		// Check for the prompt message
		if (expectingEquation.get() && message.contains("Solve this equation and get rewards!")) {
			return true;
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
					// Random delay between 1.5 and 3.5 seconds
					autoSendCountdown = AUTO_SEND_MIN_TICKS + random.nextInt(AUTO_SEND_MAX_TICKS - AUTO_SEND_MIN_TICKS + 1);
				}
			}
		}

		return true;
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

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.options == null) {
			return;
		}

		// Don't render if HUD is hidden (F1 mode)
		if (client.options.hudHidden) {
			return;
		}

		CountdownManager countdown = CountdownManager.getInstance();
		String timeDisplay = countdown.isActive() ? countdown.getFormattedTime() : "--:--";
		String autoStatus = ModConfig.getInstance().isAutoSubmitEnabled() ? "§aOn" : "§cOff";
		String timeText = timeDisplay + " §7| §fAuto: " + autoStatus;

		int x = 100;
		int y = 3;

		// Draw black background for better visibility
		int textWidth = client.textRenderer.getWidth(timeText);
		drawContext.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);

		// Draw text with shadow for better visibility
		drawContext.drawTextWithShadow(client.textRenderer, timeText, x, y, 0xFFFFFFFF);
	}
}
