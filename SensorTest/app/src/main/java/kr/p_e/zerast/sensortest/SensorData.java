package kr.p_e.zerast.sensortest;

/**
 * 센서 데이터 보관용 클래스
 */

public class SensorData {

    private String profile;
    private String timeStamp;
    private float dataTime;
    private float xAcc; // x축 가속도
    private float yAcc; // y축 가속도
    private float zAcc; // z축 가속도
    private float xOri; // x축 중심 회전(0 ~ 360)
    private float yOri; // y축 중심 회전(-180 ~ 180)
    private float zOri; // z축 중심 회전. 동서남북(0 ~ 360)
    private float xGyr; // x축 자이로
    private float yGyr; // y축 자이로
    private float zGyr; // z축 자이로
    private boolean shake;

    public SensorData(String profile, float dataTime, float xAcc, float yAcc, float zAcc, float xOri, float yOri, float zOri, float xGyr, float yGyr, float zGyr, boolean shake, String timeStamp) {
        this.profile = profile;
        this.dataTime = dataTime;
        this.timeStamp = timeStamp;
        this.xAcc = xAcc;
        this.yAcc = yAcc;
        this.zAcc = zAcc;
        this.xOri = xOri;
        this.yOri = yOri;
        this.zOri = zOri;
        this.xGyr = xGyr;
        this.yGyr = yGyr;
        this.zGyr = zGyr;
        this.shake = shake;
    }

    @Override
    public String toString() {
        return profile + "," + dataTime + "," + xAcc + "," + yAcc + "," + zAcc + "," + getAcc() + "," + (shake ? "1" : "0")
                + "," + xOri + "," + yOri + "," + zOri
                + "," + xGyr + "," + yGyr + "," + zGyr + "," + timeStamp;
    }

    /**
     * 가속도 구하기
     *
     * @return x축가속도^2 + y축가속도^2 + z축가속도^2 의 제곱근 값
     */
    private double getAcc() {
        return Math.sqrt(Math.pow(xAcc, 2) + Math.pow(yAcc, 2) + Math.pow(zAcc, 2));
    }
}
