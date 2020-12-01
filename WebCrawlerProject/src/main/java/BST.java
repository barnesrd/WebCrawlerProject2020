/**
 *  Name: Andrew Piro-Yusico and Ryan Barnes
 *  Desc: Binary search tree used to organize link data
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
    
    public void insert(Node newNode){
        // If the tree is empty then we have to insert it as the head
        if(this.head == null){
            this.head = newNode;
            this.size++;
            return;
        }
        Node parent = findParentRec(this.head, newNode);
        // Final checks to see where the node goes
        if(parent.getInstances() > newNode.getInstances()){
            parent.setLeft(newNode);
            newNode.setParent(parent);
            this.size++;
        }
        else if(parent.getInstances() < newNode.getInstances()){
            parent.setRight(newNode);
            newNode.setParent(parent);
            this.size++;
        }
        // Implementation of linked list for when instances are equal
        else{
            link(parent, newNode);
        }
    }
    
    public Node findParentRec(Node currNode, Node putNode){
        Node goNode;
        // If placed node is less than current node
        if(currNode.getInstances() > putNode.getInstances()){
            goNode = currNode.getLeft();
        }
        // If placed node is greater than current node
        else if(currNode.getInstances() < putNode.getInstances()){
            goNode = currNode.getRight();
        }
        // If they are equal
        else{
            goNode = currNode;
        }
        // If the goNode returns a null then we found our parent node
        if(goNode == null){
            return currNode;
        }
        // If the goNode is the same as currNode, we need to implement linked lists,
        // but that will happen in the insert method
        else if(goNode == currNode){
            return currNode;
        }
        // The goNode found an existing node and must run through the method again
        else{
            return findParentRec(goNode, putNode);
        }
    }
    
    public void link(Node base, Node newNode){
        Node iterator = base;
        while(iterator.getNext() != null){
            iterator = iterator.getNext();
        }
        iterator.setNext(newNode);
        this.size++;
    }
    
    public void printTree(Node currNode){
        // To the right
        if(currNode.getRight() != null){
            printTree(currNode.getRight());
        }
        // For printing the current node
        if(currNode.getNext() == null){
            System.out.format("| % 4d | " + currNode.getAttachedURL() + "\n", currNode.getInstances());
        }
        // If it is linked, print out linked nodes as well
        else{
            Node iterator = currNode;
            while(iterator.getNext()!= null){
                System.out.format("| % 4d | " + iterator.getAttachedURL() + "\n", iterator.getInstances());
                iterator = iterator.getNext();
            }
            System.out.format("| % 4d | " + iterator.getAttachedURL() + "\n", iterator.getInstances());
        }
        // To the left
        if(currNode.getLeft() != null){
            printTree(currNode.getLeft());
        }
        
    }

    public Node getHead() {
        return head;
    }

    public int getSize() {
        return size;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public void setSize(int size) {
        this.size = size;
    }

    
 
}

