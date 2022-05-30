package csv;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println(t.getClass());
        return null;
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

    }

    @Override
    public boolean isNumericColumn() {
        try {
            for(String s : values)
                Double.parseDouble(s);
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
        int cnt = 0;
        for(String s : values)
        {
            try {
                Double.parseDouble(s);
                cnt++;
            } catch (NumberFormatException e) {

            }
        }
        return cnt;
    }

    @Override
    public double getNumericMin() {
        double ret = 10000000;
        for(String s : values)
        {
            try {
                double t = Double.parseDouble(s);
                if(ret > t)
                    ret = t;
            } catch (NumberFormatException e) {

            }
        }
        return ret;
    }

    @Override
    public double getNumericMax() {
        double ret = 0;
        for(String s : values)
        {
            try {
                double t = Double.parseDouble(s);
                if(ret < t)
                    ret = t;
            } catch (NumberFormatException e) {

            }
        }
        return ret;
    }

    @Override
    public double getMean() {
        double sum = 0;
        for(String s : values)
        {
            try {
                double t = Double.parseDouble(s);
                sum += t;
            } catch (NumberFormatException e) {

            }
        }
        return sum / values.size();
    }

    @Override
    public double getStd() {
        return 0;
    }

    @Override
    public double getQ1() {
        return 0;
    }

    @Override
    public double getMedian() {
        return 0;
    }

    @Override
    public double getQ3() {
        return 0;
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
}
