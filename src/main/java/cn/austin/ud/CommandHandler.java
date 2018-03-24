package cn.austin.ud;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommandHandler implements CommandExecutor {

    private static final String helpMsg = "§a=====§e声望值§a=====\n§b/pres help §a- §e查看命令帮助\n§b/pres info <plyaer> §a- §e查看自己或他人信息,<player>不填为自己\n§b/pres top §a- §e查看排行榜\n§b/pres admin §a- §e查看管理员帮助";
    private static final String adminHelpMsg = "§a=====§e声望值管理帮助§a=====\n§b/pres set <player> <prestige> §a- §e设置玩家声望值\n§b/pres add <player> <prestige> §a- §e给予玩家声望值\n§b/pres del <player> <prestige> §a- §e删除玩家声望值\n§b/pres reload §a- §e插件重载";
    private static final String prefix = "§b[§a声望值§b]";


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§a=====§e声望值§a=====\n§b/pres help §a- §e查看命令帮助\n§b/pres info <plyaer> §a- §e查看自己或他人信息,<player>不填为自己\n§b/pres top §a- §e查看排行榜\n§b/pres admin §a- §e查看管理员帮助");
            return false;
        } else {
            label164:
            {
                String var5;
                String spend;
                int score;
                boolean score2;
                PlayerData pd2;
                switch ((var5 = args[0].toLowerCase()).hashCode()) {
                    case -934641255:
                        if (var5.equals("reload")) {
                            if (!sender.hasPermission("Prestige.admin") && !sender.hasPermission("Prestige.*")) {
                                sender.sendMessage("§b[§a声望值§b]§e你没有权限执行此命令");
                                return false;
                            }

                            long spend4 = FileManager.relaodConfig();
                            sender.sendMessage("§b[§a声望值§b]§e重载完成...耗时" + spend4 + "ms");
                            return false;
                        }
                        break;
                    case 96417:
                        if (var5.equals("add")) {
                            if (!sender.hasPermission("Prestige.admin") && !sender.hasPermission("Prestige.*")) {
                                sender.sendMessage("§b[§a声望值§b]§e你没有权限执行此命令");
                                return false;
                            }

                            if (args.length < 3) {
                                sender.sendMessage("§b[§a声望值§b]§e参数错误!输入/pres admin查看帮助");
                                return false;
                            }

                            spend = args[1].toLowerCase();
                            score2 = false;

                            try {
                                score = Integer.parseInt(args[2]);
                            } catch (NumberFormatException var13) {
                                sender.sendMessage("§b[§a声望值§b]§e请输入正确的数字!");
                                return false;
                            }

                            if (Toper.getPlayerRankMap().get(spend) == null) {
                                sender.sendMessage("§b[§a声望值§b]§e玩家不存在");
                                return false;
                            }

                            if ((pd2 = (PlayerData) Listeners.playerDatas.get(spend)) != null) {
                                pd2.addScore(score);
                                pd2.updateGrade();
                                Toper.updatePlayer(pd2);
                                Listeners.playerDatas.put(spend, pd2);
                                sender.sendMessage("§b[§a声望值§b]§e给予成功!");
                                return false;
                            }

                            pd2 = new PlayerData(new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas", spend + ".yml"));
                            pd2.addScore(score);
                            pd2.updateGrade();
                            Toper.updatePlayer(pd2);
                            sender.sendMessage("§b[§a声望值§b]§e给予成功!");
                            return false;
                        }
                        break;
                    case 99339:
                        if (var5.equals("del")) {
                            if (!sender.hasPermission("Prestige.admin") && !sender.hasPermission("Prestige.*")) {
                                sender.sendMessage("§b[§a声望值§b]§e你没有权限执行此命令");
                                return false;
                            }

                            if (args.length < 3) {
                                sender.sendMessage("§b[§a声望值§b]§e参数错误!输入/pres admin查看帮助");
                                return false;
                            }

                            spend = args[1].toLowerCase();
                            score2 = false;

                            try {
                                score = Integer.parseInt(args[2]);
                            } catch (NumberFormatException var12) {
                                sender.sendMessage("§b[§a声望值§b]§e请输入正确的数字!");
                                return false;
                            }

                            if (Toper.getPlayerRankMap().get(spend) == null) {
                                sender.sendMessage("§b[§a声望值§b]§e玩家不存在");
                                return false;
                            }

                            if ((pd2 = (PlayerData) Listeners.playerDatas.get(spend)) != null) {
                                pd2.delScore(score);
                                pd2.updateGrade();
                                Toper.updatePlayer(pd2);
                                Listeners.playerDatas.put(spend, pd2);
                                sender.sendMessage("§b[§a声望值§b]§e删除成功!");
                                return false;
                            }

                            pd2 = new PlayerData(new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas", spend + ".yml"));
                            pd2.delScore(score);
                            pd2.updateGrade();
                            Toper.updatePlayer(pd2);
                            sender.sendMessage("§b[§a声望值§b]§e删除成功!");
                            return false;
                        }
                        break;
                    case 113762:
                        if (var5.equals("set")) {
                            if (!sender.hasPermission("Prestige.admin") && !sender.hasPermission("Prestige.*")) {
                                sender.sendMessage("§b[§a声望值§b]§e你没有权限执行此命令");
                                return false;
                            }

                            if (args.length < 3) {
                                sender.sendMessage("§b[§a声望值§b]§e参数错误!输入/pres admin查看帮助");
                                return false;
                            }

                            spend = args[1].toLowerCase();
                            score2 = false;

                            try {
                                score = Integer.parseInt(args[2]);
                            } catch (NumberFormatException var11) {
                                sender.sendMessage("§b[§a声望值§b]§e请输入正确的数字!");
                                return false;
                            }

                            if (Toper.getPlayerRankMap().get(spend) == null) {
                                sender.sendMessage("§b[§a声望值§b]§e玩家不存在");
                                return false;
                            }

                            if ((pd2 = (PlayerData) Listeners.playerDatas.get(spend)) != null) {
                                pd2.setScore(score);
                                pd2.updateGrade();
                                Toper.updatePlayer(pd2);
                                Listeners.playerDatas.put(spend, pd2);
                                sender.sendMessage("§b[§a声望值§b]§e设置成功!");
                                return false;
                            }

                            pd2 = new PlayerData(new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas", spend + ".yml"));
                            pd2.setScore(score);
                            pd2.updateGrade();
                            Toper.updatePlayer(pd2);
                            sender.sendMessage("§b[§a声望值§b]§e设置成功!");
                            return false;
                        }
                        break;
                    case 115029:
                        if (var5.equals("top")) {
                            if (args.length == 1) {
                                sender.sendMessage(Toper.getRanks(1));
                                return false;
                            }

                            boolean spend2 = true;

                            int spend3;
                            try {
                                spend3 = Integer.parseInt(args[1]);
                            } catch (NumberFormatException var10) {
                                sender.sendMessage("§b[§a声望值§b]§e输入页数不正确!共§b" + Toper.getPages() + "§e页");
                                return false;
                            }

                            if (spend3 > Toper.getPages()) {
                                sender.sendMessage("§b[§a声望值§b]§e输入页数过大!共§b" + Toper.getPages() + "§e页");
                                return false;
                            }
                            break label164;
                        }
                        break;
                    case 3198785:
                        if (var5.equals("help")) {
                            break label164;
                        }
                        break;
                    case 3237038:
                        if (var5.equals("info")) {
                            if (args.length == 1) {
                                if (!(sender instanceof Player)) {
                                    sender.sendMessage("§e必须是玩家才能够查看自身声望值！");
                                    return false;
                                }

                                PlayerData spend1 = (PlayerData) Listeners.playerDatas.get(sender.getName().toLowerCase());
                                String score1 = spend1.getGrade().getName();
                                int pd1 = spend1.getScore();
                                int rank = ((Integer) Toper.getPlayerRankMap().get(sender.getName().toLowerCase())).intValue();
                                sender.sendMessage("§e玩家 §b" + sender.getName() + " §e的信息");
                                sender.sendMessage("§e当前段位为: §f" + score1);
                                sender.sendMessage("§e当前分数为: §b" + pd1);
                                sender.sendMessage("§e当前排名为: §b" + (rank + 1));
                                return false;
                            }

                            spend = args[1].toLowerCase();
                            if (Toper.getPlayerRankMap().get(spend) == null) {
                                sender.sendMessage("§b[§a声望值§b]§e玩家不存在!");
                                return false;
                            }

                            score = ((Integer) Toper.getPlayerRankMap().get(spend)).intValue();
                            Toper.Rank pd = (Toper.Rank) Toper.getRankList().get(score);
                            sender.sendMessage("§e玩家 §b" + spend + " §e的信息");
                            sender.sendMessage("§e当前段位为: §f" + pd.gradeName);
                            sender.sendMessage("§e当前分数为: §b" + pd.Score);
                            sender.sendMessage("§e当前排名为: §b" + (score + 1));
                            return false;
                        }
                        break;
                    case 92668751:
                        if (var5.equals("admin")) {
                            if (!sender.hasPermission("Prestige.admin") && !sender.hasPermission("Prestige.*")) {
                                sender.sendMessage("§b[§a声望值§b]§e你没有权限执行此命令");
                                return false;
                            }

                            sender.sendMessage("§a=====§e声望值管理帮助§a=====\n§b/pres set <player> <prestige> §a- §e设置玩家声望值\n§b/pres add <player> <prestige> §a- §e给予玩家声望值\n§b/pres del <player> <prestige> §a- §e删除玩家声望值\n§b/pres reload §a- §e插件重载");
                            return false;
                        }
                }

                sender.sendMessage("§b[§a声望值§b]§e参数错误!详情请输入/pres help查看帮助");
                return false;
            }

            sender.sendMessage("§a=====§e声望值§a=====\n§b/pres help §a- §e查看命令帮助\n§b/pres info <plyaer> §a- §e查看自己或他人信息,<player>不填为自己\n§b/pres top §a- §e查看排行榜\n§b/pres admin §a- §e查看管理员帮助");
            return false;
        }
    }
}
