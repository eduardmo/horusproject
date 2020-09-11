package nl.utwente.di.model;

public class Gps {

    private float xAxis;
    private float yAxis;

    public Gps(float x, float y) {
        this.xAxis = x;
        this.yAxis = y;
    }

    public void setxAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public void setyAxis(float yAxis) {
        this.yAxis = yAxis;
    }

    public float getxAxis() {
        return xAxis;
    }

    public float getyAxis() {
        return yAxis;
    }
}
