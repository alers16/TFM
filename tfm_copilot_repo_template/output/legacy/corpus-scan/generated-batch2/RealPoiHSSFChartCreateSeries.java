// @caseId POI_H_S_S_F_CHART_CREATE_SERIES
// @origin poi
// @project poi
// @file HSSFChart.java
// @method createSeries()
// @license [PENDIENTE]
// @sonarCCBefore 46
// @sonarCCAfter 44   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiHSSFChartCreateSeries {
    public HSSFSeries createSeries() throws Exception {
        ArrayList<RecordBase> seriesTemplate = new ArrayList<>();
        boolean seriesTemplateFilled = false;
        int idx = 0;
        int deep = 0;
        int chartRecordIdx = -1;
        int chartDeep = -1;
        int lastSeriesDeep = -1;
        int endSeriesRecordIdx = -1;
        int seriesIdx = 0;
        final List<RecordBase> records = sheet.getSheet().getRecords();
        /* store first series as template and find last series index */
        for (final RecordBase record : records) {
            idx++;
            if (record instanceof BeginRecord) {
                deep++;
            } else if (record instanceof EndRecord) {
                deep--;
                if (lastSeriesDeep == deep) {
                    lastSeriesDeep = -1;
                    endSeriesRecordIdx = idx;
                    if (!seriesTemplateFilled) {
                        seriesTemplate.add(record);
                        seriesTemplateFilled = true;
                    }
                }
                if (chartDeep == deep) {
                    break;
                }
            }
            if (record instanceof ChartRecord) {
                if (record == chartRecord) {
                    chartRecordIdx = idx;
                    chartDeep = deep;
                }
            } else if (record instanceof SeriesRecord) {
                if (chartRecordIdx != -1) {
                    seriesIdx++;
                    lastSeriesDeep = deep;
                }
            }
            if (lastSeriesDeep != -1 && !seriesTemplateFilled) {
                seriesTemplate.add(record);
            }
        }
        /* check if a series was found */
        if (endSeriesRecordIdx == -1) {
            return null;
        }
        /* next index in the records list where the new series can be inserted */
        idx = endSeriesRecordIdx + 1;
        HSSFSeries newSeries = null;
        /* duplicate record of the template series */
        ArrayList<RecordBase> clonedRecords = new ArrayList<>();
        for (final RecordBase record : seriesTemplate) {
            Record newRecord = null;
            if (record instanceof BeginRecord) {
                newRecord = new BeginRecord();
            } else if (record instanceof EndRecord) {
                newRecord = new EndRecord();
            } else if (record instanceof SeriesRecord) {
                SeriesRecord seriesRecord = ((SeriesRecord) record).copy();
                newSeries = new HSSFSeries(seriesRecord);
                newRecord = seriesRecord;
            } else if (record instanceof LinkedDataRecord) {
                LinkedDataRecord linkedDataRecord = ((LinkedDataRecord) record).copy();
                if (newSeries != null) {
                    newSeries.insertData(linkedDataRecord);
                }
                newRecord = linkedDataRecord;
            } else if (record instanceof DataFormatRecord) {
                DataFormatRecord dataFormatRecord = ((DataFormatRecord) record).copy();
                dataFormatRecord.setSeriesIndex((short) seriesIdx);
                dataFormatRecord.setSeriesNumber((short) seriesIdx);
                newRecord = dataFormatRecord;
            } else if (record instanceof SeriesTextRecord) {
                SeriesTextRecord seriesTextRecord = ((SeriesTextRecord) record).copy();
                if (newSeries != null) {
                    newSeries.setSeriesTitleText(seriesTextRecord);
                }
                newRecord = seriesTextRecord;
            } else if (record instanceof Record) {
                newRecord = ((Record) record).copy();
            }
            if (newRecord != null) {
                clonedRecords.add(newRecord);
            }
        }
        /* check if a user model series object was created */
        if (newSeries == null) {
            return null;
        }
        /* transfer series to record list */
        for (final RecordBase record : clonedRecords) {
            records.add(idx++, record);
        }
        return newSeries;
    }
}
