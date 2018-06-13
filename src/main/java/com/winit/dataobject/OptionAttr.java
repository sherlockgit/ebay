package com.winit.dataobject;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yf on 2018-04-18.
 */

@Data
public class OptionAttr {

    private String key;
    private String ckey;
    public ArrayList<Map<String, String>> children;

}
