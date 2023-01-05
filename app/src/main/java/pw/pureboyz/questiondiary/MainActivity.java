package pw.pureboyz.questiondiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import pw.pureboyz.questiondiary.memo.MemoListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 마지막에 로그인 된 계정정보를 가져온다.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // 계정정보가 존재하면 memo list 화면으로 이동.
        if(account != null)
        {
            String  personName          = account.getDisplayName();
            String  personGivenName     = account.getGivenName();
            String  personFamilyName    = account.getFamilyName();
            String  personEmail         = account.getEmail();
            String  personId            = account.getId();
            Uri     personPhoto         = account.getPhotoUrl();
            String  idToken             = account.getIdToken();
            String  authCode            = account.getServerAuthCode();

            Log.d(this.getClass().getName(), "personName : " + personName);
            Log.d(this.getClass().getName(), "personGivenName : " + personGivenName);
            Log.d(this.getClass().getName(), "personFamilyName : " + personFamilyName);
            Log.d(this.getClass().getName(), "personEmail : " + personEmail);
            Log.d(this.getClass().getName(), "personId : " + personId);
            Log.d(this.getClass().getName(), "personPhoto : " + personPhoto);
            Log.d(this.getClass().getName(), "idToken : " + idToken);
            Log.d(this.getClass().getName(), "authCode : " + authCode);

            Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
            startActivity(intent);
        }
        // 계정정보가 존재하지 않으면 login 화면으로 이동.
        else
        {
            Intent intent = new Intent(getApplicationContext(), GoogleLoginActivity.class);
            startActivity(intent);
        }
    }
}