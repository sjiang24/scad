package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class ZTranslation extends Translation{
    
    public ZTranslation(double z) {
        super(new Vector(0, 0, z));
    }
    
}
