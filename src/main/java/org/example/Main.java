package org.example;

import org.math.plot.Plot3DPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

import static org.math.array.LinearAlgebra.*;

public class Main {
    private static final double[] length = {300, 200, 300};
    private static final double[] angles = {0,0,0};
    public static double[][] AddCoordinates(double[][] finalCoord, double[][]newCoord)
    {
        /*
        Pridanie koordinátov do poľa na ďalšie kreslenie
         */
        int maxColumn = getColumnDimension(finalCoord, 0);
        if ( maxColumn >= 2)
        {
            finalCoord =  deleteColumns(finalCoord, 0);
            finalCoord = resize(finalCoord, 3, maxColumn);
        }
        else finalCoord = resize(finalCoord, 3, (maxColumn)+1);

        maxColumn = getColumnDimension(finalCoord, 0);

        for (int i = 0; i<3; i ++) finalCoord[i][ maxColumn - 1] = newCoord[i][0];

        return finalCoord;
    }

    public static double[] getLengthArray()
    {
        return length;
    }
    public static double[] getAnglesArray()
    {
        return angles;
    }

    private static boolean CheckValues()
    {
        /*
        Kontrola zadaných hodnôt
         */
        boolean lenCheck = false;
        boolean angleCheck = false;

        if (length[0] > 0 && length[1] > 0 && length[2] > 0) lenCheck = true;
        else return lenCheck;
        if (angles[0]<= 160 && angles[0]>= -160)
        {
            if (angles[1]<= 130 && angles[1]>= -50)
            {
                if (angles[2]<= 60 && angles[2]>= -30)
                {
                    angleCheck = true;
                }
                else return angleCheck;
            }
            else return angleCheck;
        }
        else return angleCheck;

        return (angleCheck && lenCheck);
    }
     public static void getUserInput()
     {
         /*
         Zadávanie údajov používateľom z konzoly
          */
         Scanner scanner = new Scanner(System.in);
         boolean done = false;
         while (!done)
         {
             System.out.print("\nPlease enter the lengths of the manipulator parts separated by spaces in mm (L1 L2 L3): ");
             for (int i =0; i<3; i++) length[i] = scanner.nextInt();


             System.out.print("Please enter the manipulator angle values separated by a space in degrees (φ1 φ2 φ3)\nNote: φ1=<-160°, 160°>, φ2=<-50°, 130°>, φ3=<-30°, 60°> : ");
             for (int i =0; i<3; i++) angles[i] = scanner.nextInt();

             done = CheckValues();
         }
         System.out.println("Calculating...\nCreating plot...\nReading data into plot...");
        scanner.close();

     }

     private static void ManipulatorPositionCalculations(Plot3DPanel plot)
     {
         double[][] startCoord  = {{0,0}, {0,0}, {0,0}}; //start coordinates
         double[][] coordPointextend;
         double[][] startMatrix = resize(startCoord, 4, 1); //extended start coordinats for calculations
         startMatrix[3][0] = 1;

         double [][]temp = times(Transformation.RotationZ( angles[0]), Transformation.TransitionZ( length[0]));
         coordPointextend = times(temp, startMatrix);

         //Vypocet koordinat pre bod A a zobrazenie na grafe
         Auxiliary.AuxiliaryLabel(plot, coordPointextend, "A");
         plot.addScatterPlot("A", Color.BLACK, coordPointextend[0], coordPointextend[1], coordPointextend[2]);
         startCoord = AddCoordinates(startCoord, coordPointextend);

         plot.addLinePlot("L1", Color.darkGray,startCoord[0], startCoord[1], startCoord[2]);
         Auxiliary.AuxiliaryLabel(plot, startCoord, "L1");

         //Vypocet koordinat pre bod B a zobrazenie na grafe
         temp = times(temp, times(Transformation.RotationY( angles[1]), Transformation.TransitionZ( length[1])));
         coordPointextend = times(temp, startMatrix);
         Auxiliary.AuxiliaryLabel(plot, coordPointextend, "B");
         plot.addScatterPlot("B", Color.BLACK, coordPointextend[0], coordPointextend[1], coordPointextend[2]);
         startCoord = AddCoordinates(startCoord, coordPointextend);

         plot.addLinePlot("L2",Color.darkGray, startCoord[0], startCoord[1], startCoord[2]);
         Auxiliary.AuxiliaryLabel(plot, startCoord, "L2");

        //Vypocet koordinat pre bod C a zobrazenie na grafe
         temp = times( temp, times(Transformation.RotationY( angles[2]), Transformation.TransitionZ( length[2])));
         coordPointextend = times(temp, startMatrix);
         Auxiliary.AuxiliaryLabel(plot, coordPointextend, "C");
         plot.addScatterPlot("C", Color.BLACK, coordPointextend[0], coordPointextend[1], coordPointextend[2]);
         startCoord = AddCoordinates(startCoord, coordPointextend);

         plot.addLinePlot("L3",Color.darkGray, startCoord[0], startCoord[1], startCoord[2]);
         Auxiliary.AuxiliaryLabel(plot, startCoord, "L3");



         Auxiliary.AddAxes(angles, length, plot);
     }

    public static void main(String[] args)
    {
        getUserInput();

        Plot3DPanel plot = new Plot3DPanel();

        ManipulatorPositionCalculations(plot);

        
        plot.setFixedBounds(0, 0,max(length)*2);
        plot.setFixedBounds(1, 0,max(length)*2);
        plot.setFixedBounds(2, 0,max(length)*2);
        JFrame frame = new JFrame("Manipulator Position Model");
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });


        WorkingArea.ReadCoordinates("XZ"); //Vykres pracovneho priestoru XZ
        WorkingArea.ReadCoordinates("XY"); //Vykres pracovneho priestoru XY



    }


}