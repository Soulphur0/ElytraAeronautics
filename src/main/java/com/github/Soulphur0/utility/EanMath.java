package com.github.Soulphur0.utility;

public class EanMath {
    static public float getLinealValue(float pointAX, float pointAY, float pointBX, float pointBY, float valueToGetX){
        // Get curve's slope and intercept.
        float slope = (pointBY-pointAY)/(pointBX-pointAX);
        float intercept = pointBY - slope * pointBX;
        return slope*valueToGetX+intercept;
    }

    static public double getLinealValue(double pointAX, double pointAY, double pointBX, double pointBY, double valueToGetX){
        // Get curve's slope and intercept.
        double slope = (pointBY-pointAY)/(pointBX-pointAX);
        double intercept = pointBY - slope * pointBX;
        return slope*valueToGetX+intercept;
    }
}
