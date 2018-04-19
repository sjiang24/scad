package transformation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import scad.Vector;

/**
 *
 * @author Dov
 */
public abstract class Rotation extends Transformation{
    protected double sin, cos, radians;

    public Rotation(double radians) {
        this.cos = cos(radians);
        this.sin = sin(radians);
        this.radians = radians;
    }

}
