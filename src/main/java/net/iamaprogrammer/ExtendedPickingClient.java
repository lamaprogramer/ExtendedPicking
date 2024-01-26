package net.iamaprogrammer;

import net.fabricmc.api.ClientModInitializer;
import net.iamaprogrammer.config.CoreConfig;
import net.iamaprogrammer.config.core.ConfigRegistry;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedPickingClient implements ClientModInitializer {
    public static String MOD_ID = "extendedpicking";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static CoreConfig CONFIG;
    @Override
    public void onInitializeClient() {
        CoreConfig defaultConfig = new CoreConfig();
        defaultConfig.setPickBlockRange((int)PlayerEntity.getReachDistance(false));
        defaultConfig.useVanillaReach(false);

        CONFIG = new ConfigRegistry<>(defaultConfig, CoreConfig.class).register();
    }
}
