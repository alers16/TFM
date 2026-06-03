public class JACKSON_DATABIND_SETTABLE_BEAN_PROPERTY_ASSIGN_INDEX {
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

