package com.winit.utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by liyou
 * 2017-07-04 01:30
 */
public class JsonUtil {

    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
    /**
	 * 依据json字符串返回Map对象
	 * @param json
	 * @return
	 */
	public static Map<Object,Object> toMap(String json){
		return JsonUtil.toMap(JsonUtil.parseJson(json));
	}
	/**
	 * 将JSONObjec对象转换成Map-List集合
	 * @param json
	 * @return
	 */
	public static Map<Object, Object> toMap(JsonObject json){
	    Map<Object, Object> map = new HashMap<Object, Object>();
	    Set<Entry<String, JsonElement>> entrySet = json.entrySet();
	    for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
	    	Entry<String, JsonElement> entry = iter.next();
	    	String key = entry.getKey();
	    	Object value = entry.getValue();
	        if(value instanceof JsonArray)
	            map.put((String) key, toList((JsonArray) value));
	        else if(value instanceof JsonObject)
	            map.put((String) key, toMap((JsonObject) value));
	        else
	            map.put((String) key, value);
	    }
	    return map;
	}
	
	/**
	 * 将JSONArray对象转换成List集合
	 * @param json
	 * @return
	 */
	public static List<Object> toList(JsonArray json){
	    List<Object> list = new ArrayList<Object>();
	    for (int i=0; i<json.size(); i++){
	    	Object value = json.get(i);
	    	if(value instanceof JsonArray){
	            list.add(toList((JsonArray) value));
	    	}
	        else if(value instanceof JsonObject){
	            list.add(toMap((JsonObject) value));
	        }
	        else{
	            list.add(value);
	        }
	    }
	    return list;
	}
	/**
	 * 获取JsonObject
	 * @param json
	 * @return
	 */
	public static JsonObject parseJson(String json){
		JsonParser parser = new JsonParser();
	    JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}
}
