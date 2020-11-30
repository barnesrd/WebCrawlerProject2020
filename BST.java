/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CrawlerPackage;
import CrawlerPackage.Node;
import java.util.*;
/**
 *
 * @author andre
 */
public class BST {
    /*
    A binary search tree data structure used to organize the data in offline
    ops for printout.
    TODO:
    We need to implement the Binary Search tree with 
        - Ability to sort when a node is inserted
        - Print tree in order
    */
    
    Node head; // the root of the tree  
    int size; // size of the tree
    
    public BST(Node head){
        this.head = head;
        this.size = 1;
    }
    
    public BST(){
        this.head = null;
        this.size = 0;
    }
    
    void insert(int instances)
    {
        head = insertRec(head,instances);
    }
    
    Node insertRec(Node head, int instances)
    {
        // if the tree is empty, return a new node
        if (head == null){
            head = new Node(instances);// this line of Code seems to complain about needing to have a String included to sort
            return head;
    }
        if (instances < head.instances)// other wise recur down the tree
            head.left = insertRec(head.left,instances);
        else if (instances > head.instances);
               head.right = insertRec(head.right, instances);
               
        return head; // return the root pointer
    }
    
    void inorderRec(Node head)
    {
        if (head != null){
            inorderRec(head.left);
            System.out.println(head.instances);
            inorderRec(head.right);
        }
    }
     public static void main(String[] args){
         
       BST tree = new BST();
       
       tree.insert(20);
         
         
     }

 
}

