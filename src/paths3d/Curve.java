package paths3d;

import scad.Vector;

/**
 *
 * @author dov
 */
public interface Curve {
    public Vector getA();
    public Vector getB();
    public Vector at(double t);
}
