
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Stark on 2017/2/8.
 */
public class Json{
	public static Map<String, Object> JsonObjectToMap(JSONObject jsonResult) throws JSONException {
        Map<String, Object> result = new HashMap<String, Object>();
        if(jsonResult!=null) {
            Iterator<String> keyIt = jsonResult.keys();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Object val = jsonResult.get(key);
                if(val!=null){
                    if (val instanceof JSONObject) {
                        Map<String, Object> valMap = JsonObjectToMap((JSONObject) val);
                        result.put(key, valMap);
                    }else if (val instanceof JSONArray) {
                        JSONArray ja = (JSONArray) val;
                        result.put(key, JsonArrayToList(ja));
                    }else {
                        result.put(key, val);
                    }
                }else {
                    result.put(key, null);
                }
            }
        }
        return result;
    }
    public static List<Object> JsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        if(jsonArray!=null){
            for (int i = 0; i < jsonArray.length(); i++) {
                Object val = jsonArray.get(i);
                if(val!=null){
                    if (val instanceof JSONObject) {
                        Map<String, Object> map = JsonObjectToMap((JSONObject) val);
                        list.add(map);
                    }else if(val instanceof JSONArray){
                        list.add(JsonArrayToList((JSONArray) val));
                    }else {
                        list.add(val);
                    }
                }
            }
        }
        return list;
    }
    public static int getType(Class<?> type)
    {
        if(type!=null&&(String.class.isAssignableFrom(type)||Character.class.isAssignableFrom(type)||Character.TYPE.isAssignableFrom(type)||char.class.isAssignableFrom(type)))
            return 0;
        if(type!=null&&(Byte.TYPE.isAssignableFrom(type)||Short.TYPE.isAssignableFrom(type)||Integer.TYPE.isAssignableFrom(type)||Integer.class.isAssignableFrom(type)||Number.class.isAssignableFrom(type)||int.class.isAssignableFrom(type)||byte.class.isAssignableFrom(type)||short.class.isAssignableFrom(type)))
            return 1;
        if(type!=null&&(Long.TYPE.isAssignableFrom(type)||long.class.isAssignableFrom(type)))
            return 2;
        if(type!=null&&(Float.TYPE.isAssignableFrom(type)||float.class.isAssignableFrom(type)))
            return 3;
        if(type!=null&&(Double.TYPE.isAssignableFrom(type)||double.class.isAssignableFrom(type)))
            return 4;
        if(type!=null&&(Boolean.TYPE.isAssignableFrom(type)||Boolean.class.isAssignableFrom(type)||boolean.class.isAssignableFrom(type)))
            return 5;
        if(type!=null&&type.isArray())
            return 6;
        if(type!=null&&Connection.class.isAssignableFrom(type))
            return 7;
        if(type!=null&&JSONArray.class.isAssignableFrom(type))
            return 8;
        if(type!=null&&List.class.isAssignableFrom(type))
            return 9;
        if(type!=null&&Map.class.isAssignableFrom(type))
            return 10;
        return 11;
    }
    public static Object JsonObjectToObject(JSONObject obj,Field field) throws JSONException{
        switch (getType(field.getType()))//field.getType:获取属性声明时类型对象（返回class对象）
        {
            case 0:
                return obj.opt(field.getName());
            case 1:
                return obj.optInt(field.getName());
            case 2:
                return obj.optLong(field.getName());
            case 3:
            case 4:
                return obj.optDouble(field.getName());
            case 5:
                return obj.optBoolean(field.getName());
            case 6:
            case 7:
            case 8://JsonArray型
                return obj.optJSONArray(field.getName());
            case 9:
                return JsonArrayToList(obj.optJSONArray(field.getName()));
            case 10:
                return JsonObjectToMap(obj.optJSONObject(field.getName()));
            default:
                return null;
        }
    }
    public static Object MapToObject(Object obj, Map<?, ?> map) throws IllegalAccessException{
        Field[] fields= obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(obj,map.get(field.getName()));
        }
        return obj;
    }
    public static String toJson(Object obj)throws IllegalAccessException,JSONException
    {
        JSONObject json=new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
                switch(getType(field.getType()))
                {
                    case 0:
                        json.put(field.getName(),(field.get(obj)==null?"":field.get(obj)));
                        break;
                    case 1:
                        json.put(field.getName(),(int)(field.get(obj)==null?0:field.get(obj)));
                        break;
                    case 2:
                        json.put(field.getName(),(long)(field.get(obj)==null?0:field.get(obj)));
                        break;
                    case 3:
                        json.put(field.getName(),(float)(field.get(obj)==null?0:field.get(obj)));
                        break;
                    case 4:
                         json.put(field.getName(),(double)(field.get(obj)==null?0:field.get(obj)));
                        break;
                    case 5:
                        json.put(field.getName(),(boolean)(field.get(obj)==null?false:field.get(obj)));
                        break;
                    case 6:
                    case 7:
                    case 8://JsonArray型
                        json.put(field.getName(),(field.get(obj)==null?null:field.get(obj)));
                        break;
                    case 9:
                        json.put(field.getName(),  new JSONArray((List<?>)field.get(obj)));
                        break;
                    case 10:
                        json.put(field.getName(),new  JSONObject((HashMap<?, ?>)field.get(obj)));
                        break;
                }
        }
        return json.toString();
    }
    public static <T> T fromJson(String JsonStr,Class<T> type)throws JSONException,NullPointerException,IllegalAccessException, InstantiationException{
        if(JsonStr ==null||JsonStr.equals("")) {
            throw new NullPointerException("JsonString can't be null");
        }
        T data = type.newInstance();;
        Field[] fields= type.getDeclaredFields();
        JSONObject jsonObject=(JSONObject)new JSONTokener(JsonStr).nextValue();
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(data,JsonObjectToObject(jsonObject, field));
        }
        return data;
    }
}
