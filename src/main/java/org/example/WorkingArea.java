package org.example;
import org.math.plot.Plot2DPanel;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import static org.math.array.DoubleArray.*;
import static org.math.array.LinearAlgebra.times;



public class WorkingArea {
    public static double[][] CalculateCoordinate(double[] angles, double[] length)
    {
        /*
        Výpočet koordinát pri daných uhloch
         */

        double result [][] = {{0}, {0}, {0}, {1}};
        double [][]temp = times(Transformation.RotationZ( angles[0]), Transformation.TransitionZ( length[0]));
        temp = times(temp, times(Transformation.RotationY( angles[1]), Transformation.TransitionZ(  length[1])));
        temp = times( temp, times(Transformation.RotationY( angles[2]), Transformation.TransitionZ(  length[2])));

        result = times(temp, result);


        result = deleteRowsRange(result, 3, 3);



        return result;
    }
    private static double[][]AddCoord(double [][] targetArray, String[] addendArray)
    {
        /*
        Pridanie koordinátov do poľa na ďalšie vykresľovanie
         */

        int existingColumns = targetArray[0].length;
        if (targetArray[0].length > 100) targetArray = resize(targetArray, 3, 1);
        else
        {
            targetArray = resize(targetArray,3, existingColumns+1);
            for (int i = 0; i<3; i++)
            {
                targetArray[i][existingColumns] = Double.parseDouble(addendArray[i]);
            }
        }
        return targetArray;
    }

    private static void AdjustPlot(Plot2DPanel plot, String type)
    {
        /*
        Nastavenie parametrov grafu
         */
        switch (type)
        {
            case "XY":plot.setAxisLabel(0, "X [mm]");
                plot.setAxisLabel(1, "Y [mm]");
                break;
            case "XZ":plot.setAxisLabel(0, "X [mm]");
                plot.setAxisLabel(1, "Z [mm]");
                break;
        }
    }

    private static void CheckExistingFiles(String fileNameXY, String fileNameXZ)
    {
        /*
        Kontrola existencie súboru so koordinatmi bodu
         */
        File fileXZ = new File(fileNameXZ);
        File fileXY = new File(fileNameXY);

        if (!(fileXY.exists()) && !(fileXZ.exists()))
        {
            WriteCoordinatesXY();
            WriteCoordinatesXZ(Main.getLengthArray());
        }
        else
        {
            if (fileXY.exists()) WriteCoordinatesXZ(Main.getLengthArray());
            else WriteCoordinatesXY();
        }
    }
    public static void ReadCoordinates(String type)
    {
        /*
        Čítanie a kreslenie koordinát v grafe
         */
        Plot2DPanel plot = new Plot2DPanel();
        int scatterCounter = 0;
        int counter = 0;
        double[][] hardCodeManipulator = {{0,0,0,0}, {0,300,500,800}, {0,0,200,500}, {0,0,0,0}};

        CheckExistingFiles("coordinatesXY.gz", "coordinatesXZ.gz");

        String fileName = type.equals("XY") ? "coordinatesXY.gz" : "coordinatesXZ.gz";

        try(BufferedReader buffReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName))))) {
            double[][] positionCoord = {{0},{0},{0}};
            String stringCoord;

            while((stringCoord = buffReader.readLine())!=null)
            {

                counter++;
                String [] partionCoord = stringCoord.split(" ");
                positionCoord = AddCoord(positionCoord, partionCoord);
                if (positionCoord[0].length == 100)
                {
                    if (type.equals("XY")) plot.addScatterPlot("scatter_"+scatterCounter,  Color.GREEN, positionCoord[0], positionCoord[1]);
                    else if (type.equals("XZ")) plot.addScatterPlot("scatter_"+scatterCounter, Color.GREEN,  positionCoord[0], positionCoord[2]);//, positionCoord[2]
                    scatterCounter++;

                }
            }

            if (type.equals("XY")) hardCodeManipulator = deleteRowsRange(hardCodeManipulator, 0, 1);
            else if (type.equals("XZ")) hardCodeManipulator = deleteRowsRange(hardCodeManipulator, 2, 3);

            plot.addLinePlot("Manipulator", Color.BLACK, hardCodeManipulator[0], hardCodeManipulator[1]);
            plot.addScatterPlot("Joints", Color.BLACK, hardCodeManipulator[0], hardCodeManipulator[1]);
            AdjustPlot(plot, type);

            JFrame frame = new JFrame("Work Space "+type);
            frame.setSize(600, 600);
            frame.setContentPane(plot);
            frame.setVisible(true);
        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public static void WriteCoordinatesXZ(double[] length)
    {
        /*
        Zápis koordinátov do súboru
         */

        int counter = 0;

        try(BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("coordinatesXZ.gz")))))
        {

           int phi1 = 0;
            for (int phi2 = -50; phi2<=130; phi2+= 1) {
                for (int phi3 = -30; phi3 <= 60; phi3 += 1) {

                    double[] positionAngles = {phi1, phi2, phi3};
                    double[][] positionCoord = CalculateCoordinate(positionAngles, Main.getLengthArray());
                    WriteChunk(buffWriter, positionCoord);
                    counter++;
                    if (0 == (counter % 100)) buffWriter.flush();
                }
            }

            buffWriter.close();

        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
    public static void WriteCoordinatesXY()
    {
        /*
        Zápis koordinátov do súboru
         */

        int counter = 0;
        int dataSize = 10;
        int precisionCounter = 0;
        double[] length = {300, 300, 200};


        try(BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("coordinatesXY.gz")))))
        {

            for (int phi1 = -160; phi1 <= 160; phi1+= 3)
            {
                for (int phi2 = -50; phi2<=130; phi2+= 3)
                {
                    for(int phi3 = -30; phi3 <= 60; phi3+= 3)
                    {
                            double[] positionAngles = {phi1, phi2, phi3};
                            double[][] positionCoord = CalculateCoordinate(positionAngles, length);
                            WriteChunk(buffWriter, positionCoord);
                            counter++;
                            if(0 == (counter % 100)) buffWriter.flush();

                    }
                }
            }

            buffWriter.close();


        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    private static void WriteChunk(BufferedWriter writer, double[][] coord) throws IOException {

        writer.write(coord[0][0]+" "+coord[1][0]+" "+coord[2][0]);
        writer.newLine();


    }
}
