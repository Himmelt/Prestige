package org.soraworld.prestige.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.soraworld.prestige.manager.FameManager;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.core.PlayerScore;
import org.soraworld.violet.command.CommandViolet;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.util.ListUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandPrestige extends CommandViolet {

    public CommandPrestige(String name, String perm, final FameManager config) {
        super(name, perm, config, config.plugin);
        addSub(new IICommand("add", Constant.PERM_ADMIN, config) {
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.size() == 2) {
                    try {
                        int score = Integer.valueOf(args.get(1));
                        PlayerScore ps = config.getScore(args.get(0));
                        ps.setScore(ps.getScore() + score);
                        config.saveScore();
                        config.send(sender, "addScore", ps.getName(), score, ps.getScore());
                    } catch (Throwable ignored) {
                        config.sendV(sender, Violets.KEY_INVALID_INT);
                    }
                } else {
                    config.sendV(sender, Violets.KEY_INVALID_ARG);
                }
                return true;
            }

            @Nonnull
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
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.size() == 2) {
                    try {
                        PlayerScore ps = config.getScore(args.get(0));
                        ps.setScore(Integer.valueOf(args.get(1)));
                        config.saveScore();
                        config.send(sender, "setScore", ps.getName(), ps.getScore());
                    } catch (Throwable ignored) {
                        config.sendV(sender, Violets.KEY_INVALID_INT);
                    }
                } else {
                    config.sendV(sender, Violets.KEY_INVALID_ARG);
                }
                return true;
            }

            @Nonnull
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
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (sender instanceof Player) {
                    PlayerScore ps = config.getScore(sender.getName());
                    config.send(sender, "playerInfo", ps.getScore(), ps.getLevel().getName());
                } else {
                    config.sendV(sender, Violets.KEY_ONLY_PLAYER);
                }
                return true;
            }
        });
        addSub(new IICommand("top", null, config) {
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.showRank(sender, 1);
                } else {
                    try {
                        config.showRank(sender, Integer.valueOf(args.get(0)));
                    } catch (Throwable ignored) {
                        config.sendV(sender, Violets.KEY_INVALID_INT);
                    }
                }
                return true;
            }
        });
        addSub(new IICommand("open", Constant.PERM_ADMIN, config) {
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        World world = ((Player) sender).getWorld();
                        config.openWorld(world);
                        config.send(sender, "openWorld", world.getName());
                    } else {
                        config.sendV(sender, Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG);
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.openWorld(world);
                        config.send(sender, "openWorld", world.getName());
                    } else {
                        config.send(sender, "invalidWorldName");
                    }
                }
                return true;
            }

            @Nonnull
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
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    if (sender instanceof Player) {
                        World world = ((Player) sender).getWorld();
                        config.closeWorld(world);
                        config.send(sender, "closeWorld", world.getName());
                    } else {
                        config.sendV(sender, Violets.KEY_ONLY_PLAYER_OR_INVALID_ARG);
                    }
                } else {
                    World world = Bukkit.getWorld(args.get(0));
                    if (world != null) {
                        config.closeWorld(world);
                        config.send(sender, "closeWorld", world.getName());
                    } else {
                        config.send(sender, "invalidWorldName");
                    }
                }
                return true;
            }

            @Nonnull
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchWorlds("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchWorlds(args.get(0));
                }
                return new ArrayList<>();
            }
        });
        addSub(new IICommand("exec", Constant.PERM_ADMIN, config) {
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    config.execCommands();
                } else {
                    config.execCommands(args.get(0));
                }
                return true;
            }

            @Nonnull
            public List<String> getTabCompletions(ArrayList<String> args) {
                if (args.isEmpty()) {
                    return ListUtil.getMatchPlayers("");
                } else if (args.size() == 1) {
                    return ListUtil.getMatchPlayers(args.get(0));
                }
                return new ArrayList<>();
            }
        });
        addSub(new IICommand("gift", null, config, true) {

            public boolean execute(Player player, ArrayList<String> args) {
                Long last = config.gifts.get(player.getName());
                if (last == null) {
                    config.gifts.put(player.getName(), System.currentTimeMillis());
                    config.execCommands(player.getName());
                    config.saveGifts();
                } else if (System.currentTimeMillis() - last > config.giftCoolDown * 60000) {
                    config.gifts.put(player.getName(), System.currentTimeMillis());
                    config.execCommands(player.getName());
                    config.saveGifts();
                } else {
                    config.send(player, "You have got your gift!");
                }
                return true;
            }
        });
    }

}
