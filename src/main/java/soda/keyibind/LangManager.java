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

    public void loadLang() {
        String langFileName = "lang_" + plugin.getConfig().getString("language", "zh") + ".yml";
        File langFile = new File(plugin.getDataFolder(), langFileName);
        if (!langFile.exists()) {
            plugin.saveResource(langFileName, false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String get(String key) {
        String msg = langConfig.getString(key, "Missing key: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
