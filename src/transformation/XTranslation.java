
package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class XTranslation extends Translation{
    
    public XTranslation(double x) {
        super(new Vector(x, 0, 0));
    }
    
}
