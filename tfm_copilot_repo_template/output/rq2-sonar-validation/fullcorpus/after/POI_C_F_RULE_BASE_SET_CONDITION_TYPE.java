public class POI_C_F_RULE_BASE_SET_CONDITION_TYPE {
protected void setConditionType(byte condition_type) {
    if ((this instanceof CFRuleRecord) && !(condition_type == CONDITION_TYPE_CELL_VALUE_IS || condition_type == CONDITION_TYPE_FORMULA)) {
        throw new IllegalArgumentException("CFRuleRecord only accepts Value-Is and Formula types");
    }
    this.condition_type = condition_type;
}
}

