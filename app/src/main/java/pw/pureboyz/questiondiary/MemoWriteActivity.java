package pw.pureboyz.questiondiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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

        // Toolbar 세팅.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_memo_write);
        setSupportActionBar(toolbar);

        // 뒤로가기 버튼 세팅.
        ActionBar actionBar = getSupportActionBar ();
        actionBar.setDisplayHomeAsUpEnabled (true);
    }

    // activity 에 메뉴 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_memo_write, menu);

        // seq 가 0 이면 작성화면이므로 삭제 메뉴를 숨김.
        MenuItem menuDelete = menu.findItem(R.id.memo_delete);
        if(this.seq == 0) { menuDelete.setVisible(false); }

        return true;
    }

    // 메뉴 클릭 시 실행
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.memo_save:
                String contents = editText.getText().toString();
                HashMap<String, String> saveParamMap = new HashMap<>();
                saveParamMap.put("seq",         String.valueOf(this.seq));
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
                Log.d(this.getClass().getName(), "뒤로가기 클릭!!!!!");
                finish();
                break;
            default:
                Log.d(this.getClass().getName(), "메뉴 클릭을 했지만, 매핑된 부분이 없음!!!!!");
        }
        return true;
    }

    // 메모 작성 후 실행.
    HttpCallback moveMemoListPage = new HttpCallback() {
        @Override
        public void onResult(String result) {
//            MemoWriteActivity.super.onBackPressed(); // 뒤로가기 method 이나, data reload 를 하지 못함.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    };
}
