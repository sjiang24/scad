package transformation;

import scad.Polyhedron;
import scad.Vector;

/**
 *
 * @author Dov
 */
public class Translation extends Transformation {

    private Vector move;

    public Translation(Vector v) {
        this.move = v;
    }

    public Vector getV() {
        return move;
    }

    @Override
    public Transformation inverse() {
    return new Translation(move.mult(-1));
    }

    
    
    @Override
    public void transform(Vector v) {
        v.x += move.x;
        v.y += move.y;
        v.z += move.z;
    }

}
