package csv;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        List<List<String>> csvList = new ArrayList<List<String>>();
        BufferedReader br = null;
        String line = "";
        Table table = null;
        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
                List<String> aLine = new ArrayList<String>();
                String[] lineArr = line.split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
                aLine = Arrays.asList(lineArr);
                csvList.add(aLine);
            }

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

            table = new TableImpl(csvCol);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close(); // 사용 후 BufferedReader를 닫아준다.
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return table;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        Table newTable = new TableImpl(table);
        return newTable.sort(byIndexOfColumn, isAscending, isNullFirst);
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        Table newTable = new TableImpl(table);
        return newTable.shuffle();
    }
}
