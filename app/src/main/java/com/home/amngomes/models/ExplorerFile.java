package com.home.amngomes.models;

/**
 * Created by a_m_n on 08/10/2016.
 */

public class ExplorerFile {
    public static final Boolean FILE = false;
    public static final Boolean DIRECTORY = true;
    public boolean type = false;
    public String name = "";
    public String mime = "";

    public boolean isDir(){
        return type == DIRECTORY;
    }
}
