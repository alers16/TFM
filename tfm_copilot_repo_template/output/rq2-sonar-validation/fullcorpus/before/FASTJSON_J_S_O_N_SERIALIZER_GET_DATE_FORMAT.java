public class FASTJSON_J_S_O_N_SERIALIZER_GET_DATE_FORMAT {
public DateFormat getDateFormat() {
    if (dateFormat == null) {
        if (dateFormatPattern != null) {
            dateFormat = this.generateDateFormat(dateFormatPattern);
        }
    }
    return dateFormat;
}
}

