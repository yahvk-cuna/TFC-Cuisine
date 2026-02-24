package moe.yahvk.tfc_cuisine.mixin;

import moe.yahvk.tfc_cuisine.HasTemperature;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractFirepitBlockEntity.class, remap = false)
public class FirepitBlockEntityMixin implements HasTemperature {
    @Shadow protected float temperature;

    @Override
    public float tfc_cuisine$getTemperature() {
        return temperature;
    }
}
