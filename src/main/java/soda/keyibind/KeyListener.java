package soda.keybind;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerQuitEvent; // 导入退出事件

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyListener implements Listener {

    private final SodaKeyBind plugin;
    // 存储玩家UUID和最后一次执行的时间戳
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public KeyListener(SodaKeyBind plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        // --- 1. 冷却检查逻辑 ---
        double cooldownSeconds = plugin.getConfig().getDouble("settings.cooldown", 1.5);
        
        if (cooldownSeconds > 0) {
            UUID uuid = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long lastTime = cooldowns.getOrDefault(uuid, 0L);
            long timeDiff = currentTime - lastTime;
            long cooldownMillis = (long) (cooldownSeconds * 1000);

            if (timeDiff < cooldownMillis) {
                // 还在冷却中
                event.setCancelled(true); // 依然取消副手交换动作
                
                // 计算剩余时间并发送提示
                double waitTime = (cooldownMillis - timeDiff) / 1000.0;
                String msg = plugin.getLangManager().get("cooldown-msg")
                        .replace("%time%", String.format("%.1f", waitTime)); // 保留一位小数
                player.sendMessage(msg);
                return; // 直接结束，不执行后续指令
            }
        }

        // --- 2. 原有的按键检测逻辑 ---
        ConfigurationSection binds = plugin.getConfig().getConfigurationSection("key-binds");
        if (binds == null) return;

        boolean commandExecuted = false; // 标记是否成功执行了指令

        for (String keyId : binds.getKeys(false)) {
            ConfigurationSection bind = binds.getConfigurationSection(keyId);
            if (bind == null) continue;

            String triggerType = bind.getString("trigger", "SWAP_HAND");
            if (!triggerType.equalsIgnoreCase("SWAP_HAND")) continue;

            boolean requireShift = bind.getBoolean("require-shift", false);
            if (requireShift && !player.isSneaking()) continue;
            if (!requireShift && player.isSneaking()) continue;

            // 权限检查
            String permission = bind.getString("permission", "soda.keybind.use");
            if (!player.hasPermission(permission)) {
                player.sendMessage(plugin.getLangManager().get("no-permission"));
                event.setCancelled(true);
                return;
            }

            // --- 执行指令 ---
            event.setCancelled(true);
            String command = bind.getString("command");
            if (command != null && !command.isEmpty()) {
                player.performCommand(command);
                commandExecuted = true; // 标记成功
            }
            break; // 匹配到一个就退出循环
        }

        // --- 3. 如果成功执行了指令，更新冷却时间 ---
        if (commandExecuted && cooldownSeconds > 0) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    // --- 内存优化：玩家退出时清除数据 ---
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }
}
