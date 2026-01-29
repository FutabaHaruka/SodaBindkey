package soda.keybind;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SodaKeyBind extends JavaPlugin {

    private static SodaKeyBind instance;
    private LangManager langManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        saveResource("lang_zh.yml", false);
        saveResource("lang_en.yml", false);

        this.langManager = new LangManager(this);

        getServer().getPluginManager().registerEvents(new KeyListener(this), this);
        
        getLogger().info("SodaKeyBind (1.21.1) 已加载");
    }

    // 新增：命令处理方法
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 判断指令是否是 sodakeybind
        if (command.getName().equalsIgnoreCase("sodakeybind")) {
            
            // 检查参数是否为 reload
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                
                // 检查权限
                if (!sender.hasPermission("soda.keybind.admin")) {
                    sender.sendMessage(langManager.get("no-permission"));
                    return true;
                }

                // --- 执行重载逻辑 ---
                reloadConfig();           // 1. 重载 config.yml
                langManager.loadLang();   // 2. 重载 语言文件
                
                sender.sendMessage(langManager.get("reload-success"));
                return true;
            }
        }
        return false;
    }

    public static SodaKeyBind getInstance() {
        return instance;
    }

    public LangManager getLangManager() {
        return langManager;
    }
}
