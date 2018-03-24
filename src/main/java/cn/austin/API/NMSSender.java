package cn.austin.API;

import cn.austin.ud.Prestige;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class NMSSender extends JavaPlugin implements Listener {

    public static void sendActionBar(Player p, String message) {
        String PackageName = Prestige.INSTANCE.getServer().getClass().getPackage().getName();
        String version = PackageName.substring(PackageName.lastIndexOf(46) + 1);
        Logger logger = Prestige.INSTANCE.getLogger();

        try {
            Class e = Class.forName("NMS." + version + ".ActionBar");
            Method method = e.getDeclaredMethod("sendActionBar", Player.class, String.class);
            method.invoke(null, p, message);
        } catch (ClassNotFoundException var7) {
            p.sendMessage(message);
        } catch (NoSuchMethodException var8) {
            logger.warning("PLUGIN IS DAMAGED");
        } catch (SecurityException var9) {
            logger.warning("PERMISSION ERROR");
        } catch (IllegalAccessException var10) {
            logger.warning("METHOD_INVOKE ERROR #1");
        } catch (IllegalArgumentException var11) {
            logger.warning("METHOD_INVOKE ERROR #2");
        } catch (InvocationTargetException var12) {
            logger.warning("METHOD_INVOKE ERROR #3");
        }

    }
}
