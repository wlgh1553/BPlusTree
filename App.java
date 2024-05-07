package org.dfpl.lecture.database.assignment2.assignment2_615458;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("Assignment 4: ");

        // 평가에서는 m (>=3), c1, c2, c3를 수정하여 수행한다.
        int m = (int) (Math.random() * 5) + 3;
        System.out.println("m:" + m);
        int c1 = 11;
        int c2 = 22;
        int c3 = (int) (Math.random() * 40) + 1;

        MyBPlusTree bpTree = new MyBPlusTree(m);
//        for (int i = 1; i <= c3; i++) {
//            bpTree.add(i);
//        }

//        for (int i = 1; i <= c3; i += 3) {
//            bpTree.add(i);
//        }
//        for (int i = 2; i <= c3; i += 3) {
//            bpTree.add(i);
//        }
//        for (int i = 3; i <= c3; i += 3) {
//            bpTree.add(i);
//        }

        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= c3; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        list.forEach(e -> {
            System.out.println(e + "삽입");
            bpTree.add(e);
        });

        bpTree.getNode(c1);
        System.out.println();
        bpTree.getNode(c2);
        System.out.println();

        bpTree.inorderTraverse();

        System.out.println("Assignment 5: ");

        ArrayList<Integer> values = new ArrayList<Integer>();
        Iterator<Integer> iterator = bpTree.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            values.add(value);
        }
        System.out.println("c3:" + c3);
        System.out.println("values.size() " + values.size());

        System.out.println("iterator test: " + (c3 == values.size()));

        System.out.println("m:" + m);
        System.out.print("삽입순서 : ");
        list.forEach(e -> System.out.print(e + ", "));

        //이젠 remove 함수 구현해서 잘 동작하는지 먼저 확인해보기!
        //bpTree.remove(3);

//        for (Integer value : values) {
//            bpTree.remove(value);
//        }
//        System.out.println("remove test: " + (values.size() == 0));
    }
}
