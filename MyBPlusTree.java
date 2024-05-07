package org.dfpl.lecture.database.assignment2.assignment2_615458;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@SuppressWarnings("unused")
public class MyBPlusTree implements NavigableSet<Integer> {

    // Data Abstraction은 예시일 뿐 자유롭게 B+ Tree의 범주 안에서 어느정도 수정가능
    private MyBPlusTreeNode root;
    private LinkedList<MyBPlusTreeNode> leafList;
    private int m;

    public MyBPlusTree(int m) {
        this.m = m;
        this.leafList = new LinkedList<>();
    }

    private int getMinKeyCnt() {
        return (int) Math.ceil(m / 2.0) - 1;
    }

    private int getMaxKeyCnt() {
        return m - 1;
    }

    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * key로 검색하면 root부터 시작하여, key를 포함할 수 있는 leaf node를 찾고 key가 실제로 존재하면 해당 Node를 반환하고, 그렇지 않다면 null을 반환한다. 중간과정을
     * System.out.println(String) 으로 출력해야 함. 6 way B+ tree에서 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 이
     * 순서대로 add되었다고 했을 때,
     * <p>
     * 예: getNode(11)을 수행하였을 때 > less than 13 > larger than or equal to 10 > 11 found 위의 3 문장을 콘솔에 출력하고 11을 포함한
     * SixWayBPlusTreeNode를 반환함
     * <p>
     * 예: getNode(22)를 수행하였을 때 > larger than or equal to 13 > larger than or equal to 19 > 22 not found 위의 3 문장을 콘솔에
     * 출력하고 null을 반환함.
     *
     * @param key
     * @return
     */
    public MyBPlusTreeNode getNode(Integer key) {
        MyBPlusTreeNode pointer = root;
        while (pointer != null && !pointer.isLeaf()) {
            pointer = pointer.findChildNodeWithLog(key);
        }

        if (pointer == null || !pointer.hasKey(key)) {
            System.out.println(key + " not found");
            return null;
        }
        System.out.println(key + " found");
        return pointer;
    }


    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * inorder traversal을 수행하여, 값을 오름차순으로 출력한다. 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 이 순서대로 add되었다고 했을
     * 때, 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 위와 같이 출력되어야 함.
     */
    public void inorderTraverse() {
        for (MyBPlusTreeNode node : leafList) {
            node.showKeys();
        }
        //TODO 나중에 여기 ln 없애기!!
        System.out.println();
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public Integer first() {
        if (leafList.isEmpty()) {
            return null;
        }
        return leafList.getFirst().getKey(0);
    }

    @Override
    public Integer last() {
        if (leafList.isEmpty()) {
            return null;
        }
        MyBPlusTreeNode last = leafList.getLast();
        return last.getKey(last.getKeyListLength() - 1);
    }

    @Override
    public int size() {
        return this.leafList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.leafList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    private void splitLeafNode(MyBPlusTreeNode node, MyBPlusTreeNode parent, boolean isRoot) {
        //left, mid, right로 쪼개기
        System.out.print("Leaf(O)");
        int mid = (int) Math.ceil((m - 1) / 2.0);
        int middleKey = node.getKey(mid);

        MyBPlusTreeNode leftNode = node.getSubNode(0, mid - 1);
        MyBPlusTreeNode rightNode = node.getSubNode(mid, node.getKeyListLength() - 1);

        if (isRoot) {
            System.out.print("Root(O)");
            root = new MyBPlusTreeNode(middleKey, leftNode, rightNode);
            leafList.clear();
            leafList.add(leftNode);
            leafList.add(rightNode);
        } else {
            System.out.print("Root(X)");
            parent.deleteChild(node);
            parent.addKey(middleKey);
            parent.addChild(leftNode);
            parent.addChild(rightNode);

            ListIterator<MyBPlusTreeNode> iterator = leafList.listIterator();
            while (iterator().hasNext()) {
                MyBPlusTreeNode value = iterator.next();
                if (value.equals(node)) {
                    iterator.add(leftNode);
                    iterator.add(rightNode);
                    break;
                }
            }
            leafList.remove(node);

            if (parent.isOverflow(getMaxKeyCnt())) {
                System.out.print("부모 노드는 overflow!\n 부모 상태:");
                parent.tempShowInfos();
                this.inorderTraverse();
                splitNode(parent);
            }
        }
    }

    private void splitNonLeafNode(MyBPlusTreeNode node, MyBPlusTreeNode parent, boolean isRoot) {
        //left, mid, right로 쪼개기
        System.out.print("Leaf(X)");
        int mid = (int) Math.floor((m - 1) / 2.0);
        int middleKey = node.getKey(mid);

        //left child, right child
        List<MyBPlusTreeNode> leftChildren = node.getSubChildren(0, mid);
        List<MyBPlusTreeNode> rightChildren = node.getSubChildren(mid + 1, node.getChildrenLength() - 1);

        //left node, right node
        MyBPlusTreeNode leftNode = node.getSubNode(0, mid - 1);
        MyBPlusTreeNode rightNode = node.getSubNode(mid + 1, node.getKeyListLength() - 1);
        leftNode.setChildren(leftChildren);
        rightNode.setChildren(rightChildren);

        if (isRoot) {
            System.out.print("Root(O)");
            root = new MyBPlusTreeNode(middleKey, leftNode, rightNode);
        } else {
            System.out.print("Root(X)");
            parent.deleteChild(node);
            parent.addKey(middleKey);
            parent.addChild(leftNode);
            parent.addChild(rightNode);

            if (parent.isOverflow(getMaxKeyCnt())) {
                System.out.print("부모 노드는 overflow!\n 부모 상태:");
                parent.tempShowInfos();
                this.inorderTraverse();
                splitNode(parent);
            }
        }
    }

    private void splitNode(MyBPlusTreeNode node) {
        //단말 노드인 경우 B+Tree 쪼개던 방식으로
        if (node.isLeaf()) {
            splitLeafNode(node, node.getParent(), node == root);
        }

        //단말 노드가 아닌 경우 B-Tree 쪼개던 방식으로
        else {
            splitNonLeafNode(node, node.getParent(), node == root);
        }
    }

    @Override
    public boolean add(Integer e) {
        //빈 트리인 경우
        if (root == null) {
            root = MyBPlusTreeNode.createRootNode(e);
            leafList.add(root);
            return true;
        }

        //해당 키가 들어갈 노드를 찾기
        MyBPlusTreeNode pointer = root;
        while (!pointer.isLeaf()) {
            pointer = pointer.findChildNode(e);
        }
        //e의 중복여부 체크 및 keyList에 add하는 함수 addKey 호출
        pointer.addKey(e);

        //overflow 처리
        if (pointer.isOverflow(getMaxKeyCnt())) {
            splitNode(pointer);
        }

        System.out.println(e + " 삽입 성공!");
        //임시
        System.out.print("트리 현황:");
        if (root != null) {
            root.tempShowInfos();
            this.inorderTraverse();
        } else {
            System.out.println("null");
        }
        //임시
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Integer key = (Integer) o;

        //빈 트리
        if (root == null) {
            return false;
        }

        //해당 키가 들어갈 노드를 찾기
        MyBPlusTreeNode pointer = root;
        while (!pointer.isLeaf()) {
            pointer = pointer.findChildNode(key);
        }

        //없는 키에 대한 remove 요청이 들어오는 경우 삭제 실패
        if (!pointer.hasKey(key)) {
            return false;
        }

        //Node내에서 인덱스가 0이어서 타고 올라가 수정해줘야하는 경우
        if (pointer.getKey(0) == key && pointer.getKeyListLength() >= 2) {
            MyBPlusTreeNode parent = pointer.getParent();
            while (parent != null) {
                if (parent.hasKey(key)) {
                    parent.updateKey(pointer.getKey(0), pointer.getKey(1));
                    break;
                }
                parent = parent.getParent();
            }
        }

        //삭제
        pointer.removeKey(key);

        //min key prop을 어기는 경우
        if (pointer.isUnderflow(getMinKeyCnt())) {
            //왼쪽, 오른쪽에서 가능하면 borrow

            //안 되면 merge & recursive검증
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public Integer lower(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer floor(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer ceiling(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer higher(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer pollFirst() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer pollLast() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Integer> iterator() {
        //Iterator를 구현한 내부 클래스
        return new MyBPlusTreeIterator();
    }

    private class MyBPlusTreeIterator implements Iterator<Integer> {
        private Iterator<MyBPlusTreeNode> nodeIterator;
        private MyBPlusTreeNode currentNode;
        private int currentIdx;

        public MyBPlusTreeIterator() {
            this.nodeIterator = leafList.iterator();
            this.currentNode = nodeIterator.hasNext() ? nodeIterator.next() : null;
            this.currentIdx = 0;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            //현재 값
            Integer value = currentNode.getKey(currentIdx);
            currentIdx++;

            //전부 순회했다면 다음 노드로 이동
            if (currentIdx >= currentNode.getKeyListLength()) {
                if (nodeIterator.hasNext()) {
                    currentNode = nodeIterator.next();
                    currentIdx = 0;
                } else {
                    currentNode = null;
                }
            }

            return value;
        }
    }

    @Override
    public NavigableSet<Integer> descendingSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Integer> descendingIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement,
                                        boolean toInclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedSet<Integer> headSet(Integer toElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedSet<Integer> tailSet(Integer fromElement) {
        // TODO Auto-generated method stub
        return null;
    }

}
