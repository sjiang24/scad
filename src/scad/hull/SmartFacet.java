package scad.hull;


import java.util.ArrayList;
import scad.*;
import scad.Triangle;

/**
 *
 * @author dov
 */
public class SmartFacet extends Facet{
    
    private ArrayList<SmartFacet> neigbors;

    public SmartFacet(ArrayList<SmartFacet> neigbors, Vector a, Vector b, Vector c) {
        super(a, b, c);
        this.neigbors = neigbors;
    }

    public SmartFacet(Facet facet, ArrayList<SmartFacet> neighbors) {
        for(Vector v: facet) add(v);
        this.neigbors = neighbors;
    }
    
    
    
    public void addNeighbor(SmartFacet f){
        neigbors.add(f);
    }

    public Facet getNeigbor(int i){
        return neigbors.get(i);
    }
    
    public Facet removeNeigbor(int i){
        return neigbors.remove(i);
    }

    public SmartFacet(Facet facet) {
        this(3);
        for(Vector v: facet) add(v);
        
    }
    
    public SmartFacet(int i) {
        super(i);
        neigbors = new ArrayList<>(3);
    }
    
    public void buddyUp(SmartFacet f){
        addNeighbor(f);
        f.addNeighbor(this);
    }
    
}
