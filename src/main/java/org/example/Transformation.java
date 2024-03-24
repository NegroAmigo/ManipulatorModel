package org.example;

import static org.math.array.DoubleArray.identity;

public class Transformation {
    private static double[][]TransformMatrix = identity(4);

    /* Inicializácia homogénnych matíc
    Názvy v závislosti od vykonávanej funkcie a osi
     */

    public static double[][] RotationX( double degrees)
    {
        TransformMatrix = identity(4);
        TransformMatrix[1][1]= Math.cos(Math.toRadians(degrees));
        TransformMatrix[1][2] = -(Math.sin(Math.toRadians(degrees)));
        TransformMatrix[2][1] = Math.sin(Math.toRadians(degrees));
        TransformMatrix[2][2] = Math.cos(Math.toRadians(degrees));

        return TransformMatrix;
    }

    public static double[][] RotationZ( double degrees)
    {
        TransformMatrix = identity(4);

        TransformMatrix[0][0]= Math.cos(Math.toRadians(degrees));
        TransformMatrix[0][1] = -(Math.sin(Math.toRadians(degrees)));
        TransformMatrix[1][0] = Math.sin(Math.toRadians(degrees));
        TransformMatrix[1][1] = Math.cos(Math.toRadians(degrees));


        return TransformMatrix;
    }

    public static double[][] RotationY( double degrees)
    {
        TransformMatrix = identity(4);
        TransformMatrix[0][0]= Math.cos(Math.toRadians(degrees));
        TransformMatrix[0][2] = Math.sin(Math.toRadians(degrees));
        TransformMatrix[2][0] = -(Math.sin(Math.toRadians(degrees)));
        TransformMatrix[2][2] = Math.cos(Math.toRadians(degrees));

        return TransformMatrix;
    }

    public static double[][] TransitionX (double length)
    {
        TransformMatrix = identity(4);
        TransformMatrix[0][3] = length;

        return TransformMatrix;
    }

    public static double[][] TransitionY ( double length)
    {
        TransformMatrix = identity(4);
        TransformMatrix[1][3] = length;

        return TransformMatrix;
    }
    public static double[][] TransitionZ ( double length)
    {
        TransformMatrix = identity(4);
        TransformMatrix[2][3] = length;

        return TransformMatrix;
    }
}
