package org.soraworld.prestige;

import org.soraworld.prestige.command.CommandPrestige;
import org.soraworld.prestige.config.Config;
import org.soraworld.prestige.constant.Constant;
import org.soraworld.prestige.listener.EventListener;
import org.soraworld.violet.VioletPlugin;
import org.soraworld.violet.command.IICommand;
import org.soraworld.violet.config.IIConfig;

import javax.annotation.Nonnull;
import java.io.File;

public class Prestige extends VioletPlugin {

    @Nonnull
    protected IIConfig registerConfig(File path) {
        return new Config(path);
    }

    protected void registerEvents() {
        if (iconfig instanceof Config) registerEvent(new EventListener((Config) iconfig));
    }

    protected IICommand registerCommand() {
        if (iconfig instanceof Config) return new CommandPrestige(Constant.PLUGIN_ID, null, (Config) iconfig);
        return null;
    }

    protected void afterEnable() {

    }

    protected void beforeDisable() {

    }

}
