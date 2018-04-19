package primitives;

import scad.Vector;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;
import scad.Edge;
import scad.Facet;
import scad.Polyhedron;
import scad.Side;
import scad.Vector;
import transformation.Transformation;
import static scad.Vector.EPSILON;

public class Sphere extends ConvexPoly {

    public double r, rMin;

    public Sphere(double radius, int res) {
        this(radius, res, res);
        this.unHistory = new ArrayList<>();
    }

    public Sphere(double radius, int longRes, int latRes) {
        super(longRes * latRes);
        this.unHistory = new ArrayList<>();
        r = radius;
        double chord = 2 * r * sin(Math.PI / Math.min(longRes, latRes));
        rMin = Math.sqrt(r * r - chord * chord / 4);
        ArrayList<Vector> verts = new ArrayList<>(longRes * latRes + 2);

        latRes /= 2;

        // first compute a broad band around the sphere excluding the poles
        final double dphi = PI / latRes; // vertically only +90 to -90 degrees
        double phi = -PI / 2 + dphi;

        for (int j = 0; j < latRes - 1; ++j, phi += dphi) {
            double z = radius * sin(phi);
            double r2 = radius * cos(phi);

            double theta = 0;
            final double dtheta = 2 * PI / longRes;
            for (int i = 0; i < longRes; ++i, theta += dtheta) {
                double x = r2 * cos(theta), y = r2 * sin(theta);
                verts.add(new Vector(x, y, z)); // first compute all points
            }
        }

        // then create quads with all the points created
        for (int i = 0; i < verts.size() - longRes; i++)
            addSquare(i + longRes,
                    i,
                    (i % longRes == longRes - 1
                            ? -longRes : 0) + i + 1, //bottom right
                    (i % longRes == longRes - 1
                            ? -longRes : 0) + i + longRes + 1,//top right 

                    verts);

        final Vector south = new Vector(0, 0, -r),
                north = new Vector(0, 0, r);
        for (int i = 0; i < longRes - 1; i++) {
            add(new Facet(verts.get(i), south, verts.get(i + 1)));
            add(new Facet(verts.get(i + verts.size() - longRes), verts.get(i + verts.size() - longRes + 1), north));
        }
        add(new Facet(verts.get(longRes - 1), south, verts.get(0)));
        add(new Facet(verts.get(verts.size() - longRes), north, verts.get(verts.size() - 1)));

        vertices = verts;
        vertices.add(north);
        vertices.add(south);
    }

    private void addSquare(int a, int b, int c, int d, List<Vector> list) {
        add(new Facet(list.get(a), list.get(b), list.get(c), list.get(d)));
    }

    private ArrayList<Transformation> unHistory;

    @Override
    public Sphere transform(Transformation t) {
        unHistory.add(t.inverse());
        super.transform(t);
        return this;
    }

    @Override
    public Side definitely(Vector v) {
        v = v.clone();
        for (int i = unHistory.size() - 1; i >= 0; i--)
            unHistory.get(i).transform(v);
        
        double vR = v.magnitudeSq();
        if (vR > r*r + EPSILON) return Side.OUT;
        if (vR < rMin*rMin - EPSILON) return Side.IN;
        return Side.SURFACE;
    }

    private Edge edge = new Edge(new Vector(0, 0, 0), new Vector(0, 0, 0));
    @Override
    protected boolean definitelyOut(Edge e) {
        edge.setTo(e);
        Vector a = edge.a, b = edge.b;
        for (int i = unHistory.size() - 1; i >= 0; i--){
            unHistory.get(i).transform(a);
            unHistory.get(i).transform(b);
        }
        double t = -a.dot(b.minus(a))/b.minus(a).magnitudeSq();
        if(t < 0) t = 0;  if(t > 1) t = 1;
        return edge.at(t).magnitudeSq() > r*r + EPSILON;
    }

    @Override
    public boolean definitelyIn(Vector v) {
        v = v.clone();
        for (int i = unHistory.size() - 1; i >= 0; i--)
            unHistory.get(i).transform(v);
        return v.magnitudeSq() < rMin*rMin - EPSILON;
    }

    @Override
    public Sphere clone() {
        Sphere clone = (Sphere) super.clone();
        clone.r = r;
        clone.rMin = rMin;
        clone.unHistory = new ArrayList<>(unHistory);
        return clone;
    }

}
