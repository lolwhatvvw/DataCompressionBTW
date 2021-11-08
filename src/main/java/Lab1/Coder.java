package Lab1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static Lab1.Huffman.*;
import static Lab1.MTF.*;
import static Lab1.NBWT.*;


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

    public static void main(String[] args) {

        StringBuilder content = new StringBuilder();
        try(FileInputStream reader = new FileInputStream(args[0]))
        {
            int c;
            while((c= reader.read())!=-1){
                content.append((char) c);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        NBWT.BWT bwt = bwt(content.toString());

        List<Integer> ls = new ArrayList<>(mtfEncode(bwt.res));

        System.out.println(ls.size());

        TreeMap<Integer, Integer> frequancies = frequancy(ls);
        ArrayList<Huffman.TreeNode> treeNodes = new ArrayList<>();
        for(Integer c: frequancies.keySet()){
            treeNodes.add(new Huffman.TreeNode(c, frequancies.get(c)));
        }

        Huffman.TreeNode tree = huffman(treeNodes);

        TreeMap<Integer, String> codes = new TreeMap<>();
        for(Integer c: frequancies.keySet()){
            codes.put(c, tree.getCode(c, ""));
        }
        String encoded = encode(codes, ls);

        System.out.println(encoded.length());

        ArrayList<Integer> decoded = decode(encoded, tree);

        String beforebwt = unmtf(decoded);
        String fil = ibwt(beforebwt, bwt.index);

        System.out.println(fil);

        File file = new File("C:\\Users\\vovkv\\Desktop\\BWT\\compressed.huf");

        saveToFile(file, bwt.index, frequancies, encoded );
    }

}
