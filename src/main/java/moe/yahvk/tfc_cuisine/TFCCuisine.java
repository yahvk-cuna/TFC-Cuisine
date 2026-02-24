package moe.yahvk.tfc_cuisine;

import com.mojang.logging.LogUtils;
import moe.yahvk.tfc_cuisine.config.Config;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TFCCuisine.MODID)
public class TFCCuisine {
    public static final String MODID = "tfc_cuisine";
    public static final Logger LOGGER = LogUtils.getLogger();

//    public TFCCuisine(FMLJavaModLoadingContext context) {
//        Config.registerConfigs(context);
//    }
    public TFCCuisine() {
        @SuppressWarnings("removal")  // Compatible with older versions of Forge
        var context = ModLoadingContext.get();
        Config.registerConfigs(context);
    }
}
