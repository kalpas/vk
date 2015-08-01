package kalpas.VKCore.simple.DO;

import java.io.Serializable;

public class Relatives implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6641733849202592982L;

    public String id;
    // grandchild, grandparent, child, sibling, parent.
    public String type;
}