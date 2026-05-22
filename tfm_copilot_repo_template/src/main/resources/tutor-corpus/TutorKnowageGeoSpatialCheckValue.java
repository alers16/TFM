// @caseId TUTOR_KNOWAGE_GEOSPATIAL_CHECK_VALUE
// @origin tutor-paper-corpus
// @project KnowageLabs/Knowage-Server knowage-core (Saborido et al. 2022, 10 proyectos)
// @file it/eng/spagobi/tools/dataset/validation/GeoSpatialDimensionDatasetValidator.java
// @method checkValue(Set, Object)
// @license LGPL-3.0-or-later
// @sonarCCBefore 20
// @description Validador de valores geo-espaciales; patron elegible:
//              if (findString) { if (admissibleValue.equals(fieldValue)) { return true; } }
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (proyecto de evaluacion).
// @sourceCommit dfed28a869

import java.util.Set;

class TutorKnowageGeoSpatialCheckValue {

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
