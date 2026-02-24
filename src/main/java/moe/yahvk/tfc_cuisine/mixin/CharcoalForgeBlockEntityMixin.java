package moe.yahvk.tfc_cuisine.mixin;

import moe.yahvk.tfc_cuisine.HasTemperature;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CharcoalForgeBlockEntity.class, remap = false)
public class CharcoalForgeBlockEntityMixin implements HasTemperature {
    @Shadow private float temperature;

    @Override
    public float tfc_cuisine$getTemperature() {
        return temperature;
    }
}
