package transformation;

import scad.Polyhedron;
import scad.Vector;

/**
 *
 * @author Dov
 */
public class Scale extends Transformation {

    @Override
    public Transformation inverse() {
        return new Scale(new Vector(1 / scale.x, 1 / scale.y, 1 / scale.z));
    }

    private Vector scale;

    public Scale(Vector scale) {
        this.scale = scale;
    }

    public Scale(double scale) {
        this.scale = new Vector(scale, scale, scale);
    }

    @Override
    public void transform(Vector v) {
        v.x *= scale.x;
        v.y *= scale.y;
        v.z *= scale.z;
    }

}
