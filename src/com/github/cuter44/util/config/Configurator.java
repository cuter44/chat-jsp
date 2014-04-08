package cn.edu.scau.tvprotal.util;

import java.util.Properties;

/**
 * 从 /config.properties 读取参数并供应给应用程序
 */
public class Configurator
{
    public static final String configPath = "/config.properties";
    private Properties prop = null;

    private static class Singleton
    {
        private static Configurator instance = new Configurator();
    }

    private Configurator()
    {
        this.load();
    }

    /** 加载配置文件
     */
    private void load()
    {
        try
        {
            this.prop = new Properties();
            this.prop.load(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(configPath)
            );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Logger.getLogger("tvprotal.confingurator")
                //.error("Read config file failed:/tvprotal.properties",ex);
        }
    }

    /** 重载配置文件
     */
    public static void reload()
    {
        Singleton.instance.load();
    }

    /**
     * 提取配置文件中的参数
     */
    public static String get(String name)
    {
        return(
            Singleton.instance.prop.getProperty(name)
        );
    }

    public static String get(String name, String defaultValue)
    {
        try
        {
            String v = Singleton.instance.prop.getProperty(name);

            if (v != null)
                return(v);
            else
                throw(new IllegalArgumentException("Missing required config key: " + name));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Logger.getLogger()
                //.error("Error on getting config:" + name, ex);
            return(defaultValue);
        }
    }


    public static Integer getInt(String name)
    {
        return(
            Integer.valueOf(
                get(name)
            )
        );
    }

    public static Integer getInt(String name, Integer defaultValue)
    {
        try
        {
            Integer v = getInt(name);

            if (v != null)
                return(v);
            else
                throw(new IllegalArgumentException("Missing required config key: " + name));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Logger.getLogger("librarica.Configurator")
                //.error("Error on getting config:" + name);
            return(defaultValue);
        }
    }

    public static Double getDouble(String name)
    {
        return(
            Double.valueOf(
                get(name)
            )
        );
    }

    public static Double getDouble(String name, Double defaultValue)
    {
        try
        {
            Double v = getDouble(name);

            if (v != null)
                return(v);
            else
                throw(new IllegalArgumentException("Missing required config key: " + name));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Logger.getLogger("librarica.Configurator")
                //.error("Error on getting config:" + name);
            return(defaultValue);
        }
    }

    public static Boolean getBoolean(String name)
    {
        return(
            Boolean.valueOf(
                get(name)
            )
        );
    }
    public static Boolean getBoolean(String name, Boolean defaultValue)
    {
        try
        {
            Boolean v = getBoolean(name);

            if (v != null)
                return(v);
            else
                throw(new IllegalArgumentException("Missing required config key: " + name));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Logger.getLogger("librarica.Configurator")
                //.error("Error on getting config:" + name);
            return(defaultValue);
        }
    }
}
