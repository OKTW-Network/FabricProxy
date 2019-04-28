package one.oktw;

import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class FabricProxy implements ModInitializer {
    public static ModConfig config;

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);

        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
