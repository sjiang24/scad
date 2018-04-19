package transformation;

import scad.Polyhedron;
import scad.Vector;

/**
 *
 * @author Dov
 */
public abstract class Transformation {

    public abstract void transform(Vector v);
   
    public abstract Transformation inverse();

    public final Polyhedron transform(Polyhedron p) {
        for (Polyhedron.VertsItr v = p.getVertsItr(); !v.done(); v.next()) {
            transform(v.val());
        }
        return p;
    }
    public Transformation transform(Transformation t2){
        Transformation t1 = this;
        return new Transformation() {
            @Override
            public void transform(Vector v) {
                t2.transform(v);
                t1.transform(v);
            }

            @Override
            public Transformation inverse() {
                return t1.inverse().transform(t2.inverse());
            }
        };
    } 
}
