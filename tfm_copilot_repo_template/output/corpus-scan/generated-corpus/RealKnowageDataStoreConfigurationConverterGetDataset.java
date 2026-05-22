// @caseId KNOWAGE_DATA_STORE_CONFIGURATION_CONVERTER_GET_DATASET
// @origin knowage
// @project knowage
// @file DataStoreConfigurationConverter.java
// @method getDataset()
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class KnowageDataStoreConfigurationConverterGetDataset {
    /*
    	 * (non-Javadoc)
    	 *
    	 * @see it.eng.spagobi.tools.dataset.metasql.query.item.IDataStoreConfiguration#getDataset()
    	 */
    @Override
    public IDataSet getDataset() {
        if (jsonConfiguration != null)
            if (this.dataSet == null)
                return DAOFactory.getDataSetDAO().loadDataSetByLabel(jsonConfiguration.getDatasetLabel());
        return this.dataSet;
    }
}
