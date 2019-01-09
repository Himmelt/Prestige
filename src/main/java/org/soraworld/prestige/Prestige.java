package org.soraworld.prestige;

import org.bukkit.event.Listener;
import org.soraworld.prestige.command.CommandPrestige;
import org.soraworld.prestige.manager.FameManager;
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
        return new FameManager(path, this);
    }

    @Nonnull
    protected List<Listener> registerEvents(IIConfig iiConfig) {
        ArrayList<Listener> listeners = new ArrayList<>();
        if (config instanceof FameManager) listeners.add(new EventListener((FameManager) config));
        return listeners;
    }

    @Nullable
    protected IICommand registerCommand(IIConfig iiConfig) {
        if (config instanceof FameManager) return new CommandPrestige(Constant.PLUGIN_ID, null, (FameManager) config);
        return null;
    }

    protected void afterEnable() {

    }

    protected void beforeDisable() {

    }

}
