package APot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; //класс для работы с файлами
import java.nio.file.Paths; //клас для работы с файлами
import java.util.*; //библиотека коллекций

public class BWTV2 {
    private static final StringBuilder builder = new StringBuilder();

    public static BWT bwt(String s) {

        builder.setLength(0);
        int len = s.length(), index = 0;
        char[] chs = s.toCharArray();
        boolean[] map = new boolean[256];
        Map<Character, List<Integer>> indexMap = new HashMap<>();
        //Для каждого символа сохраним массив индексов позиций в которых он встречается
        for(int i = 0;i < len;map[chs[i]] = true,i ++) { //Если встретили уникальный символ - записали на его позиции true
            List<Integer> list = indexMap.getOrDefault(chs[i], new ArrayList<>()); //Находим нужный массив по ключу - символу,
            // иначе символ встретился в первый раз - создаем новый лист
            list.add(i); //добавляем индекс встретившегося символа в его лист
            indexMap.put(chs[i], list); //заполняем мапу символ-лист
        }
        for(int i = 0;i < 256;i ++) { //Если мы не встретили символ ни разу - то значение false и пропускаем его
            if(!map[i]) continue;
            List<Integer> list = indexMap.get((char)i); //берем для каждого символа его лист
            //сортируем индексы в этом листе
            list.sort((o1, o2) -> {
                int i1 = 1;
                for (; i1 < len && chs[(o1 + i1) % len] == chs[(o2 + i1) % len]; i1++) ;
                return chs[(o1 + i1) % len] > chs[(o2 + i1) % len] ? 1 : -1;
            });
            for(int l : list) // для каждого набора индексов (всех строчек/листов)
                if(l == 0) {
                    index = builder.length();
                    builder.append(chs[len - 1]);
                }
                else builder.append(chs[l - 1]);
        }
        return new BWT(builder.toString(), len == 0 ? -1 : index);
    }

    public static String ibwt(String s, int n) {
        if(n < 0) return "";
        builder.setLength(0);
        int len = s.length();
        char[] preArr = s.toCharArray(), nxtArr = s.toCharArray();
        Arrays.sort(nxtArr);
        Map<Character, List<Integer>> preMap = new HashMap<>(), nxtMap = new HashMap<>();
        for(int i = 0;i < len;i ++) {
            List<Integer> prelist = preMap.getOrDefault(preArr[i], new ArrayList<>()),
                    nxtlist = nxtMap.getOrDefault(nxtArr[i], new ArrayList<>());
            prelist.add(i); nxtlist.add(i);
            preMap.put(preArr[i], prelist); nxtMap.put(nxtArr[i], nxtlist);
        }
        while(len-- > 0) {
            System.out.println(len);
            char temp = nxtArr[n];
            builder.append(temp);
            n = nxtMap.get(temp).indexOf(n);
            n = preMap.get(temp).get(n);
        }
        return builder.toString();
    }
    static class BWT {
        String res; int index;
        BWT(String res, int index){ this.res = res; this.index = index; }
        public boolean equals(BWT b) {
            System.out.println(b); return this.res.equals(b.res) && this.index == b.index;
        }
        public String toString() { return "obj :" + res + ":" + index; }
    }

    public static List<Integer> mtfEncode(String msg){
        char[] table = new char[256];
        for (int i = 0; i < 256; i++){
            table[i] = (char) i;
        }

        List<Integer> output = new LinkedList<>();
        StringBuilder s = new StringBuilder(String.valueOf(table));
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
        StringBuilder s = new StringBuilder(String.valueOf(table));
        for(int idx : idxs){
            char c = s.charAt(idx);
            output.append(c);
            s.deleteCharAt(idx).insert(0, c);
        }
        return output.toString();
    }
    static class TreeNode implements Comparable<TreeNode>{
        Integer symbol;
        int nodeWeight;
        TreeNode left;
        TreeNode right;

        public TreeNode(Integer symbol, int nodeWeight){
            this.nodeWeight = nodeWeight;
            this.symbol = symbol;
        }

        public TreeNode(Integer symbol, int nodeWeight, TreeNode left, TreeNode right){
            this(symbol, nodeWeight);
            this.left = left;
            this.right = right;
        }

        public String getCode(Integer ch, String parentPath){
            if(symbol == ch){
                return parentPath;
            } else {
                if (left!= null){
                    String path = left.getCode(ch, parentPath + 0);
                    if (path != null){
                        return path;
                    }
                }
                if (right != null){
                    return right.getCode(ch, parentPath + 1);
                }
            }
            return null;
        }

        @Override
        public int compareTo(TreeNode o) {
            return o.nodeWeight - nodeWeight;
        }
    }
    public static TreeNode huffman(ArrayList<TreeNode> treeNodes){
        while(treeNodes.size() > 1){
            Collections.sort(treeNodes);
            TreeNode left = treeNodes.remove(treeNodes.size() - 1);
            TreeNode right = treeNodes.remove(treeNodes.size() - 1);

            TreeNode parent = new TreeNode(null, right.nodeWeight + left.nodeWeight,
                    left, right);

            treeNodes.add(parent);
        }
        return treeNodes.get(0);
    }

    public static ArrayList<Integer> decode (String encoded, TreeNode tr){
        ArrayList<Integer> ls = new ArrayList<>();
        TreeNode node = tr;
        for(int i =0; i < encoded.length(); i++){
            node = encoded.charAt(i) == '0' ? node.left : node.right;
            if (node.symbol != null){
                ls.add(node.symbol);
                node = tr;
            }
        }
        return ls;
    }

    public static String encode (TreeMap<Integer, String> codes, List<Integer> ls){
        StringBuilder bld = new StringBuilder();
        ls.forEach(integer -> bld.append(codes.get(integer)));
        return bld.toString();
    }


    public static TreeMap<Integer, Integer> frequancy(List<Integer> ls){
        TreeMap<Integer, Integer>  freqMap= new TreeMap<>();
        for(int i: ls){
            Integer count = freqMap.get(i);
            freqMap.put(i, count != null? count +1 : 1);
        }
        return freqMap;
    }
    public static class BitArray {
        int size;
        byte[] bytes;

        private final byte[] masks = new byte[] {0b00000001, 0b00000010, 0b00000100, 0b00001000,
                0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000};

        public BitArray(int size) {
            this.size = size;
            int sizeInBytes = size / 8;
            if (size % 8 > 0) {
                sizeInBytes = sizeInBytes + 1;
            }
            bytes = new byte[sizeInBytes];
        }

        public BitArray(int size, byte[] bytes) {
            this.size = size;
            this.bytes = bytes;
        }

        public int get(int index) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            return (bytes[byteIndex] & masks[bitIndex]) != 0 ? 1 : 0;
        }

        public void set(int index, int value) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if (value != 0) {
                bytes[byteIndex] = (byte) (bytes[byteIndex] | masks[bitIndex]);
            } else {
                bytes[byteIndex] = (byte) (bytes[byteIndex] & ~masks[bitIndex]);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(get(i) > 0 ? '1' : '0');
            }
            return sb.toString();
        }

        public int getSize() {
            return size;
        }

        public int getSizeInBytes() {
            return bytes.length;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }


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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile(File input, int index, TreeMap<Integer, Integer> frequencies, StringBuilder bits) {
        try {
            DataInputStream os = new DataInputStream(new FileInputStream(input));
            index = os.readInt();
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
      /*  String content = new String(Files.readAllBytes(Paths.get(
                "C:\\Users\\vovkv\\Desktop\\BWT\\obj1")));*/

      StringBuilder content = new StringBuilder();
        try(FileInputStream reader = new FileInputStream("C:\\Users\\vovkv\\Desktop\\BWT\\book1"))
        {
            int c=-1;
            while((c= reader.read())!=-1){
                content.append((char) c);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        BWT bwt = bwt(content.toString());
        List <Integer> ls = new ArrayList<>(mtfEncode(bwt.res));
        System.out.println(ls.size());

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

        System.out.println(encoded.length());

        ArrayList<Integer> decoded = decode(encoded.toString(), tree);

        String beforebwt = unmtf(decoded);
        String fil = ibwt(beforebwt, bwt.index);

        System.out.println(fil);


        File file = new File("C:\\Users\\vovkv\\Desktop\\BWT\\compressed.huf");

        saveToFile(file, bwt.index, frequancies, encoded );


        int index = 0;
        TreeMap<Integer, Integer> frequencies2 = new TreeMap<>();
        StringBuilder encoded2 = new StringBuilder();

        loadFromFile(file, index, frequencies2, encoded2);

        TreeNode tree2 = huffman(treeNodes);

        // декодирование обратно исходной информации из сжатой
        ArrayList<Integer> decoded3 = decode(encoded2.toString(), tree2);

        String decod= unmtf(decoded3);

        String ib = ibwt(decod, index);


        // сохранение в файл декодированной информации



        try(FileOutputStream writer = new FileOutputStream("C:\\Users\\vovkv\\Desktop\\BWT\\decompressed.txt", false))
        {
            byte[] toWrite = ib.getBytes(StandardCharsets.ISO_8859_1);
            writer.write(toWrite);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }




      /*  TreeMap<Integer, Integer> frequencies2 = new TreeMap<>();
        StringBuilder encoded2 = new StringBuilder();
        treeNodes.clear();

        // извлечение сжатой информации из файла



        // генерация листов и постоение кодового дерева Хаффмана на основе таблицы частот сжатого файла
        for(Integer c: frequencies2.keySet()) {
            treeNodes.add(new TreeNode(c, frequencies2.get(c)));
        }
        TreeNode tree2 = huffman(treeNodes);

        // декодирование обратно исходной информации из сжатой
        ArrayList<Integer> decoded3 = decode(encoded2.toString(), tree2);

        String decod= unmtf(decoded3);

        String ib = ibwt(decod, bwt.index);

        System.out.println(ib);
        // сохранение в файл декодированной информации

        Files.write(Paths.get("C:\\Users\\vovkv\\Desktop\\BWT\\decompressed.txt"), ib.getBytes());

        System.out.println(content.equals(ib));
*/



        /*

        TreeMap<Integer, Integer> freq2 = new TreeMap<>();

        StringBuilder encoded2 = new StringBuilder();

        loadFromFile(file, freq2, encoded2);

        for(Integer i: freq2.keySet()){
            treeNodes.add(new TreeNode(i, freq2.get(i)));
        }

        TreeNode tree2 = huffman(treeNodes);

        ArrayList<Integer> decoded2 = decode(encoded2.toString(), tree2);
*/

  /*      String beforeibtw = unmtf(new ArrayList<>(decode(encoded, tree)));



        String afteribtw = ibwt(beforeibtw, bwt.index);*/









    }

}
