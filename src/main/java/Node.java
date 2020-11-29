/**
 * Name: Ryan Barnes
 * Desc:
 */

public class Node {
    private String attachedURL;
    private int instances;
    Node left;
    Node right;
    
    public Node(String url, int instances){
        this.attachedURL = url;
        this.instances = instances;
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
    
    

}
