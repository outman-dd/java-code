package code.algorithm.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/5
 */
public class P297 {

    static public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if(root == null){
            return "[]";
        }

        List<Integer> data = new ArrayList<>();
        List<TreeNode> nodeList = new ArrayList(1);
        nodeList.add(root);
        append(data, nodeList);

        int last = data.size()-1;
        //最后一个数字位置
        for(; last>=0; last--){
            if(data.get(last) != null){
                break;
            }
        }
        StringBuilder sb = new StringBuilder("[");
        for(int i=0; i<last; i++){
            sb.append(data.get(i)).append(",");
        }
        sb.append(data.get(last)).append("]");

        return sb.toString();
    }

    private void append(List<Integer> data, List<TreeNode> nodeList){
        List<TreeNode> children = new ArrayList<>();
        for (TreeNode node : nodeList){
            if(node != null){
                data.add(node.val);
                children.add(node.left);
                children.add(node.right);
            }else{
                data.add(null);
            }
        }
        if(!children.isEmpty()){
            append(data, children);
        }
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if(data.equals("[]")){
            return null;
        }
        String[] array = data.substring(1, data.length()-1).split(",");
        if(array.length == 0){
            return null;
        }

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        TreeNode root = new TreeNode(Integer.parseInt(array[0]));
        nodeQueue.add(root);

        int i = 0;
        while (!nodeQueue.isEmpty()){
            TreeNode node = nodeQueue.poll();
            if(++i >= array.length){
                break;
            }
            if(!array[i].equals("null")){
                node.left = new TreeNode(Integer.parseInt(array[i]));
                nodeQueue.add(node.left);
            }

            if(++i >= array.length){
                break;
            }
            if(!array[i].equals("null")){
                node.right = new TreeNode(Integer.parseInt(array[i]));
                nodeQueue.add(node.right);
            }
        }
        return root;
    }


    public static void main(String[] args) {
        // [1,2,3,null,null,4,5]
        TreeNode node = new TreeNode(1);
        node.left = new TreeNode(2);
        node.right = new TreeNode(3);

        node.right.left = new TreeNode(4);
        node.right.right = new TreeNode(5);

        P297 codec = new P297();
        System.out.println(codec.serialize(node));

        // [1,2,3,null,null,4,5,6,7]
        node.right.left.left = new TreeNode(6);
        node.right.left.right = new TreeNode(7);
        System.out.println(codec.serialize(node));

        // deserialize
        node = codec.deserialize("[1,2,3,null,null,4,5]");
        System.out.println(codec.serialize(node));

        node = codec.deserialize("[1,2,3,null,null,4,5,6,7]");
        System.out.println(codec.serialize(node));
    }
}
