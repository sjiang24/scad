package transformation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import scad.Polyhedron;
import scad.Vector;

/**
 *
 * @author Dov
 */
public class ZRotation extends Rotation {

    public ZRotation(double radians) {
        super(radians);
    }

    @Override
    public void transform(Vector v) {
        double x = v.x;
        v.x = cos * x + sin * v.y;
        v.y = -sin * x + cos * v.y;
    }

    @Override
    public Transformation inverse() {
        return new ZRotation(-radians);
    }

}
