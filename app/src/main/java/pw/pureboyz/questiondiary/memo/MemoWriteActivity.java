package pw.pureboyz.questiondiary.memo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pw.pureboyz.questiondiary.MainActivity;
import pw.pureboyz.questiondiary.R;
import pw.pureboyz.questiondiary.http.HttpCallback;
import pw.pureboyz.questiondiary.http.HttpRequester;
import pw.pureboyz.questiondiary.util.GlobalVariables;

public class MemoWriteActivity extends AppCompatActivity
{
    private EditText editText;

    private int     seq         = 0;
    private String  contents    = "";
    private String  createDate  = "";
    private String  updateDate  = "";
    private String  deleteDate  = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);

        editText = (EditText) findViewById(R.id.contents);

        Intent intent   = getIntent();
        if(intent.getExtras() != null)
        {
            String data = intent.getExtras().getString("memoInfo");
            try
            {
                JSONObject memoInfo = new JSONObject(data);

                this.seq         = memoInfo.getInt("seq");
                this.contents    = memoInfo.getString("contents");
                this.createDate  = memoInfo.getString("createDate");
                this.updateDate  = memoInfo.getString("updateDate");
                this.deleteDate  = memoInfo.getString("deleteDate");

                editText.setText(contents);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        // Toolbar ??????.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_memo_write);
        setSupportActionBar(toolbar);

        // ???????????? ?????? ??????.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled (true);
    }

    // activity ??? ?????? ??????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_memo_write, menu);

        // seq ??? 0 ?????? ????????????????????? ?????? ????????? ??????.
        MenuItem menuDelete = menu.findItem(R.id.memo_delete);
        if(this.seq == 0) { menuDelete.setVisible(false); }

        return true;
    }

    // ?????? ?????? ??? ??????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.memo_save:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                String contents = editText.getText().toString();
                HashMap<String, String> saveParamMap = new HashMap<>();
                saveParamMap.put("seq",         String.valueOf(this.seq));
                saveParamMap.put("userId",      account.getId());
                saveParamMap.put("contents",    contents);

                // insert
                if(this.seq == 0)
                {
                    new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/memo/Create", saveParamMap, moveMemoListPage);
                }
                // update
                else
                {
                    new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/memo/Update", saveParamMap, moveMemoListPage);
                }

                break;
            case R.id.memo_delete:
                HashMap<String, String> deleteParamMap = new HashMap<>();
                deleteParamMap.put("seq", String.valueOf(this.seq));

                new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/memo/Delete", deleteParamMap, moveMemoListPage);

                break;
            case android.R.id.home:
                Log.d(this.getClass().getName(), "???????????? ??????!!!!!");
                finish();
                break;
            default:
                Log.d(this.getClass().getName(), "?????? ????????? ?????????, ????????? ????????? ??????!!!!!");
        }
        return true;
    }

    // ?????? ?????? ??? ??????.
    HttpCallback moveMemoListPage = new HttpCallback() {
        @Override
        public void onResult(String result) {
//            MemoWriteActivity.super.onBackPressed(); // ???????????? method ??????, data reload ??? ?????? ??????.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    };
}
