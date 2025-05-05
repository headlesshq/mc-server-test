package io.github.headlesshq.testplugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.lib.PaperLib;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        PaperLib.suggestPaper(this);
        saveDefaultConfig();

        LiteralCommandNode<CommandSourceStack> testCommand = Commands.literal("mc-server-test-command")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("Hello from mc-server-test!");
                    return Command.SINGLE_SUCCESS;
                }).build();

        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                commands -> commands.registrar().register(testCommand)
        );
    }

}
