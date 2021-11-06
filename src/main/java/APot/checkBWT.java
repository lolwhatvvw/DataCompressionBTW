package APot;
import javafx.util.Pair;

import java.util.*;

public class checkBWT {

        private static Pair<String, Integer> bwt(String ss) {
            int ind = 0;
            List<String> table = new ArrayList<>();
            for (int i = 0; i < ss.length(); i++) {
                String before = ss.substring(i);
                String after = ss.substring(0, i);
                table.add(before + after);
            }
            table.sort(String::compareTo);

            StringBuilder sb = new StringBuilder();
            for (String str : table) {
                sb.append(str.charAt(str.length() - 1));
                if (str.equals(ss)){
                    ind = table.indexOf(str);
                }
            }
            return new Pair<>(sb.toString(), ind);
        }

        private static String ibwt(Pair<String, Integer> pr) {
            String r = pr.getKey();
            int len = r.length();
            List<String> table = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                table.add("");
            }
            for (int j = 0; j < len; ++j) {
                for (int i = 0; i < len; ++i) {
                    table.set(i, r.charAt(i) + table.get(i));
                }
                table.sort(String::compareTo);
            }

            return table.get(pr.getValue());
        }

    public static List<Integer> mtfEncode(String msg, String symTable){
        List<Integer> output = new LinkedList<Integer>();
        StringBuilder s = new StringBuilder(symTable);
        for(char c : msg.toCharArray()){
            int idx = s.indexOf("" + c);
            output.add(idx);
            s.deleteCharAt(idx).insert(0, c);
        }
        return output;
    }

    public static String unmtf(List<Integer> idxs, String symTable){
        StringBuilder output = new StringBuilder();
        StringBuilder s = new StringBuilder(symTable);
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

        public static void main(String[] args) {

            String s = "hiphophiphop";
            String afterBWT = bwt(s).getKey();
            Integer v = bwt(s).getValue();
            String symtable = "abcdefghijklmnopqrstuvwxyz";
            List<Integer> aftermtf = mtfEncode(afterBWT, symtable);

            aftermtf.forEach(System.out::print);
            System.out.println();
            TreeMap<Integer, Integer> frequancies = frequancy(aftermtf);

            frequancies.forEach((a,b) -> System.out.println(a + " " + b));

            ArrayList<TreeNode> treeNodes = new ArrayList<>();

           for(Integer c: frequancies.keySet()){
                treeNodes.add(new TreeNode(c, frequancies.get(c)));
            }
            TreeNode tree = huffman(treeNodes);

            TreeMap<Integer, String> codes = new TreeMap<>();
            for(Integer c: frequancies.keySet()){
                codes.put(c, tree.getCode(c, ""));
            }


            String encoded = encode(codes, aftermtf);
            System.out.println(encoded);

            ArrayList<Integer> decode = decode(encoded, tree);
            decode.forEach(System.out::println);

            String beforeibtw = unmtf(decode, symtable);
            System.out.println(beforeibtw);

            System.out.println(ibwt(new Pair<>(beforeibtw, v)));





        }
    }

