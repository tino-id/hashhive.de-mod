package codes.tino.hashhive.mixin;

import codes.tino.hashhive.CountdownManager;
import codes.tino.hashhive.EquationSolver;
import codes.tino.hashhive.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    private boolean expectingEquation = false;

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        // Check if mod is enabled
        if (!ModConfig.getInstance().isEnabled()) {
            return;
        }

        String message = packet.content().getString();

        // Check for ChatGame Event start
        if (message.contains("ChatGame Event")) {
            expectingEquation = true;

            // Start the 10-minute countdown
            CountdownManager.getInstance().startCountdown();

            return;
        }

        // Check for the prompt message
        if (expectingEquation && message.contains("Solve this equation and get rewards!")) {
            return;
        }

        // Check for the actual equation
        if (expectingEquation && message.contains("Equation:")) {
            String solution = EquationSolver.solveFromMessage(message);

            if (solution != null) {
                // Schedule message sending on the client thread to avoid threading issues
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    if (client.player != null) {
                        // Display solution in red bold text locally
                        Text solutionText = Text.literal("§c§l[HashHive] Solution: " + solution);
                        client.player.sendMessage(solutionText, false);
                    }
                });
            }

            expectingEquation = false;
        }
    }
}
