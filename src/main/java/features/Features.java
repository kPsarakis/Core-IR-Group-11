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

    public void setOtherFrequency(double frq){
        freq = frq;
    }

    public void printFeatures(){
        System.out.println("nGrams");
        System.out.println("1: "+nGrams[0]+" 2: "+nGrams[1]+" 3: "+nGrams[2]+
                    " 4: "+nGrams[3]+" 5: "+nGrams[4]+" 6: "+nGrams[5]);
        System.out.println("Frequency in historic logs: "+freq);
        System.out.println("(Character) Prefix length: "+prefCLength);
        System.out.println("(Character) Suffix length: "+suffCLength);
        System.out.println("(Character) candidate length: "+fullCLength);
        System.out.println("(Word) Prefix length: "+prefWLength);
        System.out.println("(Word) Suffix length: "+suffWLength);
        System.out.println("(Word) candidate length: "+fullWLength);
        System.out.println("Ends with space: (1 => True 0 => False): "+space);
    }


    public void setPrefCLength(int prefCLength) {
        this.prefCLength = prefCLength;
    }

    public void setSuffCLength(int suffCLength) {
        this.suffCLength = suffCLength;
    }

    public void setFullCLength(int fullCLength) {
        this.fullCLength = fullCLength;
    }

    public void setPrefWLength(int prefWLength) {
        this.prefWLength = prefWLength;
    }

    public void setSuffWLength(int suffWLength) {
        this.suffWLength = suffWLength;
    }

    public void setFullWLength(int fullWLength) {
        this.fullWLength = fullWLength;
    }

    public void setSpace(short space) {
        this.space = space;
    }
}
