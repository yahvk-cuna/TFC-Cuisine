package moe.yahvk.tfc_cuisine.config;

import moe.yahvk.tfc_cuisine.TFCCuisine;
import moe.yahvk.tfc_cuisine.math.Expression;
import moe.yahvk.tfc_cuisine.math.ExpressionParser;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TFCCuisine.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static ForgeConfigSpec.BooleanValue overrideHunger;
    public static ForgeConfigSpec.IntValue hunger;
    public static ForgeConfigSpec.ConfigValue<String> nutritionMultiplierSpec;
    public static ForgeConfigSpec.ConfigValue<String> serveSizeSpec;
    public static ForgeConfigSpec.IntValue decayModifier;
    public static ForgeConfigSpec.IntValue heatTempture;
    public static ForgeConfigSpec.BooleanValue disableStove;
    public static Expression nutritionMultiplier;
    public static Expression serveSize;

    CommonConfig(final ForgeConfigSpec.Builder builder) {
        overrideHunger = builder
                .comment("If true, all foods will share the same hunger value with respect to TFC food mechanics.")
                .define("overrideHunger", true);

        hunger = builder
                .comment("How much hunger restored per serve. By default, all TFC foods restore 4 hunger. (only valid when overrideHunger set to true)")
                .defineInRange("hunger", 4, 0, Integer.MAX_VALUE);

        nutritionMultiplierSpec = builder
                .comment("The multiplier for nutrition values of cuisine delight foods.\nSupport variables: ingredientCount, originServeSize, serveSize.\nBy default, the final nutritional value will be close to the sum of the nutritional values when there are few ingredients, and tend to be a constant value when there are many ingredients.")
                .define("nutritionMultiplier", "(-3.55 * E^(-0.3 * ($ingredientCount - 1)) + 4.5) / $ingredientCount",
                        s -> !(new ExpressionParser((String) s).hasError()));

        serveSizeSpec = builder
                .comment("The serve size for cuisine delight foods. Set to \"$originServeSize\" to use the original cuisine delight behavior.\nSupport variables: ingredientCount, originServeSize.\nBy default, the serving size is the same as that for TFC soup.")
                .define("serveSize", "floor($ingredientCount / 2) + 1",
                        s -> !(new ExpressionParser((String) s).hasError()));

        decayModifier = builder
                .comment("The decay modifier for cuisine delight foods. Higher = Faster decay. TFC soup is 3.5, salad is 4.")
                .defineInRange("decayModifier", 3, 0, Integer.MAX_VALUE);

        heatTempture = builder
                .comment("The temperature that skillet can work. Set to -1 to disable skillet work on TFC hot block.")
                .defineInRange("heatTempture", 200, -1, Integer.MAX_VALUE);

        disableStove = builder
                .comment("If true, the farmer's delight heat source (eg. stove, magma block) can not be used for cooking.")
                .define("disableStove", false);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        nutritionMultiplier = ExpressionParser.parse(nutritionMultiplierSpec.get());
        serveSize = ExpressionParser.parse(serveSizeSpec.get());
    }
}