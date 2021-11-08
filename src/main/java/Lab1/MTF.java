package Lab1;

import java.util.LinkedList;
import java.util.List;

public class MTF {
    public static List<Integer> mtfEncode(String msg){
        char[] table = new char[256];
        for (int i = 0; i < 256; i++){
            table[i] = (char) i;
        }

        List<Integer> output = new LinkedList<>();
        StringBuilder s = new StringBuilder(new String(table));
        for(char c : msg.toCharArray()){
            int idx = s.indexOf("" + c);
            output.add(idx);
            s.deleteCharAt(idx).insert(0, c);
        }
        return output;
    }

    public static String unmtf(List<Integer> idxs){
        char[] table = new char[256];
        for (int i = 0; i < 256; i++){
            table[i] = (char) i;
        }

        StringBuilder output = new StringBuilder();
        StringBuilder s = new StringBuilder(new String(table));
        for(int idx : idxs){
            char c = s.charAt(idx);
            output.append(c);
            s.deleteCharAt(idx).insert(0, c);
        }
        return output.toString();
    }
}
