public class FASTJSON_J_S_O_N_OBJECT_CONTAINS_KEY {
public boolean containsKey(Object key) {
    boolean result = map.containsKey(key);
    if (!result) {
        if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
            result = map.containsKey(key.toString());
        }
    }
    return result;
}
}

