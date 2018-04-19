package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class ZScale extends Scale{
    
    public ZScale(double z) {
        super(new Vector(1, 1, z));
    }
    
}
