package scad;

import java.util.ArrayList;
import static scad.Vector.EPSILON;

/**
 * counter clockwise. We assume unicity of vertices, i.e. no vertex coordinates
 * are duplicated and adjacent loops share common vertices.Facets are assumed
 * convex and described with a single loop. https://arxiv.org/pdf/1706.01558.pdf
 *
 * @author dov
 */
public class Facet extends ArrayList<Vector> {

    public Facet(int size) {
        super(size);
    }

    public Facet(Vector v1, Vector v2, Vector v3) {
        super(3);
        add(v1);
        add(v2);
        add(v3);
    }

    public Facet(Vector v1, Vector v2, Vector v3, Vector v4) {
        super(4);
        add(v1);
        add(v2);
        add(v3);
        add(v4);
    }

    public Vector normal() {
        return Angle.normal(get(1), get(size() - 1), get(0));
    }

    /**
     * must be added in order.
     *
     * @param e
     * @return
     */
    public void add(Edge e) {
        if (isEmpty() || e.a != get(size() - 1)) {
            add(e.a);
            if (e.b != get(0))
                add(e.b);
        } else if (e.b != get(0))
            add(e.b);
    }

    public ArrayList<Triangle> triangles() {
        ArrayList<Triangle> triangles = new ArrayList<>(size() - 2);
        for (int i = 1; i < size() - 1; i++)
            triangles.add(new Triangle(get(0), get(i), get(i + 1)));
        return triangles;
    }

    public Edge edge(int i) {
        if (i == size() - 1)
            return new Edge(get(i), get(0));
        return new Edge(get(i), get(i + 1));
    }

    public Facet flip() {
        Facet flip = new Facet(size());
        for (int i = 0; i < size(); i++)
            flip.add(get(size() - 1 - i));
        return flip;
    }

    private boolean allSide(Side side, Facet meat) {
        for (Vector v : meat)
            if (side(v) != side) return false;
        return true;
    }

    private void addToBoth(Vector v, Facet a, Facet b) {
        a.add(v);
        b.add(v);
    }

    /**
     * The point relative to this facet.
     *
     * @param v
     * @return
     */
    public double compare(Vector v) {
        return v.minus(get(0)).dot(normal());
    }

    /**
     * The point relative to this facet.
     *
     * @param v
     * @return
     */
    public boolean below(Vector v) {
        return compare(v) < -EPSILON;
    }

    /**
     * The point relative to this facet.
     *
     * @param v
     * @return
     */
    public boolean above(Vector v) {
        return compare(v) > EPSILON;
    }

    /**
     * The point relative to this facet.
     *
     * @param v
     * @return
     */
    public Side side(Vector v) {
        double c = compare(v);
        if (c > EPSILON)
            return Side.OUT;
        if (c < -EPSILON)
            return Side.IN;
        return Side.SURFACE;
    }

    public boolean coPlanar(Vector v) {
        return Math.abs(compare(v)) < EPSILON;
    }

    public void cleave(Facet meat, Polyhedron in, Polyhedron out) {
        if (allSide(Side.OUT, meat)) out.add(meat);
        else if (allSide(Side.IN, meat)) in.add(meat);
        else {
            Split<Facet> split = cleave(meat);
            if (split.hasInside()) in.add(split.inside());
            if (split.hasOutside()) out.add(split.outside());
        }
    }

    public Split<Facet> cleave(Facet meat) {

        Facet in = new Facet(size()), out = new Facet(size());

        Side compare, nextCompare = side(meat.get(0));

        for (int i = 0; i < meat.size(); i++) {

            compare = nextCompare;
            nextCompare = side(meat.sGet(i + 1));

            switch (compare) {
                case OUT:
                    out.add(meat.get(i));
                    if (nextCompare == Side.IN)
                        addToBoth(intersect(meat.edge(i)), out, in);
                    break;
                case IN:
                    in.add(meat.get(i));
                    if (nextCompare == Side.OUT)
                        addToBoth(intersect(meat.edge(i)), out, in);

                    break;
                case SURFACE:
                    Side prevCompare = side(meat.sGet(i - 1));

                    if (prevCompare == Side.OUT && nextCompare == Side.OUT)
                        out.add(meat.get(i));
                    else if (prevCompare == Side.IN && nextCompare == Side.IN)
                        in.add(meat.get(i));
                    else if (prevCompare == Side.SURFACE && nextCompare == Side.SURFACE)
                        return new Split<>(this, this);
                    else addToBoth(meat.get(i), out, in);

            }
        }

        if (in.size() < 3) in = null;
        if (out.size() < 3) out = null;
        return new Split<>(in, out);

    }

    public Vector intersect(Edge e) {
        double den = (normal().dot(e.a) - normal().dot(e.b));
        return e.at((normal().dot(get(1)) - normal().dot(e.b)) / den);
    }

    public Split<Edge> cleave(Edge e) {

        Edge inside = null, outside = null;
        Side a = side(e.a), b = side(e.b);

        if (a == Side.SURFACE && b == Side.SURFACE)
            inside = outside = e;
        else if (a == Side.IN && b == Side.IN)
            inside = e;
        else if (a == Side.OUT && b == Side.OUT)
            outside = e;
        else {
            Vector intersection = intersect(e);
            if (a == Side.IN) {
                inside = new Edge(e.a, intersection);
                outside = new Edge(intersection, e.b);
            } else {
                outside = new Edge(e.a, intersection);
                inside = new Edge(intersection, e.b);
            }
            if (inside.length() < EPSILON)
                inside = null;
            if (outside.length() < EPSILON)
                outside = null;
        }
        return new Split(inside, outside);
    }

    public Split<Polyhedron> cleave(Polyhedron meat) {//warning, these are not complete polyhedron
        Polyhedron inside = new Polyhedron(size());
        Polyhedron outside = new Polyhedron(size());

        for (Facet meatFacet : meat) cleave(meatFacet, inside, outside);

        if (!inside.is3D(this)) inside = null;
        if (!outside.is3D(this)) outside = null;

        return new Split<>(inside, outside);
    }

    public void stlString(StringBuilder sb) {
        ArrayList<Triangle> triangles = triangles();
        for (Triangle tri : triangles) {
            sb.append("\nfacet normal");
            normal().stlString(sb);
            sb.append("\nouter loop");
            for (Vector v : tri) {
                sb.append("\nvertex");
                v.stlString(sb);
            }
            sb.append("\nendloop\nendfacet\n");
        }
    }

    public Facet clean() {

        for (int i = 0; i < size(); i++)
            for (int j = i + 1; j < size(); j++)
                if (get(i).equals(get(j)))
                    remove(j);

        for (int i = 0; i < size(); i++)
            if (sGet(i + 1).isColinear(get(i), sGet(i + 2)))
                remove(sIndex(i));

        if (size() < 3 || !isCoPlanar())
            return null;

        return this;
    }

    public boolean isCoPlanar() {
        if (size() < 3)
            return false;

        for (int i = 1; i < size(); i++)
            if (normal().cross(Angle.normal(sGet(i + 1), get(i - 1), get(i)))
                    .abs() > Math.max((edge(0).length()) * EPSILON, EPSILON)) {
                System.err.println("facet is not coplanar, the descrepancy is = " + normal().cross(Angle.normal(sGet(i + 1), get(i - 1), get(i)))
                        .abs());
                return false;
            }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isCoPlanar())
            sb.append("not vallid\n");
        sb.append("size = ").append(size()).append('\n');
        for (Vector vec : this)
            sb.append(vec).append('\n');
        return sb.append('\n').toString();
    }

    @Override
    public Facet clone() {
        Facet clone = new Facet(size());
        for (Vector v : this)
            clone.add(v.clone());
        return clone;
    }

    /**
     * merges in a facet that shares to consecutive points with this one.
     *
     * @param f
     */
    public void mergeIn(Facet f) {
        //TODO:This can be made faster!  And, needs to be tested.  I suspect it does not work.

        if (f.contains(get(0)) && f.contains(get(size() - 1)))
            add(0, get(size() - 1));
        for (int i = 0; i < size(); i++)
            if (f.contains(get(i))) {
                for (int j = (f.indexOf(get(i)) + 1) % f.size(), k = 0; k < f.size() - 2; j = (j + 1) % f.size(), k++)
                    add(i + k, f.get(j));
                break;
            }
        if (clean() == null)
            throw new ArithmeticException("Facet cleaned to elimination.");
    }

    public boolean equals(Facet o) {
        if (!o.contains(get(0)))
            return false;
        clean();
        o.clean();
        for (int i = 0, j = o.indexOf(get(0)); i < size(); i++, j = (j + 1) % size())
            if (!get(i).equals(o.get(j)))
                return false;
        return true;
    }

//    public boolean safeAdd(Vector vec){
//        if(size() >= 3 && !vec.coPlanar(this)) return false;
//        
//    }
    private Vector midPoint() {
        Vector mid = new Vector(0, 0, 0);
        for (Vector vec : this)
            mid = mid.plus(vec);
        return mid.times(1.0 / size());
    }

    public boolean contains(Vector p) {
        if (!coPlanar(p))
            return false;
        for (int i = 0; i < size(); i++)
            if (edgeWall(i).above(p))
                return false;
        return true;
    }

    public boolean onEdge(Vector p) {
        for (int i = 0; i < size() - 1; i++)
            if (edge(i).contains(p))
                return true;
        return false;
    }

    /**
     * safe get treats the facet as a circle
     *
     * @param i
     * @return
     */
    public Vector sGet(int i) {
        return get(sIndex(i));
    }

    private int sIndex(int i) {
        if (i < 0)
            return sIndex(size() + i);
        return i % size();
    }

    public static Facet testFacet() {
        return new Facet(new Vector(0, 0, 0), new Vector(1, 0, 0), new Vector(0, 1, 0));
    }

    public boolean convexCheck() {
        Vector test = normal().normalized();
        for (int i = 1; i < size(); i++)
            if (!test.equals(Angle.normal(get(i - 1), get(i), sGet(i + 1)).normalized()))
                return false;
        return true;
    }

    public Angle getAngle(int i) {
        return new Angle(sGet(i + 1), sGet(i - 1), sGet(i));
    }

    public Facet edgeWall(int i) {
        Vector out = Angle.normal(sGet(i - 1), sGet(i + 1), sGet(i));
        return new Facet(sGet(i), out.plus(sGet(i)), sGet(i + 1));
    }

    private int belowFacet(Vector insertion) {
        int i = 0;
        for (int j = 0; j < size() && edgeWall(i).above(insertion); j++, i--);
        if (i == -size())
            throw new ArithmeticException("Bad Facet");
        return i;
    }

    private int firstAboveFacet(Vector insertion, int i) {
        for (int j = 0; j < size() && edgeWall(i).below(insertion); j++, i++);
        return i;
    }

    /**
     * insert a point into a facet when it may disrupt the shapes convexity.
     *
     * @param insertion
     */
    public void sInsert(Vector insertion) {
        if (size() < 3) {
            add(insertion);
            return;
        }

        if (!coPlanar(insertion))
            throw new ArithmeticException("not coplanar");

        int i = firstAboveFacet(insertion, belowFacet(insertion));
        if (i == size())
            return;

        while (edgeWall(i + 1).above(insertion) && size() > 2)
            remove(sIndex(i + 1));

        add(sIndex(i + 1), insertion);
    }

    /**
     * Use to insert a point when it is known the point will not disrupt the
     * shapes convexity.
     *
     * @param insertion
     */
    public void insert(Vector insertion) {

        Angle max = new Angle(get(0), get(1), insertion);
        int maxInd = 0;

        for (int i = 1; i < size(); i++) {
            Angle temp = new Angle(get(i), sGet(i + 1), insertion);
            if (temp.compare(max) > 0) {
                max = temp;
                maxInd = i;
            }
        }

        add(maxInd + 1, insertion);

    }

    private static final int DEF_SIZE = 3;

    public Facet() {
        this(DEF_SIZE);
    }

    public static void main(String[] args) {
        Facet f = new Facet();
        f.add(new Vector(0, 0, 0));
        f.add(new Vector(1, 0, 0));
        f.add(new Vector(1, 1, 0));

        f.sInsert(new Vector(2, 3, 0));
        f.sInsert(new Vector(0, 1, 0));
        f.sInsert(new Vector(1.1, .5, 0));

        System.out.println(f);

//        Angle a = new Angle(new Vector(1, 0, 0), new Vector(0, 1, 0));
//
//        for (double theta = 0; theta < 2 * Math.PI; theta += .1){
//            System.out.println("theta = " + (float)(theta/Math.PI) + " pi\tz = " + new Angle(new Vector(1, 0, 0), new Vector(Math.cos(theta), Math.sin(theta), 0)).compare(a));
//        }
    }

    public boolean onSamePlaneAs(Facet plane) {
        if (normal() != plane.normal())
            return false;
        return plane.coPlanar(get(0));
    }

    public Facet backSide() {//TODO:this should be moved to parent class

        final double EPSILON = .1;
        Vector shift = normal().normalized().times(-EPSILON);
        Facet backside = new Facet(size());
        for (Vector v : this)
            backside.add(v.plus(shift));
        return backside;
    }



    public boolean sharesEdgeWith(Facet f) {
        for (int i = 0; i < size(); i++)
            for (int j = 0; j < f.size(); j++)
                if (edge(i).nonDirectionalEquals(f.edge(j)))
                    return true;
        return false;
    }
}
