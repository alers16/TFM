// @caseId KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_GET_DOCUMENT_EXECUTION_FILTERS
// @origin knowage
// @project knowage
// @file DocumentExecutionResource.java
// @method getDocumentExecutionFilters(HttpServletRequest)
// @license [PENDIENTE]
// @sonarCCBefore 228
// @sonarCCAfter 220   (estimado; CC delta=-8)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class KnowageDocumentExecutionResourceGetDocumentExecutionFilters {
    /**
     * @return { filterStatus: [{ title: 'Provincia', urlName: 'provincia', type: 'list', lista:[[k,v],[k,v], [k,v]] }, { title: 'Comune', urlName: 'comune', type:
     *         'list', lista:[], dependsOn: 'provincia' }, { title: 'Free Search', type: 'manual', urlName: 'freesearch' }], isReadyForExecution: true, errors: [
     *         'role missing', 'operation not allowed' ] }
     * @throws EMFUserError
     * @throws JSONException
     * @throws IOException
     * @throws EncodingException
     */
    @POST
    @Path("/filters")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getDocumentExecutionFilters(@Context HttpServletRequest req) throws EMFUserError, IOException, JSONException, EncodingException {
        LOGGER.debug("IN");
        JSONObject requestVal = RestUtilities.readBodyAsJSONObject(req);
        // decode requestVal parameters
        JSONObject requestValParams = requestVal.getJSONObject("parameters");
        if (requestValParams != null && requestValParams.length() > 0) {
            requestVal.put("parameters", decodeRequestParameters(requestValParams));
        }
        String label = requestVal.getString("label");
        String role = requestVal.getString("role");
        JSONObject jsonCrossParameters = requestVal.getJSONObject("parameters");
        Map<String, Object> resultAsMap = new LinkedHashMap<>();
        Map<String, JSONObject> sessionParametersMap = new HashMap<>();
        if (("true").equals(SingletonConfig.getInstance().getConfigValue("SPAGOBI.SESSION_PARAMETERS_MANAGER.enabled"))) {
            sessionParametersMap = getSessionParameters(requestVal);
        }
        // keep track of par coming from cross to get descriptions from admissible values
        List<String> parsFromCross = new ArrayList<>();
        IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();
        BIObject biObject = DriversRuntimeLoaderFactory.getDriversRuntimeLoader().loadBIObjectForExecutionByLabelAndRole(label, role);
        applyRequestParameters(biObject, jsonCrossParameters, sessionParametersMap, role, getLocale(), parsFromCross);
        final ArrayList<HashMap<String, Object>> parametersArrayList = new ArrayList<>();
        DocumentRuntime dum = new DocumentRuntime(this.getUserProfile(), getLocale());
        List<DocumentDriverRuntime> parameters = DocumentExecutionUtils.getParameters(biObject, role, req.getLocale(), null, parsFromCross, true, dum);
        ArrayList<HashMap<String, Object>> datasetParametersArrayList = new ArrayList<>();
        datasetParametersArrayList = getQbeDrivers(biObject);
        UserProfile profile = getUserProfile();
        if (!datasetParametersArrayList.isEmpty()) {
            parametersArrayList.addAll(datasetParametersArrayList);
        } else {
            for (DocumentDriverRuntime objParameter : parameters) {
                Integer paruseId = objParameter.getParameterUseId();
                ParameterUse parameterUse = parameterUseDAO.loadByUseID(paruseId);
                Map<String, Object> metadata = new LinkedHashMap<>();
                BiMap<String, String> colPlaceholder2ColName = objParameter.getColPlaceholder2ColName();
                String lovDescriptionColumnName = objParameter.getLovDescriptionColumnName();
                String lovValueColumnName = objParameter.getLovValueColumnName();
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
                    List<String> paramValueLst = new ArrayList<>();
                    List<String> paramDescrLst = new ArrayList<>();
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
                                    itemVal = encoder.decodeFromURL(itemVal.replace("+", "%2B"));
                                }
                                if (!itemDescr.contains("%")) {
                                    itemDescr = encoder.decodeFromURL(itemDescr.replace("+", "%2B"));
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
                            paramValues = encoder.decodeFromURL(((String) paramValues).replace("+", "%2B"));
                        }
                        paramValueLst.add(paramValues.toString());
                        String parDescrVal = paramDescriptionValues != null && paramDescriptionValues instanceof String ? paramDescriptionValues.toString() : paramValues.toString();
                        if (!parDescrVal.contains("%")) {
                            parDescrVal = encoder.decodeFromURL(parDescrVal.replace("+", "%2B"));
                        }
                        paramDescrLst.add(parDescrVal);
                    }
                    parameterAsMap.put("parameterValue", paramValueLst);
                    parameterAsMap.put("parameterDescription", paramDescriptionValues);
                }
                boolean showParameterLov = true;
                // Parameters NO TREE
                if ("lov".equalsIgnoreCase(parameterUse.getValueSelection())) {
                    ArrayList<HashMap<String, Object>> admissibleValues = filterNullValues(objParameter.getAdmissibleValues());
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
                    if (!DocumentExecutionUtils.SELECTION_TYPE_LOOKUP.equals(parameterUse.getSelectionType()) && !DocumentExecutionUtils.SELECTION_TYPE_TREE.equals(parameterUse.getSelectionType()) && values != null && admissibleValues != null) {
                        checkIfValuesAreAdmissible(values, admissibleValues);
                    }
                }
                // DATE RANGE DEFAULT VALUE
                if (objParameter.getParType().equals("DATE_RANGE")) {
                    try {
                        List<Map<String, Object>> defaultValues = manageDataRange(biObject, role, objParameter.getId());
                        parameterAsMap.put(PROPERTY_DATA, defaultValues);
                    } catch (SerializationException e) {
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
                    if (lstValues.isEmpty()) {
                        jsonCrossParameters.remove(objParameter.getId());
                    }
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
                    if (jsonCrossParameters.isNull(objParameter.getId()) && // && !sessionParametersMap.containsKey(objParameter.getId())) {
                    !sessionParametersMap.containsKey(sessionKey)) {
                        if (valueList != null) {
                            parameterAsMap.put("parameterValue", valueList);
                        }
                    }
                }
                // in every case fill default values!
                parameterAsMap.put("driverDefaultValue", defValue);
                LovValue maxValue = objParameter.getMaxValue();
                if (maxValue != null && maxValue.getValue() != null) {
                    parameterAsMap.put("driverMaxValue", maxValue.getValue().toString());
                }
                if (!showParameterLov) {
                    parameterAsMap.put("showOnPanel", "false");
                } else {
                    parameterAsMap.put("showOnPanel", "true");
                }
                parametersArrayList.add(parameterAsMap);
            }
        }
        /*
    		 * !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!!
    		 *
    		 * This code only retrieves descriptions of LOVs. Errors are ignored by purpose.
    		 *
    		 * You are free to show the errors on the response, if you want.
    		 *
    		 * !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!! WARNING !!!
    		 */
        try {
            DriversValidationAPI validation = new DriversValidationAPI(profile, getLocale());
            validation.getParametersErrors(biObject, role, dum);
        } catch (Exception e) {
            throw new SpagoBIRuntimeException(e);
        }
        reconcileDescriptionInLovValues(parameters, parametersArrayList);
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
        if (runDocumentExecution == null || runDocumentExecution.equalsIgnoreCase("true")) {
            resultAsMap.put("isReadyForExecution", isReadyForExecution(parameters, resultAsMap));
        } else if (runDocumentExecution.equalsIgnoreCase("false")) {
            resultAsMap.put("isReadyForExecution", false);
        } else {
            throw new SpagoBIRuntimeException("The value of configuration variable document.execution.startAutomatically is not valid, contact your administrator");
        }
        LOGGER.debug("OUT");
        return Response.ok(resultAsMap).build();
    }
}
