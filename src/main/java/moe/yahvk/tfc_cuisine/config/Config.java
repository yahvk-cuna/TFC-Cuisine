package moe.yahvk.tfc_cuisine.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final CommonConfig common;
    private static final ForgeConfigSpec configCommonSpec;

    static {
        Pair<CommonConfig, ForgeConfigSpec> spec = new ForgeConfigSpec.Builder()
                .configure(CommonConfig::new);
        common = spec.getLeft();
        configCommonSpec = spec.getRight();
    }

    public static void registerConfigs(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, configCommonSpec);
    }
}
