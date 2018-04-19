/**
 * This project is licensed under GPL 2.1. See @link License.html
 */
package viewer;

import scad.Triangle;
import processing.core.*;
import java.util.*;
import static processing.core.PConstants.*;
import scad.Facet;
import scad.Polyhedron;

/**
 *
 * @author dkruger Draw SCAD shapes as solids, as wireframe, and show volumes
 * being subtracted also for ease of debugging models.
 */
public class ShapeView {

    public static Color inColor = Color.purple(), outColor = new Color(180, 150, 0);

    public enum Mode {
        WIREFRAME, TRIANGLE, DOUBLEWALL
    };

    public static PShape create(PApplet p, Polyhedron s) {
        return create(p, s, ShapeView.Mode.DOUBLEWALL, Color.white());
    }

    public static PShape create(PApplet processing, Polyhedron polyhedron, Mode m, Color stroke) {

        PShape visual = processing.createShape();

        ArrayList<Triangle> triangles = polyhedron.triangles();
        
        switch (m) {
            case DOUBLEWALL:
                visual.beginShape(TRIANGLE);
                visual.stroke(stroke.r, stroke.g, stroke.b);

                visual.fill(inColor.r, inColor.g, inColor.b);

                for (Triangle t : triangles) {
                    Facet backSide = t.backSide();
                    for (scad.Vector v : backSide)
                        visual.vertex((float) v.x, (float) v.y, (float) v.z);

                }
                visual.endShape();
            case TRIANGLE:
                visual.beginShape(TRIANGLE);
                visual.stroke(stroke.r, stroke.g, stroke.b);
                visual.fill(outColor.r, outColor.g, outColor.b);
                for (Triangle t : triangles)
                    for (scad.Vector v : t)
                        visual.vertex((float) v.x, (float) v.y, (float) v.z);
                break;
          
            
            case WIREFRAME:
                visual.beginShape(LINES);
                visual.stroke(stroke.r, stroke.g, stroke.b);

                for (Triangle t : triangles) {
                    for (scad.Vector v : t)
                        visual.vertex((float) v.x, (float) v.y, (float) v.z);
                    visual.vertex((float) t.get(0).x, (float) t.get(0).y, (float) t.get(0).z);
                }
                break;
        }
        visual.endShape();
        return visual;
    }
}
