package soda.keybind;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import java.io.File;

public class LangManager {
    private final SodaKeyBind plugin;
    private YamlConfiguration langConfig;

    public LangManager(SodaKeyBind plugin) {
        this.plugin = plugin;
        loadLang();
    }

    // 修改：让这个方法可以被外部调用以重载
    public void loadLang() {
        // 1. 重新获取 config.yml 中的 language 字段 (确保切换语言生效)
        String langCode = plugin.getConfig().getString("language", "zh");
        String langFileName = "lang_" + langCode + ".yml";
        
        File langFile = new File(plugin.getDataFolder(), langFileName);
        
        // 2. 如果文件不存在，从 jar 包释放
        if (!langFile.exists()) {
            try {
                plugin.saveResource(langFileName, false);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("找不到语言文件: " + langFileName);
            }
        }
        
        // 3. 加载语言配置
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        plugin.getLogger().info("已加载语言文件: " + langFileName);
    }

    public String get(String key) {
        if (langConfig == null) return key;
        String msg = langConfig.getString(key, "&cMissing key: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
