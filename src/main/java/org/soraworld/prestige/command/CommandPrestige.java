package org.soraworld.prestige.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.violet.Violet;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;

public class CommandPrestige extends CommandViolet {

    public CommandPrestige(String name, final Config config, final Plugin plugin) {
        super(name, config, plugin);
        addSub(new IICommand("open") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        World world = ((Player) sender).getWorld();
                        config.openWorld(world);
                        config.iiChat.send(sender, config.iiLang.format("openWorld", world.getName()));
                    } else {
                        config.iiChat.send(sender, Violet.translate(config.iiLang.getLang(), Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG));
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.openWorld(world);
                        config.iiChat.send(sender, config.iiLang.format("openWorld", world.getName()));
                    } else {
                        config.iiChat.send(sender, config.iiLang.format("invalidWorldName"));
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("close") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        World world = ((Player) sender).getWorld();
                        config.closeWorld(world);
                        config.iiChat.send(sender, config.iiLang.format("closeWorld", world.getName()));
                    } else {
                        config.iiChat.send(sender, Violet.translate(config.iiLang.getLang(), Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG));
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.closeWorld(world);
                        config.iiChat.send(sender, config.iiLang.format("closeWorld", world.getName()));
                    } else {
                        config.iiChat.send(sender, config.iiLang.format("invalidWorldName"));
                    }
                }
                return true;
            }
        });
    }

}
