package transformation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import scad.Polyhedron;
import scad.Vector;

/**
 *
 * @author Dov
 */
public class YRoation extends Rotation {

    public YRoation(double radians) {
        super(radians);
    }

    @Override
    public Transformation inverse() {
        return new YRoation(-radians);
    }

    @Override
    public void transform(Vector v) {
        double x = v.x;
        v.x = cos * x + sin * v.z;
        v.z = -sin * x + cos * v.z;
    }
}
