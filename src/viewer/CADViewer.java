//TODO:  save polyhedrons as list of facets similar to stl triangles
//also save constructor (analytical) information.
//make a demo/test file
//write method to test how long something takes



/**
 * This project is licensed under GPL 2.1. See @link License.html
 */
package viewer;

import processing.core.*;
import java.util.ArrayList;
import primitives.Cube;
import primitives.Sphere;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import scad.Polyhedron;
import scad.hull.ConvexHull;

/**
 *
 * @author dkruger Draw SCAD shapes as solids, as wire frame, and show volumes
 * being subtracted also for ease of debugging models.
 */
public class CADViewer extends PApplet {

    private ArrayList<PShape> display;
    private int origx, origy;
    private float rotx, roty;
    private float translatex, translatey;
    private float zoom;

    private static ArrayList<Polyhedron> polyhedrons;
    
    static {
        polyhedrons = new ArrayList<>();
    }
    
    public static void addPolyhedron(Polyhedron p){
        polyhedrons.add(p);
    }
        
    public void settings() {
        size(1000, 700, P3D);
        resetView();
    }

    public void resetView() {
        rotx = 0; roty = 0;
        translatex = 0; translatey = 0;
        zoom = 1;
    }
    
    

    public void setup() {
        display = new ArrayList<>(1024);        
        for(Polyhedron p: polyhedrons) display.add(ShapeView.create(this, p));
        
//        display.add(ShapeView.create(this, Test.unionTest(60)));
//        display.add(ShapeView.create(this, Test.ConvexHullTest(15)));
                
    }

    public void draw() {
        background(0);
        translate(width / 2, height / 2);
        translate(translatex, translatey, 0);
        rotateX(rotx);
        rotateY(roty);
        scale(zoom);
        for (PShape p : display) {
            shape(p);
        }
    }

    @Override
    public void keyPressed() {
        if (key == 'r') {
            resetView();
        }
    }

    @Override
    public void mousePressed() {
        origx = mouseX;
        origy = mouseY;
    }

    private final float roationSpeed = 6;

    @Override
    public void mouseDragged() {
        int dx = (mouseX - origx), dy = (mouseY - origy);
        origx = mouseX;
        origy = mouseY;

        if (mouseButton == LEFT) {

            roty += roationSpeed * dx / width;
            rotx += -1 * roationSpeed * dy / height;
        } else if (mouseButton == RIGHT) {
            translatex = dx;
            translatey = dy;
        }
    }

    @Override
    public void mouseWheel(MouseEvent e) {
        float c = e.getCount();
        if (c > 0) {
            zoom /= 1.05f;
        } else {
            zoom *= 1.05f;
        }
    }

    public void keyPressed(KeyEvent event) {
        if(38 == event.getKeyCode()) zoom *= 1.05f;
        if(40 == event.getKeyCode()) zoom /= 1.05f;
    }

    
    
    

    public static void main(String[] args) {
        CADViewer.main("viewer.CADViewer");
    }
    
    public static void openWindow(Polyhedron p){
        addPolyhedron(p);
        CADViewer.main("viewer.CADViewer");
    }

}
