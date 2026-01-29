package soda.keybind;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.configuration.ConfigurationSection;

public class KeyListener implements Listener {

    private final SodaKeyBind plugin;

    public KeyListener(SodaKeyBind plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFKey(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ConfigurationSection binds = plugin.getConfig().getConfigurationSection("key-binds");

        if (binds == null) return;

        // 遍历所有配置的绑定
        for (String keyId : binds.getKeys(false)) {
            ConfigurationSection bind = binds.getConfigurationSection(keyId);
            if (bind == null) continue;

            // 检查触发类型 (这里主要针对 SWAP_HAND 即 F键)
            String triggerType = bind.getString("trigger", "SWAP_HAND");
            if (!triggerType.equalsIgnoreCase("SWAP_HAND")) continue;

            // 检查是否需要组合键 (Shift)
            boolean requireShift = bind.getBoolean("require-shift", false);
            if (requireShift && !player.isSneaking()) continue; // 需要蹲下但没蹲
            if (!requireShift && player.isSneaking()) continue; // 不需要蹲下但蹲了 (防止冲突)

            // 权限检查
            String permission = bind.getString("permission", "soda.keybind.use");
            if (!player.hasPermission(permission)) {
                player.sendMessage(plugin.getLangManager().get("no-permission"));
                event.setCancelled(true); // 即使没权限也取消原版动作，防止误触
                return;
            }

            // 执行逻辑
            event.setCancelled(true); // 取消原版的换手动作
            String command = bind.getString("command");
            
            if (command != null && !command.isEmpty()) {
                player.performCommand(command);
                // 可选：发送消息
                // player.sendMessage(plugin.getLangManager().get("command-executed").replace("%cmd%", command));
            }
            
            // 匹配到一个后通常停止，防止一次按键触发多个指令
            break;
        }
    }
}
