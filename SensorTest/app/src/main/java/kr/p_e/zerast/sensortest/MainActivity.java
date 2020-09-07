package kr.p_e.zerast.sensortest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private EditText profileEditText; // 사용자 이름 입력 EditText
    private TextView sensorTextView; // 센서값 출력 텍스트뷰
    private Button button; // 시작 / 종료 버튼

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11; // 권한 확인을 위한 키값

    private static final int SHAKE_THRESHOLD = 800; // 흔듦 민감도. 작을수록 민감함

    private SensorManager sensorManager; // 센서 매니저
    private Sensor accSensor; // 가속도 센서
    private Sensor oriSensor; // 방향 센서
    private Sensor gyrSensor;

    private ArrayList<SensorData> list; // 데이터 리스트

    private long lastTime; // 마지막 측정시간
    private float lastX; // x축 가속도
    private float lastY; // y축 가속도
    private float lastZ; // z축 가속도

    private float xOri; // x축 중심 회전(0 ~ 360)
    private float yOri; // y축 중심 회전(-180 ~ 180)
    private float zOri; // z축 중심 회전. 동서남북(0 ~ 360)

    private float xGyr; // x축 자이로
    private float yGyr; // y축 자이로
    private float zGyr; // z축 자이로

    private boolean check; // 센서 사용 체크

    private long startTime;

    private KalmanFilter mKalmanAccX; // 칼만필터 X변수
    private KalmanFilter mKalmanAccY; // 칼만필터 Y변수

    private ImageView view ;
    private Bitmap map ;
    private Canvas canvas ;
    private Paint paint ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** **/
        view  = (ImageView)findViewById(R.id.drawView) ;
        //Bitmap theMap = BitmapFactory.decodeResource(getResources(), R.drawable.back) ;
        //Bitmap map = Bitmap.createBitmap(theMap.getWidth(), theMap.getHeight(), Bitmap.Config.ARGB_8888);
        map = Bitmap.createBitmap(360, 360, Bitmap.Config.ARGB_8888);
        paint = new Paint() ;
        canvas = new Canvas(map) ;
        canvas.drawCircle(180,180,3,paint) ;
        view.setImageBitmap(map) ;
        //view.setImageDrawable(new BitmapDrawable(getResources(), map));
        /** **/

        //칼만필터 초기화
        mKalmanAccX = new KalmanFilter(0.0f);
        mKalmanAccY = new KalmanFilter(0.0f);

        if (checkPermission()) // 권한 확인
            init(); // 확인 후 초기화
    }

    /**
     * 권한 물어본 이후 그 결과값 받음
     *
     * @param requestCode  요청코드
     * @param permissions  요청한 권한
     * @param grantResults 결과값
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(R.string.toast_error_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager == null) // 예외처리
            dataInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) { // 예외처리
            sensorManager.unregisterListener(this);
            check = false;
        }
    }

    private void init() {
        viewInit(); // View 초기화
        dataInit(); // 기타 사용데이터 초기화
    }

    private void viewInit() {
        profileEditText = (EditText) findViewById(R.id.profile_editText);
        sensorTextView = (TextView) findViewById(R.id.sensor_textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    private void dataInit() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        list = new ArrayList<>();
        check = false;
    }

    /**
     * 권한 확인
     *
     * @return 관한 결과
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    private void startSensor() {
        list.clear();
        profileEditText.setEnabled(false);

        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Toast.makeText(this, getString(R.string.toast_start), Toast.LENGTH_SHORT).show();
        button.setText(getString(R.string.button_stop));

        check = true;
    }

    private void stopSensor() {
        sensorManager.unregisterListener(this);
        Toast.makeText(this, getString(R.string.toast_stop), Toast.LENGTH_SHORT).show();
        button.setText(getString(R.string.button_start));
        check = false;

        LogWriter writer = new LogWriter();
        writer.createCentralLog(profileEditText.getText().toString(), list);

        profileEditText.setEnabled(true);
    }

    /**
     * 버튼 클릭 시
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (profileEditText.getText().toString().trim().equals(""))
                    Toast.makeText(this, getString(R.string.toast_error_profile), Toast.LENGTH_SHORT).show();
                else if (check)
                    stopSensor();
                else
                    startSensor();
                startTime = System.currentTimeMillis();
                break;
        }
    }

    /**
     * 센서의 값이 넘어오는 리스너
     * 데이터 저장 타이밍은 가속도센서에 맞춰서 저장함
     *
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // **********************************
        double tempZ ;
        double tempX ;
        float x = 180, y = 180 ;

        // **********************************

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            long gabOfTime = (currentTime - lastTime);

            float dataTime = (float) (currentTime - startTime) / 1000;

            if (gabOfTime > 100) {
                lastTime = currentTime;
                float xAcc = sensorEvent.values[0]; // x 가속도
                float yAcc = sensorEvent.values[1]; // y 가속도
                float zAcc = sensorEvent.values[2]; // z 가속도

                xAcc = (float) mKalmanAccX.update(xAcc) ;
                yAcc = (float) mKalmanAccX.update(yAcc) ;
                zAcc = (float) mKalmanAccX.update(zAcc) ;

                double speed = Math.abs(xAcc + yAcc + zAcc - lastX - lastY - lastZ) / gabOfTime * 10000; // 가속도 변동값 ( 밀리초 보정값 센서 보정값)

                boolean shake = speed > SHAKE_THRESHOLD;

                lastX = sensorEvent.values[0];
                lastY = sensorEvent.values[1];
                lastZ = sensorEvent.values[2];

                list.add(new SensorData(profileEditText.getText().toString(), dataTime, xAcc, yAcc, zAcc,
                        xOri, yOri, zOri, xGyr, yGyr, zGyr, shake, getDate(currentTime)));

                /*
                sensorTextView.setText("X Acc: " + xAcc +
                        "\nY Acc: " + yAcc +
                        "\nZ Acc: " + zAcc +
                        "\nX Ori: " + xOri +
                        "\nY Ori: " + yOri +
                        "\nZ Ori: " + zOri +
                        "\nX Gyr: " + xGyr +
                        "\nY Gyr: " + yGyr +
                        "\nZ Gyr: " + zGyr);
                */
            }
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            tempZ = zOri ;
            tempX = xOri ;
            zOri = sensorEvent.values[0];
            xOri = sensorEvent.values[1];
            yOri = sensorEvent.values[2];

            // **********************************
            x = (float)(Math.tan(tempZ+zOri)*15) ;
            y = (float)(Math.sin(tempX+xOri)*15) ;
            Log.e("!!","X= "+x+" // Y= "+y) ;
            canvas = new Canvas(map) ;
            canvas.drawCircle(x,y,3, paint) ;
            view.setImageBitmap(map) ;

            // **********************************

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xGyr = sensorEvent.values[0];
            yGyr = sensorEvent.values[1];
            zGyr = sensorEvent.values[2];
        }
    }

    /**
     * 사용하지 않음
     *
     * @param sensor
     * @param i
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public String getDate(long currentTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(currentTime);
    }

    class KalmanFilter {
        private double Q = 0.00001;
        private double R = 0.001;
        private double X = 0, P = 1, K;

        //첫번째값을 입력받아 초기화 한다. 예전값들을 계산해서 현재값에 적용해야 하므로 반드시 하나이상의 값이 필요하므로~
        KalmanFilter(double initValue) {
            X = initValue;
        }

        //예전값들을 공식으로 계산한다
        private void measurementUpdate(){
            K = (P + Q) / (P + Q + R);
            P = R * (P + Q) / (R + P + Q);
        }

        //현재값을 받아 계산된 공식을 적용하고 반환한다
        public double update(double measurement){
            measurementUpdate();
            X = X + (measurement - X) * K;

            return X;
        }
    }

}
