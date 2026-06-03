public class POI_INTERNAL_SHEET_VISIT_CONTAINED_RECORDS {
public void visitContainedRecords(RecordVisitor rv, int offset) {
    PositionTrackingVisitor ptv = new PositionTrackingVisitor(rv, offset);
    boolean haveSerializedIndex = false;
    for (int k = 0; k < _records.size(); k++) {
        RecordBase recordBase = _records.get(k);
        if (recordBase instanceof RecordAggregate) {
            RecordAggregate agg = (RecordAggregate) recordBase;
            agg.visitContainedRecords(ptv);
        } else if (recordBase instanceof Record) {
            ptv.visitRecord((Record) recordBase);
        }
        // If the BOF record was just serialized then add the IndexRecord
        if (recordBase instanceof BOFRecord) {
            if (!haveSerializedIndex) {
                haveSerializedIndex = true;
                // Add an optional UncalcedRecord. However, we should add
                //  it in only the once, after the sheet's own BOFRecord.
                // If there are diagrams, they have their own BOFRecords,
                //  and one shouldn't go in after that!
                if (_isUncalced) {
                    ptv.visitRecord(new UncalcedRecord());
                }
                //Can there be more than one BOF for a sheet? If not then we can
                //remove this guard. So be safe it is left here.
                if (_rowsAggregate != null) {
                    // find forward distance to first RowRecord
                    int initRecsSize = getSizeOfInitialSheetRecords(k);
                    int currentPos = ptv.getPosition();
                    ptv.visitRecord(_rowsAggregate.createIndexRecord(currentPos, initRecsSize));
                }
            }
        }
    }
}
}

