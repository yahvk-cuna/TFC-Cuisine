package moe.yahvk.tfc_cuisine.mixin;

import moe.yahvk.tfc_cuisine.HasTemperature;
import moe.yahvk.tfc_cuisine.config.CommonConfig;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(value = vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity.class, remap = false)
public interface HeatableBlockEntityMixin {
    /**
     * @author yahvk
     * @reason Injector in interface is unsupported. This allow TFC HeatBlock to heat skillet.
     */
    @Overwrite
    default boolean isHeated(Level level, BlockPos pos) {
        if (CommonConfig.heatTempture.get() >= 0) {
            var below = level.getBlockEntity(pos.below());
            if (below != null) {
                var heat = below.getCapability(HeatCapability.BLOCK_CAPABILITY).resolve();
                if (heat.isPresent()) {
                    return (heat.get().getTemperature() > CommonConfig.heatTempture.get());
                } else if (below instanceof HasTemperature temperature) {
                    return temperature.tfc_cuisine$getTemperature() > CommonConfig.heatTempture.get();
                }
            }
        }
        if (CommonConfig.disableStove.get()) {
            return false;
        }

        BlockState stateBelow = level.getBlockState(pos.below());

        if (stateBelow.is(ModTags.HEAT_SOURCES)) {
            if (stateBelow.hasProperty(BlockStateProperties.LIT))
                return stateBelow.getValue(BlockStateProperties.LIT);
            return true;
        }

        if (!this.requiresDirectHeat() && stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = level.getBlockState(pos.below(2));
            if (stateFurtherBelow.is(ModTags.HEAT_SOURCES)) {
                if (stateFurtherBelow.hasProperty(BlockStateProperties.LIT))
                    return stateFurtherBelow.getValue(BlockStateProperties.LIT);
                return true;
            }
        }

        return false;
    }

    @Shadow
    default boolean requiresDirectHeat() {
        return false;
    }
}
