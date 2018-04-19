/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import BinaryCombinations.Difference;
import BinaryCombinations.Intersection;
import BinaryCombinations.Union;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.text.*;
import primitives.*;
import scad.Polyhedron;
import scad.hull.ConvexHull;
import viewer.CADViewer;

/**
 *
 * @author yangbai
 */
public class gui extends JFrame {

    private JTextArea area = new JTextArea(20, 120);
    private ArrayList<Polyhedron> polyhedrons = new ArrayList<>();

    public gui() {
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);
        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu insert = new JMenu("Insert");
        JMenu transform = new JMenu("Transform");
        JMenu binaryOperation = new JMenu("Bineary Operation");
        JMB.add(file);
        JMB.add(edit);
        JMB.add(insert);
        JMB.add(transform);
        JMB.add(binaryOperation);
        file.add(File);
        file.add(Load);
        file.add(Export);
        file.add(Import);
        insert.add(Sphere);
        insert.add(Cylinder);
        insert.add(Cube);
        insert.add(Polyhydren);
        transform.add(Translate);
        transform.add(Scale);
        transform.add(Rotate);
        binaryOperation.add(Union);
        binaryOperation.add(Intersection);
        binaryOperation.add(Difference);
        binaryOperation.add(ConvexHull);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setTitle("GUI");
        setVisible(true);
    }
    Action File = new AbstractAction("File") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Load = new AbstractAction("Load") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Export = new AbstractAction("Export") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Import = new AbstractAction("Import") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Sphere = new AbstractAction("Sphere") {
        public void actionPerformed(ActionEvent e) {
            JFrame jFrame = new JFrame("Insert Sphere");
            jFrame.setSize(600, 100);
            jFrame.setLayout(new FlowLayout());
            JLabel label1 = new JLabel("radius");
            jFrame.add(label1);
            JTextField text1 = new JTextField("", 10);
            jFrame.add(text1);
            JButton jButton = new JButton("Insert");
            jFrame.add(jButton);
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double radius = Double.valueOf(text1.getText());
                    polyhedrons.add(new Sphere(radius, 100));
                    jFrame.setVisible(false);
                }
            });
            jFrame.setVisible(true);
        }
    };
    Action Cylinder = new AbstractAction("Cylinder") {
        public void actionPerformed(ActionEvent e) {
            JFrame jFrame = new JFrame("Insert Cylinder");
            jFrame.setSize(600, 100);
            jFrame.setLayout(new FlowLayout());
            JLabel label1 = new JLabel("radius");
            jFrame.add(label1);
            JTextField text1 = new JTextField("", 10);
            jFrame.add(text1);
            JLabel label2 = new JLabel("height");
            jFrame.add(label2);
            JTextField text2 = new JTextField("", 10);
            jFrame.add(text2);
            JButton jButton = new JButton("Insert");
            jFrame.add(jButton);
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double radius = Double.valueOf(text1.getText());
                    double height = Double.valueOf(text2.getText());
                    polyhedrons.add(new Cylinder(radius, height, 100));
                    jFrame.setVisible(false);
                }
            });
            jFrame.setVisible(true);
        }
    };
    Action Cube = new AbstractAction("Cube") {
        public void actionPerformed(ActionEvent e) {
JFrame jFrame = new JFrame("Insert Cube");
            jFrame.setSize(600, 100);
            jFrame.setLayout(new FlowLayout());
            JLabel label1 = new JLabel("side");
            jFrame.add(label1);
            JTextField text1 = new JTextField("", 10);
            jFrame.add(text1);
            JButton jButton = new JButton("Insert");
            jFrame.add(jButton);
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double side = Double.valueOf(text1.getText());
                    polyhedrons.add(new Cube(side));
                    jFrame.setVisible(false);
                }
            });
            jFrame.setVisible(true);
        }
    };
    Action Polyhydren = new AbstractAction("Polyhydren") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Translate = new AbstractAction("Translate") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Scale = new AbstractAction("Scale") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Rotate = new AbstractAction("Rotate") {
        public void actionPerformed(ActionEvent e) {

        }
    };
    Action Union = new AbstractAction("Union") {
        public void actionPerformed(ActionEvent e) {
            Polyhedron p = new Union(polyhedrons.get(0), polyhedrons.get(1));
            CADViewer.openWindow(p);
        }
    };
    Action Intersection = new AbstractAction("Intersection") {
        public void actionPerformed(ActionEvent e) {
            Polyhedron p = new Intersection(polyhedrons.get(0), polyhedrons.get(1));
            CADViewer.openWindow(p);
        }
    };
    Action Difference = new AbstractAction("Difference") {
        public void actionPerformed(ActionEvent e) {
            Polyhedron p = new Difference(polyhedrons.get(0), polyhedrons.get(1));
            CADViewer.openWindow(p);
        }
    };
    Action ConvexHull = new AbstractAction("ConvexHull") {
        public void actionPerformed(ActionEvent e) {
            Polyhedron p = new ConvexHull(new Union(polyhedrons.get(0), polyhedrons.get(1)));
            CADViewer.openWindow(p);

        }
    };

    public static void main(String[] arg) {
        new gui();

    }
}
