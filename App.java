package org.dfpl.lecture.database.assignment2.assignment2_615458;

public class App {

    public static void main(String[] args) {
        System.out.println("Assignment 4: ");

        // 평가에서는 m (>=3), c1, c2, c3를 수정하여 수행한다.
        int m = 6;
        int c1 = 11;
        int c2 = 22;
        int c3 = 31;

        MyBPlusTree bpTree = new MyBPlusTree(m);
        for (int i = 1; i < c3; i++) {
            bpTree.add(i);
        }
//
//		bpTree.getNode(c1);
//		System.out.println();
//		bpTree.getNode(c2);
//		System.out.println();
//		bpTree.inorderTraverse();
//
//		System.out.println("Assignment 5: ");
//
//		ArrayList<Integer> values = new ArrayList<Integer>();
//		Iterator<Integer> iterator = bpTree.iterator();
//		while(iterator.hasNext()) {
//			Integer value = iterator.next();
//			values.add(value);
//		}
//
//		System.out.println("iterator test: " + (c3 == values.size()));
//
//		for(Integer value: values) {
//			bpTree.remove(value);
//		}
//		System.out.println("remove test: " + (values.size() == 0));
    }
}
