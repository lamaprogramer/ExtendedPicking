package net.iamaprogrammer.config;

import net.iamaprogrammer.ExtendedPickingClient;
import net.iamaprogrammer.config.core.Config;

public class CoreConfig implements Config {
    private int pickBlockRange;

    public CoreConfig() {}
    public CoreConfig(int renderItemModelDistance) {
        this.pickBlockRange = renderItemModelDistance;
    }
    public CoreConfig(CoreConfig copy) {
        this(copy.pickBlockRange);
    }

    public int getPickBlockRange() {
        return this.pickBlockRange;
    }

    public void setPickBlockRange(int pickBlockRange) {
        this.pickBlockRange = pickBlockRange;
    }
    @Override
    public String fileName() {
        return ExtendedPickingClient.MOD_ID;
    }
}
