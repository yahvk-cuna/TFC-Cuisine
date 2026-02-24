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
    public static ForgeConfigSpec.ConfigValue<String> nutritionPostProcessingSpec;
    public static ForgeConfigSpec.ConfigValue<String> hungerSpec;
    public static ForgeConfigSpec.ConfigValue<String> serveSizeSpec;
    public static ForgeConfigSpec.ConfigValue<String> saturationPreIngredientSpec;
    public static ForgeConfigSpec.ConfigValue<String> saturationPostProcessingSpec;
    public static ForgeConfigSpec.ConfigValue<String> waterPreIngredientSpec;
    public static ForgeConfigSpec.ConfigValue<String> waterPostProcessingSpec;
    public static ForgeConfigSpec.IntValue decayModifier;
    public static ForgeConfigSpec.IntValue heatTempture;
    public static ForgeConfigSpec.BooleanValue disableStove;
    public static Expression nutritionPostProcessing;
    public static Expression serveSize;
    public static Expression hunger;
    public static Expression saturationPreIngredient;
    public static Expression saturationPostProcessing;
    public static Expression waterPreIngredient;
    public static Expression waterPostProcessing;

    CommonConfig(final ForgeConfigSpec.Builder builder) {
        hungerSpec = builder
                .comment("""
How much hunger restored per serve. By default, all TFC foods restore 4 hunger.
IMPORTANT: You generally shouldn't modify this configuration. If you want food to make you feel fuller for longer, you should increase its saturation level. Hunger value involved in the calculation of the nutrition window; eating foods with high hunger value will result in less nutrition for the player.
Set to "$originHunger" to use the original cuisine delight behavior.

Support variables: ingredientCount, serveSize, hungerSum, originHunger.""")
                .define("hungerFormula", "4",
                        s -> !(new ExpressionParser((String) s).hasError()));

        nutritionPostProcessingSpec = builder
                .comment("""
The post-processing function that will apply to each nutrition value of Cuisine Delight foods.
By default, the final nutritional value will be close to the sum of the nutritional values when there are few ingredients, and tend to be a constant value when there are many ingredients.
Support variables: ingredientCount, serveSize, nutritionType, nutrition.""")
                .define("nutritionPostProcessing", "$nutrition * (-3.55 * E^(-0.3 * ($ingredientCount - 1)) + 4.5) / $ingredientCount",
                        s -> !(new ExpressionParser((String) s).hasError()));

        serveSizeSpec = builder
                .comment("The serve size for cuisine delight foods. Set to \"$originServeSize\" to use the original cuisine delight behavior.\nBy default, the serving size is the same as that for TFC soup.\n\nSupport variables: ingredientCount, originServeSize.")
                .define("serveSize", "floor($ingredientCount / 2) + 1",
                        s -> !(new ExpressionParser((String) s).hasError()));

        saturationPreIngredientSpec = builder
                .comment("""
Saturation level contributed by each ingredient.

Support variables:
$saturation: ingredient saturation value.
$count: quantity of this raw material.
$raw: 1 if the ingredient is raw, 0 otherwise.
$overcooked: 1 if the ingredient is overcooked, 0 otherwise.
$burnt: 1 if the ingredient is burnt, 0 otherwise.
$rawPenalty, $overcookPenalty: the corresponding penalty defined in Cuisine Delight for this ingredient.
$score: equal to "1 - $rawPenalty * $raw - $overcookPenalty * ($overcooked + $burnt - $overcooked * $burnt)\"""")
                .define("saturationPreIngredient", "$saturation * $count * $score",
                        s -> !(new ExpressionParser((String) s).hasError()));

        saturationPostProcessingSpec = builder
                .comment("""
Post-processing function for the sum of ingredient saturation.

Support variables:
$totalSaturation: sum of pre ingredient saturation.
$ingredientCount: The total quantity of all ingredients.
$originServeSize: Cuisine Delight origin serve size.
$serveSize: The final serve size calculated by serveSize config.""")
                .define("saturationPostProcessing", "2 + $totalSaturation / $serveSize",
                        s -> !(new ExpressionParser((String) s).hasError()));

        waterPreIngredientSpec = builder
                .comment("""
Water value contributed by each ingredient.""")
                .define("waterPreIngredient", "$water * $count * (1 - $overcooked)",
                        s -> !(new ExpressionParser((String) s).hasError()));

        waterPostProcessingSpec = builder
                .comment("""
Post-processing function for water.""")
                .define("waterPostProcessing", "$totalWater * (-3.55 * E^(-0.3 * ($ingredientCount - 1)) + 4.5) / $ingredientCount",
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
        nutritionPostProcessing = ExpressionParser.parse(nutritionPostProcessingSpec.get());
        serveSize = ExpressionParser.parse(serveSizeSpec.get());
        saturationPreIngredient = ExpressionParser.parse(saturationPreIngredientSpec.get());
        saturationPostProcessing = ExpressionParser.parse(saturationPostProcessingSpec.get());
        hunger = ExpressionParser.parse(hungerSpec.get());
        waterPreIngredient = ExpressionParser.parse(waterPreIngredientSpec.get());
        waterPostProcessing = ExpressionParser.parse(waterPostProcessingSpec.get());
    }
}