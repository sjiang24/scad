package primitives;

import scad.Vector;
import static java.lang.Math.*;
import java.util.ArrayList;
import scad.Edge;
import scad.Facet;
import scad.Polyhedron;
import scad.Side;
import transformation.Transformation;
import static scad.Vector.EPSILON;

public class Cylinder extends ConvexPoly {
    
    private final double r, rMin, h;

    public Cylinder(double r, double h, int res) {
        super(res * 3);
        this.unHistory = new ArrayList<>();
        this.h = h;
        this.r = r;
        double chord = 2 * r * sin(Math.PI / res);
        rMin = Math.sqrt(r * r - chord * chord / 4);
        vertices = new ArrayList<>();
        
        Facet bottom = new Facet(res), top = new Facet(res);
        add(top);
        add(bottom);
        top.add( new Vector(r, 0, h));
        bottom.add(new Vector(r, 0, 0));
        vertices.add(top.get(0));
        vertices.add(bottom.get(0));

        addBottomTopSide(
               top.get(0),
                bottom.get(0),
                new Vector(r * cos(2 * PI / res), r * sin(2 * PI / res), h),
                new Vector(r * cos(2 * PI / res), r * sin(2 * PI / res), 0), top, bottom);

        for (double theta = 4 * PI / res; theta < 2 * PI - EPSILON; theta += 2 * PI / res) {
            double x = r * cos(theta), y = r * sin(theta);

            addBottomTopSide(
                    get(size() - 1).get(3),
                    get(size() - 1).get(2),
                    new Vector(x, y, h),
                    new Vector(x, y, 0), top, bottom);
        }
        add(new Facet(get(size() - 1).get(3), get(size() - 1).get(2), get(2).get(1), get(2).get(0)));

        set(1, get(1).flip());
    }

    private final void addBottomTopSide(Vector topLeft, Vector bottomLeft, Vector topRight, Vector bottomRight, Facet top, Facet bottom) {
        vertices.add(topRight);
        vertices.add(bottomRight);
        top.add(topRight);
        bottom.add(bottomRight);
        add(new Facet(topLeft, bottomLeft, bottomRight, topRight));

    }

    private ArrayList<Transformation> unHistory;

    @Override
    public Cylinder transform(Transformation t) {
        unHistory.add(t.inverse());
        super.transform(t);
        return this;
    }

    
    
    @Override
    public Side definitely(Vector v) {

        v = v.clone();
        for (int i = unHistory.size() - 1; i >= 0; i--)
            unHistory.get(i).transform(v);

        if( v.z > h + EPSILON || v.z < -EPSILON) 
            return Side.OUT;
        
        double vd = v.x * v.x + v.y * v.y;
        
        if (vd > r * r + EPSILON) return Side.OUT;

        if (vd < rMin*rMin - EPSILON && v.z > EPSILON && v.z < h - EPSILON)
            return Side.IN;

        return Side.SURFACE;
    }
    
    @Override
    public boolean definitelyIn(Vector v) {

        v = v.clone();
        for (int i = unHistory.size() - 1; i >= 0; i--)
            unHistory.get(i).transform(v);

        double vd = v.x * v.x + v.y * v.y;

        if (vd < rMin*rMin - EPSILON && v.z > EPSILON && v.z < h - EPSILON)
            return true;

        return false;
    }

    private Edge s =  new Edge(new Vector(0, 0, 0), new Vector(0, 0, 0));
    @Override
    protected boolean definitelyOut(Edge e) {
        s.setTo(e);
        Vector a = s.a, b = s.b;
        
        for (int i = unHistory.size() - 1; i >= 0; i--) {
            unHistory.get(i).transform(a);
            unHistory.get(i).transform(b);
        }

        if (a.z > h + EPSILON && b.z > h + EPSILON
                || a.z < -EPSILON && b.z < -EPSILON) return true;

        double sDen = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);

        if (Math.abs(sDen) < EPSILON)
            if (a.x * a.x + a.y * a.y < r * r + EPSILON) return false;
            else return true;

        double s = -(a.x * (b.x - a.x) + a.y * (b.y - a.y)) / sDen,
               t = (a.z + (b.z - a.z) * s) / h;

        if (t < 0) t = 0;
        else if (t > 1) t = 1;
        if (s < 0) s = 0;
        else if (s > 1) s = 1;

        Edge edge = new Edge(a, b);

        
        if (edge.at(s).z < 0) {
            s = -a.z / (b.z - a.z);
            t = 0;
        }

        else if (edge.at(s).z > h) {
            s = (h - a.z) / (b.z - a.z);
            t = 1;
        }

        return edge.at(s).minus(new Vector(0, 0, t*h)).magnitudeSq() > r*r + EPSILON;
    }

}
