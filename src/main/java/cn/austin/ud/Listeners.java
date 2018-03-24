package cn.austin.ud;

import cn.austin.API.NMSSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Listeners implements Listener {

    private static final int messagePoolSendDelay = 80;
    static Map playerDatas = new HashMap();
    private static Map playerMessagePool = new HashMap();



    public void handlePlayerMessage(final Player p) {
        int i = 1;

        for (Iterator var4 = ((List) playerMessagePool.get(p.getName().toLowerCase())).iterator(); var4.hasNext(); ++i) {
            final String msg = (String) var4.next();
            Prestige.INSTANCE.getServer().getScheduler().runTaskLater(Prestige.INSTANCE, new Runnable() {
                public void run() {
                    if (!p.isDead() && p.isOnline()) {
                        NMSSender.sendActionBar(p, msg);
                    }
                }
            }, (long) (i == 0 ? 10 : 80 * i));
        }

        playerMessagePool.put(p.getName().toLowerCase(), null);
    }


}
