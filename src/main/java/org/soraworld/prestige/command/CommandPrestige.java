package org.soraworld.prestige.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.config.LangKeys;
import org.soraworld.prestige.util.ServerUtils;

import java.util.ArrayList;

public class CommandPrestige extends IICommand {

    public CommandPrestige(String name, final Plugin plugin, final Config config) {
        super(name);
        addSub(new IICommand("save") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                config.save();
                ServerUtils.send(sender, LangKeys.format("configSaved"));
                return true;
            }
        });
        addSub(new IICommand("reload") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                config.load();
                ServerUtils.send(sender, LangKeys.format("configReloaded"));
                return true;
            }
        });
        addSub(new IICommand("lang") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    ServerUtils.send(sender, LangKeys.format("language", config.getLang()));
                } else {
                    config.setLang(args.get(0));
                    ServerUtils.send(sender, LangKeys.format("language", config.getLang()));
                }
                return true;
            }
        });
        addSub(new IICommand("open") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        config.openWorld(((Player) sender).getWorld());
                        ServerUtils.send(sender, LangKeys.format("openWorld", ((Player) sender).getWorld().getName()));
                    } else {
                        ServerUtils.send(sender, LangKeys.format("onlyPlayerOrEmptyArg"));
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.openWorld(world);
                        ServerUtils.send(sender, LangKeys.format("openWorld", world.getName()));
                    } else {
                        ServerUtils.send(sender, LangKeys.format("invalidWorldName"));
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
                        config.closeWorld(((Player) sender).getWorld());
                        ServerUtils.send(sender, LangKeys.format("closeWorld"));
                    } else {
                        ServerUtils.send(sender, LangKeys.format("onlyPlayerOrEmptyArg"));
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.closeWorld(world);
                        ServerUtils.send(sender, LangKeys.format("closeWorld"));
                    } else {
                        ServerUtils.send(sender, LangKeys.format("invalidWorldName"));
                    }
                }
                return true;
            }
        });
    }

    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (args.size() >= 1) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) {
                return sub.execute(sender, args);
            }
        }
        return false;
    }

}
