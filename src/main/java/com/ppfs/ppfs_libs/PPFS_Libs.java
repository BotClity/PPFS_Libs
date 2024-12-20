package com.ppfs.ppfs_libs;

import com.ppfs.ppfs_libs.service.logger.LoggerService;
import com.ppfs.ppfs_libs.service.logger.PaperLogger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PPFS_Libs extends JavaPlugin {
    @Getter
    private static PPFS_Libs instance;
    @Getter
    private static PaperLogger paperLogger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        LoggerService.initialize(this);
        instance = this;
        paperLogger = LoggerService.getInstance().getOrCreateLogger("PPFS_Libs", true, true);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
