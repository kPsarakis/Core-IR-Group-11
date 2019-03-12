package features;

public class Features {

    /**
     * All the features
     */
    private double[] nGrams; // Array holding the n-grams n:(1-6)
    private double freq; // frequency (Other features)
    private int prefCLength, suffCLength, fullCLength; // Character length (Other features)
    private int prefWLength, suffWLength, fullWLength; // Word length (Other features)
    private short space; // prefix ends with whitespace (Other features)
    private short relevanceJudgment; // relevance judgment of the feature vector

    /**
     * Constructor that initializes all features to zero
     */
    public Features() {
        nGrams = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        freq = 0.0;
        prefCLength = 0;
        suffCLength = 0;
        fullCLength = 0;
        prefWLength = 0;
        suffWLength = 0;
        fullWLength = 0;
        space = 0;
        relevanceJudgment = 0;
    }

    /**
     * Function that sets the query candidate as relevant
     */
    public void makeItRelevant() {
        relevanceJudgment = 1;
    }

    /**
     * Function that does the sum of ngramfreq_i = sum(freq(g))
     *
     * @param i   the i-gram's frequency that gets increased
     * @param frq the amount that gets increased
     */
    public void incrementNGram(int i, double frq) {
        nGrams[i] += frq;
    }

    /**
     * Function that sets the frequency of the query in historical logs
     *
     * @param frq the amount to set the frequency
     */
    public void setOtherFrequency(double frq) {
        freq = frq;
    }

    /**
     * Function that returns the LambdaMART features
     *
     * @return the features as string in svmlight format
     */
    public String getLambdaFeatures() {
        return "1:" + nGrams[0] + " 2:" + nGrams[1] + " 3:" + nGrams[2] + " 4:" + nGrams[3] + " 5:" + nGrams[4]
                + " 6:" + nGrams[5] + " 7:" + freq + " 8:" + prefCLength + " 9:" + suffCLength + " 10:" + fullCLength
                + " 11:" + prefWLength + " 12:" + suffWLength + " 13:" + fullWLength + " 14:" + space;
    }

    /**
     * Generic getters and setters
     */
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

    public short getRelevanceJudgment() {
        return relevanceJudgment;
    }

}
