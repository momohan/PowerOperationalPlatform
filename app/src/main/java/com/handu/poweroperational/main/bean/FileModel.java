package com.handu.poweroperational.main.bean;

import java.io.Serializable;

/**
 * 柳梦
 * 文件模板
 */

public class FileModel implements Serializable {
    private static final long serialVersionUID = 2072893447591548402L;

    private String name;
    private String url;
    private String iconUrl;
    private boolean isInstall;


    public FileModel() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public FileModel(String name, String url, String iconUrl) {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
