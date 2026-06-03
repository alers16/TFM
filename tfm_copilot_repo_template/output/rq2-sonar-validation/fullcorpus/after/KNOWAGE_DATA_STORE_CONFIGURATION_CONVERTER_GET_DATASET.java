public class KNOWAGE_DATA_STORE_CONFIGURATION_CONVERTER_GET_DATASET {
/*
    	 * (non-Javadoc)
    	 *
    	 * @see it.eng.spagobi.tools.dataset.metasql.query.item.IDataStoreConfiguration#getDataset()
    	 */
@Override
public IDataSet getDataset() {
    if (jsonConfiguration != null && this.dataSet == null) {
        return DAOFactory.getDataSetDAO().loadDataSetByLabel(jsonConfiguration.getDatasetLabel());
    }
    return this.dataSet;
}
}

