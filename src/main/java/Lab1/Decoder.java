package Lab1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;

import static Lab1.Huffman.*;
import static Lab1.MTF.unmtf;
import static Lab1.NBWT.ibwt;

public class Decoder {
    private static Integer loadFromFile(File input, TreeMap<Integer, Integer> frequencies, StringBuilder bits) {
        try {
            DataInputStream os = new DataInputStream(new FileInputStream(input));
            Integer index = os.readInt();
            int frequencyTableSize = os.readInt();
            for (int i = 0; i < frequencyTableSize; i++) {
                frequencies.put(os.readInt(), os.readInt());
            }
            int dataSizeBits = os.readInt();
            BitArray bitArray = new BitArray(dataSizeBits);
            os.read(bitArray.bytes, 0, bitArray.getSizeInBytes());
            os.close();

            for (int i = 0; i < bitArray.size; i++) {
                bits.append(bitArray.get(i) != 0 ? "1" : 0);
            }

            return index;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        TreeMap<Integer, Integer> frequencies = new TreeMap<>();

        StringBuilder encoded = new StringBuilder();

        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        File file = new File("C:\\Users\\vovkv\\Desktop\\BWT\\compressed.huf");

        Integer index = loadFromFile(file, frequencies, encoded);
        System.out.println(index);

        for(Integer c: frequencies.keySet()) {
            treeNodes.add(new TreeNode(c, frequencies.get(c)));
        }

        Huffman.TreeNode tree = huffman(treeNodes);

        // декодирование обратно исходной информации из сжатой
        ArrayList<Integer> decoded3 = decode(encoded.toString(), tree);

        String decode = unmtf(decoded3);

        String ib = ibwt(decode, index);

        System.out.println(ib);

        try(FileOutputStream writer = new FileOutputStream("C:\\Users\\vovkv\\Desktop\\BWT\\decompressed.txt", false))
        {
            byte[] toWrite = ib.getBytes(StandardCharsets.ISO_8859_1);
            writer.write(toWrite);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
