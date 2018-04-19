package scad;

import BinaryCombinations.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Collections;
import primitives.ConvexPoly;
import transformation.*;

public class Polyhedron extends ArrayList<Facet> {

    protected ArrayList<Vector> vertices;//always use VertsIter

    public ArrayList<Vector> getVertices() {
        return new VertsItr().all();
    }

    public class VertsItr {

        private void setVertices() {
            vertices = new ArrayList<>(size() * 2);
            for (int i = 0; i < size(); i++)
                for (Vector vec : get(i))
                    if (!vertices.contains(vec))
                        vertices.add(vec);
        }

        int i = 0;

        public VertsItr() {
            if (vertices == null)
                setVertices();
        }

        public boolean done() {
            return i >= vertices.size();
        }

        public void next() {
            i++;
        }

        public Vector val() {
            return vertices.get(i);
        }

        public ArrayList<Vector> all() {
            return vertices;
        }

    }

    public VertsItr getVertsItr() {
        return new VertsItr();
    }

    public int numVertices() {
        VertsItr vi = new VertsItr();
        return vertices.size();
    }

    public Polyhedron(int size) {
        super(size);
    }

    public Polyhedron() {
    }

    public Polyhedron flip() {
        Polyhedron flip = new Polyhedron(size());
        for (Facet facet : this)
            flip.add(facet.flip());
        return flip;
    }

    private boolean flatOnPlane(Facet cleavingPlane) {
        for (Facet facet : this)
            if (!facet.onSamePlaneAs(cleavingPlane))
                return false;
        return true;
    }

    public boolean is3D(Facet cleavingPlane) {
        if (isEmpty())
            return false;
        if (flatOnPlane(cleavingPlane))
            return false;
        return true;
    }

    protected BSPTree bspTree;
    protected boolean shuffled = false;

    public boolean shuffle() {
        if (shuffled)
            return false;
        shuffled = true;
        Collections.shuffle(this);
        return true;
    }

    public Split<Polyhedron> cleave(Polyhedron meat) {
        shuffle();
        Polyhedron in = new Polyhedron(), out = new Polyhedron();
        getBspTree().polyCleave(meat, in, out);
        return new Split<>(in, out);
    }

    public Polyhedron difference(Polyhedron p) {
        return new Difference(this, p);
    }

    public Polyhedron union(Polyhedron p) {
        return new Union(this, p);
    }

    public Polyhedron intersect(Polyhedron p) {
        return new Intersection(this, p);
    }

    public String stlString() {
        StringBuilder sb = new StringBuilder();
        for (Facet f : this)
            f.stlString(sb);
        return sb.toString();
    }

    //removes redundancies and empty facets.  Should never need to be called.
    private void clean() {
        for (int facet = 0; facet < size(); facet++) {
            if (get(facet).size() < 3)
                remove(facet);
            get(facet).clean();

        }
    }

    public ArrayList<Triangle> triangles() {
        //clean();
        ArrayList<Triangle> triangles = new ArrayList<>(2 * size());
        for (Facet f : this)
            if (f.isCoPlanar())
                triangles.addAll(f.triangles());
            else
                throw new ArithmeticException("bad facet " + f);
        return triangles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Facet f : this)
            sb.append(f).append("\n");
        return sb.toString();
    }

    @Override
    public Polyhedron clone() {
        Polyhedron clone = sameType();
        clone.ensureCapacity(size());
        this.forEach((f) -> {
            clone.add(f.clone());
        });
        return clone;
    }

    @Override
    public boolean add(Facet e) {
        if (bspTree != null)
            bspTree.add(e);
        shuffled = false;
        return super.add(e); //To change body of generated methods, choose Tools | Templates.
    }

    public BSPTree getBspTree() {
        if (bspTree == null)
            bspTree = new BSPTree(this);
        return bspTree;
    }

    /**
     * This function should be overriden whenever possible.
     *
     * @param v
     * @return
     */
    public Side definitely(Vector v) {
        return getBspTree().side(v);
    }

    public Side definitely(Edge e) {
        return Side.SURFACE;//TODO: This needs to be made better!
    }

    public final Side definitely(Facet f) {

        int i = 0;

        Side side = definitely(f.edge(i));

        while (i < f.size() && side == Side.IN) {
            i++;
            if (i < f.size()) side = definitely(f.edge(i));
        }

        if (i == f.size()) return Side.IN;

        if (i > 0) return Side.SURFACE;

        while (i < f.size() && side == Side.OUT) {
            i++;
            if (i < f.size()) side = definitely(f.edge(i));
        }

        if (i == f.size()) return Side.OUT;

        return Side.SURFACE;
    }

    public final void definitely(Polyhedron p, Polyhedron out, Polyhedron in, Polyhedron unsure) {
        for (Facet f : p)
            switch (definitely(f)) {
                case OUT:
                    if (out != null)
                        out.add(f);
                    break;
                case IN:
                    if (in != null)
                        in.add(f);
                    break;
                case SURFACE:
                    if (unsure != null)
                        unsure.add(f);
            }
    }

    public Polyhedron sameType() {
        return new Polyhedron();
    }

    public int badFacets() {
        int numBadFacets = 0;
        for (Facet f : this) if (!f.isCoPlanar()) numBadFacets++;
        return numBadFacets;
    }

    public Polyhedron transform(Transformation t) {
        return t.transform(this);
    }

    public Polyhedron translate(Vector t) {
        transform(new Translation(t));
        return this;
    }

    public Polyhedron translate(double x, double y, double z) {        
        return translate(new Vector(x, y, z));
    }

    public Polyhedron translateX(double x) {
        transform(new XTranslation(x));
        return this;
    }

    public Polyhedron translateY(double y) {
        transform(new YTranslation(y));
        return this;
    }

    public Polyhedron translateZ(double z) {
        transform(new ZTranslation(z));
        return this;
    }

    public Polyhedron rotateX(double a) {
        transform(new XRotation(a));
        return this;
    }

    public Polyhedron rotateY(double a) {
        transform(new YRoation(a));
        return this;
    }

    public Polyhedron rotateZ(double a) {
        transform(new ZRotation(a));
        return this;
    }

    public Polyhedron scale(double sx, double sy, double sz) {
        transform(new Scale(new Vector(sx, sy, sz)));
        return this;
    }

    public Polyhedron scale(double a) {
        transform(new Scale(a));
        return this;
    }

    public Polyhedron scaleX(double a) {
        transform(new XScale(a));
        return this;
    }

    public Polyhedron scaleY(double a) {
        transform(new YScale(a));
        return this;
    }

    public Polyhedron scaleZ(double a) {
        transform(new ZScale(a));
        return this;
    }

}
