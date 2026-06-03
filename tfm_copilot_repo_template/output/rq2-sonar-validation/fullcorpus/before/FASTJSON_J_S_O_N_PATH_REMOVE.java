public class FASTJSON_J_S_O_N_PATH_REMOVE {
public boolean remove(Object rootObject) {
    if (rootObject == null) {
        return false;
    }
    init();
    Object currentObject = rootObject;
    Object parentObject = null;
    Segment lastSegment = segments[segments.length - 1];
    for (int i = 0; i < segments.length; ++i) {
        if (i == segments.length - 1) {
            parentObject = currentObject;
            break;
        }
        Segment segement = segments[i];
        if (i == segments.length - 2 && lastSegment instanceof FilterSegment && segement instanceof PropertySegment) {
            FilterSegment filterSegment = (FilterSegment) lastSegment;
            if (currentObject instanceof List) {
                PropertySegment propertySegment = (PropertySegment) segement;
                List list = (List) currentObject;
                for (Iterator it = list.iterator(); it.hasNext(); ) {
                    Object item = it.next();
                    Object result = propertySegment.eval(this, rootObject, item);
                    if (result instanceof Iterable) {
                        filterSegment.remove(this, rootObject, result);
                    } else if (result instanceof Map) {
                        if (filterSegment.filter.apply(this, rootObject, currentObject, result)) {
                            it.remove();
                        }
                    }
                }
                return true;
            } else if (currentObject instanceof Map) {
                PropertySegment propertySegment = (PropertySegment) segement;
                Object result = propertySegment.eval(this, rootObject, currentObject);
                if (result == null) {
                    return false;
                }
                if (result instanceof Map && filterSegment.filter.apply(this, rootObject, currentObject, result)) {
                    propertySegment.remove(this, currentObject);
                    return true;
                }
            }
        }
        currentObject = segement.eval(this, rootObject, currentObject);
        if (currentObject == null) {
            break;
        }
    }
    if (parentObject == null) {
        return false;
    }
    if (lastSegment instanceof PropertySegment) {
        PropertySegment propertySegment = (PropertySegment) lastSegment;
        if (parentObject instanceof Collection) {
            if (segments.length > 1) {
                Segment parentSegment = segments[segments.length - 2];
                if (parentSegment instanceof RangeSegment || parentSegment instanceof MultiIndexSegment) {
                    Collection collection = (Collection) parentObject;
                    boolean removedOnce = false;
                    for (Object item : collection) {
                        boolean removed = propertySegment.remove(this, item);
                        if (removed) {
                            removedOnce = true;
                        }
                    }
                    return removedOnce;
                }
            }
        }
        return propertySegment.remove(this, parentObject);
    }
    if (lastSegment instanceof ArrayAccessSegment) {
        return ((ArrayAccessSegment) lastSegment).remove(this, parentObject);
    }
    if (lastSegment instanceof FilterSegment) {
        FilterSegment filterSegment = (FilterSegment) lastSegment;
        return filterSegment.remove(this, rootObject, parentObject);
    }
    throw new UnsupportedOperationException();
}
}

