public class FASTJSON_DEFAULT_J_S_O_N_PARSER_PARSE_ARRAY {
public <T> List<T> parseArray(Class<T> clazz) {
    List<T> array = new ArrayList<T>();
    parseArray(clazz, array);
    return array;
}
}

