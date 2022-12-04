package com.com.totoro;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @author Totoro
 * @create 06 15:07
 * @Description: leecode
 */
public class test {

    @Test
    public void test(){
        totalFruit(new int[]{3,3,3,1,2,1,1,2,3,3,4});
    }
    @Test
    public void test44(){

    }

    @Test
    public void test2(){

        ListNode head = new ListNode(1);
        ListNode p = head;
        p.next = new ListNode(2);
        p = p.next;
        p.next = new ListNode(6);
        p = p.next;
        p.next = new ListNode(3);
        p = p.next;
        p.next = new ListNode(4);
        p = p.next;
        p.next = new ListNode(5);
        p = p.next;
        p.next = new ListNode(6);

        removeElements(head,6);
    }

    @Test
    public void test111(){
        System.out.println(100%10);
    }

    @Test
    public void test3(){
        String s = "aacc";
        String t = "ccac";
        isAnagram(s,t);
        System.out.println(100%10);
    }

    public boolean isAnagram(String s, String t) {
        if(s.length()!=t.length()){
            return false;
        }
        HashMap<Character,Integer> bin = new HashMap<>();
        for(int i = 0;i<s.length();i++){
            char c = s.charAt(i);
            // bin.put(s.charAt(i),bin.getOrDefault(s.charAt(i),0)+1);
            bin.put(c,bin.getOrDefault(c,0)+1);
        }
        for(int j = 0;j<t.length();j++){
            if(bin.get(t.charAt(j))==null|| t.charAt(j)==0){
                return false;
            }
            bin.put(t.charAt(j),bin.get(t.charAt(j))-1);
        }
        return true;
    }

    public class ListNode{
        int val;
        ListNode next;
        ListNode(){}
        ListNode(int val){this.val =val;}
        ListNode(int val, ListNode next){this.val = val;this.next = next;}
    }

    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode p = dummy;
        while(p.next!=null){
            if(p.next.val==val){
                if(p.next.next==null){
                    p.next =null;
                }else{
                    p.next = p.next.next;}
            }
            if(p.next!=null){
            p = p.next;}
        }
        return dummy.next;
    }

    public int totalFruit(int[] fruits) {
        int left = 0,right = 0,res = 0,valid = 0;
        HashMap<Integer,Integer> window = new HashMap<>();

        while(right<fruits.length){
            int a = fruits[right];
            right++;
            if(window.get(a)==null||window.get(a)==0)valid++;
            window.put(a,window.getOrDefault(a,0)+1);
            // if(window.get(a)==1)valid++;
            while(valid>2){
                int b = fruits[left];
                window.put(b,window.get(b)-1);
                if(window.get(b)==0)valid--;
                left++;
            }
            if(valid==2){
                res = right-left>res?right-left:res;
            }
        }
        return res;
    }
}
