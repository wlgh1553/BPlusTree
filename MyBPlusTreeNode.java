package org.dfpl.lecture.database.assignment2.assignment2_615458;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class MyBPlusTreeNode {

    // Data Abstraction은 예시일 뿐 자유롭게 B+ Tree의 범주 안에서 어느정도 수정가능
    private MyBPlusTreeNode parent;
    private List<Integer> keyList;
    private List<MyBPlusTreeNode> children;

    public MyBPlusTreeNode(int key, MyBPlusTreeNode left, MyBPlusTreeNode right) {
        //쪼개지는 경우 위로 올라가는 노드를 생성하는 생성자
        //key가 현재 노드의 keylist로 들어가고, left와 right가 현재 노드의 children으로 들어감
        parent = null;
        keyList = new ArrayList<>(); //인덱스로 접근할 일이 많으므로 LinkedList가 아닌 ArrayList로 생성
        keyList.add(key);
        children = new ArrayList<>();//인덱스로 접근할 일이 많으므로 LinkedList가 아닌 ArrayList로 생성
        children.add(left);
        children.add(right);
        left.parent = this;
        right.parent = this;
    }

    private MyBPlusTreeNode(MyBPlusTreeNode parent, List<Integer> keys, List<MyBPlusTreeNode> children) {
        //쪼개지는 경우에 만들 노드를 위한 생성자.
        this.parent = parent;
        this.keyList = new ArrayList<>(keys);
        this.children = new ArrayList<>(children);

        //children의 parent연결
        for (MyBPlusTreeNode child : this.children) {
            child.parent = this;
        }
    }

    private MyBPlusTreeNode(MyBPlusTreeNode parent, MyBPlusTreeNode node1, MyBPlusTreeNode node2) {
        //node merge를 위한 생성자.
        this.parent = parent;
        this.keyList = new ArrayList<>();
        if (!node1.keyList.isEmpty()) {
            keyList.addAll(node1.keyList);
        }
        if (!node2.keyList.isEmpty()) {
            keyList.addAll(node2.keyList);
        }
        this.children = new ArrayList<>();
        children.addAll(node1.children);
        children.addAll(node2.children);
        node1.children.forEach(e -> e.parent = this);
        node2.children.forEach(e -> e.parent = this);
    }

    private MyBPlusTreeNode(int key) {
        //root 노드를 만들 때에만 사용하는 생성자
        this.parent = null;
        this.keyList = new ArrayList<>();
        this.keyList.add(key);
        this.children = new ArrayList<>();
    }

    public static MyBPlusTreeNode createRootNode(int key) {
        return new MyBPlusTreeNode(key);
    }

    public void removeChild(MyBPlusTreeNode node) {
        if (this.children != null) {
            this.children.remove(node);
        }
    }

    public MyBPlusTreeNode getSubNode(int startIdx, int endIdx) {
        //노드의 부분만 쪼개서 새로운 노드로 만들어 반환
        //keyList의 부분 key들 [startIdx, endIdx] 만큼을 추출
        List<Integer> subKeys = this.keyList.subList(startIdx, endIdx + 1);

        //subKey와 대응되는 children 추출. 인덱스는 [startIdx, endIdx + 1]까지 (즉, sub key list보다 1개 많음)
        List<MyBPlusTreeNode> subChildren = new ArrayList<>();
        if (!this.children.isEmpty()) {
            subChildren = new ArrayList<>(this.children.subList(startIdx, endIdx + 2));
        }

        //새로운 노드로 만들어 반환
        MyBPlusTreeNode newNode = new MyBPlusTreeNode(this.parent, subKeys, subChildren);
        return newNode;
    }

    public int getChildrenLength() {
        return this.children.size();
    }

    public void setChildren(List<MyBPlusTreeNode> children) {
        //children 노드의 'parent'값이 이 노드를 가리키도록 만듦
        for (MyBPlusTreeNode child : children) {
            child.parent = this;
        }
        this.children = children;
    }

    public List<MyBPlusTreeNode> getSubChildren(int startIdx, int endIdx) {
        //부분 children을 찾아 반환
        List<MyBPlusTreeNode> subChildren = new ArrayList<>(children.subList(startIdx, endIdx + 1));
        return subChildren;
    }

    public Boolean addKey(Integer key) {
        //1. key가 삽입될 위치 찾기
        //  키가 정렬되어 있으므로 이진탐색을 통해 빠르게 자리를 찾을 수 있다.
        //  Collections.binarySearch는 리스트에서 원소를 찾지 못했을 경우 삽입 위치를 음수로 알려준다.
        //  음수값은 -(삽입위치) - 1의 형식으로 반환된다.
        //  따라서 현재 데이터를 삽입할 수 있는 위치 : (-반환값-1)
        int index = Collections.binarySearch(keyList, key);
        if (index >= 0) {
            System.out.println("이미 존재하는 key이므로 add할 수 없습니다.");
            return false; //이미 존재하는 key 이므로 삽입 실패
        }
        //2. key 삽입
        //  index가 음수라면 존재하지 않는 key이므로 add해준다.
        index = -index - 1;
        //  index자리에 key를 add하면 해당 위치부터 끝까지의 원소들이 뒤로 한 칸 밀리고, 그 자리에 key가 들어간다.
        keyList.add(index, key);
        return true;
    }

    public void addChild(MyBPlusTreeNode child) {
        //child노드의 key의 첫번째 값을 기반으로 자리 탐색
        int firstKey = child.keyList.get(0);

        //부모 노드의 key값을 기반으로 child노드 자리 찾기
        int index = Collections.binarySearch(keyList, firstKey);
        //음수인 경우 -index-1이 child의 위치가 됨
        if (index < 0) {
            index = -index - 1;
        } else { //양수인 경우 index+1이 child의 위치가 됨
            index = index + 1;
        }

        //children에 add하기
        this.children.add(index, child);
        //child의 parent가 이 노드를 가리키도록 연결
        child.parent = this;
    }

    public int getKey(int idx) {
        //인덱스로 key 찾기
        return this.keyList.get(idx);
    }

    public MyBPlusTreeNode findChildNode(int key) {
        //key값을 가지고있는 childNode를 찾기

        //1. 먼저 현재 keylist에서 key값의 위치를 이진 탐색으로 찾기
        int index = Collections.binarySearch(keyList, key);
        if (index < 0) {
            index = -index - 1;
        } else {
            index = index + 1;
        }

        //2. B+tree 특징에 따라 key값을 가진 node는 index번째 children이 된다.
        return this.children.get(index);
    }

    public MyBPlusTreeNode findChildNodeWithLog(int key) {
        //위의 findChildNode와 동일하게 child를 찾지만 log를 출력하는 함수

        //1. 현재 keylist에서 key값의 위치를 이진 탐색으로 찾기
        int index = Collections.binarySearch(keyList, key);
        if (index < 0) {
            index = -index - 1;
        } else {
            index = index + 1;
        }

        //2. 탐색 결과에 알맞는 log 찍기
        if (index == 0) {
            System.out.println("less than " + keyList.get(index));
        } else if (index == keyList.size()) {
            System.out.println("larger than or equal to " + keyList.get(keyList.size() - 1));
        } else {
            System.out.println("larger than or equal to " + keyList.get(index - 1)
                    + " and less than " + keyList.get(index));
        }

        //3. children노드를 반환
        return this.children.get(index);
    }

    public boolean hasKey(int key) {
        //keylist에 key가 있는지를 확인해주는 함수
        return this.keyList.contains(key);
    }

    public boolean isOverflow(int maxKeyCnt) {
        //keylist개수가 maxkey prop을 어겼는지 확인해주는 함수
        return keyList.size() > maxKeyCnt;
    }

    public boolean isUnderflow(int minKeyCnt) {
        //keylist개수가 minkey prop을 어겼는지 확인해주는 함수
        return keyList.size() < minKeyCnt;
    }

    public MyBPlusTreeNode getParent() {
        //부모 노드를 반환하는 함수
        return this.parent;
    }

    public int getKeyListLength() {
        //key list의 길이를 반환하는 함수
        return this.keyList.size();
    }

    public boolean isLeaf() {
        //leaf node인지 확인해주는 함수
        //leaf node는 children이 없다.
        return this.children.isEmpty();
    }

    public void changeKey(int from, int to) {
        //key list에서 from값을 찾아 to값으로 바꿔주는 함수
        int index = Collections.binarySearch(keyList, from); //keyList에서 from값의 위치를 찾기
        keyList.set(index, to); //해당 위치값을 to로 바꾸기
    }

    public void removeKey(int key) {
        //key list에서 key값을 찾아 삭제하는 함수
        int index = Collections.binarySearch(keyList, key); //keyList에서 key값의 위치 찾기
        keyList.remove(index); //해당 위치값을 index로 바꾸기
    }

    public int removeKeyByIdx(int idx) {
        int value = keyList.get(idx); //반환을 위해 기록
        keyList.remove(idx); //해당 위치값을 제거
        return value; //삭제한 값 반환
    }

    public int removeMaxKey() {
        int ret = keyList.get(keyList.size() - 1); //반환을 위해 기록
        keyList.remove(keyList.size() - 1); //가장 큰 값을 제거
        return ret; //삭제한 값 반환
    }

    public int removeMinKey() {
        int ret = keyList.get(0); //반환을 위해 기록
        keyList.remove(0); //가장 작은 값을 제거
        return ret; //삭제한 값 반환
    }

    public boolean isEmpty() {
        return keyList.isEmpty(); //key List가 비어있는지 확인
    }

    public MyBPlusTreeNode getLeftChild(MyBPlusTreeNode baseChild) {
        //baseChild를 기준으로 왼쪽 child 반환
        int index = children.indexOf(baseChild);
        if (index == 0) { //왼쪽값이 없는 경우 null 반환
            return null;
        }
        return children.get(index - 1);
    }

    public MyBPlusTreeNode getRightChild(MyBPlusTreeNode baseChild) {
        //baseChild를 기준으로 오른쪽 child 반환
        int index = children.indexOf(baseChild);
        if (index == children.size() - 1) { //오른쪽 값이 없는 경우 null 반환
            return null;
        }
        return children.get(index + 1);
    }

    public MyBPlusTreeNode getMinChild() {
        //가장 작은 child node 반환
        return children.get(0);
    }

    public MyBPlusTreeNode getMaxChild() {
        //가장 큰 child node 반환
        return children.get(children.size() - 1);
    }

    public void changeParent(MyBPlusTreeNode toParent) {
        //이 노드의 parent를 toParent로 바꾸는 함수
        //fromParent에 기존 parent 기록
        MyBPlusTreeNode fromParent = this.parent;

        //fromParent의 children 리스트에서 현재 노드를 삭제
        fromParent.removeChild(this);
        //toParent의 children 리스트에 현재 리스트 추가
        toParent.addChild(this);
        //이 리스트의 parent를 toParent로 지정
        this.parent = toParent;
    }

    public int getMaxKey() {
        //keylist에서 가장 큰 key 반환
        return keyList.get(keyList.size() - 1);
    }

    public int getMinKey() {
        //keylist에서 가장 작은 key 반환
        return keyList.get(0);
    }

    public int getChildIdx(MyBPlusTreeNode child) {
        //children list에서 해당 child의 인덱스 반환
        return this.children.indexOf(child);
    }

    public static MyBPlusTreeNode mergeNodes(MyBPlusTreeNode parent, MyBPlusTreeNode n1, MyBPlusTreeNode n2) {
        //n1, n2 노드를 parent 노드를 부모로 하여 merge
        return new MyBPlusTreeNode(parent, n1, n2);
    }

    public void setParentNull() {
        //부모 연결을 끊어줌
        this.parent = null;
    }

    public void showKeys() {
        this.keyList.forEach(e -> {
            System.out.println(e);
        });
    }
}
