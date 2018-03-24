package cn.austin.ud;

import java.io.File;
import java.util.*;

public class Toper {

    private static List ranks;
    private static Map rankFind = new HashMap();
    private static int pages;
    private static final int pageShowRanks = 10;


    public static void sortRank() {
        ranks = new ArrayList();
        File[] var3;
        int var2 = (var3 = (new File(Prestige.INSTANCE.getDataFolder() + "\\PlayerDatas")).listFiles()).length;

        for (int rank = 0; rank < var2; ++rank) {
            File i = var3[rank];
            if (!i.isDirectory() && i.getName().toLowerCase().endsWith(".yml")) {
                ranks.add(new Toper.Rank(new PlayerData(i)));
            }
        }

        Collections.sort(ranks, new Comparator<Toper.Rank>() {
            @Override
            public int compare(Toper.Rank rank1, Toper.Rank rank2) {
                return Integer.compare(rank2.Score, rank1.Score);
            }
        });
        int var4 = 0;

        for (Iterator var6 = ranks.iterator(); var6.hasNext(); ++var4) {
            Toper.Rank var5 = (Toper.Rank) var6.next();
            rankFind.put(var5.name.toLowerCase(), var4);
        }

        pages = ranks.size() / 10 + ranks.size() % 10 == 0 ? 0 : 1;
    }

    public static List getRankList() {
        return ranks;
    }

    public static int getPages() {
        return pages;
    }

    public static Map getPlayerRankMap() {
        return rankFind;
    }

    public static void updatePlayer(PlayerData pd) {
        boolean hasChange = false;
        int currentIndex = (Integer) rankFind.get(pd.getName().toLowerCase());

        Toper.Rank newRank;
        Toper.Rank rank;
        for (newRank = new Toper.Rank(pd); currentIndex - 1 >= 0 && newRank.Score > (rank = (Toper.Rank) ranks.get(currentIndex - 1)).Score; --currentIndex) {
            hasChange = true;
            ranks.set(currentIndex, rank);
            ranks.set(currentIndex - 1, newRank);
            rankFind.put(rank.name.toLowerCase(), currentIndex);
            rankFind.put(newRank.name.toLowerCase(), currentIndex - 1);
        }

        while (currentIndex + 1 < ranks.size() && newRank.Score < (rank = (Toper.Rank) ranks.get(currentIndex + 1)).Score) {
            hasChange = true;
            ranks.set(currentIndex, rank);
            ranks.set(currentIndex + 1, newRank);
            rankFind.put(rank.name.toLowerCase(), currentIndex);
            rankFind.put(newRank.name.toLowerCase(), currentIndex + 1);
            ++currentIndex;
        }

        if (!hasChange) {
            ranks.set(currentIndex, newRank);
        }

    }

    public static void addNewRank(PlayerData pd) {
        ranks.add(new Toper.Rank(pd));
    }

    public static String getRanks(int page) {
        String result = "§b===== §e声望值排行榜 §b=====";
        int index = (page - 1) * 10 + 1;

        for (int i = index; i < index + 10; ++i) {
            Toper.Rank rank = (Toper.Rank) ranks.get(i - 1);
            result = result + "\n§c" + i + "§d.§b" + rank.name + " §e- §f" + rank.gradeName + " §e- §a" + rank.Score + "§e声望值";
            if (i == ranks.size()) {
                break;
            }
        }

        result = result + "\n->§a当前第§e" + page + "§e页" + "/" + "共§b" + pages + "§e页<-";
        result = result + "\n§b小贴士：§e输入/pres top <页数>进行换页";
        return result;
    }

    public static class Rank {

        String name;
        String gradeName;
        int Score;


        public Rank(PlayerData pd) {
            this.name = pd.getName();
            this.Score = pd.getScore();
            this.gradeName = pd.getGrade().getName();
        }

        public int hashCode() {
            return this.Score;
        }

        public boolean equals(Object obj) {
            Toper.Rank rank = (Toper.Rank) obj;
            return this.name.equalsIgnoreCase(rank.name) && this.Score == rank.Score && rank.gradeName.equalsIgnoreCase(this.gradeName);
        }
    }
}
