package electricMeters;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class ProfileParser {
    
    public static JSONObject readDataFromFile(File file)
    {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath(), Charset.forName("windows-1251"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        List<String> header = lines.subList(0, 3);
        List<String> restOfLines = lines.subList(6, lines.size());
        int beginIndex = header.get(0).indexOf('№') + 1;
        int profileNum = Integer.parseInt(header.get(0).substring(beginIndex, beginIndex+1));
        int meterId = Integer.parseInt(header.get(1).split("№")[1].split(",")[0].trim());
        String consumer = header.get(1).substring(header.get(1).indexOf('-') + 1).trim();
        beginIndex = header.get(2).indexOf(' ') + 1;
        String date = header.get(2).substring(beginIndex, beginIndex + 8);
        String time = header.get(2).substring(header.get(2).indexOf(' ') + 10);
        
        JSONObject json = new JSONObject()
                .put("ProfileNum", profileNum)
                .put("MeterId", meterId)
                .put("Consumer", consumer)
                .put("Date", date)
                .put("Time", time);
        
        return json;
        //DbHandler.getInstance().insert(json, "ProfileEMInfo");
        
//        using (var connection = new SqliteConnection("FileName=C:\\Users\\Yulya\\Desktop\\diplom\\mysql\\ver1.db"))
//        {
//            connection.Open();
//
//            string insertEMQuery = @"INSERT INTO ProfileEMInfo (ProfileNum, MeterId, Consumer, Date, Time)
//            VALUES (@ProfileNum, @MeterId, @Consumer, @Date, @Time)";
//            SqliteCommand insertEMCommand = new SqliteCommand(insertEMQuery, connection);
//
//            insertEMCommand.Parameters.AddWithValue("@ProfileNum", profileNum);
//            insertEMCommand.Parameters.AddWithValue("@MeterId", meterId);
//            insertEMCommand.Parameters.AddWithValue("@Consumer", consumer);
//            insertEMCommand.Parameters.AddWithValue("@Date", date);
//            insertEMCommand.Parameters.AddWithValue("@Time", time);
//
//            insertEMCommand.ExecuteNonQuery();
//
//            //connection.Close();
//
//            // Получаем ID последней вставленной записи
//            SqliteCommand selectLastInsertRowIdCommand = new SqliteCommand("SELECT last_insert_rowid()", connection);
//            long profileEMInfo = (long)selectLastInsertRowIdCommand.ExecuteScalar();
//            int number = 0;
//
//            //int profileEMInfo = Convert.ToInt32(insertEMCommand.ExecuteScalar());
//
//            foreach (string line in restOfLines)
//            {
//                string[] fields = line.Split("\t");
//                    /*string insertQuery = @"INSERT INTO ProfilesEM (date, time, aPos, aNeg, rPos, rNeg)
//                                        VALUES (@date, @time, @aPos, @aNeg, @rPos, @rNeg)";*/
//                string insertQuery = @"INSERT INTO ProfilesEM (date, time, aPos, aNeg, rPos, rNeg, profileEMInfo)
//                VALUES (@date, @time, @aPos, @aNeg, @rPos, @rNeg, @profileEMInfo)";
//                SqliteCommand insertCommand = new SqliteCommand(insertQuery, connection);
//
//                insertCommand.Parameters.AddWithValue("@date", fields[0]);
//                insertCommand.Parameters.AddWithValue("@time", fields[1]);
//                insertCommand.Parameters.AddWithValue("@aPos", ConvertTo(fields[2]));
//                insertCommand.Parameters.AddWithValue("@aNeg", fields[3]);
//                insertCommand.Parameters.AddWithValue("@rPos", fields[4]);
//                insertCommand.Parameters.AddWithValue("@rNeg", fields[5]);
//                insertCommand.Parameters.AddWithValue("@profileEMInfo", profileEMInfo);
//                number = insertCommand.ExecuteNonQuery();
//            }
//            connection.Close();
//        }
        
        
    }
    
}
