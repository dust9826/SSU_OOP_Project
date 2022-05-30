package csv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class TableImpl implements Table{
    private List<Column> columns;
    TableImpl(List<List<String>> csvList) {
        int cSize = csvList.get(0).size();
        int rSize = csvList.size();

        List<List<String>> csvCol = new ArrayList<>();
        for(int i=0; i<cSize; i++)
            csvCol.add(new ArrayList<>());
        for(int i=0; i<rSize; i++)
        {
            List<String> rowData = csvList.get(i);
            String temp = "";
            int cIdx = 0;
            for(int j=0; j<rowData.size(); j++)
            {
                temp += rowData.get(j);
                if(!temp.equals("") && temp.charAt(0) == '"')
                {
                    int cnt, length = temp.length();
                    for(cnt = 0;; cnt++)
                        if(temp.charAt(length - cnt - 1) != '"')
                            break;
                    if(cnt % 2 == 0)
                    {
                        temp += ",";
                        continue;
                    }
                    String t = "";
                    for(int k=1; k<temp.length()-1; k++)
                    {
                        if(temp.charAt(k) == '"')
                            k++;
                        t += temp.charAt(k);
                    }
                    temp = t;
                }
                csvCol.get(cIdx++).add(temp);
                temp = "";
            }
            while(cIdx < cSize)
                csvCol.get(cIdx++).add("");
        }

        columns = new ArrayList<>();
        for(int i = 0; i < cSize; i++)
        {
            Column newColumn = new ColumnImpl(csvCol.get(i));
            columns.add(newColumn);
        }
    }

    @Override
    public void print() {

    }

    @Override
    public Table getStats() {
        return null;
    }

    @Override
    public Table head() {
        return null;
    }

    @Override
    public Table head(int lineCount) {
        return null;
    }

    @Override
    public Table tail() {
        return null;
    }

    @Override
    public Table tail(int lineCount) {
        return null;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        return null;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        return null;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        return null;
    }

    @Override
    public Table shuffle() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Column getColumn(int index) {
        return null;
    }

    @Override
    public Column getColumn(String name) {
        return null;
    }

    @Override
    public boolean fillNullWithMean() {
        return false;
    }

    @Override
    public boolean fillNullWithZero() {
        return false;
    }

    @Override
    public boolean standardize() {
        return false;
    }

    @Override
    public boolean normalize() {
        return false;
    }

    @Override
    public boolean factorize() {
        return false;
    }

    @Override
    public String toString() {
        String ret = "<" + super.toString() + ">\n";
        ret += "RangeIndex: " + columns.get(0).count() + " entries, 0 to " + (columns.get(0).count() - 1) + "\n";
        ret += "Data columns (total " + columns.size() + " columns):\n";
        ret += String.format(" # | %12s | %14s | %7s\n", "Column", "Non-Null Count", "Dtype");
        for(int i = 0; i < columns.size(); i++)
        {
            String header = columns.get(i).getHeader();
            int nnc = columns.get(i).count() - (int)columns.get(i).getNullCount();
            boolean b = columns.get(i).isNumericColumn();
            ret += String.format("%2d | %12s | %14s | %7s %d\n", i, header, nnc, columns.get(i).getStd(), b ? 1 : 0);
        }
        return ret;
    }
}
