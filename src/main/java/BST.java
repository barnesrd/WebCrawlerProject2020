/**
 * Name: Ryan Barnes
 * Desc:
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
    
    Node head;
    int size;
    
    public BST(Node head){
        this.head = head;
        this.size = 1;
    }
    
    public BST(){
        this.head = null;
        this.size = 0;
    }
    
    
}
