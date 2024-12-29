package com.ppfs.ppfs_libs;

import com.ppfs.ppfs_libs.service.logger.LoggerService;
import com.ppfs.ppfs_libs.service.logger.PaperLogger;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class PPFS_Libs extends JavaPlugin {
    @Getter
    private static PPFS_Libs instance;
    @Getter
    private static PaperLogger paperLogger;

    private static Metrics metrics;

    @Override
    public void onEnable() {
        LoggerService.initialize(this);
        instance = this;
        paperLogger = LoggerService.getInstance().getOrCreateLogger("PPFS_Libs", true, true);

        metrics = new Metrics(this, 24257);

    }

    @Override
    public void onDisable() {
        LoggerService.getInstance().shutdown();
    }
}
