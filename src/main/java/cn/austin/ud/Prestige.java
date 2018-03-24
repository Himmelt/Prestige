package cn.austin.ud;

import org.bukkit.plugin.java.JavaPlugin;

public class Prestige extends JavaPlugin {

    public static Prestige INSTANCE;


    public void onEnable() {
        this.getLogger().info("声望值插件加载成功");
        INSTANCE = this;
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
        FileManager.relaodConfig();
        this.getServer().getPluginCommand("prestige").setExecutor(new CommandHandler());
    }
}
