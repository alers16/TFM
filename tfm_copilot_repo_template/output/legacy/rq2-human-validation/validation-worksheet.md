# Hoja de trabajo - Validacion humana (30 casos, seed 42)

Para cada caso: comprueba el veredicto automatico y marca en validation-sample.csv
(deteccion_ok / precondiciones_ok / cc_before_ok / cc_after_ok = true/false).

## 1. KNOWAGE_BUSINESS_MODEL_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET
- metodo: Response getDriversFromQbeDataSet(String, Map<String, Object>, String)
- AUTO: elegible=True  CC before=205  after=196  delta=-9

### before
```java
private Response getDriversFromQbeDataSet(final String role, final Map<String, Object> resultAsMap, String businessModelName) {
    final List<HashMap<String, Object>> parametersArrayList = (List<HashMap<String, Object>>) resultAsMap.get("filterStatus");
    final List<BusinessModelDriverRuntime> parameters = new ArrayList<>();
    IMetaModelsDAO dao = DAOFactory.getMetaModelsDAO();
    IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();
    ParameterUse parameterUse;
    MetaModel businessModel = dao.loadMetaModelForExecutionByNameAndRole(businessModelName, role, false);
    BusinessModelOpenParameters bmop = new BusinessModelOpenParameters();
    try {
        // role = this.getUserProfile().getRoles().iterator().next().toString();
        Locale locale = request.getLocale();
        BusinessModelRuntime dum = new BusinessModelRuntime(this.getUserProfile(), locale);
        parameters.addAll(BusinessModelOpenUtils.getParameters(businessModel, role, request.getLocale(), null, true, dum));
    } catch (SpagoBIRestServiceException e) {
        LOGGER.debug(e.getCause(), e);
        throw new SpagoBIRuntimeException(e.getMessage(), e);
    }
    for (BusinessModelDriverRuntime objParameter : parameters) {
        final Map<String, Object> metadata = new LinkedHashMap<>();
        BiMap<String, String> colPlaceholder2ColName = objParameter.getColPlaceholder2ColName();
        String lovDescriptionColumnName = objParameter.getLovDescriptionColumnName();
        String lovValueColumnName = objParameter.getLovValueColumnName();
        Integer paruseId = objParameter.getParameterUseId();
        try {
            parameterUse = parameterUseDAO.loadByUseID(paruseId);
        } catch (EMFUserError e1) {
            LOGGER.debug("Error loading parameter use with id " + paruseId, e1);
            throw new SpagoBIRuntimeException(e1.getMessage(), e1);
        }
        HashMap<String, Object> parameterAsMap = new HashMap<>();
        parameterAsMap.put("id", objParameter.getBiObjectId());
        parameterAsMap.put("label", objParameter.getLabel());
        parameterAsMap.put("urlName", objParameter.getId());
        parameterAsMap.put("type", objParameter.getParType());
        parameterAsMap.put("selectionType", objParameter.getSelectionType());
        parameterAsMap.put("valueSelection", parameterUse.getValueSelection());
        parameterAsMap.put("visible", objParameter.isVisible());
        parameterAsMap.put("mandatory", objParameter.isMandatory());
        parameterAsMap.put("multivalue", objParameter.isMultivalue());
        parameterAsMap.put("driverLabel", objParameter.getPar().getLabel());
        parameterAsMap.put("driverUseLabel", objParameter.getAnalyticalDriverExecModality().getLabel());
        parameterAsMap.put(PROPERTY_METADATA, metadata);
        parameterAsMap.put("allowInternalNodeSelection", objParameter.getPar().getModalityValue().getLovProvider().contains("<LOVTYPE>treeinner</LOVTYPE>"));
        // get values
        if (objParameter.getDriver().getParameterValues() != null) {
            List paramValueLst = new ArrayList();
            List paramDescrLst = new ArrayList();
            Object paramValues = objParameter.getDriver().getParameterValues();
            Object paramDescriptionValues = objParameter.getDriver().getParameterValuesDescription();
            Encoder encoder = OwaspDefaultEncoderFactory.getInstance().getEncoder();
            if (paramValues instanceof List) {
                List<String> valuesList = (List) paramValues;
                List<String> descriptionList = (List) paramDescriptionValues;
                if (paramDescriptionValues == null || !(paramDescriptionValues instanceof List)) {
                    descriptionList = new ArrayList<>();
                }
                // String item = null;
                for (int k = 0; k < valuesList.size(); k++) {
                    String itemVal = valuesList.get(k);
                    String itemDescr = descriptionList.size() > k && descriptionList.get(k) != null ? descriptionList.get(k) : itemVal;
                    try {
                        // % character breaks decode method
                        if (!itemVal.contains("%")) {
                            itemVal = encoder.decodeFromURL(itemVal);
                        }
                        if (!itemDescr.contains("%")) {
                            itemDescr = encoder.decodeFromURL(itemDescr);
                        }
                        // check input value and convert if it's an old multivalue syntax({;{xxx;yyy}STRING}) to list of values :["A-OMP", "A-PO", "CL"]
                        if (objParameter.isMultivalue() && itemVal.indexOf("{") >= 0) {
                            String sep = itemVal.substring(1, 2);
                            String val = itemVal.substring(3, itemVal.indexOf("}"));
                            String[] valLst = val.split(sep);
                            for (int k2 = 0; k2 < valLst.length; k2++) {
                                String itemVal2 = valLst[k2];
                                if (itemVal2 != null && !"".equals(itemVal2)) {
                                    paramValueLst.add(itemVal2);
                                }
                            }
                        } else {
                            if (itemVal != null && !"".equals(itemVal)) {
                                paramValueLst.add(itemVal);
                            }
                            paramDescrLst.add(itemDescr);
                        }
                    } catch (EncodingException e) {
                        LOGGER.debug("An error occured while decoding parameter with value[" + itemVal + "]" + e);
                    }
                }
            } else if (paramValues instanceof String) {
                // % character breaks decode method
                if (!((String) paramValues).contains("%")) {
                    try {
                        paramValues = encoder.decodeFromURL((String) paramValues);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramValueLst.add(paramValues.toString());
                String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                if (!parDescrVal.contains("%")) {
                    try {
                        parDescrVal = encoder.decodeFromURL(parDescrVal);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramDescrLst.add(parDescrVal);
            }
            parameterAsMap.put("parameterValue", paramValueLst);
            parameterAsMap.put("parameterDescription", paramDescriptionValues);
        }
        boolean showParameterLov = true;
        // Parameters NO TREE
        if ("lov".equalsIgnoreCase(parameterUse.getValueSelection()) && !objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_TREE)) {
            ArrayList<HashMap<String, Object>> admissibleValues = objParameter.getAdmissibleValues();
            metadata.put("colsMap", colPlaceholder2ColName);
            metadata.put("descriptionColumn", lovDescriptionColumnName);
            metadata.put("invisibleColumns", objParameter.getLovInvisibleColumnsNames());
            metadata.put("valueColumn", lovValueColumnName);
            metadata.put("visibleColumns", objParameter.getLovVisibleColumnsNames());
            if (!objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_LOOKUP)) {
                parameterAsMap.put(PROPERTY_DATA, admissibleValues);
            } else {
                parameterAsMap.put(PROPERTY_DATA, new ArrayList<>());
            }
            // hide the parameter if is mandatory and have one value in lov (no error parameter)
            if (admissibleValues != null && admissibleValues.size() == 1 && objParameter.isMandatory() && !admissibleValues.get(0).containsKey("error") && (objParameter.getDataDependencies() == null || objParameter.getDataDependencies().isEmpty()) && (objParameter.getLovDependencies() == null || objParameter.getLovDependencies().isEmpty())) {
                showParameterLov = false;
            }
            // if parameterValue is not null and is array, check if all element are present in lov
            Object values = parameterAsMap.get("parameterValue");
            if (values != null && admissibleValues != null) {
                bmop.checkIfValuesAreAdmissible(values, admissibleValues);
            }
        }
        // DATE RANGE DEFAULT VALUE
        if (objParameter.getParType().equals("DATE_RANGE")) {
            try {
                ArrayList<HashMap<String, Object>> defaultValues = bmop.manageDataRange(businessModel, role, objParameter.getId());
                parameterAsMap.put("defaultValues", defaultValues);
            } catch (SerializationException | EMFUserError | JSONException | IOException e) {
                LOGGER.debug("Filters DATE RANGE ERRORS ", e);
            }
        }
        // convert the parameterValue from array of string in array of object
        DefaultValuesList parameterValueList = new DefaultValuesList();
        Object oVals = parameterAsMap.get("parameterValue");
        Object oDescr = parameterAsMap.get("parameterDescription") != null ? parameterAsMap.get("parameterDescription") : new ArrayList<String>();
        if (oVals != null) {
            if (oVals instanceof List) {
                // CROSS NAV : INPUT PARAM PARAMETER TARGET DOC IS STRING
                if (oVals.toString().startsWith("[") && oVals.toString().endsWith("]") && parameterUse.getValueSelection().equals("man_in")) {
                    List<String> valList = (ArrayList) oVals;
                    String stringResult = "";
                    for (int k = 0; k < valList.size(); k++) {
                        String itemVal = valList.get(k);
                        if (objParameter.getParType().equals("STRING") && objParameter.isMultivalue()) {
                            stringResult += "'" + itemVal + "'";
                        } else {
                            stringResult += itemVal;
                        }
                        if (k != valList.size() - 1) {
                            stringResult += ",";
                        }
                    }
                    LovValue defValue = new LovValue();
                    defValue.setValue(stringResult);
                    defValue.setDescription(stringResult);
                    parameterValueList.add(defValue);
                } else {
                    List<String> valList = (ArrayList) oVals;
                    List<String> descrList = (ArrayList) oDescr;
                    for (int k = 0; k < valList.size(); k++) {
                        String itemVal = valList.get(k);
                        String itemDescr = descrList.size() > k ? descrList.get(k) : itemVal;
                        LovValue defValue = new LovValue();
                        defValue.setValue(itemVal);
                        defValue.setDescription(itemDescr != null ? itemDescr : itemVal);
                        parameterValueList.add(defValue);
                    }
                }
                parameterAsMap.put("parameterValue", parameterValueList);
            }
        }
        addDependencies(objParameter, parameterAsMap);
        // load DEFAULT VALUE if present and if the parameter value is empty
        Object defValue = null;
        if (objParameter.getDefaultValues() != null && !objParameter.getDefaultValues().isEmpty() && objParameter.getDefaultValues().get(0).getValue() != null) {
            DefaultValuesList valueList = null;
            // check if the parameter is really valorized (for example if it isn't an empty list)
            List lstValues = (List) parameterAsMap.get("parameterValue");
            // if (lstValues.size() == 0)
            // jsonCrossParameters.remove(objParameter.getId());
            String parLab = objParameter.getDriver() != null && objParameter.getDriver().getParameter() != null ? objParameter.getDriver().getParameter().getLabel() : "";
            String useModLab = objParameter.getAnalyticalDriverExecModality() != null ? objParameter.getAnalyticalDriverExecModality().getLabel() : "";
            String sessionKey = parLab + "_" + useModLab;
            valueList = objParameter.getDefaultValues();
            if (!valueList.isEmpty()) {
                defValue = valueList.stream().map(e -> {
                    BiMap<String, String> inverse = colPlaceholder2ColName.inverse();
                    String valColName = inverse.get(lovValueColumnName);
                    String descColName = inverse.get(lovDescriptionColumnName);
                    // TODO : workaround
                    valColName = Optional.ofNullable(valColName).orElse("value");
                    descColName = Optional.ofNullable(descColName).orElse("desc");
                    Map<String, Object> ret = new LinkedHashMap<>();
                    ret.put(valColName, e.getValue());
                    if (!valColName.equals(descColName)) {
                        ret.put(descColName, e.getDescription());
                    }
                    return ret;
                }).collect(Collectors.toList());
            }
            // if (jsonCrossParameters.isNull(objParameter.getId())
            // // && !sessionParametersMap.containsKey(objParameter.getId())) {
            // && !sessionParametersMap.containsKey(sessionKey)) {
            // if (valueList != null) {
            // parameterAsMap.put("parameterValue", valueList);
            // }
            // }
            // in every case fill default values!
            parameterAsMap.put("driverDefaultValue", valueList);
        }
        if (!showParameterLov) {
            parameterAsMap.put("showOnPanel", "false");
        } else {
            parameterAsMap.put("showOnPanel", "true");
        }
        parametersArrayList.add(parameterAsMap);
    }
    for (int z = 0; z < parametersArrayList.size(); z++) {
        Map docP = parametersArrayList.get(z);
        DefaultValuesList defvalList = (DefaultValuesList) docP.get("parameterValue");
        if (defvalList != null && defvalList.size() == 1) {
            LovValue defval = defvalList.get(0);
            if (defval != null) {
                Object val = defval.getValue();
                if (val != null && val.equals("$")) {
                    docP.put("parameterValue", "");
                }
            }
        }
    }
    for (int i = 0; i < parametersArrayList.size(); i++) {
        Map<String, Object> parameter = parametersArrayList.get(i);
        List<Map<String, Object>> defaultValuesList = (List<Map<String, Object>>) parameter.get(PROPERTY_DATA);
        parameter.remove("parameterValue");
        if (defaultValuesList != null) {
            // Filter out null values
            defaultValuesList.removeIf(e -> e.get("value") == JSONObject.NULL || e.get("description") == JSONObject.NULL);
            // Fix JSON structure of admissible values
            defaultValuesList.forEach(e -> {
                List<String> fieldsToBeRemoved = new ArrayList<>();
                e.keySet().forEach(f -> {
                    if (!f.startsWith("_col")) {
                        fieldsToBeRemoved.add(f);
                    }
                });
                fieldsToBeRemoved.forEach(e::remove);
            });
        }
    }
    resultAsMap.put("filterStatus", parametersArrayList);
    resultAsMap.put("isReadyForExecution", bmop.isReadyForExecution(parameters));
    LOGGER.debug("OUT");
    return Response.ok(resultAsMap).build();
}
```
### after
```java
private Response getDriversFromQbeDataSet(final String role, final Map<String, Object> resultAsMap, String businessModelName) {
    final List<HashMap<String, Object>> parametersArrayList = (List<HashMap<String, Object>>) resultAsMap.get("filterStatus");
    final List<BusinessModelDriverRuntime> parameters = new ArrayList<>();
    IMetaModelsDAO dao = DAOFactory.getMetaModelsDAO();
    IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();
    ParameterUse parameterUse;
    MetaModel businessModel = dao.loadMetaModelForExecutionByNameAndRole(businessModelName, role, false);
    BusinessModelOpenParameters bmop = new BusinessModelOpenParameters();
    try {
        // role = this.getUserProfile().getRoles().iterator().next().toString();
        Locale locale = request.getLocale();
        BusinessModelRuntime dum = new BusinessModelRuntime(this.getUserProfile(), locale);
        parameters.addAll(BusinessModelOpenUtils.getParameters(businessModel, role, request.getLocale(), null, true, dum));
    } catch (SpagoBIRestServiceException e) {
        LOGGER.debug(e.getCause(), e);
        throw new SpagoBIRuntimeException(e.getMessage(), e);
    }
    for (BusinessModelDriverRuntime objParameter : parameters) {
        final Map<String, Object> metadata = new LinkedHashMap<>();
        BiMap<String, String> colPlaceholder2ColName = objParameter.getColPlaceholder2ColName();
        String lovDescriptionColumnName = objParameter.getLovDescriptionColumnName();
        String lovValueColumnName = objParameter.getLovValueColumnName();
        Integer paruseId = objParameter.getParameterUseId();
        try {
            parameterUse = parameterUseDAO.loadByUseID(paruseId);
        } catch (EMFUserError e1) {
            LOGGER.debug("Error loading parameter use with id " + paruseId, e1);
            throw new SpagoBIRuntimeException(e1.getMessage(), e1);
        }
        HashMap<String, Object> parameterAsMap = new HashMap<>();
        parameterAsMap.put("id", objParameter.getBiObjectId());
        parameterAsMap.put("label", objParameter.getLabel());
        parameterAsMap.put("urlName", objParameter.getId());
        parameterAsMap.put("type", objParameter.getParType());
        parameterAsMap.put("selectionType", objParameter.getSelectionType());
        parameterAsMap.put("valueSelection", parameterUse.getValueSelection());
        parameterAsMap.put("visible", objParameter.isVisible());
        parameterAsMap.put("mandatory", objParameter.isMandatory());
        parameterAsMap.put("multivalue", objParameter.isMultivalue());
        parameterAsMap.put("driverLabel", objParameter.getPar().getLabel());
        parameterAsMap.put("driverUseLabel", objParameter.getAnalyticalDriverExecModality().getLabel());
        parameterAsMap.put(PROPERTY_METADATA, metadata);
        parameterAsMap.put("allowInternalNodeSelection", objParameter.getPar().getModalityValue().getLovProvider().contains("<LOVTYPE>treeinner</LOVTYPE>"));
        // get values
        if (objParameter.getDriver().getParameterValues() != null) {
            List paramValueLst = new ArrayList();
            List paramDescrLst = new ArrayList();
            Object paramValues = objParameter.getDriver().getParameterValues();
            Object paramDescriptionValues = objParameter.getDriver().getParameterValuesDescription();
            Encoder encoder = OwaspDefaultEncoderFactory.getInstance().getEncoder();
            if (paramValues instanceof List) {
                List<String> valuesList = (List) paramValues;
                List<String> descriptionList = (List) paramDescriptionValues;
                if (paramDescriptionValues == null || !(paramDescriptionValues instanceof List)) {
                    descriptionList = new ArrayList<>();
                }
                // String item = null;
                for (int k = 0; k < valuesList.size(); k++) {
                    String itemVal = valuesList.get(k);
                    String itemDescr = descriptionList.size() > k && descriptionList.get(k) != null ? descriptionList.get(k) : itemVal;
                    try {
                        // % character breaks decode method
                        if (!itemVal.contains("%")) {
                            itemVal = encoder.decodeFromURL(itemVal);
                        }
                        if (!itemDescr.contains("%")) {
                            itemDescr = encoder.decodeFromURL(itemDescr);
                        }
                        // check input value and convert if it's an old multivalue syntax({;{xxx;yyy}STRING}) to list of values :["A-OMP", "A-PO", "CL"]
                        if (objParameter.isMultivalue() && itemVal.indexOf("{") >= 0) {
                            String sep = itemVal.substring(1, 2);
                            String val = itemVal.substring(3, itemVal.indexOf("}"));
                            String[] valLst = val.split(sep);
                            for (int k2 = 0; k2 < valLst.length; k2++) {
                                String itemVal2 = valLst[k2];
                                if (itemVal2 != null && !"".equals(itemVal2)) {
                                    paramValueLst.add(itemVal2);
                                }
                            }
                        } else {
                            if (itemVal != null && !"".equals(itemVal)) {
                                paramValueLst.add(itemVal);
                            }
                            paramDescrLst.add(itemDescr);
                        }
                    } catch (EncodingException e) {
                        LOGGER.debug("An error occured while decoding parameter with value[" + itemVal + "]" + e);
                    }
                }
            } else if (paramValues instanceof String) {
                // % character breaks decode method
                if (!((String) paramValues).contains("%")) {
                    try {
                        paramValues = encoder.decodeFromURL((String) paramValues);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramValueLst.add(paramValues.toString());
                String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                if (!parDescrVal.contains("%")) {
                    try {
                        parDescrVal = encoder.decodeFromURL(parDescrVal);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramDescrLst.add(parDescrVal);
            }
            parameterAsMap.put("parameterValue", paramValueLst);
            parameterAsMap.put("parameterDescription", paramDescriptionValues);
        }
        boolean showParameterLov = true;
        // Parameters NO TREE
        if ("lov".equalsIgnoreCase(parameterUse.getValueSelection()) && !objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_TREE)) {
            ArrayList<HashMap<String, Object>> admissibleValues = objParameter.getAdmissibleValues();
            metadata.put("colsMap", colPlaceholder2ColName);
            metadata.put("descriptionColumn", lovDescriptionColumnName);
            metadata.put("invisibleColumns", objParameter.getLovInvisibleColumnsNames());
            metadata.put("valueColumn", lovValueColumnName);
            metadata.put("visibleColumns", objParameter.getLovVisibleColumnsNames());
            if (!objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_LOOKUP)) {
                parameterAsMap.put(PROPERTY_DATA, admissibleValues);
            } else {
                parameterAsMap.put(PROPERTY_DATA, new ArrayList<>());
            }
            // hide the parameter if is mandatory and have one value in lov (no error parameter)
            if (admissibleValues != null && admissibleValues.size() == 1 && objParameter.isMandatory() && !admissibleValues.get(0).containsKey("error") && (objParameter.getDataDependencies() == null || objParameter.getDataDependencies().isEmpty()) && (objParameter.getLovDependencies() == null || objParameter.getLovDependencies().isEmpty())) {
                showParameterLov = false;
            }
            // if parameterValue is not null and is array, check if all element are present in lov
            Object values = parameterAsMap.get("parameterValue");
            if (values != null && admissibleValues != null) {
                bmop.checkIfValuesAreAdmissible(values, admissibleValues);
            }
        }
        // DATE RANGE DEFAULT VALUE
        if (objParameter.getParType().equals("DATE_RANGE")) {
            try {
                ArrayList<HashMap<String, Object>> defaultValues = bmop.manageDataRange(businessModel, role, objParameter.getId());
                parameterAsMap.put("defaultValues", defaultValues);
            } catch (SerializationException | EMFUserError | JSONException | IOException e) {
                LOGGER.debug("Filters DATE RANGE ERRORS ", e);
            }
        }
        // convert the parameterValue from array of string in array of object
        DefaultValuesList parameterValueList = new DefaultValuesList();
        Object oVals = parameterAsMap.get("parameterValue");
        Object oDescr = parameterAsMap.get("parameterDescription") != null ? parameterAsMap.get("parameterDescription") : new ArrayList<String>();
        if (oVals != null && oVals instanceof List) {
            // CROSS NAV : INPUT PARAM PARAMETER TARGET DOC IS STRING
            if (oVals.toString().startsWith("[") && oVals.toString().endsWith("]") && parameterUse.getValueSelection().equals("man_in")) {
                List<String> valList = (ArrayList) oVals;
                String stringResult = "";
                for (int k = 0; k < valList.size(); k++) {
                    String itemVal = valList.get(k);
                    if (objParameter.getParType().equals("STRING") && objParameter.isMultivalue()) {
                        stringResult += "'" + itemVal + "'";
                    } else {
                        stringResult += itemVal;
                    }
                    if (k != valList.size() - 1) {
                        stringResult += ",";
                    }
                }
                LovValue defValue = new LovValue();
                defValue.setValue(stringResult);
                defValue.setDescription(stringResult);
                parameterValueList.add(defValue);
            } else {
                List<String> valList = (ArrayList) oVals;
                List<String> descrList = (ArrayList) oDescr;
                for (int k = 0; k < valList.size(); k++) {
                    String itemVal = valList.get(k);
                    String itemDescr = descrList.size() > k ? descrList.get(k) : itemVal;
                    LovValue defValue = new LovValue();
                    defValue.setValue(itemVal);
                    defValue.setDescription(itemDescr != null ? itemDescr : itemVal);
                    parameterValueList.add(defValue);
                }
            }
            parameterAsMap.put("parameterValue", parameterValueList);
        }
        addDependencies(objParameter, parameterAsMap);
        // load DEFAULT VALUE if present and if the parameter value is empty
        Object defValue = null;
        if (objParameter.getDefaultValues() != null && !objParameter.getDefaultValues().isEmpty() && objParameter.getDefaultValues().get(0).getValue() != null) {
            DefaultValuesList valueList = null;
            // check if the parameter is really valorized (for example if it isn't an empty list)
            List lstValues = (List) parameterAsMap.get("parameterValue");
            // if (lstValues.size() == 0)
            // jsonCrossParameters.remove(objParameter.getId());
            String parLab = objParameter.getDriver() != null && objParameter.getDriver().getParameter() != null ? objParameter.getDriver().getParameter().getLabel() : "";
            String useModLab = objParameter.getAnalyticalDriverExecModality() != null ? objParameter.getAnalyticalDriverExecModality().getLabel() : "";
            String sessionKey = parLab + "_" + useModLab;
            valueList = objParameter.getDefaultValues();
            if (!valueList.isEmpty()) {
                defValue = valueList.stream().map(e -> {
                    BiMap<String, String> inverse = colPlaceholder2ColName.inverse();
                    String valColName = inverse.get(lovValueColumnName);
                    String descColName = inverse.get(lovDescriptionColumnName);
                    // TODO : workaround
                    valColName = Optional.ofNullable(valColName).orElse("value");
                    descColName = Optional.ofNullable(descColName).orElse("desc");
                    Map<String, Object> ret = new LinkedHashMap<>();
                    ret.put(valColName, e.getValue());
                    if (!valColName.equals(descColName)) {
                        ret.put(descColName, e.getDescription());
                    }
                    return ret;
                }).collect(Collectors.toList());
            }
            // if (jsonCrossParameters.isNull(objParameter.getId())
            // // && !sessionParametersMap.containsKey(objParameter.getId())) {
            // && !sessionParametersMap.containsKey(sessionKey)) {
            // if (valueList != null) {
            // parameterAsMap.put("parameterValue", valueList);
            // }
            // }
            // in every case fill default values!
            parameterAsMap.put("driverDefaultValue", valueList);
        }
        if (!showParameterLov) {
            parameterAsMap.put("showOnPanel", "false");
        } else {
            parameterAsMap.put("showOnPanel", "true");
        }
        parametersArrayList.add(parameterAsMap);
    }
    for (int z = 0; z < parametersArrayList.size(); z++) {
        Map docP = parametersArrayList.get(z);
        DefaultValuesList defvalList = (DefaultValuesList) docP.get("parameterValue");
        if (defvalList != null && defvalList.size() == 1) {
            LovValue defval = defvalList.get(0);
            if (defval != null) {
                Object val = defval.getValue();
                if (val != null && val.equals("$")) {
                    docP.put("parameterValue", "");
                }
            }
        }
    }
    for (int i = 0; i < parametersArrayList.size(); i++) {
        Map<String, Object> parameter = parametersArrayList.get(i);
        List<Map<String, Object>> defaultValuesList = (List<Map<String, Object>>) parameter.get(PROPERTY_DATA);
        parameter.remove("parameterValue");
        if (defaultValuesList != null) {
            // Filter out null values
            defaultValuesList.removeIf(e -> e.get("value") == JSONObject.NULL || e.get("description") == JSONObject.NULL);
            // Fix JSON structure of admissible values
            defaultValuesList.forEach(e -> {
                List<String> fieldsToBeRemoved = new ArrayList<>();
                e.keySet().forEach(f -> {
                    if (!f.startsWith("_col")) {
                        fieldsToBeRemoved.add(f);
                    }
                });
                fieldsToBeRemoved.forEach(e::remove);
            });
        }
    }
    resultAsMap.put("filterStatus", parametersArrayList);
    resultAsMap.put("isReadyForExecution", bmop.isReadyForExecution(parameters));
    LOGGER.debug("OUT");
    return Response.ok(resultAsMap).build();
}
```

## 2. FASTJSON_J_S_O_N_PATH_PATCH_ADD
- metodo: void patchAdd(Object, Object, boolean)
- AUTO: elegible=True  CC before=19  after=16  delta=-3

### before
```java
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
```
### after
```java
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
        if (currentObject == null && i != segments.length - 1 && segment instanceof PropertySegment) {
            currentObject = new JSONObject();
            ((PropertySegment) segment).setValue(this, parentObject, currentObject);
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
```

## 3. ANT_STRIP_JAVA_COMMENTS_READ
- metodo: int read()
- AUTO: elegible=True  CC before=57  after=48  delta=-9

### before
```java
/**
 * Returns the next character in the filtered stream, not including
 * Java comments.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
public int read() throws IOException {
    int ch = -1;
    if (readAheadCh != -1) {
        ch = readAheadCh;
        readAheadCh = -1;
    } else {
        ch = in.read();
        if (ch == '"' && !quoted) {
            inString = !inString;
            quoted = false;
        } else if (ch == '\\') {
            quoted = !quoted;
        } else {
            quoted = false;
            if (!inString) {
                if (ch == '/') {
                    ch = in.read();
                    if (ch == '/') {
                        while (ch != '\n' && ch != -1 && ch != '\r') {
                            ch = in.read();
                        }
                    } else if (ch == '*') {
                        while (ch != -1) {
                            ch = in.read();
                            if (ch == '*') {
                                ch = in.read();
                                while (ch == '*') {
                                    ch = in.read();
                                }
                                if (ch == '/') {
                                    ch = read();
                                    break;
                                }
                            }
                        }
                    } else {
                        readAheadCh = ch;
                        ch = '/';
                    }
                }
            }
        }
    }
    return ch;
}
```
### after
```java
/**
 * Returns the next character in the filtered stream, not including
 * Java comments.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
public int read() throws IOException {
    int ch = -1;
    if (readAheadCh != -1) {
        ch = readAheadCh;
        readAheadCh = -1;
    } else {
        ch = in.read();
        if (ch == '"' && !quoted) {
            inString = !inString;
            quoted = false;
        } else if (ch == '\\') {
            quoted = !quoted;
        } else {
            quoted = false;
            if (!inString && ch == '/') {
                ch = in.read();
                if (ch == '/') {
                    while (ch != '\n' && ch != -1 && ch != '\r') {
                        ch = in.read();
                    }
                } else if (ch == '*') {
                    while (ch != -1) {
                        ch = in.read();
                        if (ch == '*') {
                            ch = in.read();
                            while (ch == '*') {
                                ch = in.read();
                            }
                            if (ch == '/') {
                                ch = read();
                                break;
                            }
                        }
                    }
                } else {
                    readAheadCh = ch;
                    ch = '/';
                }
            }
        }
    }
    return ch;
}
```

## 4. JACKSON_CORE_PARSER_BASE_DECODE_BASE64_ESCAPE
- metodo: int _decodeBase64Escape(Base64Variant, int, int)
- AUTO: elegible=True  CC before=7  after=5  delta=-2

### before
```java
protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws JacksonException {
    // Need to handle escaped chars
    if (ch != '\\') {
        _reportInvalidBase64Char(b64variant, ch, index);
    }
    int unescaped = _decodeEscaped();
    // if white space, skip if first triplet; otherwise errors
    if (unescaped <= INT_SPACE) {
        if (index == 0) {
            // whitespace only allowed to be skipped between triplets
            return -1;
        }
    }
    // otherwise try to find actual triplet value
    int bits = b64variant.decodeBase64Char(unescaped);
    if (bits < 0) {
        if (bits != Base64Variant.BASE64_VALUE_PADDING) {
            _reportInvalidBase64Char(b64variant, unescaped, index);
        }
    }
    return bits;
}
```
### after
```java
protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws JacksonException {
    // Need to handle escaped chars
    if (ch != '\\') {
        _reportInvalidBase64Char(b64variant, ch, index);
    }
    int unescaped = _decodeEscaped();
    // if white space, skip if first triplet; otherwise errors
    if (unescaped <= INT_SPACE && index == 0) {
        // whitespace only allowed to be skipped between triplets
        return -1;
    }
    // otherwise try to find actual triplet value
    int bits = b64variant.decodeBase64Char(unescaped);
    if (bits < 0 && bits != Base64Variant.BASE64_VALUE_PADDING) {
        _reportInvalidBase64Char(b64variant, unescaped, index);
    }
    return bits;
}
```

## 5. REAL_ANT_SCAN_ENABLED
- metodo: void scan(java.io.File, boolean, String[])
- AUTO: elegible=True  CC before=3  after=2  delta=-1

### before
```java
void scan(java.io.File dir, boolean enabled, String[] patterns) {
    if (dir != null) {
        if (enabled) {
            includes = patterns;
        }
    }
}
```
### after
```java
void scan(java.io.File dir, boolean enabled, String[] patterns) {
    if (dir != null && enabled) {
        includes = patterns;
    }
}
```

## 6. POI_C_F_RULE_BASE_SET_CONDITION_TYPE
- metodo: void setConditionType(byte)
- AUTO: elegible=True  CC before=4  after=3  delta=-1

### before
```java
protected void setConditionType(byte condition_type) {
    if ((this instanceof CFRuleRecord)) {
        if (!(condition_type == CONDITION_TYPE_CELL_VALUE_IS || condition_type == CONDITION_TYPE_FORMULA)) {
            throw new IllegalArgumentException("CFRuleRecord only accepts Value-Is and Formula types");
        }
    }
    this.condition_type = condition_type;
}
```
### after
```java
protected void setConditionType(byte condition_type) {
    if ((this instanceof CFRuleRecord) && !(condition_type == CONDITION_TYPE_CELL_VALUE_IS || condition_type == CONDITION_TYPE_FORMULA)) {
        throw new IllegalArgumentException("CFRuleRecord only accepts Value-Is and Formula types");
    }
    this.condition_type = condition_type;
}
```

## 7. JACKSON_CORE_U_T_F8_STREAM_JSON_PARSER_SKIP_COLON_FAST
- metodo: int _skipColonFast(int)
- AUTO: elegible=True  CC before=36  after=28  delta=-8

### before
```java
// Variant called when we know there's at least 4 more bytes available
private final int _skipColonFast(int ptr) throws JacksonException {
    int i = _inputBuffer[ptr++];
    if (i == INT_COLON) {
        // common case, no leading space
        i = _inputBuffer[ptr++];
        if (i > INT_SPACE) {
            // nor trailing
            if (i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        } else if (i == INT_SPACE || i == INT_TAB) {
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE) {
                if (i != INT_SLASH && i != INT_HASH) {
                    _inputPtr = ptr;
                    return i;
                }
            }
        }
        _inputPtr = ptr - 1;
        // true -> skipped colon
        return _skipColon2(true);
    }
    if (i == INT_SPACE || i == INT_TAB) {
        i = _inputBuffer[ptr++];
    }
    if (i == INT_COLON) {
        i = _inputBuffer[ptr++];
        if (i > INT_SPACE) {
            if (i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        } else if (i == INT_SPACE || i == INT_TAB) {
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE) {
                if (i != INT_SLASH && i != INT_HASH) {
                    _inputPtr = ptr;
                    return i;
                }
            }
        }
        _inputPtr = ptr - 1;
        return _skipColon2(true);
    }
    _inputPtr = ptr - 1;
    return _skipColon2(false);
}
```
### after
```java
// Variant called when we know there's at least 4 more bytes available
private final int _skipColonFast(int ptr) throws JacksonException {
    int i = _inputBuffer[ptr++];
    if (i == INT_COLON) {
        // common case, no leading space
        i = _inputBuffer[ptr++];
        if (i > INT_SPACE) {
            // nor trailing
            if (i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        } else if (i == INT_SPACE || i == INT_TAB) {
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE && i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        }
        _inputPtr = ptr - 1;
        // true -> skipped colon
        return _skipColon2(true);
    }
    if (i == INT_SPACE || i == INT_TAB) {
        i = _inputBuffer[ptr++];
    }
    if (i == INT_COLON) {
        i = _inputBuffer[ptr++];
        if (i > INT_SPACE) {
            if (i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        } else if (i == INT_SPACE || i == INT_TAB) {
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE && i != INT_SLASH && i != INT_HASH) {
                _inputPtr = ptr;
                return i;
            }
        }
        _inputPtr = ptr - 1;
        return _skipColon2(true);
    }
    _inputPtr = ptr - 1;
    return _skipColon2(false);
}
```

## 8. JACKSON_CORE_U_T_F8_STREAM_JSON_PARSER_RELEASE_BUFFERS
- metodo: void _releaseBuffers()
- AUTO: elegible=True  CC before=6  after=4  delta=-2

### before
```java
/**
 * Method called to release internal buffers owned by the base
 * reader. This may be called along with {@link #_closeInput} (for
 * example, when explicitly closing this reader instance), or
 * separately (if need be).
 */
@Override
protected void _releaseBuffers() {
    super._releaseBuffers();
    // Merge found symbols, if any:
    _symbols.release();
    if (_bufferRecyclable) {
        byte[] buf = _inputBuffer;
        if (buf != null) {
            // Let's not set it to null; this way should get slightly more meaningful
            // error messages in case someone closes parser indirectly, without realizing.
            if (buf != NO_BYTES) {
                _inputBuffer = NO_BYTES;
                _ioContext.releaseReadIOBuffer(buf);
            }
        }
    }
}
```
### after
```java
/**
 * Method called to release internal buffers owned by the base
 * reader. This may be called along with {@link #_closeInput} (for
 * example, when explicitly closing this reader instance), or
 * separately (if need be).
 */
@Override
protected void _releaseBuffers() {
    super._releaseBuffers();
    // Merge found symbols, if any:
    _symbols.release();
    if (_bufferRecyclable) {
        byte[] buf = _inputBuffer;
        if (buf != null && buf != NO_BYTES) {
            _inputBuffer = NO_BYTES;
            _ioContext.releaseReadIOBuffer(buf);
        }
    }
}
```

## 9. JMETAL_L_Z09_OBJECTIVE
- metodo: void objective(List<Double>, List<Double>)
- AUTO: elegible=True  CC before=42  after=39  delta=-3

### before
```java
void objective(List<Double> xVar, List<Double> yObj) {
    // 2-objective case
    if (nobj == 2) {
        if (ltype == 21 || ltype == 22 || ltype == 23 || ltype == 24 || ltype == 26) {
            double g = 0, h = 0, a, b;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 2 == 0) {
                    // linkage
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            // shape function
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
        if (ltype == 25) {
            double g = 0, h = 0, a, b;
            double /*e = 0,*/
            c;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 3 == 0) {
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else if (n % 3 == 1) {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                } else {
                    c = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 3);
                    if (n % 2 == 0) {
                        aa.add(c);
                    } else {
                        bb.add(c);
                    }
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
    }
    // 3-objective case
    if (nobj == 3) {
        if (ltype == 31 || ltype == 32) {
            double g = 0, h = 0, e = 0, a;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            ArrayList<Double> cc = new ArrayList<Double>();
            for (int n = 2; n < nvar; n++) {
                a = psfunc3(xVar.get(n), xVar.get(0), xVar.get(1), n, ltype);
                if (n % 3 == 0) {
                    aa.add(a);
                } else if (n % 3 == 1) {
                    bb.add(a);
                } else {
                    cc.add(a);
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            e = betaFunction(cc, dtype);
            double[] alpha = new double[3];
            alphaFunction(alpha, xVar, 3, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            yObj.set(2, alpha[2] + e);
            aa.clear();
            bb.clear();
            cc.clear();
        }
    }
}
```
### after
```java
void objective(List<Double> xVar, List<Double> yObj) {
    // 2-objective case
    if (nobj == 2) {
        if (ltype == 21 || ltype == 22 || ltype == 23 || ltype == 24 || ltype == 26) {
            double g = 0, h = 0, a, b;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 2 == 0) {
                    // linkage
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            // shape function
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
        if (ltype == 25) {
            double g = 0, h = 0, a, b;
            double /*e = 0,*/
            c;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 3 == 0) {
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else if (n % 3 == 1) {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                } else {
                    c = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 3);
                    if (n % 2 == 0) {
                        aa.add(c);
                    } else {
                        bb.add(c);
                    }
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
    }
    // 3-objective case
    if (nobj == 3 && (ltype == 31 || ltype == 32)) {
        double g = 0, h = 0, e = 0, a;
        ArrayList<Double> aa = new ArrayList<Double>();
        ArrayList<Double> bb = new ArrayList<Double>();
        ArrayList<Double> cc = new ArrayList<Double>();
        for (int n = 2; n < nvar; n++) {
            a = psfunc3(xVar.get(n), xVar.get(0), xVar.get(1), n, ltype);
            if (n % 3 == 0) {
                aa.add(a);
            } else if (n % 3 == 1) {
                bb.add(a);
            } else {
                cc.add(a);
            }
        }
        g = betaFunction(aa, dtype);
        h = betaFunction(bb, dtype);
        e = betaFunction(cc, dtype);
        double[] alpha = new double[3];
        alphaFunction(alpha, xVar, 3, ptype);
        yObj.set(0, alpha[0] + h);
        yObj.set(1, alpha[1] + g);
        yObj.set(2, alpha[2] + e);
        aa.clear();
        bb.clear();
        cc.clear();
    }
}
```

## 10. POI_BLANK_CELL_SHEET_GROUP_CONTAINS_CELL
- metodo: boolean containsCell(int, int)
- AUTO: elegible=True  CC before=11  after=8  delta=-3

### before
```java
public boolean containsCell(int rowIndex, int columnIndex) {
    if (rowIndex > _lastDefinedRow)
        return true;
    for (int i = _rectangleGroups.size() - 1; i >= 0; i--) {
        BlankCellRectangleGroup bcrg = _rectangleGroups.get(i);
        if (bcrg.containsCell(rowIndex, columnIndex)) {
            return true;
        }
    }
    if (_currentRectangleGroup != null && _currentRectangleGroup.containsCell(rowIndex, columnIndex)) {
        return true;
    }
    if (_currentRowIndex != -1 && _currentRowIndex == rowIndex) {
        if (_firstColumnIndex <= columnIndex && columnIndex <= _lastColumnIndex) {
            return true;
        }
    }
    return false;
}
```
### after
```java
public boolean containsCell(int rowIndex, int columnIndex) {
    if (rowIndex > _lastDefinedRow)
        return true;
    for (int i = _rectangleGroups.size() - 1; i >= 0; i--) {
        BlankCellRectangleGroup bcrg = _rectangleGroups.get(i);
        if (bcrg.containsCell(rowIndex, columnIndex)) {
            return true;
        }
    }
    if (_currentRectangleGroup != null && _currentRectangleGroup.containsCell(rowIndex, columnIndex)) {
        return true;
    }
    if (_currentRowIndex != -1 && _currentRowIndex == rowIndex && _firstColumnIndex <= columnIndex && columnIndex <= _lastColumnIndex) {
        return true;
    }
    return false;
}
```

## 11. ANT_CONCAT_FILTER_READ
- metodo: int read()
- AUTO: elegible=True  CC before=11  after=9  delta=-2

### before
```java
/**
 * Returns the next character in the filtered stream. If the desired
 * number of lines have already been read, the resulting stream is
 * effectively at an end. Otherwise, the next character from the
 * underlying stream is read and returned.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
@Override
public int read() throws IOException {
    // do the "singleton" initialization
    if (!getInitialized()) {
        initialize();
        setInitialized(true);
    }
    int ch = -1;
    // The readers return -1 if they end. So simply read the "prepend"
    // after that the "content" and at the end the "append" file.
    if (prependReader != null) {
        ch = prependReader.read();
        if (ch == -1) {
            // I am the only one so I have to close the reader
            prependReader.close();
            prependReader = null;
        }
    }
    if (ch == -1) {
        ch = super.read();
    }
    if (ch == -1) {
        // don't call super.close() because that reader is used
        // on other places ...
        if (appendReader != null) {
            ch = appendReader.read();
            if (ch == -1) {
                // I am the only one so I have to close the reader
                appendReader.close();
                appendReader = null;
            }
        }
    }
    return ch;
}
```
### after
```java
/**
 * Returns the next character in the filtered stream. If the desired
 * number of lines have already been read, the resulting stream is
 * effectively at an end. Otherwise, the next character from the
 * underlying stream is read and returned.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
@Override
public int read() throws IOException {
    // do the "singleton" initialization
    if (!getInitialized()) {
        initialize();
        setInitialized(true);
    }
    int ch = -1;
    // The readers return -1 if they end. So simply read the "prepend"
    // after that the "content" and at the end the "append" file.
    if (prependReader != null) {
        ch = prependReader.read();
        if (ch == -1) {
            // I am the only one so I have to close the reader
            prependReader.close();
            prependReader = null;
        }
    }
    if (ch == -1) {
        ch = super.read();
    }
    if (ch == -1 && appendReader != null) {
        ch = appendReader.read();
        if (ch == -1) {
            // I am the only one so I have to close the reader
            appendReader.close();
            appendReader = null;
        }
    }
    return ch;
}
```

## 12. JACKSON_DATABIND_METHOD_PROPERTY_SET_AND_RETURN
- metodo: Object setAndReturn(DeserializationContext, Object, Object)
- AUTO: elegible=True  CC before=5  after=4  delta=-1

### before
```java
@Override
public Object setAndReturn(DeserializationContext ctxt, Object instance, Object value) throws JacksonException {
    if (value == null) {
        if (_skipNulls) {
            return instance;
        }
    }
    try {
        Object result = _setterReturn.get().invokeExact(instance, value);
        return (result == null) ? instance : result;
    } catch (Throwable e) {
        _throwAsJacksonE(ctxt.getParser(), e, value);
        return null;
    }
}
```
### after
```java
@Override
public Object setAndReturn(DeserializationContext ctxt, Object instance, Object value) throws JacksonException {
    if (value == null && _skipNulls) {
        return instance;
    }
    try {
        Object result = _setterReturn.get().invokeExact(instance, value);
        return (result == null) ? instance : result;
    } catch (Throwable e) {
        _throwAsJacksonE(ctxt.getParser(), e, value);
        return null;
    }
}
```

## 13. JACKSON_DATABIND_ENUM_RESOLVER_FIND_ENUM
- metodo: Enum<?> findEnum(String)
- AUTO: elegible=True  CC before=3  after=2  delta=-1

### before
```java
/*
        /**********************************************************************
        /* Public API
        /**********************************************************************
         */
public Enum<?> findEnum(String key) {
    Enum<?> en = _enumsById.get(key);
    if (en == null) {
        if (_isIgnoreCase) {
            return _findEnumIgnoreCase(key);
        }
    }
    return en;
}
```
### after
```java
/*
        /**********************************************************************
        /* Public API
        /**********************************************************************
         */
public Enum<?> findEnum(String key) {
    Enum<?> en = _enumsById.get(key);
    if (en == null && _isIgnoreCase) {
        return _findEnumIgnoreCase(key);
    }
    return en;
}
```

## 14. ANT_U_R_L_RESOURCE_GET_U_R_L
- metodo: URL getURL()
- AUTO: elegible=True  CC before=10  after=7  delta=-3

### before
```java
/**
 * Get the URL used by this URLResource.
 * @return a URL object.
 */
public synchronized URL getURL() {
    if (isReference()) {
        return getRef().getURL();
    }
    if (url == null) {
        if (baseURL != null) {
            if (relPath == null) {
                throw new BuildException("must provide relativePath" + " attribute when using baseURL.");
            }
            try {
                url = new URL(baseURL, relPath);
            } catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }
    }
    return url;
}
```
### after
```java
/**
 * Get the URL used by this URLResource.
 * @return a URL object.
 */
public synchronized URL getURL() {
    if (isReference()) {
        return getRef().getURL();
    }
    if (url == null && baseURL != null) {
        if (relPath == null) {
            throw new BuildException("must provide relativePath" + " attribute when using baseURL.");
        }
        try {
            url = new URL(baseURL, relPath);
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }
    return url;
}
```

## 15. KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_TRANSFORM_RUNTIME_DRIVERS
- metodo: ArrayList<HashMap<String, Object>> transformRuntimeDrivers(List<BusinessModelDriverRuntime>, IParameterUseDAO, String, MetaModel, BusinessModelOpenParameters)
- AUTO: elegible=True  CC before=187  after=178  delta=-9

### before
```java
public ArrayList<HashMap<String, Object>> transformRuntimeDrivers(List<BusinessModelDriverRuntime> parameters, IParameterUseDAO parameterUseDAO, String role, MetaModel businessModel, BusinessModelOpenParameters BMOP) {
    ArrayList<HashMap<String, Object>> parametersArrayList = new ArrayList<>();
    ParameterUse parameterUse;
    for (BusinessModelDriverRuntime objParameter : parameters) {
        Integer paruseId = objParameter.getParameterUseId();
        try {
            parameterUse = parameterUseDAO.loadByUseID(paruseId);
        } catch (EMFUserError e1) {
            LOGGER.debug(e1.getCause(), e1);
            throw new SpagoBIRuntimeException(e1.getMessage(), e1);
        }
        HashMap<String, Object> parameterAsMap = new HashMap<>();
        parameterAsMap.put("id", objParameter.getBiObjectId());
        parameterAsMap.put("label", objParameter.getLabel());
        parameterAsMap.put("urlName", objParameter.getId());
        parameterAsMap.put("type", objParameter.getParType());
        parameterAsMap.put("typeCode", objParameter.getTypeCode());
        parameterAsMap.put("selectionType", objParameter.getSelectionType());
        parameterAsMap.put("valueSelection", parameterUse.getValueSelection());
        parameterAsMap.put("selectedLayer", objParameter.getSelectedLayer());
        parameterAsMap.put("selectedLayerProp", objParameter.getSelectedLayerProp());
        parameterAsMap.put("visible", objParameter.isVisible());
        parameterAsMap.put("mandatory", objParameter.isMandatory());
        parameterAsMap.put("multivalue", objParameter.isMultivalue());
        parameterAsMap.put("driverLabel", objParameter.getPar().getLabel());
        parameterAsMap.put("driverUseLabel", objParameter.getAnalyticalDriverExecModality().getLabel());
        parameterAsMap.put("allowInternalNodeSelection", objParameter.getPar().getModalityValue().getLovProvider().contains("<LOVTYPE>treeinner</LOVTYPE>"));
        // get values
        if (objParameter.getDriver().getParameterValues() != null) {
            List paramValueLst = new ArrayList();
            List paramDescrLst = new ArrayList();
            Object paramValues = objParameter.getDriver().getParameterValues();
            Object paramDescriptionValues = objParameter.getDriver().getParameterValuesDescription();
            Encoder encoder = OwaspDefaultEncoderFactory.getInstance().getEncoder();
            if (paramValues instanceof List) {
                List<String> valuesList = (List) paramValues;
                List<String> descriptionList = (List) paramDescriptionValues;
                if (paramDescriptionValues == null || !(paramDescriptionValues instanceof List)) {
                    descriptionList = new ArrayList<>();
                }
                // String item = null;
                for (int k = 0; k < valuesList.size(); k++) {
                    String itemVal = valuesList.get(k);
                    String itemDescr = descriptionList.size() > k && descriptionList.get(k) != null ? descriptionList.get(k) : itemVal;
                    try {
                        // % character breaks decode method
                        if (!itemVal.contains("%")) {
                            itemVal = encoder.decodeFromURL(itemVal);
                        }
                        if (!itemDescr.contains("%")) {
                            itemDescr = encoder.decodeFromURL(itemDescr);
                        }
                        // check input value and convert if it's an old multivalue syntax({;{xxx;yyy}STRING}) to list of values :["A-OMP", "A-PO", "CL"]
                        if (objParameter.isMultivalue() && itemVal.indexOf("{") >= 0) {
                            String sep = itemVal.substring(1, 2);
                            String val = itemVal.substring(3, itemVal.indexOf("}"));
                            String[] valLst = val.split(sep);
                            for (int k2 = 0; k2 < valLst.length; k2++) {
                                String itemVal2 = valLst[k2];
                                if (itemVal2 != null && !"".equals(itemVal2)) {
                                    paramValueLst.add(itemVal2);
                                }
                            }
                        } else {
                            if (itemVal != null && !"".equals(itemVal)) {
                                paramValueLst.add(itemVal);
                            }
                            paramDescrLst.add(itemDescr);
                        }
                    } catch (EncodingException e) {
                        LOGGER.debug("An error occured while decoding parameter with value[" + itemVal + "]" + e);
                    }
                }
            } else if (paramValues instanceof String) {
                // % character breaks decode method
                if (!((String) paramValues).contains("%")) {
                    try {
                        paramValues = encoder.decodeFromURL((String) paramValues);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramValueLst.add(paramValues.toString());
                String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                if (!parDescrVal.contains("%")) {
                    try {
                        parDescrVal = encoder.decodeFromURL(parDescrVal);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramDescrLst.add(parDescrVal);
            }
            parameterAsMap.put("parameterValue", paramValueLst);
            parameterAsMap.put("parameterDescription", paramDescriptionValues);
        }
        boolean showParameterLov = true;
        // Parameters NO TREE
        if ("lov".equalsIgnoreCase(parameterUse.getValueSelection()) && !objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_TREE)) {
            ArrayList<HashMap<String, Object>> admissibleValues = objParameter.getAdmissibleValues();
            if (!objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_LOOKUP)) {
                parameterAsMap.put(PROPERTY_DATA, admissibleValues);
            } else {
                parameterAsMap.put(PROPERTY_DATA, new ArrayList<>());
            }
            parameterAsMap.put("defaultValuesMeta", objParameter.getLovVisibleColumnsNames());
            parameterAsMap.put(DocumentExecutionUtils.VALUE_COLUMN_NAME_METADATA, objParameter.getLovValueColumnName());
            parameterAsMap.put(DocumentExecutionUtils.DESCRIPTION_COLUMN_NAME_METADATA, objParameter.getLovDescriptionColumnName());
            // hide the parameter if is mandatory and have one value in lov (no error parameter)
            if (admissibleValues != null && admissibleValues.size() == 1 && objParameter.isMandatory() && !admissibleValues.get(0).containsKey("error") && (objParameter.getDataDependencies() == null || objParameter.getDataDependencies().isEmpty()) && (objParameter.getLovDependencies() == null || objParameter.getLovDependencies().isEmpty())) {
                showParameterLov = false;
            }
            // if parameterValue is not null and is array, check if all element are present in lov
            Object values = parameterAsMap.get("parameterValue");
            if (values != null && admissibleValues != null) {
                BMOP.checkIfValuesAreAdmissible(values, admissibleValues);
            }
        }
        // DATE RANGE DEFAULT VALUE
        if (objParameter.getParType().equals("DATE_RANGE")) {
            try {
                ArrayList<HashMap<String, Object>> defaultValues = BMOP.manageDataRange(businessModel, role, objParameter.getId());
                parameterAsMap.put(PROPERTY_DATA, defaultValues);
            } catch (SerializationException | EMFUserError | JSONException | IOException e) {
                LOGGER.debug("Filters DATE RANGE ERRORS ", e);
            }
        }
        // convert the parameterValue from array of string in array of object
        DefaultValuesList parameterValueList = new DefaultValuesList();
        Object oVals = parameterAsMap.get("parameterValue");
        Object oDescr = parameterAsMap.get("parameterDescription") != null ? parameterAsMap.get("parameterDescription") : new ArrayList<String>();
        if (oVals != null) {
            if (oVals instanceof List) {
                // CROSS NAV : INPUT PARAM PARAMETER TARGET DOC IS STRING
                if (oVals.toString().startsWith("[") && oVals.toString().endsWith("]") && parameterUse.getValueSelection().equals("man_in")) {
                    List<String> valList = (ArrayList) oVals;
                    String stringResult = "";
                    for (int k = 0; k < valList.size(); k++) {
                        String itemVal = valList.get(k);
                        if (objParameter.getParType().equals("STRING") && objParameter.isMultivalue()) {
                            stringResult += "'" + itemVal + "'";
                        } else {
                            stringResult += itemVal;
                        }
                        if (k != valList.size() - 1) {
                            stringResult += ",";
                        }
                    }
                    LovValue defValue = new LovValue();
                    defValue.setValue(stringResult);
                    defValue.setDescription(stringResult);
                    parameterValueList.add(defValue);
                } else {
                    List<String> valList = (ArrayList) oVals;
                    List<String> descrList = (ArrayList) oDescr;
                    for (int k = 0; k < valList.size(); k++) {
                        String itemVal = valList.get(k);
                        String itemDescr = descrList.size() > k ? descrList.get(k) : itemVal;
                        LovValue defValue = new LovValue();
                        defValue.setValue(itemVal);
                        defValue.setDescription(itemDescr != null ? itemDescr : itemVal);
                        parameterValueList.add(defValue);
                    }
                }
                parameterAsMap.put("parameterValue", parameterValueList);
            }
        }
        addDependencies(objParameter, parameterAsMap);
        // load DEFAULT VALUE if present and if the parameter value is empty
        if (objParameter.getDefaultValues() != null && !objParameter.getDefaultValues().isEmpty() && objParameter.getDefaultValues().get(0).getValue() != null) {
            DefaultValuesList valueList = null;
            // check if the parameter is really valorized (for example if it isn't an empty list)
            List lstValues = (List) parameterAsMap.get("parameterValue");
            // if (lstValues.size() == 0)
            // jsonCrossParameters.remove(objParameter.getId());
            String parLab = objParameter.getDriver() != null && objParameter.getDriver().getParameter() != null ? objParameter.getDriver().getParameter().getLabel() : "";
            String useModLab = objParameter.getAnalyticalDriverExecModality() != null ? objParameter.getAnalyticalDriverExecModality().getLabel() : "";
            String sessionKey = parLab + "_" + useModLab;
            valueList = objParameter.getDefaultValues();
            // in every case fill default values!
            parameterAsMap.put("driverDefaultValue", valueList);
        }
        if (!showParameterLov) {
            parameterAsMap.put("showOnPanel", "false");
        } else {
            parameterAsMap.put("showOnPanel", "true");
        }
        parametersArrayList.add(parameterAsMap);
    }
    for (int z = 0; z < parametersArrayList.size(); z++) {
        Map docP = parametersArrayList.get(z);
        DefaultValuesList defvalList = (DefaultValuesList) docP.get("parameterValue");
        if (defvalList != null && defvalList.size() == 1) {
            LovValue defval = defvalList.get(0);
            if (defval != null) {
                Object val = defval.getValue();
                if (val != null && val.equals("$")) {
                    docP.put("parameterValue", "");
                }
            }
        }
    }
    return parametersArrayList;
}
```
### after
```java
public ArrayList<HashMap<String, Object>> transformRuntimeDrivers(List<BusinessModelDriverRuntime> parameters, IParameterUseDAO parameterUseDAO, String role, MetaModel businessModel, BusinessModelOpenParameters BMOP) {
    ArrayList<HashMap<String, Object>> parametersArrayList = new ArrayList<>();
    ParameterUse parameterUse;
    for (BusinessModelDriverRuntime objParameter : parameters) {
        Integer paruseId = objParameter.getParameterUseId();
        try {
            parameterUse = parameterUseDAO.loadByUseID(paruseId);
        } catch (EMFUserError e1) {
            LOGGER.debug(e1.getCause(), e1);
            throw new SpagoBIRuntimeException(e1.getMessage(), e1);
        }
        HashMap<String, Object> parameterAsMap = new HashMap<>();
        parameterAsMap.put("id", objParameter.getBiObjectId());
        parameterAsMap.put("label", objParameter.getLabel());
        parameterAsMap.put("urlName", objParameter.getId());
        parameterAsMap.put("type", objParameter.getParType());
        parameterAsMap.put("typeCode", objParameter.getTypeCode());
        parameterAsMap.put("selectionType", objParameter.getSelectionType());
        parameterAsMap.put("valueSelection", parameterUse.getValueSelection());
        parameterAsMap.put("selectedLayer", objParameter.getSelectedLayer());
        parameterAsMap.put("selectedLayerProp", objParameter.getSelectedLayerProp());
        parameterAsMap.put("visible", objParameter.isVisible());
        parameterAsMap.put("mandatory", objParameter.isMandatory());
        parameterAsMap.put("multivalue", objParameter.isMultivalue());
        parameterAsMap.put("driverLabel", objParameter.getPar().getLabel());
        parameterAsMap.put("driverUseLabel", objParameter.getAnalyticalDriverExecModality().getLabel());
        parameterAsMap.put("allowInternalNodeSelection", objParameter.getPar().getModalityValue().getLovProvider().contains("<LOVTYPE>treeinner</LOVTYPE>"));
        // get values
        if (objParameter.getDriver().getParameterValues() != null) {
            List paramValueLst = new ArrayList();
            List paramDescrLst = new ArrayList();
            Object paramValues = objParameter.getDriver().getParameterValues();
            Object paramDescriptionValues = objParameter.getDriver().getParameterValuesDescription();
            Encoder encoder = OwaspDefaultEncoderFactory.getInstance().getEncoder();
            if (paramValues instanceof List) {
                List<String> valuesList = (List) paramValues;
                List<String> descriptionList = (List) paramDescriptionValues;
                if (paramDescriptionValues == null || !(paramDescriptionValues instanceof List)) {
                    descriptionList = new ArrayList<>();
                }
                // String item = null;
                for (int k = 0; k < valuesList.size(); k++) {
                    String itemVal = valuesList.get(k);
                    String itemDescr = descriptionList.size() > k && descriptionList.get(k) != null ? descriptionList.get(k) : itemVal;
                    try {
                        // % character breaks decode method
                        if (!itemVal.contains("%")) {
                            itemVal = encoder.decodeFromURL(itemVal);
                        }
                        if (!itemDescr.contains("%")) {
                            itemDescr = encoder.decodeFromURL(itemDescr);
                        }
                        // check input value and convert if it's an old multivalue syntax({;{xxx;yyy}STRING}) to list of values :["A-OMP", "A-PO", "CL"]
                        if (objParameter.isMultivalue() && itemVal.indexOf("{") >= 0) {
                            String sep = itemVal.substring(1, 2);
                            String val = itemVal.substring(3, itemVal.indexOf("}"));
                            String[] valLst = val.split(sep);
                            for (int k2 = 0; k2 < valLst.length; k2++) {
                                String itemVal2 = valLst[k2];
                                if (itemVal2 != null && !"".equals(itemVal2)) {
                                    paramValueLst.add(itemVal2);
                                }
                            }
                        } else {
                            if (itemVal != null && !"".equals(itemVal)) {
                                paramValueLst.add(itemVal);
                            }
                            paramDescrLst.add(itemDescr);
                        }
                    } catch (EncodingException e) {
                        LOGGER.debug("An error occured while decoding parameter with value[" + itemVal + "]" + e);
                    }
                }
            } else if (paramValues instanceof String) {
                // % character breaks decode method
                if (!((String) paramValues).contains("%")) {
                    try {
                        paramValues = encoder.decodeFromURL((String) paramValues);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramValueLst.add(paramValues.toString());
                String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                if (!parDescrVal.contains("%")) {
                    try {
                        parDescrVal = encoder.decodeFromURL(parDescrVal);
                    } catch (EncodingException e) {
                        LOGGER.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramDescrLst.add(parDescrVal);
            }
            parameterAsMap.put("parameterValue", paramValueLst);
            parameterAsMap.put("parameterDescription", paramDescriptionValues);
        }
        boolean showParameterLov = true;
        // Parameters NO TREE
        if ("lov".equalsIgnoreCase(parameterUse.getValueSelection()) && !objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_TREE)) {
            ArrayList<HashMap<String, Object>> admissibleValues = objParameter.getAdmissibleValues();
            if (!objParameter.getSelectionType().equalsIgnoreCase(DocumentExecutionUtils.SELECTION_TYPE_LOOKUP)) {
                parameterAsMap.put(PROPERTY_DATA, admissibleValues);
            } else {
                parameterAsMap.put(PROPERTY_DATA, new ArrayList<>());
            }
            parameterAsMap.put("defaultValuesMeta", objParameter.getLovVisibleColumnsNames());
            parameterAsMap.put(DocumentExecutionUtils.VALUE_COLUMN_NAME_METADATA, objParameter.getLovValueColumnName());
            parameterAsMap.put(DocumentExecutionUtils.DESCRIPTION_COLUMN_NAME_METADATA, objParameter.getLovDescriptionColumnName());
            // hide the parameter if is mandatory and have one value in lov (no error parameter)
            if (admissibleValues != null && admissibleValues.size() == 1 && objParameter.isMandatory() && !admissibleValues.get(0).containsKey("error") && (objParameter.getDataDependencies() == null || objParameter.getDataDependencies().isEmpty()) && (objParameter.getLovDependencies() == null || objParameter.getLovDependencies().isEmpty())) {
                showParameterLov = false;
            }
            // if parameterValue is not null and is array, check if all element are present in lov
            Object values = parameterAsMap.get("parameterValue");
            if (values != null && admissibleValues != null) {
                BMOP.checkIfValuesAreAdmissible(values, admissibleValues);
            }
        }
        // DATE RANGE DEFAULT VALUE
        if (objParameter.getParType().equals("DATE_RANGE")) {
            try {
                ArrayList<HashMap<String, Object>> defaultValues = BMOP.manageDataRange(businessModel, role, objParameter.getId());
                parameterAsMap.put(PROPERTY_DATA, defaultValues);
            } catch (SerializationException | EMFUserError | JSONException | IOException e) {
                LOGGER.debug("Filters DATE RANGE ERRORS ", e);
            }
        }
        // convert the parameterValue from array of string in array of object
        DefaultValuesList parameterValueList = new DefaultValuesList();
        Object oVals = parameterAsMap.get("parameterValue");
        Object oDescr = parameterAsMap.get("parameterDescription") != null ? parameterAsMap.get("parameterDescription") : new ArrayList<String>();
        if (oVals != null && oVals instanceof List) {
            // CROSS NAV : INPUT PARAM PARAMETER TARGET DOC IS STRING
            if (oVals.toString().startsWith("[") && oVals.toString().endsWith("]") && parameterUse.getValueSelection().equals("man_in")) {
                List<String> valList = (ArrayList) oVals;
                String stringResult = "";
                for (int k = 0; k < valList.size(); k++) {
                    String itemVal = valList.get(k);
                    if (objParameter.getParType().equals("STRING") && objParameter.isMultivalue()) {
                        stringResult += "'" + itemVal + "'";
                    } else {
                        stringResult += itemVal;
                    }
                    if (k != valList.size() - 1) {
                        stringResult += ",";
                    }
                }
                LovValue defValue = new LovValue();
                defValue.setValue(stringResult);
                defValue.setDescription(stringResult);
                parameterValueList.add(defValue);
            } else {
                List<String> valList = (ArrayList) oVals;
                List<String> descrList = (ArrayList) oDescr;
                for (int k = 0; k < valList.size(); k++) {
                    String itemVal = valList.get(k);
                    String itemDescr = descrList.size() > k ? descrList.get(k) : itemVal;
                    LovValue defValue = new LovValue();
                    defValue.setValue(itemVal);
                    defValue.setDescription(itemDescr != null ? itemDescr : itemVal);
                    parameterValueList.add(defValue);
                }
            }
            parameterAsMap.put("parameterValue", parameterValueList);
        }
        addDependencies(objParameter, parameterAsMap);
        // load DEFAULT VALUE if present and if the parameter value is empty
        if (objParameter.getDefaultValues() != null && !objParameter.getDefaultValues().isEmpty() && objParameter.getDefaultValues().get(0).getValue() != null) {
            DefaultValuesList valueList = null;
            // check if the parameter is really valorized (for example if it isn't an empty list)
            List lstValues = (List) parameterAsMap.get("parameterValue");
            // if (lstValues.size() == 0)
            // jsonCrossParameters.remove(objParameter.getId());
            String parLab = objParameter.getDriver() != null && objParameter.getDriver().getParameter() != null ? objParameter.getDriver().getParameter().getLabel() : "";
            String useModLab = objParameter.getAnalyticalDriverExecModality() != null ? objParameter.getAnalyticalDriverExecModality().getLabel() : "";
            String sessionKey = parLab + "_" + useModLab;
            valueList = objParameter.getDefaultValues();
            // in every case fill default values!
            parameterAsMap.put("driverDefaultValue", valueList);
        }
        if (!showParameterLov) {
            parameterAsMap.put("showOnPanel", "false");
        } else {
            parameterAsMap.put("showOnPanel", "true");
        }
        parametersArrayList.add(parameterAsMap);
    }
    for (int z = 0; z < parametersArrayList.size(); z++) {
        Map docP = parametersArrayList.get(z);
        DefaultValuesList defvalList = (DefaultValuesList) docP.get("parameterValue");
        if (defvalList != null && defvalList.size() == 1) {
            LovValue defval = defvalList.get(0);
            if (defval != null) {
                Object val = defval.getValue();
                if (val != null && val.equals("$")) {
                    docP.put("parameterValue", "");
                }
            }
        }
    }
    return parametersArrayList;
}
```

## 16. JACKSON_DATABIND_SETTABLE_BEAN_PROPERTY_ASSIGN_INDEX
- metodo: void assignIndex(int)
- AUTO: elegible=True  CC before=3  after=2  delta=-1

### before
```java
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
```
### after
```java
/**
 * Method used to assign index for property.
 */
public void assignIndex(int index) {
    if (_propertyIndex != -1 && _propertyIndex != index) {
        throw new IllegalStateException("Property '" + getName() + "' already had index (" + _propertyIndex + "), trying to assign " + index);
    }
    _propertyIndex = index;
}
```

## 17. POI_LINEAR_REGRESSION_FUNCTION_EVALUATE_INTERNAL
- metodo: double evaluateInternal(ValueVector, ValueVector, int)
- AUTO: elegible=True  CC before=19  after=17  delta=-2

### before
```java
private double evaluateInternal(ValueVector x, ValueVector y, int size) throws EvaluationException {
    // error handling is as if the x is fully evaluated before y
    ErrorEval firstYerr = null;
    boolean accumlatedSome = false;
    // first pass: read in data, compute xbar and ybar
    double sumx = 0.0, sumy = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        if (vx instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) vx);
        }
        if (vy instanceof ErrorEval) {
            if (firstYerr == null) {
                firstYerr = (ErrorEval) vy;
                continue;
            }
        }
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            accumlatedSome = true;
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            sumx += nx.getNumberValue();
            sumy += ny.getNumberValue();
        }
    }
    if (firstYerr != null) {
        throw new EvaluationException(firstYerr);
    }
    if (!accumlatedSome) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double xbar = sumx / size;
    double ybar = sumy / size;
    // second pass: compute summary statistics
    double xxbar = 0.0, xybar = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            xxbar += (nx.getNumberValue() - xbar) * (nx.getNumberValue() - xbar);
            xybar += (nx.getNumberValue() - xbar) * (ny.getNumberValue() - ybar);
        }
    }
    if (xxbar == 0) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double beta1 = xybar / xxbar;
    double beta0 = ybar - beta1 * xbar;
    return (function == FUNCTION.INTERCEPT) ? beta0 : beta1;
}
```
### after
```java
private double evaluateInternal(ValueVector x, ValueVector y, int size) throws EvaluationException {
    // error handling is as if the x is fully evaluated before y
    ErrorEval firstYerr = null;
    boolean accumlatedSome = false;
    // first pass: read in data, compute xbar and ybar
    double sumx = 0.0, sumy = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        if (vx instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) vx);
        }
        if (vy instanceof ErrorEval && firstYerr == null) {
            firstYerr = (ErrorEval) vy;
            continue;
        }
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            accumlatedSome = true;
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            sumx += nx.getNumberValue();
            sumy += ny.getNumberValue();
        }
    }
    if (firstYerr != null) {
        throw new EvaluationException(firstYerr);
    }
    if (!accumlatedSome) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double xbar = sumx / size;
    double ybar = sumy / size;
    // second pass: compute summary statistics
    double xxbar = 0.0, xybar = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            xxbar += (nx.getNumberValue() - xbar) * (nx.getNumberValue() - xbar);
            xybar += (nx.getNumberValue() - xbar) * (ny.getNumberValue() - ybar);
        }
    }
    if (xxbar == 0) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double beta1 = xybar / xxbar;
    double beta0 = ybar - beta1 * xbar;
    return (function == FUNCTION.INTERCEPT) ? beta0 : beta1;
}
```

## 18. JACKSON_DATABIND_BEAN_DESERIALIZER_BASE_DELEGATE_DESERIALIZER
- metodo: ValueDeserializer<Object> _delegateDeserializer(JsonParser)
- AUTO: elegible=True  CC before=7  after=5  delta=-2

### before
```java
/**
 * Alternate to {@link #_delegateDeserializer()} which will only consider
 * {@code _arrayDelegateDeserializer} if given {@link JsonParser} points to
 * {@link JsonToken#START_ARRAY} token.
 */
protected final ValueDeserializer<Object> _delegateDeserializer(JsonParser p) {
    if (_delegateDeserializer == null) {
        // Note! Will not call `JsonParser.isExpectedArrayToken()` as that could
        // "transform" `JsonToken.START_OBJECT` into `JsonToken.START_ARRAY` and
        // here there is no strong expectation of Array value
        if (_arrayDelegateDeserializer != null) {
            // Alas, need bit elaborate logic: either JSON Array, OR no
            // Properties-based Creator
            if (p.hasToken(JsonToken.START_ARRAY) || (_propertyBasedCreator == null)) {
                return _arrayDelegateDeserializer;
            }
        }
    }
    return _delegateDeserializer;
}
```
### after
```java
/**
 * Alternate to {@link #_delegateDeserializer()} which will only consider
 * {@code _arrayDelegateDeserializer} if given {@link JsonParser} points to
 * {@link JsonToken#START_ARRAY} token.
 */
protected final ValueDeserializer<Object> _delegateDeserializer(JsonParser p) {
    if (_delegateDeserializer == null && _arrayDelegateDeserializer != null) {
        // Alas, need bit elaborate logic: either JSON Array, OR no
        // Properties-based Creator
        if (p.hasToken(JsonToken.START_ARRAY) || (_propertyBasedCreator == null)) {
            return _arrayDelegateDeserializer;
        }
    }
    return _delegateDeserializer;
}
```

## 19. ANT_PATH_CONVERT_SET_DEST
- metodo: void setDest(Resource)
- AUTO: elegible=True  CC before=3  after=2  delta=-1

### before
```java
/**
 * Set destination resource.
 * @param dest
 * @since Ant 1.10.13
 */
public void setDest(Resource dest) {
    if (dest != null) {
        if (this.dest != null) {
            throw new BuildException("@dest already set");
        }
    }
    this.dest = dest;
}
```
### after
```java
/**
 * Set destination resource.
 * @param dest
 * @since Ant 1.10.13
 */
public void setDest(Resource dest) {
    if (dest != null && this.dest != null) {
        throw new BuildException("@dest already set");
    }
    this.dest = dest;
}
```

## 20. PILOT_VALID_SIMPLE
- metodo: void processIfInRange(int, int)
- AUTO: elegible=True  CC before=3  after=2  delta=-1

### before
```java
/**
 * Procesa un valor si estÃ¡ en rango positivo y dentro del lÃ­mite.
 * PatrÃ³n tÃ­pico de validaciÃ³n de entrada en mÃ©todos de negocio.
 */
void processIfInRange(int value, int limit) {
    if (value > 0) {
        if (value <= limit) {
            System.out.println("Valor aceptado: " + value);
        }
    }
}
```
### after
```java
/**
 * Procesa un valor si estÃ¡ en rango positivo y dentro del lÃ­mite.
 * PatrÃ³n tÃ­pico de validaciÃ³n de entrada en mÃ©todos de negocio.
 */
void processIfInRange(int value, int limit) {
    if (value > 0 && value <= limit) {
        System.out.println("Valor aceptado: " + value);
    }
}
```

## 21. REAL_COMMONS_LANG_INNER_METHOD_CALL
- metodo: boolean hasContent(String)
- AUTO: elegible=False  CC before=3  after=3  delta=0
- motivo descarte: Descartado por: METHOD_CALL_IN_CONDITION, SINGLE_STATEMENT_NOT_IF

### before
```java
boolean hasContent(String s) {
    if (s != null) {
        if (s.isEmpty()) {
            return false;
        }
    }
    return true;
}
```

## 22. REAL_COMMONS_LANG_DECREMENT_COND
- metodo: boolean checkBound(int, int)
- AUTO: elegible=False  CC before=3  after=3  delta=0
- motivo descarte: Descartado por: INCREMENT_OR_DECREMENT_IN_CONDITION, SINGLE_STATEMENT_NOT_IF

### before
```java
boolean checkBound(int count, int max) {
    if (count > 0) {
        if (--count < max) {
            return true;
        }
    }
    return false;
}
```

## 23. JACKSON_DATABIND_FLOAT_DESER_DESERIALIZE
- metodo: T deserialize(JsonParser, DeserializationContext, T)
- AUTO: elegible=False  CC before=2  after=2  delta=0
- motivo descarte: Descartado por: SINGLE_STATEMENT_NOT_IF

### before
```java
@Override
public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws JacksonException {
    T newValue = deserialize(p, ctxt);
    if (existing == null) {
        return newValue;
    }
    int len = Array.getLength(existing);
    if (len == 0) {
        return newValue;
    }
    return _concat(existing, newValue);
}
```

## 24. REAL_COMMONS_COLLECTIONS_GET
- metodo: Object getObject(java.util.Map<String, Object>, String)
- AUTO: elegible=False  CC before=3  after=3  delta=0
- motivo descarte: Descartado por: METHOD_CALL_IN_CONDITION, SINGLE_STATEMENT_NOT_IF

### before
```java
Object getObject(java.util.Map<String, Object> map, String key) {
    if (map != null) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
    }
    return null;
}
```

## 25. JACKSON_DATABIND_DOUBLE_DESER_DESERIALIZE
- metodo: T deserialize(JsonParser, DeserializationContext, T)
- AUTO: elegible=False  CC before=2  after=2  delta=0
- motivo descarte: Descartado por: SINGLE_STATEMENT_NOT_IF

### before
```java
@Override
public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws JacksonException {
    T newValue = deserialize(p, ctxt);
    if (existing == null) {
        return newValue;
    }
    int len = Array.getLength(existing);
    if (len == 0) {
        return newValue;
    }
    return _concat(existing, newValue);
}
```

## 26. POI_DATE_FUNC_EVALUATE
- metodo: ValueEval evaluate(int, int, ValueEval, ValueEval, ValueEval)
- AUTO: elegible=False  CC before=1  after=1  delta=0
- motivo descarte: No se detectaron oportunidades de refactorizaciÃ³n

### before
```java
public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
    double result;
    try {
        double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
        double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
        double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
        result = evaluate(getYear(d0), (int) (d1 - 1), (int) d2);
        NumericFunction.checkValue(result);
    } catch (EvaluationException e) {
        return e.getErrorEval();
    }
    return new NumberEval(result);
}
```

## 27. REAL_ANT_EXECUTE_TASK
- metodo: void executeTask(boolean, boolean, String)
- AUTO: elegible=False  CC before=3  after=3  delta=0
- motivo descarte: Descartado por: OUTER_BLOCK_MULTIPLE_STATEMENTS, SINGLE_STATEMENT_NOT_IF

### before
```java
void executeTask(boolean projectValid, boolean taskEnabled, String taskName) {
    if (projectValid) {
        System.out.println("Preparing: " + taskName);
        if (taskEnabled) {
            System.out.println("Executing: " + taskName);
        }
    }
}
```

## 28. REAL_COMMONS_LANG_IS_NUMERIC
- metodo: boolean isNumeric(String)
- AUTO: elegible=False  CC before=10  after=10  delta=0
- motivo descarte: Descartado por: METHOD_CALL_IN_CONDITION, OUTER_BLOCK_MULTIPLE_STATEMENTS, SINGLE_STATEMENT_NOT_IF

### before
```java
boolean isNumeric(String cs) {
    if (cs != null) {
        if (cs.length() > 0) {
            for (int i = 0; i < cs.length(); i++) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
    return false;
}
```

## 29. REAL_COMMONS_LANG_CHOMP
- metodo: String chomp(String)
- AUTO: elegible=False  CC before=6  after=6  delta=0
- motivo descarte: Descartado por: METHOD_CALL_IN_CONDITION, OUTER_BLOCK_MULTIPLE_STATEMENTS, SINGLE_STATEMENT_NOT_IF

### before
```java
String chomp(String str) {
    if (str != null) {
        if (str.length() > 0) {
            int lastIdx = str.length() - 1;
            char last = str.charAt(lastIdx);
            if (last == '\n') {
                return str.substring(0, lastIdx);
            }
            return str;
        }
    }
    return str;
}
```

## 30. PILOT_INVALID_METHOD_GUARD
- metodo: void processIfNotEmpty(String, int)
- AUTO: elegible=False  CC before=3  after=3  delta=0
- motivo descarte: Descartado por: METHOD_CALL_IN_CONDITION, SINGLE_STATEMENT_NOT_IF

### before
```java
/**
 * PatrÃ³n tÃ­pico: comprobar isEmpty() antes de acceder.
 * El detector rechaza porque isEmpty() es una llamada a mÃ©todo
 * y la polÃ­tica conservadora del MVP la trata como posible side effect.
 */
void processIfNotEmpty(String input, int threshold) {
    if (!input.isEmpty()) {
        if (input.length() > threshold) {
            System.out.println("Input largo: " + input);
        }
    }
}
```


