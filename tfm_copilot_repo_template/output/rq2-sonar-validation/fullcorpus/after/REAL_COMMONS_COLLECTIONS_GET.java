public class REAL_COMMONS_COLLECTIONS_GET {
Object getObject(java.util.Map<String, Object> map, String key) {
    if (map != null) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
    }
    return null;
}
}

