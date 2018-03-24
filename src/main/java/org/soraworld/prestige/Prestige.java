package org.soraworld.prestige;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.prestige.command.CommandPrestige;
import org.soraworld.prestige.command.IICommand;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.listener.EventListener;
import org.soraworld.prestige.util.ListUtils;

import java.util.List;

public class Prestige extends JavaPlugin {

    private Config config;
    private IICommand command;

    @Override
    public void onEnable() {
        config = new Config(this.getDataFolder(), this);
        config.load();
        config.save();
        this.getServer().getPluginManager().registerEvents(new EventListener(config, this), this);
        command = new CommandPrestige("prestige", this, config);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return command.execute(sender, ListUtils.arrayList(args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return command.onTabComplete(sender, cmd, alias, args);
    }

}
