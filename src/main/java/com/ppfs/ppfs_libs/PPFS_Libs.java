// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfs.ppfs_libs;

import com.ppfs.ppfs_libs.listeners.menu.MenuListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PPFS_Libs extends JavaPlugin {
    @Getter
    private static PPFS_Libs instance;

    private static Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        metrics = new Metrics(this, 24257);

        registerListners();
    }

    private void registerListners(){
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new MenuListener(), this);
    }

}
