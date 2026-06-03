public class KNOWAGE_GEO_SPATIAL_DIMENSION_DATASET_VALIDATOR_CHECK_VALUE {
public boolean checkValue(Set admissibleValues, Object fieldValue) {
    boolean findString = false;
    boolean findNumber = false;
    if (fieldValue instanceof String) {
        findString = true;
    } else if (fieldValue instanceof Number) {
        findNumber = true;
    }
    for (Object admissibleValue : admissibleValues) {
        if (admissibleValue instanceof String) {
            if (findString) {
                if (admissibleValue.equals(fieldValue)) {
                    return true;
                }
            }
        } else if (admissibleValue instanceof Number) {
            if (findNumber) {
                double admissibleValueDouble = ((Number) admissibleValue).doubleValue();
                double fieldValueDouble = ((Number) fieldValue).doubleValue();
                if (admissibleValueDouble == fieldValueDouble) {
                    return true;
                }
            }
        }
    }
    return false;
}
}

