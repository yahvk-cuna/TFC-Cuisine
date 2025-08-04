package moe.yahvk.tfc_cuisine.mixin;

import dev.xkmc.cuisinedelight.content.item.BaseFoodItem;
import dev.xkmc.cuisinedelight.content.logic.CookedFoodData;
import dev.xkmc.cuisinedelight.content.logic.IngredientConfig;
import moe.yahvk.tfc_cuisine.TFCCuisine;
import moe.yahvk.tfc_cuisine.config.CommonConfig;
import net.dries007.tfc.common.capabilities.food.*;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.dries007.tfc.common.capabilities.food.FoodCapability.CAPABILITY;

@Mixin(value = dev.xkmc.cuisinedelight.content.recipe.BaseCuisineRecipe.class, remap = false)
public abstract class BaseCuisineRecipeMixin {
    @Inject(method = "findBestMatch", at = @At("RETURN"))
    private static void findBestMatch(Level level, CookedFoodData food, CallbackInfoReturnable<ItemStack> cir) {
        var stack = cir.getReturnValue();
        final IFood newFood = Helpers.getCapability(stack, CAPABILITY);
        if (newFood == null) {
            TFCCuisine.LOGGER.error("Failed to get IFood capability from stack: {}", stack);
            return;
        }

        float water = 0, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        final List<ItemStack> ingredients = new ArrayList<>();
        int ingredientCount = 0;
        boolean isRot = false;
        for (var entry : food.entries) {
            IngredientConfig.IngredientEntry config = IngredientConfig.get().getEntry(entry.stack());
            float score = 1f;
            if (config != null) {
                if (entry.raw()) {
                    score -= config.raw_penalty;
                }

                if (entry.overcooked() || entry.burnt()) {
                    score -= config.overcook_penalty;
                }
            }

            final ItemStack ingredient = entry.getEatenStack();
            final @Nullable IFood foodIngredient = FoodCapability.get(ingredient);
            if (foodIngredient != null) {
                ingredients.add(ingredient);
                if (foodIngredient.isRotten()) {
                    isRot = true;
                }

                water += entry.overcooked() ? 0 : foodIngredient.getData().water() * ingredient.getCount();
                saturation += foodIngredient.getData().saturation() * ingredient.getCount() * score;

                for (Nutrient nutrient : Nutrient.VALUES) {
                    nutrition[nutrient.ordinal()] += foodIngredient.getData().nutrient(nutrient) * ingredient.getCount() * score;
                }
                ingredientCount += ingredient.getCount();
            }
        }

        var vars = new HashMap<String, Double>();
        vars.put("originServeSize", (double) food.size);
        vars.put("ingredientCount", (double) ingredientCount);

        food.size = (int) CommonConfig.serveSize.eval(vars);
        BaseFoodItem.setData(stack, food);
        vars.put("serveSize", (double) food.size);

        if (!ingredients.isEmpty()) {
            float multiplier = (float) CommonConfig.nutritionMultiplier.eval(vars);
            TFCCuisine.LOGGER.info("Multiplier: {} ingredientCount: {} food size: {}", multiplier, ingredientCount, food.size);

            for (Nutrient nutrient : Nutrient.VALUES) {
                nutrition[nutrient.ordinal()] *= multiplier;
            }

            if (newFood instanceof FoodHandler.Dynamic handler) {
                int hunger = food.toFoodData().getNutrition();
                if (CommonConfig.overrideHunger.get()) {
                    hunger = CommonConfig.hunger.get();
                }
                handler.setFood(FoodData.create(hunger, water * multiplier, saturation * multiplier, nutrition, CommonConfig.decayModifier.get()));
                handler.setIngredients(ingredients);
                handler.setCreationDate(isRot ? Long.MIN_VALUE : FoodCapability.getRoundedCreationDate());
            }
        }
    }
}
