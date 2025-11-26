package codes.tino.hashhive;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HashHiveCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("hh")
                // /hh - show full status
                .executes(HashHiveCommand::status)
                // /hh toggle - toggle mod
                .then(literal("toggle")
                        .executes(HashHiveCommand::toggleMod))
                // /hh enable - enable mod
                .then(literal("enable")
                        .executes(HashHiveCommand::enableMod))
                // /hh disable - disable mod
                .then(literal("disable")
                        .executes(HashHiveCommand::disableMod))
                // /hh auto - auto-send subcommands
                .then(literal("auto")
                        .executes(HashHiveCommand::autoStatus)
                        .then(literal("toggle")
                                .executes(HashHiveCommand::toggleAuto))
                        .then(literal("enable")
                                .executes(HashHiveCommand::enableAuto))
                        .then(literal("disable")
                                .executes(HashHiveCommand::disableAuto)))
                // /hh help - show help
                .then(literal("help")
                        .executes(HashHiveCommand::help)));
    }

    // --- Mod toggle commands ---

    private static int status(CommandContext<FabricClientCommandSource> context) {
        ModConfig config = ModConfig.getInstance();
        boolean modEnabled = config.isEnabled();
        boolean autoEnabled = config.isAutoSubmitEnabled();

        String modStatus = modEnabled ? "§aenabled" : "§cdisabled";
        String autoStatus = autoEnabled ? "§aenabled" : "§cdisabled";

        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Status:"));
        context.getSource().sendFeedback(Text.literal("  Mod: " + modStatus));
        context.getSource().sendFeedback(Text.literal("  Auto-send: " + autoStatus));
        return 1;
    }

    private static int toggleMod(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().toggle();
        boolean enabled = ModConfig.getInstance().isEnabled();

        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod is now " + status));
        return 1;
    }

    private static int enableMod(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setEnabled(true);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod §aenabled"));
        return 1;
    }

    private static int disableMod(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setEnabled(false);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Mod §cdisabled"));
        return 1;
    }

    // --- Auto-send commands ---

    private static int autoStatus(CommandContext<FabricClientCommandSource> context) {
        boolean enabled = ModConfig.getInstance().isAutoSubmitEnabled();
        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send: " + status));
        return 1;
    }

    private static int toggleAuto(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().toggleAutoSubmit();
        boolean enabled = ModConfig.getInstance().isAutoSubmitEnabled();

        String status = enabled ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send is now " + status));
        return 1;
    }

    private static int enableAuto(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setAutoSubmitEnabled(true);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send §aenabled"));
        return 1;
    }

    private static int disableAuto(CommandContext<FabricClientCommandSource> context) {
        ModConfig.getInstance().setAutoSubmitEnabled(false);
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Auto-send §cdisabled"));
        return 1;
    }

    // --- Help command ---

    private static int help(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("§6[HashHive]§r Commands:"));
        context.getSource().sendFeedback(Text.literal("  §e/hh§r - Show status"));
        context.getSource().sendFeedback(Text.literal("  §e/hh toggle§r - Toggle mod on/off"));
        context.getSource().sendFeedback(Text.literal("  §e/hh enable§r - Enable mod"));
        context.getSource().sendFeedback(Text.literal("  §e/hh disable§r - Disable mod"));
        context.getSource().sendFeedback(Text.literal("  §e/hh auto§r - Show auto-send status"));
        context.getSource().sendFeedback(Text.literal("  §e/hh auto toggle§r - Toggle auto-send"));
        context.getSource().sendFeedback(Text.literal("  §e/hh auto enable§r - Enable auto-send"));
        context.getSource().sendFeedback(Text.literal("  §e/hh auto disable§r - Disable auto-send"));
        return 1;
    }
}
