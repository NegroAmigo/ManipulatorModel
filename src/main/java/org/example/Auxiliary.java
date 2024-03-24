package org.example;
import org.math.plot.Plot3DPanel;
import java.awt.*;
import static org.math.array.DoubleArray.resize;
import static org.math.array.LinearAlgebra.times;

public abstract class Auxiliary {

    private static final double[][] startCoord = {{0},{0},{0}, {1}};
    private static double[][] endCoord = {{0},{0},{0}, {1}};
    private static final String[] axisNames = {"X", "Y", "Z"};

    private static Color axisColor = Color.white;

    private static double[] CenterVectorCoord(double[][]lineCoord)
    {
        double [] centerCoord = {0,0,0};
        for (int i = 0; i<3; i++)
        {
            centerCoord[i] = (lineCoord[i][0]+lineCoord[i][1])/2;
        }

        return centerCoord;
    }
    public static void AuxiliaryLabel(Plot3DPanel plot, double[][]sourceCoord, String nameLabel) {
        double[] labelCoord = {0, 0, 0};
        if (sourceCoord[0].length != 1) {
            labelCoord = CenterVectorCoord(sourceCoord);
            if ((sourceCoord[2][1] - sourceCoord[2][0]) >= 300) labelCoord[0] += 30;
            else labelCoord[2] += 40;
        } else {
            for (int i = 0; i < 3; i++) {
                labelCoord[i] = sourceCoord[i][0];
            }
            labelCoord[1] += 40;
        }

        plot.addLabel(nameLabel, Color.BLACK, labelCoord);
    }

    private static void InitEndCoord(String axisName)
    {
        endCoord = times(endCoord, 0);

        switch (axisName) {
            case "X" -> {
                endCoord[0][0] = 50;
                axisColor = Color.red;
            }
            case "Y" -> {
                endCoord[1][0] = 50;
                axisColor = Color.green;
            }
            case "Z" -> {
                endCoord[2][0] = 50;
                axisColor = Color.blue;
            }
        }
        endCoord[3][0] = 1;
    }
    private static void CalculateAxis(double[] angles, double[]length, Plot3DPanel plot, String axisName)
    {
        double[][] coordFinal = {{0,0},{0,0},{0,0},{0,0}};
        double[][] startFinal;
        double [][] endFinal;
        double[][] tempCalculations;
        InitEndCoord(axisName);


        coordFinal = MergeMatrix(coordFinal, startCoord, endCoord);
        plot.addLinePlot("Aux_"+axisName+"0",axisColor , coordFinal[0], coordFinal[1], coordFinal[2]);

        tempCalculations = times(Transformation.RotationZ( angles[0]), Transformation.TransitionZ( length[0]));
        startFinal = times(tempCalculations, startCoord);
        endFinal = times(tempCalculations, endCoord);
       coordFinal = MergeMatrix(coordFinal, startFinal, endFinal);
        plot.addLinePlot("Aux_"+axisName+"1",axisColor ,coordFinal[0],coordFinal[1],coordFinal[2]);

        tempCalculations = times(tempCalculations, times(Transformation.RotationY( angles[1]), Transformation.TransitionZ( length[1])));
        startFinal = times(tempCalculations, startCoord);
        endFinal = times(tempCalculations, endCoord);
       coordFinal = MergeMatrix(coordFinal, startFinal, endFinal);
        plot.addLinePlot("Aux_"+axisName+ "2",axisColor ,coordFinal[0],coordFinal[1],coordFinal[2]);

        tempCalculations = times( tempCalculations, times(Transformation.RotationY( angles[2]), Transformation.TransitionZ( length[2])));
        startFinal = times(tempCalculations, startCoord);
        endFinal = times(tempCalculations, endCoord);
       coordFinal = MergeMatrix(coordFinal, startFinal, endFinal);
        plot.addLinePlot("Aux_"+axisName+"3",axisColor ,coordFinal[0],coordFinal[1],coordFinal[2]);
    }
    public static void AddAxes(double[] angles, double[]length, Plot3DPanel plot )
    {
        for (int i = 0; i<3; i++)
        {
            CalculateAxis(angles, length, plot, axisNames[i]);
        }
    }
    private static double[][] MergeMatrix(double[][]targetMatrix, double[][]firstMatrix, double[][] secondMatrix)
    {
        for (int i =0; i<3; i++)
        {
            targetMatrix[i][0] = firstMatrix[i][0];
            targetMatrix[i][1] = secondMatrix[i][0];
        }

        return targetMatrix;
    }
    private static void AddCoordinates(double[][]targetMatrix, double[][]addendMatrix)
    {
        int newColumnsCount = addendMatrix[0].length;
        int existingColumns = targetMatrix[0].length;
        targetMatrix = resize(targetMatrix, 4, existingColumns+newColumnsCount);

        for(int i= 0; i<4; i++)
        {
            for (int j = 0; j<newColumnsCount; j++)
            {
                targetMatrix[i][j+existingColumns] = addendMatrix[i][j];
            }
        }
    }
}
