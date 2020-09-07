package kr.p_e.zerast.sensortest;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * CSV 파일을 만드는 클래스
 */
public class LogWriter {

    /**
     * CSV 파일 양식.
     * 인덱스, 시간, x가속도, y가속도, z가속도, 평균가속도, 흔듦 체크
     */
    private static final String FORMAT = "Name,dataTime,XAcc,YAcc,ZAcc,Acc,Shake,XOri,YOri,ZOri,XGyr,YGyr,ZGyr,Timestamp";

    /**
     * 사용자이름_시간.csv 형태로 SensorCSV 폴더에 파일 생성
     * 데이터는 List<SensorData> 형태로 받는다.
     *
     * @param profile  사용자 이름
     * @param itemList 센서값 리스트
     */

    public void createCentralLog(String profile, List<SensorData> itemList) {

        File folder = new File(Environment.getExternalStorageDirectory() + "/SensorCSV");

        if (!folder.exists())
            folder.mkdir();

        File file = new File(Environment.getExternalStorageDirectory() + "/SensorCSV/data.csv");
        boolean check = file.exists();
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file.toString(), true);

            if (!check) {
                fileWriter.append("\uFEFF");
                fileWriter.append(FORMAT.toString());
                fileWriter.append("\n");
            }

            for (SensorData item : itemList) {
                fileWriter.append(String.valueOf(item.toString()));
                fileWriter.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 현재 시간 가져오기
     * 년월일_시분초 형식
     * ex) 2016년 10월 06일 13시 06분 23초 -> 20161006_130623
     *
     * @return String 현재시간
     */

    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return CurDateFormat.format(date);
    }

}
