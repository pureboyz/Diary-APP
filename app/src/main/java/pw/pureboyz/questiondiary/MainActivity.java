package pw.pureboyz.questiondiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import pw.pureboyz.questiondiary.http.HttpCallback;
import pw.pureboyz.questiondiary.http.HttpRequester;
import pw.pureboyz.questiondiary.memo.MemoListActivity;
import pw.pureboyz.questiondiary.util.GlobalVariables;
import pw.pureboyz.questiondiary.util.PermissionSupport;

public class MainActivity extends AppCompatActivity
{
    private Context             mContext;
    private WifiManager         wifiManager;
    private List<ScanResult>    scanResult;

    private int resultAccessWiFiState;

    // 클래스를 선언
    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 체크
        permissionCheck();

//        resultAccessWiFiState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        Log.d(this.getClass().getName(), "resultAccessWiFiState1 : " + resultAccessWiFiState);
//
//        if(resultAccessWiFiState == -1) { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x00000001); }
//        else
//        {
//            Log.d(this.getClass().getName(), "zzzzz[1]");
//            movePage();
//        }

    }



    // 권한 체크
    private void permissionCheck(){
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if(Build.VERSION.SDK_INT >= 23){
            Log.d(this.getClass().getName(), "SDK 23 이상");
            // 클래스 객체 생성
            permission =  new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if(!permission.checkPermission()){
                Log.d(this.getClass().getName(), "permission false..");
                permission.requestPermission();
            }
            else
            {
                Log.d(this.getClass().getName(), "zzzzz[2]");
                movePage();
            }
        }
    }

    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 리턴이 false일 경우 앱 종료
        if (!permission.permissionResult(requestCode, permissions, grantResults))
        {
            moveTaskToBack(true);
            finishAndRemoveTask();
            System.exit(0);
        }
        else
        {
            Log.d(this.getClass().getName(), "zzzzz[3]");
            movePage();
        }
    }




    public void movePage()
    {
        // Wi-Fi 정보를 가져온다.
        mContext    = this.getApplicationContext();
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        getWIFIScanResult();
        Log.d(this.getClass().getName(), "Connected Wi-Fi : " +wifiManager.getConnectionInfo().getBSSID());

        // 마지막에 로그인 된 계정정보를 가져온다.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // 계정정보가 존재하면 memo list 화면으로 이동.
        if(account != null)
        {
            String  personId            = account.getId();
            String  personName          = account.getDisplayName();
            String  personGivenName     = account.getGivenName();
            String  personFamilyName    = account.getFamilyName();
            String  personEmail         = account.getEmail();
            Uri     personPhoto         = account.getPhotoUrl();
//                String  idToken             = account.getIdToken();
//                String  authCode            = account.getServerAuthCode();

            Log.d(this.getClass().getName(), "personId : " + personId);
            Log.d(this.getClass().getName(), "personName : " + personName);
            Log.d(this.getClass().getName(), "personGivenName : " + personGivenName);
            Log.d(this.getClass().getName(), "personFamilyName : " + personFamilyName);
            Log.d(this.getClass().getName(), "personEmail : " + personEmail);
            Log.d(this.getClass().getName(), "personPhoto : " + personPhoto);
//                Log.d(this.getClass().getName(), "idToken : " + idToken);
//                Log.d(this.getClass().getName(), "authCode : " + authCode);

            // HttpRequest -> 사용자 정보를 업데이트 함.
            HashMap<String, String> selectListParamMap = new HashMap<>();
            selectListParamMap.put("id",            personId);
            selectListParamMap.put("name",          personName);
            selectListParamMap.put("givenName",     personGivenName);
            selectListParamMap.put("familyName",    personFamilyName);
            selectListParamMap.put("email",         personEmail);
            selectListParamMap.put("photoUrl",      String.valueOf(personPhoto));
            new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/user/update", selectListParamMap, moveMemoListPage);
        }
        // 계정정보가 존재하지 않으면 login 화면으로 이동.
        else
        {
            Intent intent = new Intent(getApplicationContext(), GoogleLoginActivity.class);
            startActivity(intent);
        }
    }







    // 사용자 정보를 DB 에서 업데이트 한 후 메모 리스트 페이지로 이동.
    HttpCallback moveMemoListPage = new HttpCallback() {
        @Override
        public void onResult(String result) {
            Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
            startActivity(intent);
        }
    };


    public void getWIFIScanResult(){
        scanResult = wifiManager.getScanResults();
        Log.d(this.getClass().getName(), "scanResult.size() : " + scanResult.size());
        for (int i = 0; i < scanResult.size(); i++) {
            ScanResult result = scanResult.get(i);

            Log.d(this.getClass().getName(), (i + 1)
                    + ". SSID : " + result.SSID
                    + "\t\t RSSI : " + result.level + " dBm"
                    + "\t\t BSSID : " + result.BSSID + " \n");
        }
    }
}