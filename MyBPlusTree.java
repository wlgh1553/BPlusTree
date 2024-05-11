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
    private MyBPlusTreeNode root;
    private LinkedList<MyBPlusTreeNode> leafList;
    private int m;

    public MyBPlusTree(int m) {
        //BPlusTree 생성자
        this.m = m;
        this.leafList = new LinkedList<>();
    }

    private int getMinKeyCnt() {
        //이 트리의 minKeyProperty
        return (int) Math.ceil(m / 2.0) - 1;
    }

    private int getMaxKeyCnt() {
        //이 트리의 maxKeyProperty
        return m - 1;
    }

    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * key로 검색하면 root부터 시작하여, key를 포함할 수 있는 leaf node를 찾고 key가 실제로 존재하면 해당 Node를 반환하고, 그렇지 않다면 null을 반환한다. 중간과정을
     * System.out.println(String) 으로 출력해야 함. 6 way B+ tree에서 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 이
     * 순서대로 add되었다고 했을 때,
     * <p>
     * 예: getNode(11)을 수행하였을 때 > larger than or equal to 10 > less than 13 > 11 found 위의 3 문장을 콘솔에 출력하고 11을 포함한
     * MyBPlusTreeNode를 반환함
     * <p>
     * 예: getNode(22)를 수행하였을 때 > larger than or equal to 10 > larger than or equal to 19 > 22 not found 위의 3 문장을 콘솔에
     * 출력하고 null을 반환함.
     *
     * @param key
     * @return
     */
    public MyBPlusTreeNode getNode(Integer key) { //반환값이 MyBPlusTreeNode여야 할 것 같아 수정하였습니다.
        //root에서 탐색 시작
        MyBPlusTreeNode pointer = root;

        //leaf노드까지 내려오며 탐색
        while (pointer != null && !pointer.isLeaf()) {
            pointer = pointer.findChildNodeWithLog(key);
        }

        //leaf 노드에 해당 key가 존재하지 않는 경우
        if (pointer == null || !pointer.hasKey(key)) {
            System.out.println(key + " not found");
            return null;
        }

        //leaf 노드에 해당 key가 존재하는 경
        System.out.println(key + " found");
        return pointer;
    }


    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * inorder traversal을 수행하여, 값을 오름차순으로 출력한다. 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22 이 순서대로 add되었다고
     * 했을 때, 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 위와 같이 출력되어야 함.
     */
    public void inorderTraverse() {
        //B+Tree의 특징을 살려서 leafList를 순회
        for (MyBPlusTreeNode node : leafList) {
            node.showKeys();
        }
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public Integer first() {
        //맨 앞 원소 반환
        if (leafList.isEmpty()) {
            return null;
        }
        return leafList.getFirst().getKey(0); //leaflist에서 빠르게 찾을 수 있음
    }

    @Override
    public Integer last() {
        //맨 뒤 원소 반환
        if (leafList.isEmpty()) {
            return null;
        }
        MyBPlusTreeNode last = leafList.getLast(); //leaflist에서 빠르게 찾을 수 있음
        return last.getKey(last.getKeyListLength() - 1); //해당 노드의 맨 뒤 값
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
        int mid = (int) Math.ceil((m - 1) / 2.0); //leaf 노드에서 mid 인덱스를 구하는 공식
        int middleKey = node.getKey(mid);  //mid인덱스에 해당하는 값

        MyBPlusTreeNode leftNode = node.getSubNode(0, mid - 1); //left로 쪼개지는 키들 인덱스 : [0,mid)
        MyBPlusTreeNode rightNode = node.getSubNode(mid,
                node.getKeyListLength() - 1); //right로 쪼개지는 키들 인덱스 : [mid,node.length)

        //Leaf노드이면서 Root노드를 split하는 경우
        if (isRoot) {
            //leftNode와 rightNode가 middleKey가 있는 노드의 child가 됨
            root = new MyBPlusTreeNode(middleKey, leftNode, rightNode); //middle key를 key로 가진 노드가 root
            leafList.clear(); //leaf list 초기화
            leafList.add(leftNode); //leaf list에 leftNode를 추가
            leafList.add(rightNode); //leaf list에 rightNode를 추가
        } else { //leaf노드를 split하는 경우
            parent.removeChild(node); //node를 부모와 연결 끊음
            parent.addKey(middleKey); //middleKey를 부모로 올림
            parent.addChild(leftNode); //parent에 leftNode를 자식으로 연결함
            parent.addChild(rightNode); //parent에 rightNode를 자식으로 연결함

            //딱 node위치에 leftNode, rightNode라는 분할 노드를 넣어주기
            //linked list이므로 iterator로 순회하면서 자리를 찾아줌
            ListIterator<MyBPlusTreeNode> iterator = leafList.listIterator();
            while (iterator().hasNext()) {
                MyBPlusTreeNode value = iterator.next();
                if (value.equals(node)) {
                    //자리를 찾은 경우 leftNode, rightNode를 그 위치에 추가하고 break
                    iterator.add(leftNode);
                    iterator.add(rightNode);
                    break;
                }
            }
            leafList.remove(node); //분할 전인 node는 제거

            //만약 maxKey prop을 어겼다면 splitNode 수행
            if (parent.isOverflow(getMaxKeyCnt())) {
                splitNode(parent);
            }
        }
    }

    private void splitInternalNode(MyBPlusTreeNode node, MyBPlusTreeNode parent, boolean isRoot) {
        //left, mid, right로 쪼개기
        int mid = (int) Math.floor((m - 1) / 2.0); //leaf 노드에서 mid 인덱스를 구하는 공식
        int middleKey = node.getKey(mid); //mid인덱스에 해당하는 값

        //left child, right child
        List<MyBPlusTreeNode> leftChildren = node.getSubChildren(0, mid); //왼쪽으로 쪼개질 노드의 자식 찾기
        List<MyBPlusTreeNode> rightChildren = node.getSubChildren(mid + 1,
                node.getChildrenLength() - 1); //오른쪽으로 쪼개질 노드의 자식 찾기

        //left node, right node
        MyBPlusTreeNode leftNode = node.getSubNode(0, mid - 1); //왼쪽으로 쪼개질 노드 찾기
        MyBPlusTreeNode rightNode = node.getSubNode(mid + 1, node.getKeyListLength() - 1); //오른쪽으로 쪼개질 노드 찾기
        leftNode.setChildren(leftChildren); //왼쪽 노드의 자식 설정
        rightNode.setChildren(rightChildren); //오른쪽 노드의 자식 설정

        if (isRoot) { //root인 경우 middleKey를 key list로 가진 node가 root가 됨
            root = new MyBPlusTreeNode(middleKey, leftNode, rightNode);
        } else {
            parent.removeChild(node); //부모로부터 node 연결 끊기
            parent.addKey(middleKey); //middleKey를 parent 노드에 추가
            parent.addChild(leftNode); //parent 노드의 자식으로 leftNode 추가
            parent.addChild(rightNode); //parent 노드의 자식으로 rightNode 추가

            //만약 maxKey prop을 어겼다면 splitNode 수행
            if (parent.isOverflow(getMaxKeyCnt())) {
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
            splitInternalNode(node, node.getParent(), node == root);
        }
    }

    @Override
    public boolean add(Integer e) {
        //빈 트리인 경우
        if (root == null) {
            root = MyBPlusTreeNode.createRootNode(e); //노드 하나 생성, 그 노드를 root로 설정
            leafList.add(root); //root를 leafList에 연결
            return true;
        }

        //해당 키가 들어갈 노드를 찾기
        MyBPlusTreeNode pointer = root; //root부터 탐색 시작
        while (!pointer.isLeaf()) { //leafNode에 도달할 때까지 찾기
            pointer = pointer.findChildNode(e);
        }
        //e의 중복여부 체크 및 keyList에 add하는 함수 addKey 호출
        boolean addSuccess = pointer.addKey(e);
        if (!addSuccess) {
            return false; //이미 존재하는 키를 add하려고 한 경우
        }

        //overflow 처리
        if (pointer.isOverflow(getMaxKeyCnt())) {
            //max key prop을 어기는 경우 splitNode 수행
            splitNode(pointer);
        }

        return true;
    }

    private int removeAndUpdate(MyBPlusTreeNode pointer, int key) {
        int pointerMinKey = pointer.getMinKey(); //pointer의 가장 작은 키
        boolean isMinKey = (key == pointerMinKey); //삭제하려는 게 제일 작은 키인지
        pointer.removeKey(key); //key 제거

        //올라가서 분기점을 업데이트 해줘야 하는 경우
        if (isMinKey && !pointer.isEmpty()) {
            int result = updateInternalNodeFromLeaf(pointer, key, pointer.getMinKey());
            if (result != -1) {
                pointerMinKey = result; //만약 remove이후 pointer의 minkey가 바뀌었다면 업데이트
            }
        }
        return pointerMinKey; //만약 pointer의 제일 작은 key 반환
    }

    private void addAndUpdate(MyBPlusTreeNode pointer, int delKey, int newKey) {
        pointer.addKey(newKey); //newKey를 pointer에 추가
        if (pointer.getMinKey() == newKey) { //만약 지금 추가한 newkey가 해당 노드에서 제일 작다면 internal노드 업데이트 필요
            updateInternalNodeFromLeaf(pointer, delKey, newKey);
        }
    }

    private int updateInternalNodeFromLeaf(MyBPlusTreeNode pointer, int delKey, int newKey) {
        //internal 노드들에서 delKey를 찾아 newKey로 바꿔주는 함수

        MyBPlusTreeNode editNode = pointer.getParent(); //pointer의 parent노드부터 순회
        while (editNode != null) { //root에 도달하여 더이상 위로 올라갈 수 없을 때까지
            if (editNode.hasKey(delKey)) { //delKey를 찾은 경우
                editNode.changeKey(delKey, newKey); //delKey를 newKey로 바꿈
                return pointer.getMinKey(); //해당 pointer의 minkey를 반환
            }
            editNode = editNode.getParent(); //parent로 올라가기
        }
        return -1; //delKey를 찾지 못한 경우 -1반환
    }

    private boolean borrowAtLeaf(MyBPlusTreeNode pointer, MyBPlusTreeNode sibling, boolean isLeftBorrow, int delKey) {
        //빌릴 수 없는 경우 : sibling이 없거나, sibling에서 하나 삭제되면 minKey prop을 어기는 경우
        if (sibling == null || sibling.getKeyListLength() - 1 < getMinKeyCnt()) {
            return false;
        }

        //빌리기
        //leftBorrow라면 해당 노드중 가장 큰 키를, rightBorrow라면 해당 노드 중 가장 작은 키를 빌려온다.
        int borrowKey = isLeftBorrow ? sibling.getMaxKey() : sibling.getMinKey();
        removeAndUpdate(sibling, borrowKey); //sibling에서 빌려온 키를 제거
        addAndUpdate(pointer, delKey, borrowKey); //pointer에 빌려온 키를 추가
        return true;
    }

    private MyBPlusTreeNode mergeLeafNodes(MyBPlusTreeNode pointer, MyBPlusTreeNode sibling, int delKey,
                                           boolean isLeftMerge) {
        //leafNode간의 merge
        MyBPlusTreeNode parent = pointer.getParent();

        //parent 하나 줄여주기
        int pointerIdx = parent.getChildIdx(pointer);
        int rIdx = isLeftMerge ? pointerIdx - 1 : pointerIdx;
        //제거할 parent key의 인덱스는 leftMerge인 경우 pointerIdx - 1, rightMerge인 경우 pointerIdx
        parent.removeKeyByIdx(rIdx);

        //제거에 따른 연결 업데이트
        if (parent.isEmpty() && parent.getParent() == null) {
            //조상이 아무것도 없는 경우
            pointer.setParentNull();
            sibling.setParentNull();
        } else {
            //parent의 child 지우기
            parent.removeChild(sibling);
            parent.removeChild(pointer);
        }

        //merge한 노드 생성, 부모랑 잇기, leafnode랑 잇기
        MyBPlusTreeNode merge;
        if (isLeftMerge) { //왼쪽 merge라면 sibling, pointer순으로 오름차순 merge
            merge = MyBPlusTreeNode.mergeNodes(parent, sibling, pointer);
        } else { //오른쪽 merge라면 pointer, sibling순으로 오름차순 merge
            merge = MyBPlusTreeNode.mergeNodes(parent, pointer, sibling);
        }
        //parent 연결
        if (parent.isEmpty() && parent.getParent() == null) {
            //merge노드 위의 노드가 없어졌으므로 merge노드가 root가 된 경우
            root = merge;
            parent = null;
        } else {
            //parent의 child로 merge노드 연결
            parent.addChild(merge);
            //merge노드가 생성되었으므로 merge노드 위로 타고 올라가며 key 업데이트
            updateInternalNodeFromLeaf(merge, delKey, merge.getMinKey());
        }

        //딱 pointer위치에 merge 노드를 넣어주기
        ListIterator<MyBPlusTreeNode> iterator = leafList.listIterator();
        while (iterator().hasNext()) {
            MyBPlusTreeNode value = iterator.next();
            if (value.equals(pointer)) { //pointer위치 발견한 위치에 merge 노드 넣어줌
                iterator.add(merge);
                break;
            }
        }
        //merge될 때 사용된 sibling과 pointer노드는 제거
        leafList.remove(sibling);
        leafList.remove(pointer);

        return parent;
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

        //삭제 및 타고 올라가서 업데이트
        int pointerMinKey = removeAndUpdate(pointer, key);

        //key를 삭제한 노드가 root노드였다면 함수 종료
        if (pointer == root) {
            if (pointer.isEmpty()) {//아예 tree가 비어버린 경우
                leafList.clear();
                root = null;
            }
            return true;
        }

        //min key prop을 어기지 않으면 끝
        if (!pointer.isUnderflow(getMinKeyCnt())) {
            return true;
        }

        //min key prop을 어기는 경우
        //1. 왼쪽에서 가능하면 borrow
        MyBPlusTreeNode leftSibling = pointer.getParent().getLeftChild(pointer); //leftSibling 구하기
        boolean borrowResult = borrowAtLeaf(pointer, leftSibling, true, pointerMinKey); //borrow 시도
        if (borrowResult) {
            return true; //borrow에 성공한 경우 종료
        }

        //2. 오른쪽에서 가능하면 borrow
        MyBPlusTreeNode rightSibling = pointer.getParent().getRightChild(pointer); //rightSibling 구하기
        borrowResult = borrowAtLeaf(pointer, rightSibling, false, pointerMinKey); //borrow 시도
        if (borrowResult) {
            return true; //borrow에 성공한 경우 종료
        }

        //3. 안 되면 merge & recursive하면서 중간 노드 업데이트
        MyBPlusTreeNode parent;
        //왼쪽 merge
        if (leftSibling != null) {
            //leftSibling이 있다면 left merge
            parent = mergeLeafNodes(pointer, leftSibling, pointerMinKey, true);
        } else {
            //leftSibling이 없다면 right merge
            parent = mergeLeafNodes(pointer, rightSibling, pointerMinKey, false);
        }

        //만약 internal 노드에서 min key prop을 어기게 된다면 재귀적으로 업데이트 진행
        if (parent != null && parent.isUnderflow(getMinKeyCnt())) {
            updateInternalNode(parent);
        }
        return true;
    }

    private void updateInternalNode(MyBPlusTreeNode pointer) {
        //root노드인 경우 borrow, merge가 필요 없음
        if (pointer == root) {
            return;
        }

        //1. 왼쪽에서 borrow
        MyBPlusTreeNode leftSibling = pointer.getParent().getLeftChild(pointer);
        boolean borrowResult = borrowAtInternal(pointer, leftSibling, true);
        if (borrowResult) { //borrow에 성공한 경우 종료
            return;
        }

        //2. 오른쪽에서 borrow
        MyBPlusTreeNode rightSibling = pointer.getParent().getRightChild(pointer);
        borrowResult = borrowAtInternal(pointer, rightSibling, false);
        if (borrowResult) { //borrow에 성공한 경우 종료
            return;
        }

        //3. 안 되면 merge
        MyBPlusTreeNode parent = pointer.getParent();
        if (leftSibling != null) {
            //leftSibling이 있다면 left merge
            pointer = mergeInternalNodes(pointer, leftSibling, true);
        } else {
            //leftSibling이 없다면 right merge
            pointer = mergeInternalNodes(pointer, rightSibling, false);
        }
        if (pointer.getParent() == null) {
            return;
        }

        //merge 후 min key prop을 어긴 경우 재귀적으로 internal node 업데이트
        if (pointer.getParent().isUnderflow(getMinKeyCnt())) {
            updateInternalNode(pointer.getParent());
        }
    }

    private boolean borrowAtInternal(MyBPlusTreeNode pointer, MyBPlusTreeNode sibling, boolean isLeftSibling) {
        //internal 노드에서 borrow를 수행하는 함수

        //sibling이 없거나 빌렸을 때 min key prop을 어겨버리면 borrow 수행 불가
        if (sibling == null || sibling.getKeyListLength() - 1 < getMinKeyCnt()) {
            return false;
        }

        //sibling에게 빌려올 key 찾기
        int borrowKey;
        if (isLeftSibling) { //왼쪽 sibling에게 빌려온다면, sibling의 가장 큰 key를 빌려와야 함
            borrowKey = sibling.removeMaxKey();
        } else { //오른쪽 sibling에게 빌려온다면, sibling의 가장 작은 key를 빌려와야 함
            borrowKey = sibling.removeMinKey();
        }

        //부모 노드에서 내려올 key 찾기
        int pointerIdx = pointer.getParent().getChildIdx(pointer);
        int downKey = pointer.getParent().getKey(isLeftSibling ? pointerIdx - 1 : pointerIdx);

        //부모 노드에서는 downKey를 borrowKey로 바꿔줌 (빌린 키를 올림)
        pointer.getParent().changeKey(downKey, borrowKey);
        //pointer노드에서는 downKey를 추가해줌 (키를 내림)
        pointer.addKey(downKey);

        //바뀐 child에 대해 parent 연결해주기
        if (isLeftSibling) {
            sibling.getMaxChild().changeParent(pointer);
        } else {
            sibling.getMinChild().changeParent(pointer);
        }

        return true;
    }

    private MyBPlusTreeNode mergeInternalNodes(MyBPlusTreeNode pointer, MyBPlusTreeNode sibling, boolean isLeftMerge) {
        //internal 노드에서 merge 수행하는 함수

        MyBPlusTreeNode parent = pointer.getParent();

        //parent노드에서 merge할 key를 찾음
        int pointerIdx = parent.getChildIdx(pointer);
        int parentValue = parent.removeKeyByIdx(isLeftMerge ? pointerIdx - 1 : pointerIdx);

        //parent와의 연결 제거
        parent.removeChild(sibling);
        parent.removeChild(pointer);
        sibling.setParentNull();
        pointer.setParentNull();

        //merge한 노드 생성, 부모랑 잇기, leafnode랑 잇기
        MyBPlusTreeNode merge;
        if (isLeftMerge) { //왼쪽 merge라면 sibling, pointer순으로 오름차순 merge
            merge = MyBPlusTreeNode.mergeNodes(parent, sibling, pointer);
        } else { //오른쪽 merge라면 pointer, sibling순으로 오름차순 merge
            merge = MyBPlusTreeNode.mergeNodes(parent, pointer, sibling);
        }
        merge.addKey(parentValue); //parentValue를 merge에 추가함

        //merge 노드의 부모 노드가 없거나, merge노드의 부모 노드가 빈 경우라면 merge 노드가 root가 된다.
        if (merge.getParent() == null || (merge.getParent().getParent() == null && merge.getParent().isEmpty())) {
            root = merge;
            merge.setParentNull();
        } else {
            //merge 노드를 merge 노드의 부모와 연결한다.
            merge.getParent().addChild(merge);
        }
        return merge;
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
        return new MyBPlusTreeIterator();
    }

    private class MyBPlusTreeIterator implements Iterator<Integer> {
        private Iterator<MyBPlusTreeNode> nodeIterator;
        private MyBPlusTreeNode currentNode;
        private int currentIdx;

        public MyBPlusTreeIterator() {
            //leafList의 iterator
            this.nodeIterator = leafList.iterator();

            //현재 노드와 그 노드 내의 key를 가리키는 index
            this.currentNode = nodeIterator.hasNext() ? nodeIterator.next() : null;
            this.currentIdx = 0;
        }

        @Override
        public boolean hasNext() {
            //현재 노드가 null이 아니라면 next를 가지고 있는 것임
            return currentNode != null;
        }

        @Override
        public Integer next() {
            if (!hasNext()) { //next가 수행될 수 없는 경우 예외처리
                throw new NoSuchElementException();
            }

            //현재 값 기록
            Integer value = currentNode.getKey(currentIdx);
            currentIdx++;

            //해당 node를 전부 순회했다면 다음 노드로 이동
            if (currentIdx >= currentNode.getKeyListLength()) {
                //다음 노드가 있다면 해당 노드로 이동하고 index를 0으로 설정
                if (nodeIterator.hasNext()) {
                    currentNode = nodeIterator.next();
                    currentIdx = 0;
                } else { //다음 노드가 없다면 currentNode를 0으로 설정
                    currentNode = null;
                }
            }

            //기록해둔 값 반환
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
