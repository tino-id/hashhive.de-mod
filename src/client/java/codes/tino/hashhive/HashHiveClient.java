package codes.tino.hashhive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HashHiveClient implements ClientModInitializer {
	public static final String MOD_ID = "hashhive";
	private boolean expectingEquation = false;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


	@Override
	public void onInitializeClient() {
		// Register HUD renderer
		HudRenderCallback.EVENT.register(new HudRenderer());

		// Register client tick event to update countdown
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CountdownManager.getInstance().tick();
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
	}

	private void handleGameMessage(String message) {
		// Check if mod is enabled
		if (!ModConfig.getInstance().isEnabled()) {
			return;
		}

		// Check for ChatGame Event start
		if (message.contains("ChatGame Event")) {
			expectingEquation = true;
			CountdownManager.getInstance().startCountdown();
			return;
		}

		// Check for the prompt message
		if (expectingEquation && message.contains("Solve this equation and get rewards!")) {
			return;
		}

		// Check for the actual equation
		if (expectingEquation && message.contains("Equation:")) {
			expectingEquation = false;
			String solution = EquationSolver.solveFromMessage(message);

			if (solution != null) {
				MinecraftClient client = MinecraftClient.getInstance();
				if (client.player != null) {
					// Display solution in red bold text locally
					Text solutionText = Text.literal("§c§l[HashHive] Solution: " + solution);
					client.player.sendMessage(solutionText, false);

					// Check if auto-submit is enabled
					if (ModConfig.getInstance().isAutoSubmitEnabled()) {
						scheduler.schedule(() -> {
							if (client.player != null) {
								client.player.networkHandler.sendChatMessage(solution);
								Text sentMessage = Text.literal("§a[HashHive] Sent solution to the server!");
								client.player.sendMessage(sentMessage, false);
							}
						}, 1500, TimeUnit.MILLISECONDS);
					}
				}
			}
		}
	}
}
