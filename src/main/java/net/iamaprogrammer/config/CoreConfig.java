package net.iamaprogrammer.config;

import net.iamaprogrammer.ExtendedPickingClient;
import net.iamaprogrammer.config.core.Config;

public class CoreConfig implements Config {
    private int pickBlockRange;
    private boolean useVanillaReach;

    public CoreConfig() {}
    public CoreConfig(int renderItemModelDistance, boolean useVanillaReach) {
        this.pickBlockRange = renderItemModelDistance;
        this.useVanillaReach = useVanillaReach;
    }
    public CoreConfig(CoreConfig copy) {
        this(copy.pickBlockRange, copy.useVanillaReach);
    }

    public int getPickBlockRange() {
        return this.pickBlockRange;
    }

    public void setPickBlockRange(int pickBlockRange) {
        this.pickBlockRange = pickBlockRange;
    }
    public boolean shouldUseVanillaReach() {
        return this.useVanillaReach;
    }
    public void useVanillaReach(boolean useVanillaReach) {
        this.useVanillaReach = useVanillaReach;
    }
    @Override
    public String fileName() {
        return ExtendedPickingClient.MOD_ID;
    }
}
