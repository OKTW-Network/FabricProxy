package one.oktw.mixin;

import net.minecraft.util.UserCache;
import one.oktw.FabricProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UserCache.class)
public class UserCacheMixin {
    @Shadow private static boolean useRemote;

    @Redirect(method = "findProfileByName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/UserCache;shouldUseRemote()Z"))
    private static boolean findProfileByName() {
        if (FabricProxy.config.getBungeeCord() || FabricProxy.config.getVelocity()) {
            return true;
        }
        return useRemote;
    }
}
