package Lab1;

import java.util.*;

public class NBWT {
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


}
