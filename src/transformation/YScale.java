package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class YScale extends Scale{
    
    public YScale(double scale) {
        super(new Vector(1, scale, 1));
    }
    
}
