package soda.keybind;

import org.bukkit.plugin.java.JavaPlugin;

public class SodaKeyBind extends JavaPlugin {

    private static SodaKeyBind instance;
    private LangManager langManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认资源
        saveDefaultConfig();
        saveResource("lang_zh.yml", false);
        saveResource("lang_en.yml", false);

        // 初始化语言管理器
        this.langManager = new LangManager(this);

        // 注册监听器
        getServer().getPluginManager().registerEvents(new KeyListener(this), this);

        getLogger().info("SodaKeyBind (1.21.1) 已加载 - 按键监听就绪");
    }

    public static SodaKeyBind getInstance() {
        return instance;
    }

    public LangManager getLangManager() {
        return langManager;
    }
}
