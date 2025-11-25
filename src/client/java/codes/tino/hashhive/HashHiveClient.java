package codes.tino.hashhive;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class HashHiveClient implements ClientModInitializer {
	public static final String MOD_ID = "hashhive";

	@Override
	public void onInitializeClient() {
		// Register HUD renderer
		HudRenderCallback.EVENT.register(new HudRenderer());

		// Register client tick event to update countdown
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CountdownManager.getInstance().tick();
		});

		// Register toggle command
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			ToggleCommand.register(dispatcher);
		});
	}
}
