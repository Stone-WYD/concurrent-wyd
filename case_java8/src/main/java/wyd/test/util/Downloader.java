package wyd.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Downloader {

    public static List<String> download() throws IOException {

        List<String> lines = new ArrayList<>();
        HttpURLConnection conn = (HttpURLConnection) new URL("https://www.baidu.com/").openConnection();
        try (BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                ){
            String line;
            while ((line = reader.readLine())!= null){
                lines.add(line);
            }
        }
        return lines;
    }
}
