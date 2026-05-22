// @caseId KNOWAGE_GEO_SPATIAL_DIMENSION_DATASET_VALIDATOR_CHECK_VALUE
// @origin knowage
// @project knowage
// @file GeoSpatialDimensionDatasetValidator.java
// @method checkValue(Set, Object)
// @license [PENDIENTE]
// @sonarCCBefore 20
// @sonarCCAfter 17   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class KnowageGeoSpatialDimensionDatasetValidatorCheckValue {
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
