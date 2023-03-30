package wyd.test.safe;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetTestData {
    static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        int length = ALPHA.length();
        int count = 200;
        List<String> list = new ArrayList<>(length * count);
        for (int i = 0; i < length; i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }
        // 打乱list顺序
        Collections.shuffle(list);
        for (int i = 0; i < length; i++) {
            try(PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("tmp/" + (i+1) + ".txt" ))) ){
                String collect = String.join("\n", list.subList(i * count, (i + 1) * count));
                out.print(collect);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
