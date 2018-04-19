package primitives;

import scad.Vector;

import scad.Facet;
import scad.Polyhedron;

/**
 *
 * @author Dov Kruger
 */
public class Cube extends ConvexPoly {

    public Vector corner1, corner2;
    public boolean center;

    private Cube(Vector v1, Vector v2) {
        super(6);
        Vector fbl = v1,  /*Front Bottom Right*/ fbr = new Vector(v2.x, v1.y, v1.z),
               ftl = new Vector(v1.x, v2.y, v1.z), ftr = new Vector(v2.x, v2.y, v1.z),
               bbl = new Vector(v1.x, v1.y, v2.z), bbr = new Vector(v2.x, v1.y, v2.z),
               btl = new Vector(v1.x, v2.y, v2.z), btr = v2;
        add(new Facet(ftl, fbl, fbr, ftr));//front
        add(new Facet(ftr, fbr, bbr, btr));//right
        add(new Facet(fbl, bbl, bbr, fbr));//bottom
        add(new Facet(ftl, btl, bbl, fbl));//left
        add(new Facet(btl, btr, bbr, bbl));//back
        add(new Facet(btl, ftl, ftr, btr));//top
    }

    public Cube(double s) {
        this(new Vector(s * .5, s * .5, s * .5), new Vector(-s * .5, -s * .5, -s / 2));
    }
}
