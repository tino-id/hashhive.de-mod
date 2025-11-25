package codes.tino.hashhive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ToggleCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("hashhive")
                .then(literal("toggle")
                        .executes(ToggleCommand::toggle))
                .then(literal("enable")
                        .executes(ToggleCommand::enable))
                .then(literal("disable")
                        .executes(ToggleCommand::disable))
                .executes(ToggleCommand::status));
    }

    private static int toggle(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().toggle();
        boolean enabled = ModConfig.getInstance().isEnabled();

        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod is now " + status));

        return 1;
    }

    private static int enable(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setEnabled(true);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod §aenabled"));
        return 1;
    }

    private static int disable(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setEnabled(false);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod §cdisabled"));
        return 1;
    }

    private static int status(CommandContext<FabricClientCommandSource> context) {
        boolean enabled = ModConfig.getInstance().isEnabled();
        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Status: " + status));
        return 1;
    }
}
