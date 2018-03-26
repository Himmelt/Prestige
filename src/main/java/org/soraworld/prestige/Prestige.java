package org.soraworld.prestige;

import org.bukkit.event.Listener;
import org.soraworld.prestige.command.CommandPrestige;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.listener.EventListener;
import org.soraworld.violet.VioletPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Prestige extends VioletPlugin {

    @Nonnull
    protected IIConfig registerConfig(File path) {
        return new Config(path, this);
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig config) {
        ArrayList<Listener> listeners = new ArrayList<>();
        if (config instanceof Config) listeners.add(new EventListener((Config) config, this));
        return listeners;
    }

    @Nullable
    protected IICommand registerCommand(IIConfig config) {
        if (config instanceof Config) return new CommandPrestige(Constant.PLUGIN_ID, null, (Config) config, this);
        return null;
    }

}
