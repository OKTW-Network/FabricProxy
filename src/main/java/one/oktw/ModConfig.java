package one.oktw;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Config(name = "FabricProxy")
public class ModConfig implements ConfigData {
    private Boolean BungeeCord = false;
    private Boolean Velocity = false;
    private Boolean allowBypassProxy = false;

    @Comment("Velocity proxy secret")
    private String secret = "";

    public Boolean getVelocity() {
        String env = System.getenv("FABRIC_PROXY_VELOCITY");
        if (env == null) {
            return Velocity;
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    public Boolean getBungeeCord() {
        String env = System.getenv("FABRIC_PROXY_BUNGEECORD");
        if (env == null) {
            return BungeeCord;
        } else {
            return env.equalsIgnoreCase("true");
        }
    }

    public String getSecret() {
        String env = System.getenv("FABRIC_PROXY_SECRET");
        if (env == null) {
            return secret;
        } else {
            return env;
        }
    }

    public Boolean getAllowBypassProxy() {
        String env = System.getenv("FABRIC_PROXY_ALLOW_BYPASS_PROXY");
        if (env == null) {
            return allowBypassProxy;
        } else {
            return Boolean.valueOf(env);
        }
    }
}
