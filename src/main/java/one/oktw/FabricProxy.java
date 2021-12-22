package one.oktw;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class FabricProxy implements IMixinConfigPlugin {
    public static ModConfig config;
    private final Logger logger = LogManager.getLogger("FabricProxy");

    @Override
    public void onLoad(String mixinPackage) {
        if (config == null) {
            var configFile = FabricLoader.getInstance().getConfigDir().resolve("FabricProxy.toml");
            if (!Files.exists(configFile)) {
                config = new ModConfig();
                try {
                    new TomlWriter().write(config, configFile.toFile());
                } catch (IOException e) {
                    LogManager.getLogger().error("Init config failed.", e);
                }
            } else {
                config = new Toml().read(FabricLoader.getInstance().getConfigDir().resolve("FabricProxy.toml").toFile()).to(ModConfig.class);
            }
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String module = mixinClassName.split("\\.")[3];
        if (module.equals("bungee") && config.getBungeeCord()) {
            logger.info("BungeeCord support injected: {}", mixinClassName);
            return true;
        }

        if (module.equals("velocity") && config.getVelocity()) {
            if (config.getSecret().isEmpty()) {
                logger.error("Error: velocity secret is empty!");
            } else {
                logger.info("Velocity support injected: {}", mixinClassName);
                return true;
            }
        }

        return false;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
