package electricMeters.service;

import electricMeters.util.DateUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class ProfileParser {
    
    static JSONObject readDataFromFile(File file)
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
        int meterNumber = Integer.parseInt(header.get(1).split("№")[1].split(",")[0].trim());
        String consumer = header.get(1).substring(header.get(1).indexOf('-') + 1).trim();
        beginIndex = header.get(2).indexOf(' ') + 1;
        String date = header.get(2).substring(beginIndex, beginIndex + 8);
        String time = header.get(2).substring(header.get(2).indexOf(' ') + 10);
        
        LocalDateTime localDateTime = LocalDateTime.parse(date + " " + time, DateUtil.SHORT_DATE_TIME_FORMAT);
        String dateTime = localDateTime.format(DateUtil.DB_DATE_TIME_FORMAT);
        
        JSONObject mainJson = new JSONObject()
                .put("PROFILE_NUM", profileNum)
                .put("METER_NUMBER", meterNumber)
                .put("CONSUMER", consumer)
                .put("DATE", localDateTime.format(DateUtil.DB_DATE_FORMAT))
                .put("TIME", time)
                .put("DATE_TIME", dateTime);
        
        List<JSONObject> childs = new ArrayList<>();
        for (String line : restOfLines) {
            String[] fields = line.split("\t");
            JSONObject json = new JSONObject()
                    .put("DATE", DateUtil.convert(fields[0], DateUtil.SHORT_DATE_FORMAT, DateUtil.DB_DATE_FORMAT))
                    .put("HOUR", LocalTime.parse(fields[1].split("-")[0]).getHour())
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
