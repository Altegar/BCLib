package org.betterx.bclib.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(WorldGenSettingsComponent.class)
public class WorldGenSettingsComponentMixin {
    @ModifyArg(method = "init", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withValues(Ljava/util/List;Ljava/util/List;)Lnet/minecraft/client/gui/components/CycleButton$Builder;"))
    public List<Holder<WorldPreset>> bcl_SortLists(List<Holder<WorldPreset>> list) {
        final Predicate<Holder<WorldPreset>> vanilla = (p -> p.unwrapKey()
                                                              .orElseThrow()
                                                              .location()
                                                              .getNamespace()
                                                              .equals("minecraft"));
        List<Holder<WorldPreset>> custom = list
                .stream()
                .filter(p -> !vanilla.test(p))
                .collect(Collectors.toCollection(LinkedList::new));
        custom.addAll(list.stream().filter(vanilla).toList());

        return custom;
    }
}
