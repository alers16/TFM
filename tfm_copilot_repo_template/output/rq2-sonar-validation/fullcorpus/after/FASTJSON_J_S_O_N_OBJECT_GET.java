public class FASTJSON_J_S_O_N_OBJECT_GET {
public Object get(Object key) {
    Object val = map.get(key);
    if (val == null && (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID)) {
        val = map.get(key.toString());
    }
    return val;
}
}

