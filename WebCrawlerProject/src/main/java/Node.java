/**
 * Name: Ryan Barnes
 * Desc:
 */

public class Node {
    private String attachedURL;
    private int instances;
    private Node next;
    private Node parent;
    private Node left;
    private Node right;
    
    public Node(String url, int instances, Node parent){
        this.parent = parent;
        this.attachedURL = url;
        this.instances = instances;
    }
    
    public Node(String url, int instances){
        this.attachedURL = url;
        this.instances = instances;
    }
    
    public Node(){
        
    }

    public String getAttachedURL() {
        return attachedURL;
    }

    public int getInstances() {
        return instances;
    }

    public void setAttachedURL(String attachedURL) {
        this.attachedURL = attachedURL;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getNext() {
        return next;
    }

    public Node getParent() {
        return parent;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    

}
