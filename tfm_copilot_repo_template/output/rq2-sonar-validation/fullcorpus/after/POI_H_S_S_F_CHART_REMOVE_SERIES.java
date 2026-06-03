public class POI_H_S_S_F_CHART_REMOVE_SERIES {
public boolean removeSeries(HSSFSeries remSeries) {
    int deep = 0;
    int chartDeep = -1;
    int lastSeriesDeep = -1;
    int seriesIdx = -1;
    boolean removeSeries = false;
    boolean chartEntered = false;
    boolean result = false;
    final List<RecordBase> records = sheet.getSheet().getRecords();
    /* store first series as template and find last series index */
    Iterator<RecordBase> iter = records.iterator();
    while (iter.hasNext()) {
        RecordBase record = iter.next();
        if (record instanceof BeginRecord) {
            deep++;
        } else if (record instanceof EndRecord) {
            deep--;
            if (lastSeriesDeep == deep) {
                lastSeriesDeep = -1;
                if (removeSeries) {
                    removeSeries = false;
                    result = true;
                    iter.remove();
                }
            }
            if (chartDeep == deep) {
                break;
            }
        }
        if (record instanceof ChartRecord) {
            if (record == chartRecord) {
                chartDeep = deep;
                chartEntered = true;
            }
        } else if (record instanceof SeriesRecord) {
            if (chartEntered) {
                if (remSeries.series == record) {
                    lastSeriesDeep = deep;
                    removeSeries = true;
                } else {
                    seriesIdx++;
                }
            }
        } else if (record instanceof DataFormatRecord && chartEntered && !removeSeries) {
            DataFormatRecord dataFormatRecord = (DataFormatRecord) record;
            dataFormatRecord.setSeriesIndex((short) seriesIdx);
            dataFormatRecord.setSeriesNumber((short) seriesIdx);
        }
        if (removeSeries) {
            iter.remove();
        }
    }
    return result;
}
}

