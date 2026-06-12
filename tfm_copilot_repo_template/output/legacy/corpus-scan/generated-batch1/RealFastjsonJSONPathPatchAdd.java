// @caseId FASTJSON_J_S_O_N_PATH_PATCH_ADD
// @origin fastjson
// @project fastjson
// @file JSONPath.java
// @method patchAdd(Object, Object, boolean)
// @license [PENDIENTE]
// @sonarCCBefore 19
// @sonarCCAfter 16   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJSONPathPatchAdd {
    public void patchAdd(Object rootObject, Object value, boolean replace) {
        if (rootObject == null) {
            return;
        }
        init();
        Object currentObject = rootObject;
        Object parentObject = null;
        for (int i = 0; i < segments.length; ++i) {
            parentObject = currentObject;
            Segment segment = segments[i];
            currentObject = segment.eval(this, rootObject, currentObject);
            if (currentObject == null && i != segments.length - 1) {
                if (segment instanceof PropertySegment) {
                    currentObject = new JSONObject();
                    ((PropertySegment) segment).setValue(this, parentObject, currentObject);
                }
            }
        }
        Object result = currentObject;
        if ((!replace) && result instanceof Collection) {
            Collection collection = (Collection) result;
            collection.add(value);
            return;
        }
        Object newResult;
        if (result != null && !replace) {
            Class<?> resultClass = result.getClass();
            if (resultClass.isArray()) {
                int length = Array.getLength(result);
                Object descArray = Array.newInstance(resultClass.getComponentType(), length + 1);
                System.arraycopy(result, 0, descArray, 0, length);
                Array.set(descArray, length, value);
                newResult = descArray;
            } else if (Map.class.isAssignableFrom(resultClass)) {
                newResult = value;
            } else {
                throw new JSONException("unsupported array put operation. " + resultClass);
            }
        } else {
            newResult = value;
        }
        Segment lastSegment = segments[segments.length - 1];
        if (lastSegment instanceof PropertySegment) {
            PropertySegment propertySegment = (PropertySegment) lastSegment;
            propertySegment.setValue(this, parentObject, newResult);
            return;
        }
        if (lastSegment instanceof ArrayAccessSegment) {
            ((ArrayAccessSegment) lastSegment).setValue(this, parentObject, newResult);
            return;
        }
        throw new UnsupportedOperationException();
    }
}
