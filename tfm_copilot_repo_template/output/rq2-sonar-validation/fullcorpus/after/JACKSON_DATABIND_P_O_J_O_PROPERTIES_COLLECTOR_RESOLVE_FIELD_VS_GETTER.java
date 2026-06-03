public class JACKSON_DATABIND_P_O_J_O_PROPERTIES_COLLECTOR_RESOLVE_FIELD_VS_GETTER {
/**
 *  Method that will be given a {@link List} with 2 or more accessors
 *  that may be in conflict: it will need to remove lower-priority accessors
 *  to leave just a single highest-priority accessor to use.
 *  If this succeeds method returns {@code true}, otherwise {@code false}.
 * <p>
 *  NOTE: method will directly modify given {@code List} directly, regardless
 *  of whether it ultimately succeeds or not.
 *
 *  @return True if seeming conflict was resolved and there only remains
 *     single accessor
 */
protected boolean _resolveFieldVsGetter(List<AnnotatedMember> accessors) {
    do {
        AnnotatedMember acc1 = accessors.get(0);
        AnnotatedMember acc2 = accessors.get(1);
        if (acc1 instanceof AnnotatedField) {
            if (acc2 instanceof AnnotatedMethod) {
                // Method has precedence, remove first entry
                accessors.remove(0);
                continue;
            }
        } else if (acc1 instanceof AnnotatedMethod && acc2 instanceof AnnotatedField) {
            accessors.remove(1);
            continue;
        }
        // Not a field/method pair; fail
        return false;
    } while (accessors.size() > 1);
    return true;
}
}

