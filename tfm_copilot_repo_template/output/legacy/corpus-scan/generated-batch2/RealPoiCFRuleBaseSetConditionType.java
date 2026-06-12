// @caseId POI_C_F_RULE_BASE_SET_CONDITION_TYPE
// @origin poi
// @project poi
// @file CFRuleBase.java
// @method setConditionType(byte)
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 3   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiCFRuleBaseSetConditionType {
    protected void setConditionType(byte condition_type) {
        if ((this instanceof CFRuleRecord)) {
            if (!(condition_type == CONDITION_TYPE_CELL_VALUE_IS || condition_type == CONDITION_TYPE_FORMULA)) {
                throw new IllegalArgumentException("CFRuleRecord only accepts Value-Is and Formula types");
            }
        }
        this.condition_type = condition_type;
    }
}
