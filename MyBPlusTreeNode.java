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

    //생성자 나중에 맨 위로 올려라!!
    public MyBPlusTreeNode(int key, MyBPlusTreeNode left, MyBPlusTreeNode right) {
        //쪼개지는 경우 위로 올라가는 노드를 생성하는 생성자
        parent = null;
        keyList = new ArrayList<>();
        keyList.add(key);
        children = new ArrayList<>();
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

        for (MyBPlusTreeNode child : this.children) {
            child.parent = this;
        }
    }

    public void deleteChild(MyBPlusTreeNode node) {
        if (this.children != null) {
            this.children.remove(node);
        }
    }

    public MyBPlusTreeNode getSubNode(int startIdx, int endIdx) {
        List<Integer> subKeys = this.keyList.subList(startIdx, endIdx + 1);
        List<MyBPlusTreeNode> subChildren = new ArrayList<>();
        if (!this.children.isEmpty()) {
            subChildren = new ArrayList<>(this.children.subList(startIdx, endIdx + 2));
        }

        //이 노드에서 일부를 쪼개고, 그 쪼갠 노드를 새로 만들어 반환
        MyBPlusTreeNode newNode = new MyBPlusTreeNode(this.parent, subKeys, subChildren);
        return newNode;
    }

    public void tempShowChild() {
        if (this.children.isEmpty()) {
            return;
        }
        System.out.print("child : ");
        this.children.forEach(e -> {
            e.keyList.forEach(ee -> {
                System.out.print(ee + " ");
            });
            System.out.print("//");
        });
        System.out.println();
    }

    public int getChildrenLength() {
        return this.children.size();
    }

    public void setChildren(List<MyBPlusTreeNode> children) {
        for (MyBPlusTreeNode child : children) {
            child.parent = this;
        }
        this.children = children;
    }

    public List<MyBPlusTreeNode> getSubChildren(int startIdx, int endIdx) {
        List<MyBPlusTreeNode> subChildren = new ArrayList<>(this.children.subList(startIdx, endIdx + 1));
        return subChildren;
    }

    public Boolean addKey(Integer key) {
        //키가 정렬되어 있으므로 이진탐색을 통해 빠르게 자리를 찾을 수 있다.
        //Collections.binarySearch는 리스트에서 원소를 찾지 못했을 경우 삽입 위치를 음수로 알려준다.
        //음수값은 -(삽입위치) - 1의 형식으로 반환된다.
        //따라서 현재 데이터를 삽입할 수 있는 위치 : (-반환값-1)
        int index = Collections.binarySearch(keyList, key);
        if (index >= 0) {
            System.out.println("이미 존재하는 key이므로 add할 수 없습니다.");
            return false; //이미 존재하는 key 이므로 삽입 실패
        }
        //index가 음수라면 존재하지 않는 key이므로 add해준다.
        index = -index - 1;
        //index자리에 key를 add하면 해당 위치부터 끝까지의 원소들이 뒤로 한 칸 밀리고, 그 자리에 key가 들어간다.
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

        this.children.add(index, child);
        child.parent = this;
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


    public int getKey(int idx) {
        return this.keyList.get(idx);
    }

    public MyBPlusTreeNode findChildNode(int key) {
        int index = Collections.binarySearch(keyList, key);
        if (index < 0) {
            index = -index - 1;
        } else {
            index = index + 1;
        }
        return this.children.get(index);
    }

    public Boolean isOverflow(int maxKeyCnt) {
        return keyList.size() > maxKeyCnt;
    }

    public Boolean isUnderflow(int minKeyCnt) {
        return keyList.size() < minKeyCnt;
    }

    public MyBPlusTreeNode getParent() {
        return this.parent;
    }

    public int getKeyListLength() {
        return this.keyList.size();
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public void tempShowKeys() {
        this.keyList.forEach(e -> {
            System.out.print(e + ", ");
        });
    }

    public void tempShowInfos() {
        System.out.print("나의 keys : ");
        tempShowKeys();
        System.out.print(" 나의 parent : ");
        if (this.parent == null) {
            System.out.print("null");
        } else {
            this.parent.keyList.forEach(e -> {
                System.out.print(e + " ");
            });
        }
        System.out.print(" 나의 children : ");
        this.children.forEach(e -> {
            e.keyList.forEach(ee -> {
                System.out.print(ee + " ");
            });
            System.out.print(" // ");
        });
        System.out.println(" 다음으로~");
        this.children.forEach(e -> {
            e.tempShowInfos();
        });
    }
}
