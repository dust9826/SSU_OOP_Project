package csv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

class TableImpl implements Table{
    private List<Column> columns;
    TableImpl(List<List<String>> csvList) {
        columns = new ArrayList<>();
        for(int i = 0; i < csvList.size(); i++)
        {
            Column newColumn = new ColumnImpl(csvList.get(i));
            columns.add(newColumn);
        }
    }

    TableImpl(Table table) {
        columns = new ArrayList<>();
        for(int i = 0; i < table.getColumnCount(); i++)
        {
            Column newColumn = new ColumnImpl(table.getColumn(i));
            columns.add(newColumn);
        }
    }

    @Override
    public void print() {
        if(columns.isEmpty())
            return;
        List<Integer> widths = new ArrayList<>();
        for(int i=0; i<columns.size(); i++)
        {
           widths.add(getColumnWidth(columns.get(i)));
           printValue(columns.get(i).getHeader(), widths.get(i));
            System.out.print(" | ");
        }
        System.out.println();
        for(int i=0; i<columns.get(0).count(); i++)
        {
            for(int j=0; j<columns.size(); j++)
            {
                printValue(columns.get(j).getValue(i), widths.get(j));
                System.out.print(" | ");
            }
            System.out.println();
        }
    }

    @Override
    public Table getStats() {
        List<List<String>> csvList = new ArrayList<>();
        csvList.add(new ArrayList<>());
        csvList.get(0).add(" ");
        csvList.get(0).add("count");
        csvList.get(0).add("mean");
        csvList.get(0).add("std");
        csvList.get(0).add("min");
        csvList.get(0).add("25%");
        csvList.get(0).add("50%");
        csvList.get(0).add("75%");
        csvList.get(0).add("max");
        for(Column colmn : columns) {
            if(colmn.getNumericCount() == 0)
                continue;
            int idx = csvList.size();
            csvList.add(new ArrayList<>());
            csvList.get(idx).add(colmn.getHeader());
            csvList.get(idx).add(String.valueOf(colmn.getNumericCount()));
            csvList.get(idx).add(String.valueOf(colmn.getMean()));
            csvList.get(idx).add(String.valueOf(colmn.getStd()));
            csvList.get(idx).add(String.valueOf(colmn.getNumericMin()));
            csvList.get(idx).add(String.valueOf(colmn.getQ1()));
            csvList.get(idx).add(String.valueOf(colmn.getMedian()));
            csvList.get(idx).add(String.valueOf(colmn.getQ3()));
            csvList.get(idx).add(String.valueOf(colmn.getNumericMax()));
        }
        return new TableImpl(csvList);
    }

    @Override
    public Table head() { return selectRows(0, 5); }

    @Override
    public Table head(int lineCount) { return selectRows(0, lineCount); }

    @Override
    public Table tail() { return selectRows(columns.get(0).count() - 5, columns.get(0).count()); }

    @Override
    public Table tail(int lineCount) {
        return selectRows(columns.get(0).count() - lineCount, columns.get(0).count());
    }
    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        List<List<String>> csvList = new ArrayList<>();

        for(int i=0; i<columns.size(); i++)
        {
            List<String> newColList = new ArrayList<>();
            Column column = columns.get(i);
            newColList.add(column.getHeader());
            for(int j=beginIndex; j<endIndex; j++)
            {
                newColList.add(column.getValue(j));
            }
            csvList.add(newColList);
        }
        return new TableImpl(csvList);
    }

    @Override
    public Table selectRowsAt(int... indices) {
        List<List<String>> csvList = new ArrayList<>();

        for(int i=0; i<columns.size(); i++)
        {
            List<String> newColList = new ArrayList<>();
            Column column = columns.get(i);
            newColList.add(column.getHeader());
            for(int j : indices)
            {
                newColList.add(column.getValue(j));
            }
            csvList.add(newColList);
        }
        return new TableImpl(csvList);
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        List<List<String>> csvList = new ArrayList<>();

        for(int i=beginIndex; i<endIndex; i++)
        {
            List<String> newColList = new ArrayList<>();
            Column column = columns.get(i);
            newColList.add(column.getHeader());
            for(int j=0; j<column.count(); j++)
            {
                newColList.add(column.getValue(j));
            }
            csvList.add(newColList);
        }
        return new TableImpl(csvList);
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        List<List<String>> csvList = new ArrayList<>();

        for(int i : indices)
        {
            List<String> newColList = new ArrayList<>();
            Column column = columns.get(i);
            newColList.add(column.getHeader());
            for(int j=0; j<column.count(); j++)
            {
                newColList.add(column.getValue(j));
            }
            csvList.add(newColList);
        }
        return new TableImpl(csvList);
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        List<List<String>> csvList = new ArrayList<>();
        int index = 0;
        for(int i=0; i<columns.size(); i++)
        {
            List<String> newColList = new ArrayList<>();
            newColList.add(columns.get(i).getHeader());
            csvList.add(newColList);
            if(columns.get(i).getHeader().equals(columnName))
                index = i;
        }
        for(int i=0; i<columns.get(0).count();i++) {
            boolean isPass = false;
            try {
                String s = columns.get(index).getValue(i);
                if(s.equals(""))
                    s = null;
                if(predicate.test((T)s))
                    isPass = true;
            } catch (Exception e) { }
            try {
                Double d = columns.get(index).getValue(i, Double.class);;
                if(predicate.test((T)d))
                    isPass = true;
            } catch (Exception e) {}
            try {
                Integer d = columns.get(index).getValue(i, Integer.class);;
                if(predicate.test((T)d))
                    isPass = true;
            } catch (Exception e) { }
            if(!isPass)
                continue;
            for (int j=0; j<columns.size(); j++)
                csvList.get(j).add(columns.get(j).getValue(i));
        }
        return new TableImpl(csvList);
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        Column column = columns.get(byIndexOfColumn);
        int len = column.count();
        for(int i=0; i<len; i++)
            for(int j=0; j<len - i - 1; j++)
                if(compare(column.getValue(j), column.getValue(j+1), isAscending, isNullFirst))
                    swap(j, j+1);
        return this;
    }

    @Override
    public Table shuffle() {
        int len = columns.get(0).count();
        for(int i=0; i<len; i++){
            int a = (int)(Math.random() * len);
            int b = (int)(Math.random() * len);
            swap(a, b);
        }
        return this;
    }

    @Override
    public int getRowCount() {
        return columns.get(0).count();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Column getColumn(int index) {
        return columns.get(index);
    }

    @Override
    public Column getColumn(String name) {
        for (Column column : columns)
            if(column.getHeader().equals(name))
                return column;
        return null;
    }

    @Override
    public boolean fillNullWithMean() {
        boolean isChanged = false;
        for(Column column : columns)
        {
            if(column.fillNullWithMean())
                isChanged = true;
        }
        return isChanged;
    }

    @Override
    public boolean fillNullWithZero() {
        boolean isChanged = false;
        for(Column column : columns)
        {
            if(column.fillNullWithZero())
                isChanged = true;
        }
        return isChanged;
    }

    @Override
    public boolean standardize() {
        boolean isChanged = false;
        for(Column column : columns)
        {
            if(!column.standardize())
                continue;
            isChanged = true;
        }
        return isChanged;
    }

    @Override
    public boolean normalize() {
        boolean isChanged = false;
        for(Column column : columns)
        {
            if(!column.normalize())
                continue;
            isChanged = true;
        }
        return isChanged;
    }

    @Override
    public boolean factorize() {
        boolean isChanged = false;
        for(Column column : columns)
        {
            if(!column.factorize())
                continue;
            isChanged = true;
        }
        return isChanged;
    }

    @Override
    public String toString() {
        String ret = "<" + super.toString() + ">\n";
        ret += "RangeIndex: " + columns.get(0).count() + " entries, 0 to " + (columns.get(0).count() - 1) + "\n";
        ret += "Data columns (total " + columns.size() + " columns):\n";
        ret += String.format(" # |%11s |%14s |%s\n", "Column", "Non-Null Count", "Dtype");
        int[] dtypes = new int[3];
        String[] dtype2S = {"int", "double", "String"};
        for(int i = 0; i < columns.size(); i++)
        {
            String header = columns.get(i).getHeader();
            int nnc = columns.get(i).count() - (int)columns.get(i).getNullCount();
            boolean b = columns.get(i).isNumericColumn();
            String isNull = columns.get(i).getNullCount() != columns.get(0).count() ? "non-null" : "null";
            int dtype = 0;
            for(int j=0; j<columns.get(i).count(); j++){
                String value = columns.get(i).getValue(j);
                if(value.equals("")) continue;
                try {
                    Double.parseDouble(value);
                    try {
                        Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        if(dtype < 1)
                            dtype = 1;
                    }
                } catch (NumberFormatException e) {
                    if(header.equals("Age"))
                        System.out.println(value);
                    dtype = 2;
                }
            }
            dtypes[dtype]++;
            ret += String.format("%2d |%11s |%5d %8s |%s\n", i, header, nnc, isNull, dtype2S[dtype]);
        }
        ret += String.format("dtypes: double(%d), int(%d), String(%d)\n", dtypes[0], dtypes[1], dtypes[2]);
        return ret;
    }

    int getColumnWidth(Column column) {
        int width = column.getHeader().length();
        for(int i=0; i<column.count(); i++) {
            String t = column.getValue(i);
            if(t.equals(""))
                t = "null";
            if(width < t.length())
                width = t.length();
        }
        return width;
    }

    void printValue(String value, int width) {
        if(value.equals(""))
        {
            value = "null";
            width = width > 4 ? width : 4;
        }
        for(int i=value.length(); i<width; i++)
            System.out.print(" ");
        System.out.print(value);
    }

    boolean compare(String a, String b, boolean isAscending, boolean isNullFirst) {
        if(a.equals(""))
            return !isNullFirst;
        if(b.equals(""))
            return isNullFirst;
        try {
            Double ad = Double.parseDouble(a);
            Double bd = Double.parseDouble(b);
            return (ad > bd) == isAscending;
        } catch (NumberFormatException e) {
            return (a.compareTo(b) > 0) == isAscending;
        }
    }

    void swap(int a, int b) {
        for(Column column : columns) {
            String temp = column.getValue(a);
            column.setValue(a, column.getValue(b));
            column.setValue(b, temp);
        }
    }
}
