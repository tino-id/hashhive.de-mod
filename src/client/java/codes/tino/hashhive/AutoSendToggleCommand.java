package codes.tino.hashhive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AutoSendToggleCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("hhauto")
                .then(literal("toggle")
                        .executes(AutoSendToggleCommand::toggle))
                .then(literal("enable")
                        .executes(AutoSendToggleCommand::enable))
                .then(literal("disable")
                        .executes(AutoSendToggleCommand::disable))
                .executes(AutoSendToggleCommand::status));
    }

    private static int toggle(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().toggleAutoSubmit();
        boolean enabled = ModConfig.getInstance().isAutoSubmitEnabled();

        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send is now " + status));

        return 1;
    }

    private static int enable(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setAutoSubmitEnabled(true);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send §aenabled"));
        return 1;
    }

    private static int disable(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setAutoSubmitEnabled(false);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send §cdisabled"));
        return 1;
    }

    private static int status(CommandContext<FabricClientCommandSource> context) {
        boolean enabled = ModConfig.getInstance().isAutoSubmitEnabled();
        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send status: " + status));
        return 1;
    }
}
