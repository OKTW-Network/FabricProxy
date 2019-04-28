package one.oktw;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings("FieldCanBeLocal")
@Config(name = "FabricProxy")
public class ModConfig implements ConfigData {
    private Boolean BungeeCord = false;
    private Boolean Velocity = false;

    @Comment("Velocity proxy secret")
    private String secret = "";

    public Boolean getVelocity() {
        return Velocity;
    }

    public Boolean getBungeeCord() {
        return BungeeCord;
    }

    public String getSecret() {
        return secret;
    }
}
