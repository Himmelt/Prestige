package org.soraworld.prestige.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.violet.Violet;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;

import java.util.ArrayList;

public class CommandPrestige extends CommandViolet {

    public CommandPrestige(String name, String perm, final Config config, final Plugin plugin) {
        super(name, perm, config, plugin);
        addSub(new IICommand("add", Constant.PERM_ADMIN, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.size() == 2) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
                    if (player == null) {
                        config.iiChat.send(sender, config.iiLang.format("noSuchPlayer", args.get(0)));
                        return true;
                    }
                    try {
                        int point = Integer.valueOf(args.get(1));
                        config.setScore(player, config.getScore(player) + point);
                        config.iiChat.send(sender, config.iiLang.format("addScore", player.getName(), point, config.getScore(player)));
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), "invalidInt"));
                    }
                } else {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), "invalidArg"));
                }
                return true;
            }
        });
        addSub(new IICommand("set", Constant.PERM_ADMIN, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.size() == 2) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
                    if (player == null) {
                        config.iiChat.send(sender, config.iiLang.format("noSuchPlayer", args.get(0)));
                        return true;
                    }
                    try {
                        int point = Integer.valueOf(args.get(1));
                        config.setScore(player, point);
                        config.iiChat.send(sender, config.iiLang.format("setScore", player.getName(), config.getScore(player)));
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), "invalidInt"));
                    }
                } else {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), "invalidArg"));
                }
                return true;
            }
        });
        addSub(new IICommand("info", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (sender instanceof Player) {
                    int score = config.getScore((Player) sender);
                    config.iiChat.send(sender, config.iiLang.format("infoScore", score));
                    config.iiChat.send(sender, config.iiLang.format("infoLevel", config.getLevel(score).getName()));
                }
                return true;
            }
        });
        addSub(new IICommand("open", Constant.PERM_ADMIN, config) {
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
        addSub(new IICommand("close", Constant.PERM_ADMIN, config) {
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
