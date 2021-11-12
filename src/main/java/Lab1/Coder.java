package Lab1;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Lab1.Huffman.*;
import static Lab1.MTF.mtfEncode;
import static Lab1.NBWT.BWT;
import static Lab1.NBWT.bwt;
import static java.lang.Math.log;

public class Coder {

    private static void saveToFile(File output, int index, TreeMap<Integer, Integer> frequencies, String bits) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(output));
            os.writeInt(index);
            os.writeInt(frequencies.size());
            for (Integer character: frequencies.keySet()) {
                os.writeInt(character);
                os.writeInt(frequencies.get(character));
            }
            int compressedSizeBits = bits.length();
            BitArray bitArray = new BitArray(compressedSizeBits);
            for (int i = 0; i < bits.length(); i++) {
                bitArray.set(i, bits.charAt(i) != '0' ? 1 : 0);
            }

            os.writeInt(compressedSizeBits);
            os.write(bitArray.bytes, 0, bitArray.getSizeInBytes());
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<Byte, Double> calc_letter(String s){
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        TreeMap<Byte, Double>  freqMap = new TreeMap<>();
        for(byte i: b){
            Double count = freqMap.get(i);
            freqMap.put(i, count != null? count +1 : 1);}
        return freqMap;
    }

    public static TreeMap<Byte, TreeMap<Byte, Double>> calc_letter2(String s){
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        TreeMap<Byte, TreeMap<Byte, Double>> res = new TreeMap<>();
        boolean bool = false;
        byte prev = b[0];
        for (byte i : b){
            if (!bool){
                bool = true;
                continue;
            }
            TreeMap<Byte, Double> tm;
            if (res.containsKey(prev)){
                tm = res.get(prev);
                tm.put(i, tm.get(i)!= null? tm.get(i) + 1 : 1);
            }
            else{
                tm = new TreeMap<>();
                tm.put(i, 1D);
            }
            res.put(prev, tm);
            prev = i;
        }
        return res;
    }

    public static HashMap<Map<Byte, Byte>, Double> calc_2l(String s){
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        HashMap<Map<Byte, Byte>, Double> tm = new HashMap<>();
        for(int i = 1 ; i< b.length; i++){
            int finalI = i;
            Map <Byte, Byte> tmp = new HashMap<>() {{
                put(b[finalI -1],b[finalI]);
            }};
            if ( tm.containsKey(tmp)){
                tm.put(tmp, tm.get(tmp) + 1);
            } else {
                tm.put(tmp, 1D);
            }
        }
        return tm;
    }

    public static HashMap<Map<Byte, Byte>, HashMap<Byte, Double>> calc_2cnt (String s){
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        HashMap<Map<Byte, Byte>, HashMap<Byte, Double>> tm = new HashMap<>();
        for (int i = 2; i < b.length; i++){
            int finalI = i;
            Map <Byte, Byte> pr = new HashMap<>() {{
                put(b[finalI -2],b[finalI -1]);
            }

            };
            HashMap<Byte, Double> mp ;

            if (tm.containsKey(pr)) {
                mp = tm.get(pr);
                if (mp.containsKey(b[i])){
                    mp.put(b[i], mp.get(b[i]) + 1);
                } else {
                    mp.put(b[i], 1D);
                }
            } else {
                mp = new HashMap<>();
                mp.put(b[i], 1D);
            }
            tm.put(pr, mp);
        }
        return tm;
    }

    public static double entropy_HX(String s){
        TreeMap<Byte, Double> letter1cnt = calc_letter(s);
        double entropy_HX = 0;
        for(Map.Entry<Byte, Double> mp : letter1cnt.entrySet()){
            entropy_HX -= mp.getValue()/s.length() * log(mp.getValue()/s.length()) / log(2);
        }
        return entropy_HX;
    }

    public static double entropy_HIH(String s){
        TreeMap<Byte, Double> letter1cnt = calc_letter(s);
        TreeMap<Byte, TreeMap<Byte, Double>> letter2cnt = calc_letter2(s);
        double entropy_HIH = 0;
        for (Map.Entry<Byte, Double> e : letter1cnt.entrySet()){
            double sum = 0;
            double py = e.getValue() / s.length();
            for (Map.Entry<Byte, Double> e2 : letter2cnt.get(e.getKey()).entrySet()){
                double pxy = (double) e2.getValue() / e.getValue();
                sum += pxy * Math.log(pxy) / Math.log(2);
            }
            entropy_HIH -= py * sum;
        }
        return entropy_HIH;
    }

    public static double entropy_hXIXX(String s){
        HashMap<Map<Byte, Byte>, HashMap<Byte, Double>> tmp = calc_2cnt(s);
        HashMap<Map<Byte, Byte>, Double> tp = calc_2l(s);

        double entropy_HXIXX = 0;
        for (Map.Entry<Map<Byte, Byte>, Double> e: tp.entrySet()){
            double sum = 0;
            double py =  e.getValue() / s.length() ;
            for (Map.Entry<Byte, Double> ew: tmp.get(e.getKey()).entrySet()){
                double pxy = ew.getValue()/e.getValue();
                sum+= pxy * log(pxy) / log(2);
            }
            entropy_HXIXX -= py * sum;
        }
        return entropy_HXIXX;
    }


    public static void main(String[] args) {
        Path p = Paths.get("C:\\Users\\vovkv\\Desktop\\BWT\\book2");

        StringBuilder content = new StringBuilder();

        try (FileInputStream reader = new FileInputStream(String.valueOf(p))) {
            int c;
            while ((c = reader.read()) != -1) {
                content.append((char) c);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(entropy_HX(content.toString()));
        System.out.println(entropy_HIH(content.toString()));
        System.out.println(entropy_hXIXX(content.toString()));


    }
}
