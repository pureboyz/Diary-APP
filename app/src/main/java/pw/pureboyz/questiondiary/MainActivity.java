package pw.pureboyz.questiondiary;

import android.content.Intent;
import android.os.Bundle;

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
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // 계정정보가 존재하면 memo list 화면으로 이동.
        if(googleSignInAccount != null)
        {
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