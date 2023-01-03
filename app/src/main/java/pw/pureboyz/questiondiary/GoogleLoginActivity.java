package pw.pureboyz.questiondiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import pw.pureboyz.questiondiary.memo.MemoListActivity;

public class GoogleLoginActivity extends AppCompatActivity
{
    private SignInButton googleLoginBtn;

    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        googleLoginBtn = (SignInButton) findViewById(R.id.googleLoginBtn);

        TextView textView = (TextView) googleLoginBtn.getChildAt(0);
        textView.setText("Google 계정으로 로그인");

        // GoogleSignInOptions.DEFAULT_SIGN_IN 파라미터를 통해 기본적인 프로필 정보를 요청한다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // GoogleSignInClient 객체
        gsc = GoogleSignIn.getClient(GoogleLoginActivity.this, gso);

        // 구글 로그인 버튼 클릭 이벤트
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if(account != null)
            {
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                Log.d(this.getClass().getName(), "personName : " + personName);
                Log.d(this.getClass().getName(), "personGivenName : " + personGivenName);
                Log.d(this.getClass().getName(), "personFamilyName : " + personFamilyName);
                Log.d(this.getClass().getName(), "personEmail : " + personEmail);
                Log.d(this.getClass().getName(), "personId : " + personId);
                Log.d(this.getClass().getName(), "personPhoto : " + personPhoto);

                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}

