package xyz.oribuin.blockbreak;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.blockbreak.hook.BlockExpansion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BlockStatisticPlugin extends JavaPlugin implements Listener {

    private static final Set<Material> BLOCKS = new HashSet<>();
    private static final Table<UUID, Material, Integer> amountBroken = HashBasedTable.create();

    static {
        BLOCKS.addAll(
                Arrays.stream(Material.values())
                        .filter(material -> material.isBlock() && !material.isAir())
                        .toList()
        );
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        new BlockExpansion().register();

        CompletableFuture.runAsync(() ->
                this.getServer().getOnlinePlayers().forEach(BlockStatisticPlugin::calculate)
        );
    }

    /**
     * Get the total amount of blocks broken by a player.
     *
     * @param user The player
     *
     * @return The total amount of blocks broken
     */
    public static int total(UUID user) {
        Map<Material, Integer> broken = amountBroken.row(user);
        if (broken.isEmpty()) return 0;

        return broken.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Get the total amount of blocks broken by a player.
     *
     * @param player The player
     *
     * @return The total amount of blocks broken
     */
    public static int total(OfflinePlayer player) {
        return total(player.getUniqueId());
    }

    /**
     * Get the amount of blocks broken by a player.
     *
     * @param player   The player
     * @param material The material
     *
     * @return The amount of blocks broken
     */
    public static int total(UUID player, Material material) {
        Map<Material, Integer> broken = amountBroken.row(player);
        if (broken.isEmpty()) return 0;

        return broken.getOrDefault(material, 0);
    }

    /**
     * Get the amount of blocks broken by a player.
     *
     * @param player   The player
     * @param material The material
     *
     * @return The amount of blocks broken
     */
    public static int total(OfflinePlayer player, Material material) {
        return total(player.getUniqueId(), material);
    }

    /**
     * Calculate the amount of blocks broken by a player.
     *
     * @param player The player
     */
    public static void calculate(OfflinePlayer player) {
        for (Material material : BLOCKS) {
            int statistic = player.getStatistic(Statistic.MINE_BLOCK, material);
            if (statistic == 0) continue;

            amountBroken.put(player.getUniqueId(), material, statistic);
        }
    }


    /**
     * Get the amount of blocks broken by a player.
     *
     * @param event The event
     */
    @EventHandler
    public void onStat(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic() != Statistic.MINE_BLOCK) return;
        if (event.getNewValue() == 0) return;
        if (!BLOCKS.contains(event.getMaterial())) return;

        amountBroken.put(
                event.getPlayer().getUniqueId(),
                event.getMaterial(),
                event.getNewValue()
        );
    }


    /**
     * Calculate the amount of blocks broken by a player when they login.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        CompletableFuture.runAsync(() -> calculate(player));
    }

    /**
     * Remove the player's data when they leave.
     */
    @EventHandler
    public void onLeave(PlayerLoginEvent event) {
        amountBroken.rowKeySet().remove(event.getPlayer().getUniqueId());
    }


}
