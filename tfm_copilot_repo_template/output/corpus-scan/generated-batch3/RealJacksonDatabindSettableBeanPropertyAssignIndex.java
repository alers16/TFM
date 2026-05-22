// @caseId JACKSON_DATABIND_SETTABLE_BEAN_PROPERTY_ASSIGN_INDEX
// @origin jackson-databind
// @project jackson-databind
// @file SettableBeanProperty.java
// @method assignIndex(int)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindSettableBeanPropertyAssignIndex {
    /**
     * Method used to assign index for property.
     */
    public void assignIndex(int index) {
        if (_propertyIndex != -1) {
            if (_propertyIndex != index) {
                throw new IllegalStateException("Property '" + getName() + "' already had index (" + _propertyIndex + "), trying to assign " + index);
            }
        }
        _propertyIndex = index;
    }
}
