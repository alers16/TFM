public class KNOWAGE_DATA_SET_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET {
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
        logger.debug(e.getCause(), e);
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
            logger.debug("Error loading parameter use with id " + paruseId, e1);
            throw new SpagoBIRuntimeException(e1.getMessage(), e1);
        }
        HashMap<String, Object> parameterAsMap = new HashMap<>();
        parameterAsMap.put("id", objParameter.getBiObjectId());
        parameterAsMap.put("label", objParameter.getLabel());
        parameterAsMap.put("urlName", objParameter.getId());
        parameterAsMap.put("type", objParameter.getParType());
        parameterAsMap.put("selectionType", objParameter.getSelectionType());
        parameterAsMap.put("valueSelection", parameterUse.getValueSelection());
        parameterAsMap.put("visible", ((objParameter.isVisible())));
        parameterAsMap.put("mandatory", ((objParameter.isMandatory())));
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
                        logger.debug("An error occured while decoding parameter with value[" + itemVal + "]" + e);
                    }
                }
            } else if (paramValues instanceof String) {
                // % character breaks decode method
                if (!((String) paramValues).contains("%")) {
                    try {
                        paramValues = encoder.decodeFromURL((String) paramValues);
                    } catch (EncodingException e) {
                        logger.debug(e.getCause(), e);
                        throw new SpagoBIRuntimeException(e.getMessage(), e);
                    }
                }
                paramValueLst.add(paramValues.toString());
                String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                if (!parDescrVal.contains("%")) {
                    try {
                        parDescrVal = encoder.decodeFromURL(parDescrVal);
                    } catch (EncodingException e) {
                        logger.debug(e.getCause(), e);
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
                logger.debug("Filters DATE RANGE ERRORS ", e);
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
        if (objParameter.getDefaultValues() != null && objParameter.getDefaultValues().size() > 0 && objParameter.getDefaultValues().get(0).getValue() != null) {
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
                fieldsToBeRemoved.forEach(f -> {
                    e.remove(f);
                });
            });
        }
    }
    resultAsMap.put("filterStatus", parametersArrayList);
    resultAsMap.put("isReadyForExecution", bmop.isReadyForExecution(parameters));
    logger.debug("OUT");
    return Response.ok(resultAsMap).build();
}
}

