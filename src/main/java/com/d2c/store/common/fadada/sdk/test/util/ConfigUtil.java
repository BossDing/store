package com.d2c.store.common.fadada.sdk.test.util;

import java.io.*;
import java.util.Properties;

public class ConfigUtil {

    static Properties applicationPro = new Properties();

    static {
        try {
            applicationPro.load(new InputStreamReader(ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAppProParam(String key) {
        return applicationPro.get(key).toString();
    }

    public static void setAppProParam(String key, String value) {
        applicationPro.setProperty(key, value);
        OutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(ConfigUtil.class.getClassLoader().getResource("config.properties").getPath()));
            fos.flush();
            applicationPro.store(fos, "写入到propertise文件");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSaasAppId() {
        return applicationPro.get("saas.app_id").toString();
    }

    public static String getSaasSecret() {
        return applicationPro.get("saas.secret").toString();
    }

    public static String getSaasV() {
        return applicationPro.get("saas.v").toString();
    }

    public static String getSaasHost() {
        return applicationPro.get("saas.host").toString();
    }

    // ===========================================Local
    public static String getLocalAppId() {
        return applicationPro.get("local.app_id").toString();
    }

    public static String getLocalSecret() {
        return applicationPro.get("local.secret").toString();
    }

    public static String getLocalV() {
        return applicationPro.get("local.v").toString();
    }

    public static String getLocalHost() {
        return applicationPro.get("local.host").toString();
    }

    // ===========================================Local
    public static String getHaierAppId() {
        return applicationPro.get("haier.app_id").toString();
    }

    public static String getHaierSecret() {
        return applicationPro.get("haier.secret").toString();
    }

    public static String getHaierV() {
        return applicationPro.get("haier.v").toString();
    }

    public static String getHaierHost() {
        return applicationPro.get("haier.host").toString();
    }

    public static String getReturnUrl() {
        return applicationPro.get("return_url").toString();
    }

    public static String getFilePath() {
        return applicationPro.get("file_path").toString();
    }

    public static String getPersonName() {
        return applicationPro.get("person_name").toString();
    }

    public static String getPersonIdentNo() {
        return applicationPro.get("person_ident_no").toString();
    }

    public static String getCompanyName() {
        return applicationPro.get("company_name").toString();
    }

    public static String getCompanyIdentNo() {
        return applicationPro.get("company_ident_no").toString();
    }

    public static String getUserMobile() {
        return applicationPro.get("user_mobile").toString();
    }

    public static String getUserEmail() {
        return applicationPro.get("user_email").toString();
    }

    public static void main(String[] args) {
        System.out.println(getFilePath());
    }

}
