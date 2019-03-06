package features;

import java.util.ArrayList;
import java.util.List;

public class Features {

    private double[] nGrams;
    private double freq;
    private int prefCLength, suffCLength, fullCLength;
    private int prefWLength, suffWLength, fullWLength;
    private short space;

    public Features(){
        nGrams = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        freq = 0.0;
        prefCLength = 0; suffCLength = 0; fullCLength = 0;
        prefWLength = 0; suffWLength = 0; fullWLength = 0;
        space = 0;
    }


    public void incrementNGram(int i, double frq){
        nGrams[i] += frq;
    }

    public void printNGrams(){
            System.out.println("1: "+nGrams[0]+" 2: "+nGrams[1]+" 3: "+nGrams[2]+
                    " 4: "+nGrams[3]+" 5: "+nGrams[4]+" 6: "+nGrams[5]);
    }



}
