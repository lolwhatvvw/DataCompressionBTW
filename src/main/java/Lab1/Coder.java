package Lab1;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static Lab1.Huffman.*;
import static Lab1.MTF.mtfEncode;
import static Lab1.NBWT.BWT;
import static Lab1.NBWT.bwt;

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

        Path p = Paths.get(args[0]);

        StringBuilder content = new StringBuilder();

        try(FileInputStream reader = new FileInputStream(String.valueOf(p)))
        {
            int c;
            while((c= reader.read())!=-1){
                content.append((char) c);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        BWT bwt = bwt(content.toString());

        List<Integer> ls = new ArrayList<>(mtfEncode(bwt.res));

        TreeMap<Integer, Integer> frequancies = frequancy(ls);
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        for(Integer c: frequancies.keySet()){
            treeNodes.add(new TreeNode(c, frequancies.get(c)));
        }

        TreeNode tree = huffman(treeNodes);

        TreeMap<Integer, String> codes = new TreeMap<>();
        for(Integer c: frequancies.keySet()){
            codes.put(c, tree.getCode(c, ""));
        }
        String encoded = encode(codes, ls);



        File file = new File(args[1]);

        saveToFile(file, bwt.index, frequancies, encoded);
    }
}
