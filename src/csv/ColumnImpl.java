package csv;

import javax.management.InstanceNotFoundException;
import java.util.*;

class ColumnImpl implements Column{
    private String header;
    private List<String> values;

    ColumnImpl(List<String> data)
    {
        this.header = data.get(0);
        values = new ArrayList<>();
        for(int i = 0; i < data.size() - 1; i++)
            values.add(data.get(i+1));
    }

    ColumnImpl(Column column)
    {
        header = column.getHeader();
        values = new ArrayList<>();
        for(int i=0; i<column.count(); i++)
            values.add(column.getValue(i));
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public String getValue(int index) {
        return values.get(index);
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        T ret = null;
        if(t == Integer.class)
            ret = t.cast(Integer.parseInt(values.get(index)));
        else if(t == Double.class)
            ret = t.cast(Double.parseDouble(values.get(index)));
        else
            ret = t.cast(values.get(index));
        return ret;
    }

    @Override
    public void setValue(int index, String value) {
        values.set(index, value);
    }

    @Override
    public <T extends Number> void setValue(int index, T value) {
        values.set(index, value.toString());
    }

    @Override
    public int count() {
        return values.size();
    }

    @Override
    public void print() {
        int width = getWidth();
        System.out.println(header);
        for(int i=0; i<values.size(); i++)
        {
            printValue(values.get(i), width);
            System.out.println();
        }
    }

    @Override
    public boolean isNumericColumn() {
        try {
            for(String s : values) {
                if(s.equals(""))
                    continue;
                Double.parseDouble(s);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public long getNullCount() {
        int cnt = 0;
        for(String s : values)
            if(s.equals(""))
                cnt++;
        return cnt;
    }

    @Override
    public long getNumericCount() {
        return getNumericList().size();
    }

    @Override
    public double getNumericMin() {
        List<Double> array = getNumericList();
        double ret = 10000000;
        for(Double d : array)
        {
            if(ret > d)
                ret = d;
        }
        return ret;
    }

    @Override
    public double getNumericMax() {
        List<Double> array = getNumericList();
        double ret = 0;
        for(Double d : array)
        {
            if(ret < d)
                ret = d;
        }
        return ret;
    }

    @Override
    public double getMean() {
        List<Double> array = getNumericList();
        double sum = 0;
        for(Double d : array)
            sum += d;
        double mean = sum / getNumericCount();
        return Math.round(mean * 1000000) / 1000000.0;
    }

    @Override
    public double getStd() {
        List<Double> array = getNumericList();
        double sum = 0;
        double mean = getMean();
        for(Double d : array)
            sum += Math.pow((d - mean), 2);
        double std = Math.sqrt(sum / getNumericCount());
        return Math.round(std * 1000000) / 1000000.0;
    }

    @Override
    public double getQ1() {
        List<Double> array = getNumericList();
        Collections.sort(array);
        int len = array.size();
        array = array.subList(0, len / 2 + 1);
        return median(array);
    }

    @Override
    public double getMedian() {
        List<Double> array = getNumericList();
        Collections.sort(array);
        return median(array);
    }

    @Override
    public double getQ3() {
        List<Double> array = getNumericList();
        Collections.sort(array);
        int len = array.size();
        array = array.subList(len / 2, len);
        return median(array);
    }

    @Override
    public boolean fillNullWithMean() {
        boolean isChange = false;
        String mean = String.valueOf(getMean());
        if(!isNumericColumn())
            return false;
        for(int i=0; i<values.size(); i++) {
            if(values.get(i).equals("")) {
                values.set(i, mean);
                isChange = true;
            }
        }
        return isChange;
    }

    @Override
    public boolean fillNullWithZero() {
        boolean isChange = false;
        if(!isNumericColumn())
            return false;
        for(int i=0; i<values.size(); i++) {
            if(values.get(i).equals("")) {
                values.set(i, "0");
                isChange = true;
            }
        }
        return isChange;
    }

    @Override
    public boolean standardize() {
        if(!isNumericColumn())
            return false;
        double std = getStd();
        double mean = getMean();
        boolean isChange = false;
        for(int i=0; i<values.size(); i++) {
            try {
                Double d = Double.parseDouble(values.get(i));
                d = (d - mean) / std;
                d = Math.round(d * 1000000) / 1000000.0;
                String s = String.valueOf(d);
                values.set(i, s);
                isChange = true;
            } catch (NumberFormatException e) { }
        }
        return isChange;
    }

    @Override
    public boolean normalize() {
        if(!isNumericColumn())
            return false;
        double min = getNumericMin();
        double max = getNumericMax();
        boolean isChange = false;
        for(int i=0; i<values.size(); i++) {
            try {
                Double d = Double.parseDouble(values.get(i));
                d = (d - min) / (max - min);
                d = Math.round(d * 1000000) / 1000000.0;
                String s = String.valueOf(d);
                values.set(i, s);
                isChange = true;
            } catch (NumberFormatException e) { }
        }
        return isChange;
    }

    @Override
    public boolean factorize() {
        boolean isChange = false;
        if(!canFactorize())
            return false;
        String firstValue = "";
        for(int i=0; i<values.size(); i++) {
            if(values.get(i).equals(""))
                continue;
            if(firstValue.equals(""))
                firstValue = values.get(i);
            values.set(i, firstValue.equals(values.get(i)) ? "1" : "0");
            isChange = true;
        }
        return isChange;
    }

    int getWidth() {
        int width = header.length();
        for(int i=0; i<values.size(); i++) {
            String t = values.get(i);
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

    int getType(String s) {
        if(s.equals(""))
            return -1;
        try {
            Double.parseDouble(s);
            try {
                Integer.parseInt(s);
                return 0;
            } catch (NumberFormatException e) {
                return 1;
            }
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    List<Double> getNumericList() {
        List<Double> array = new ArrayList<>();
        for(String s : values)
        {
            try {
                double t = Double.parseDouble(s);
                array.add(t);
            } catch (NumberFormatException e) { }
        }
        return array;
    }

    Double median(List<Double> list) {
        int len = list.size();
        Double ret = 0.0;
        if(len % 2 == 1)
            ret = list.get(len / 2);
        else
            ret = (list.get(len / 2 - 1) + list.get(len / 2)) / 2;
        return Math.round(ret * 1000000) / 1000000.0;
    }

    boolean canFactorize() {
        int cnt = 0;
        List<String> data = new ArrayList<>();
        for(String value : values) {
            if(value.equals(""))
                continue;
            if(data.size() == 0)
                data.add(value);
            else {
                boolean isIn = false;
                for (String d : data)
                    if(d.equals(value))
                        isIn = true;
                if(!isIn)
                    data.add(value);
                if(data.size() > 2)
                    break;
            }
        }
        return data.size() == 2;
    }
}
