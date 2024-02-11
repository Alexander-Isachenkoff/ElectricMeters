package electricMeters;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProfileParser {
    
    public static JSONObject readDataFromFile(File file)
    {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), Charset.forName("windows-1251"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        List<String> header = lines.subList(0, 3);
        List<String> restOfLines = lines.subList(5, lines.size());
        int beginIndex = header.get(0).indexOf('№') + 1;
        int profileNum = Integer.parseInt(header.get(0).substring(beginIndex, beginIndex+1));
        int meterId = Integer.parseInt(header.get(1).split("№")[1].split(",")[0].trim());
        String consumer = header.get(1).substring(header.get(1).indexOf('-') + 1).trim();
        beginIndex = header.get(2).indexOf(' ') + 1;
        String date = header.get(2).substring(beginIndex, beginIndex + 8);
        String time = header.get(2).substring(header.get(2).indexOf(' ') + 10);
        
        JSONObject mainJson = new JSONObject()
                .put("ProfileNum", profileNum)
                .put("MeterId", meterId)
                .put("Consumer", consumer)
                .put("Date", date)
                .put("Time", time);
        
        List<JSONObject> childs = new ArrayList<>();
        for (String line : restOfLines) {
            String[] fields = line.split("\t");
            JSONObject json = new JSONObject()
                    .put("date", fields[0])
                    .put("time", fields[1])
                    .put("aPos", parseDouble(fields[2]))
                    .put("aNeg", parseDouble(fields[3]))
                    .put("rPos", parseDouble(fields[4]))
                    .put("rNeg", parseDouble(fields[5]));
            childs.add(json);
        }
        
        mainJson.put("childs", childs);
        
        return mainJson;
    }
    
    private static double parseDouble(String string) {
        return Double.parseDouble(string.replace(',', '.'));
    }
    
}
