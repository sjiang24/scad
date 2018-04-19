package scad.hull;

import java.util.ArrayList;
import primitives.ConvexPoly;
import primitives.Pyramid;
import scad.BSPTree;
import scad.Edge;
import scad.Facet;
import scad.Polyhedron;
import scad.Side;
import scad.Vector;

/**
 * Using the incremental convex hull algo, more or less.
 *
 * @author dov
 */
public final class ConvexHull extends ConvexPoly {

    private ArrayList<Vector> loadPoints;

    public ConvexHull(ArrayList<Vector> points) {
        super(points.size() * 3);
        loadPoints = points;

        if (points.size() < 3)
            return;

        int i = expandTri(firstTirangle());

        i = firstTetraHedron(i);

        for (; i < loadPoints.size(); i++)
            if (!addPoint(loadPoints.get(i)))
                loadPoints.remove(i--);

    }

    public boolean addPoint(Vector v) {
         if (definitely(v) != Side.OUT) return false;

        ArrayList<Facet> facingFacets = new ArrayList<>();
        for (int j = 0; j < size(); j++)
            if (!get(j).below(v))
                facingFacets.add(remove(j--));

        addAll(new Cone(facingFacets, v));
        return true;
    }

    private int extraPoint(int a, int b, int c) {
        Vector va = loadPoints.get(a),
                vb = loadPoints.get(b),
                vc = loadPoints.get(c);
        if (new Edge(va, vb).containsCL(vc))
            return c;
        if (new Edge(vb, vc).containsCL(vc))
            return a;
        return b;
    }

    private int firstTetraHedron(int i) {
        addAll(new Pyramid(remove(0), loadPoints.get(i)));

        return i + 1;
    }

    private int expandTri(int i) {
        while (get(0).contains(loadPoints.get(i)) && i < loadPoints.size())
            loadPoints.remove(i);

        while (get(0).coPlanar(loadPoints.get(i)))
            get(0).sInsert(loadPoints.get(i++));

        return i;
    }

    private int firstTirangle() {
        add(new SmartFacet(3));
        int i = 0;

        for (boolean cont = true; cont;) //Something seems to be wrong here.

            if (loadPoints.get(0).isColinear(loadPoints.get(1), loadPoints.get(2)))
                loadPoints.remove(extraPoint(0, 1, 2));
            else if (loadPoints.get(0).equals(loadPoints.get(1)))
                loadPoints.remove(1);
            else if (loadPoints.get(2).equals(loadPoints.get(1))
                    || loadPoints.get(2).equals(loadPoints.get(0)))
                loadPoints.remove(2);
            else
                cont = false;

        for (; i < 3; i++)
            get(0).add(loadPoints.get(i));

        return i;
    }

    public static void main(String args[]) {
        ArrayList<Vector> points = new ArrayList<>();
        points.add(new Vector(0, 0, 0));
        points.add(new Vector(5, 0, 0));
        points.add(new Vector(0, 5, 0));
        points.add(new Vector(0, 0, 5));
        points.add(new Vector(5, 5, 0));
//        points.add(new Vector(5, 0, 5));
//        points.add(new Vector(0, 5, 5));
//        points.add(new Vector(5, 5, 5));
        new ConvexHull(points);
    }

    public ConvexHull(Polyhedron a) {
        this(a.getVertices());
    }

}
