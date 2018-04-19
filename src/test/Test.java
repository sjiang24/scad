package test;

import BinaryCombinations.*;
import primitives.*;
import scad.*;
import scad.hull.ConvexHull;
import transformation.*;
import viewer.CADViewer;

/**
 *
 * @author dov
 */
public class Test {

    public Polyhedron p;
    public double time;

    public Test(Polyhedron p, double time) {
        this.p = p;
        this.time = time;
        
    }

    public static Test translateTest(int res) {
        int r = 200;
        int dist = 150;

        Polyhedron p = new Sphere(r, res);

        long T0 = System.nanoTime();
        p.transform(new XTranslation(dist));

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static Test rotateTest() {
        int r = 200;

        Cube p = new Cube(r);

        long T0 = System.nanoTime();
        p.transform(new ZRotation(Math.PI / 4));

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static Test unionTest(int res) {

        int r = 400;

        Sphere s1 = new Sphere(r, res);
        Polyhedron s2 = new Sphere(r, res).transform(new XTranslation(3 * r / 2));

        long T0 = System.nanoTime();
        Polyhedron p = new Union(s1, s2);

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static Test differenceTest(int res) {

        int r = 300;

        Sphere s1 = new Sphere(r, res);
        Polyhedron s2 = new Cylinder(r / 2, 2 * r, res);

        long T0 = System.nanoTime();
        Polyhedron p = new Difference(s1, s2);

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static double averageTime(Test... a) {
        int sum = 0;
        for (int i = 1; i < a.length; i++)
            sum += a[i].time;
        CADViewer.openWindow(a[0].p);
        return sum * 1.0 / a.length;
    }

    public static Test intersectionTest(int res) {
        long T0 = System.nanoTime();

        int r = 200;
        int dist = 150;

        Sphere s1 = new Sphere(r, res);

        Polyhedron s2 = new Sphere(r, res).transform(new XTranslation(r / 2));

        Polyhedron p = new Intersection(s1, s2);

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static Test ConvexHullTest(int res) {
        long T0 = System.nanoTime();

        int r = 200;
        int dist = 150;

        Sphere s1 = new Sphere(r, res);
        Polyhedron s2 = new Cube(r / 5).transform(new Translation(new Vector(2 * r, 0, 0)));

        Polyhedron p = new ConvexHull(new Union(s1, s2));

        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }

    public static Test multiBinaryTest(int res) {
        return multiBinaryTest(res, res);
    }
    public static Test multiBinaryTest(int sRes, int cRes) {//Do not change this test.  It reveals an important bug if I use unsureB instead of b in Union.unsure().
        Polyhedron c = new Sphere(100, sRes);
        Polyhedron c1 = new Cylinder(30, 230, cRes);
        Polyhedron c2 = new Cylinder(30, 230, cRes).transform(new YRoation(Math.PI / 2));

        long T0 = System.nanoTime();

        Polyhedron d = c.intersect(c2.union(c1));

        return new Test(d, (System.nanoTime() - T0) * 1e-9);

    }
    
    public static Test multiSphereTest(int n, int res){
        long T0 = System.nanoTime();
        Polyhedron p = new Sphere(100, res);
        for(int i = 1; i < n; i++)
            p = p.union(new Sphere(100, res).translateX(i*50));
        return new Test(p, (System.nanoTime() - T0) * 1e-9);
    }
    
    
    public static Test crossCylinderTest(int res) {
        Polyhedron c1 = new Cylinder(30, 300, res);
        Polyhedron c2 = new Cylinder(30, 200, res).transform(new YRoation(Math.PI / 2));

        long T0 = System.nanoTime();

        Polyhedron c3 = c1.union(c2);

        return new Test(c3, (System.nanoTime() - T0) * 1e-9);

    }

    public static void SphereTransformDefinitelyTest() {
        Sphere s = new Sphere(100, 100).transform(new XTranslation(201)).transform(new XTranslation(100));
        System.out.println(s.definitely(new Vector(300, 0, 0)));
    }

    public static void main(String[] args) {
        Test t1 = differenceTest(100);
         CADViewer.openWindow(t1.p);
//        Test t = multiSphereTest(4, 100);
//        System.out.println(t.time);
//        CADViewer.openWindow(t.p);
//        Polyhedron c1 = new Cylinder(30, 300, 3).rotateY(Math.PI/2);
//        CADViewer.openWindow(c1);
//        System.out.println(c1.numVertices());
    }
}
