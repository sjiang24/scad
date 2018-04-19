package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class YTranslation extends Translation{
    
    public YTranslation(double y) {
        super(new Vector(0, y, 0));
    }
    
}
