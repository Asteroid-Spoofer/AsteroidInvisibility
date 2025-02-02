package me.serbob.asteroidinvisibility;

import me.serbob.asteroidapi.behaviour.FakePlayerSpawn;
import me.serbob.asteroidapi.enums.MinecraftVersion;
import me.serbob.asteroidapi.interfaces.Version;
import me.serbob.asteroidapi.registries.FakePlayerEntity;
import me.serbob.asteroidapi.extension.ExtensionLifecycle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/*
 * Ik I keep doing getConfig().getBoolean("debug", false)
 * It's a lot better to just store a debug variable, will change soon tho...
 */
@Version(MinecraftVersion.ALL)
public class Invis extends ExtensionLifecycle implements FakePlayerSpawn {
    private final Random random = new Random();
    private static Invis instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getLogger().info("Asteroid Invisibility Extension has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Asteroid Invisibility Extension has been disabled!");
    }

    @Override
    public void onSpawnFakePlayerNMSAsync(FakePlayerEntity fakePlayerEntity, JavaPlugin plugin) {
        Player fakePlayer = fakePlayerEntity.getEntityPlayer();

        int duration = getConfig().getInt("invisibility.duration", 5);
        int secondsUntilWearsOut = getConfig().getInt("invisibility.seconds_until_wears_out", 5);
        int chance = getConfig().getInt("invisibility.chance", 50);

        if (random.nextInt(101) > chance) {
            if (getConfig().getBoolean("debug", false)) {
                getLogger().info("Player " + fakePlayer.getName() + " failed chance check for invisibility");
            }
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ((LivingEntity) fakePlayer).addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    duration == -1 ? -1 : 20 * duration,
                    0,
                    false,
                    false
            ));
            fakePlayer.setInvisible(true);

            if (getConfig().getBoolean("debug", false)) {
                getLogger().info("Applied invisibility to " + fakePlayer.getName());
            }
        });

        if (secondsUntilWearsOut != -1) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                fakePlayer.setInvisible(false);
                if (getConfig().getBoolean("debug", false)) {
                    getLogger().info("Removed invisibility from " + fakePlayer.getName());
                }
            }, 20L * secondsUntilWearsOut);
        }
    }

    @Override
    public void onSpawnFakePlayerAfterLoadAsync(FakePlayerEntity fakePlayerEntity, JavaPlugin plugin) {}
}
