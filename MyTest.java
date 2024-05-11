package org.dfpl.lecture.database.assignment2.assignment2_615458;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyTest {

    public static void main(String[] args) {
        System.out.println("Assignment 4: ");

        // 평가에서는 m (>=3), c1, c2, c3를 수정하여 수행한다.
        //int m = (int) (Math.random() * 5) + 3;
        int m = 3;
        int c1 = 11;
        int c2 = 22;
        int c3 = 15;
        //int c3 = (int) (Math.random() * 40) + 1;

        MyBPlusTree bpTree = new MyBPlusTree(m);
//        for (int i = 1; i <= c3; i++) {
//            bpTree.add(i);
//        }

        //개인 검증용
//        for (int i = 1; i <= c3; i += 3) {
//            bpTree.add(i);
//        }
//        for (int i = 2; i <= c3; i += 3) {
//            bpTree.add(i);
//        }
//        for (int i = 3; i <= c3; i += 3) {
//            bpTree.add(i);
//        }
//
        List<Integer> list = new ArrayList<>(List.of(2, 13, 9, 7, 4, 11, 8, 5, 14, 10, 15, 12, 1, 3, 6));
//        for (int i = 1; i <= c3; i++) {
//            list.add(i);
//        }
//        Collections.shuffle(list);
        list.forEach(e -> {
            System.out.println(e + "삽입");
            bpTree.add(e);
        });
        //개인 검증용

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

        //개인 검증용
        System.out.println("m:" + m);
        System.out.print("삽입순서 : ");
        list.forEach(e -> System.out.print(e + ", "));
        System.out.println();
        //개인 검증용

        List<Integer> rlist = new ArrayList<>(List.of(4, 2, 9, 1, 6, 3, 12, 10, 5, 13, 8, 15, 11, 14, 7
        ));
//        for (int i = 1; i <= c3; i++) {
//            rlist.add(i);
//        }
//        Collections.shuffle(rlist);
        System.out.println("삭제 순서 : ");
        rlist.forEach(e -> System.out.print(e + ", "));
        rlist.forEach(e -> {
            System.out.println("==" + e + "삭제==");
            bpTree.remove(e);
            bpTree.showTree();

            //검증로직
            if (bpTree.getNode(e) == null) {
                System.out.println("잉 안찾아짐 -> 성공!!");
            }
        });

        list.forEach(e -> {
            System.out.println(e + "삽입");
            bpTree.add(e);
        });

        //개인 검증용
        //아래는 min key prop을 어기지 않는 선에서 윗부분 update 해주는 삭제 시나리오
//        bpTree.showTree();
//        bpTree.remove(19);
//        bpTree.remove(20);
//        bpTree.remove(1);
//        bpTree.showTree();
        //개인 검증용

//        for (int i = c3; i >= 0; i--) {
//            System.out.println("==" + i + "제거==");
//            bpTree.remove(i);
//            bpTree.showTree();
//        }

//        for (Integer value : values) {
//            bpTree.remove(value);
//        }
//        System.out.println("remove test: " + (values.size() == 0));
    }
}
