package org.soraworld.prestige.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.violet.Violet;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

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
                        int score = Integer.valueOf(args.get(1));
                        PlayerScore ps = config.getScore(player);
                        ps.setScore(ps.getScore() + score);
                        config.iiChat.send(sender, config.iiLang.format("addScore", ps.getName(), score, ps.getScore()));
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_INT));
                    }
                } else {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_ARG));
                }
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchPlayers("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchPlayers(args.get(0));
                }
                return new ArrayList<>();
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
                        PlayerScore ps = config.getScore(player);
                        ps.setScore(Integer.valueOf(args.get(1)));
                        config.iiChat.send(sender, config.iiLang.format("setScore", ps.getName(), ps.getScore()));
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_INT));
                    }
                } else {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_ARG));
                }
                return true;
            }

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchPlayers("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchPlayers(args.get(0));
                }
                return new ArrayList<>();
            }
        });
        addSub(new IICommand("info", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (sender instanceof Player) {
                    PlayerScore ps = config.getScore((Player) sender);
                    config.iiChat.send(sender, config.iiLang.format("infoScore", ps.getScore()));
                    config.iiChat.send(sender, config.iiLang.format("infoLevel", ps.getLevel().getName()));
                } else {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_ONLY_PLAYER));
                }
                return true;
            }
        });
        addSub(new IICommand("top", null, config) {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    showRank(sender, 1);
                } else {
                    try {
                        int page = Integer.valueOf(args.get(0));
                        showRank(sender, page);
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_INT));
                    }
                }
                return true;
            }

            private void showRank(CommandSender sender, int page) {
                if (page < 1) page = 1;
                config.iiChat.send(sender, config.iiLang.format("rankHead"));
                for (int i = page; i <= page + 10; i++) {
                    config.iiChat.send(sender, config.iiLang.format("rankLine", i));
                }
                config.iiChat.send(sender, config.iiLang.format("rankFoot"));
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

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchWorlds("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchWorlds(args.get(0));
                }
                return new ArrayList<>();
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

            @Override
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchWorlds("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchWorlds(args.get(0));
                }
                return new ArrayList<>();
            }
        });
        addSub(new IICommand("createlvl", Constant.PERM_ADMIN, config, "clvl") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_ARG));
                } else {
                    try {
                        int score = Integer.valueOf(args.get(0));
                        config.createLevel(score);
                    } catch (Throwable ignored) {
                        config.iiChat.send(sender, Violet.translate(config.getLang(), Violets.KEY_INVALID_INT));
                    }
                }
                return true;
            }
        });
    }

}
