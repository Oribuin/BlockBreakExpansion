package xyz.oribuin.blockbreak.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.blockbreak.BlockStatisticPlugin;

import java.util.Arrays;

public class BlockExpansion extends PlaceholderExpansion {

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!player.hasPlayedBefore()) return null;

        if (params.equalsIgnoreCase("total")) {
            return String.valueOf(BlockStatisticPlugin.total(player));
        }

        String[] materialNames = params.toUpperCase().split(",");
        if (materialNames.length == 0) return null;

        return Arrays.stream(materialNames)
                .map(s -> {
                    Material material = Material.getMaterial(s);
                    if (material == null) return 0;

                    return BlockStatisticPlugin.total(player, material);
                })
                .reduce(0, Integer::sum)
                .toString();

    }

    @Override
    public @NotNull String getIdentifier() {
        return "blockbroken";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

}
