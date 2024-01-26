package net.iamaprogrammer.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.iamaprogrammer.ExtendedPickingClient;
import net.iamaprogrammer.compat.modmenu.screen.ConfigScreen;
import net.iamaprogrammer.config.CoreConfig;
import net.minecraft.text.Text;

import java.util.List;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<ConfigScreen<?>>) screen -> ConfigScreen.builder(screen, new CoreConfig(ExtendedPickingClient.CONFIG),
                        CoreConfig.class,
                        (config) -> ExtendedPickingClient.CONFIG = config)
                .addCyclingButtonWidget(
                        Text.translatable("extendedpicking.option.desc.usevanillareach"),
                        List.of(true, false),
                        CoreConfig::useVanillaReach,
                        CoreConfig::shouldUseVanillaReach
                )
                .addSliderWidget(
                        Text.translatable("extendedpicking.option.desc.pickblockrange"),
                        0, 256,
                        CoreConfig::setPickBlockRange,
                        CoreConfig::getPickBlockRange
                )
                .build();
    }
}
