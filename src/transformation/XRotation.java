package transformation;

import scad.Vector;

/**
 *
 * @author Dov
 */
public class XRotation extends Rotation {

    public XRotation(double radians) {
        super(radians);
    }

    @Override
    public XRotation inverse() {
        return new XRotation(-radians);
    }

    @Override
    public void transform(Vector v) {
        double y = v.y;
        v.y = cos * y + sin * v.z;
        v.z = -sin * y + cos * v.z;
    }
}
