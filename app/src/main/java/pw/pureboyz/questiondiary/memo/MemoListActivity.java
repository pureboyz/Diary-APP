package pw.pureboyz.questiondiary.memo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pw.pureboyz.questiondiary.GoogleLoginActivity;
import pw.pureboyz.questiondiary.R;
import pw.pureboyz.questiondiary.element.TextViewCreator;
import pw.pureboyz.questiondiary.http.HttpCallback;
import pw.pureboyz.questiondiary.http.HttpRequester;
import pw.pureboyz.questiondiary.util.GlobalVariables;

public class MemoListActivity extends AppCompatActivity
{
    private GoogleSignInClient gsc;

    private MemoListActivity    memoListActivity;
    private LinearLayout        linearLayout;
    private ScrollView          scrollView;

    private int     begin   = 0;    // 가져올 메모 리스트의 시작점.
    private boolean isExist = true; // 가져올 메모 리스트의 존재여부

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        // activity_memo_list.xml 의 Toolbar 세팅.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        memoListActivity    = this;
        linearLayout    = (LinearLayout) findViewById(R.id.linearLayout);
        scrollView      = (ScrollView) findViewById(R.id.scrollView);

        // toolbar 에 사이드메뉴 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_sidemenu_dehaze_24);

        // 사이드 메뉴 구성
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // 사이드 메뉴 내 메뉴를 가져옴.
        Menu menu = navigationView.getMenu();
        // 이런식으로 동적으로 메뉴 아이템을 추가할 수 있음.
        menu.getItem(0).getSubMenu().add(Menu.NONE, 3, Menu.NONE, "메롱");
        /*
        *
        * DB 에서 item list 를 가져와서 동적으로 추가하자.
        *
        */

        // 메뉴의 각 item 클릭 이벤트 연결
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                Log.d(this.getClass().getName(), "zzzzz[1] " + id);
                String title = menuItem.getTitle().toString();

                if(id == R.id.account){
                    Toast.makeText(context, title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.setting){
                    Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.logout){
                    Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        // scrollView 움직임 감지.
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(!view.canScrollVertically(1))
                {
                    // 가져올 메모 리스트가 존재하는 경우에만 10개를 추가로 불러온다.
                    if(isExist) { getMemoList(); }
                }
            }
        });

        // 메모 리스트 10개를 불러온다.
        getMemoList();
    }






    public void getMemoList()
    {
        // HttpRequest -> 메모 리스트를 불러와서 memoSelectListCallback callback method를 실행시킴.
        HashMap<String, String> selectListParamMap = new HashMap<>();
        selectListParamMap.put("begin", String.valueOf(this.begin));
        new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/memo/SelectList", selectListParamMap, memoSelectListCallback);
        this.begin += 10;
    }

    // activity에 메뉴 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_memo_list, menu);
        return true;
    }

    // 메뉴 클릭 시 실행
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            // 메모 작성 버튼
            case R.id.writememo:
                intent = new Intent(getApplicationContext(), MemoWriteActivity.class);
                startActivity(intent);
                break;
            // 로그아웃 버튼
            case R.id.logout:
                signOut();

                intent = new Intent(getApplicationContext(), GoogleLoginActivity.class);
                startActivity(intent);
                break;
            // 햄버거 버튼
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                Log.d(this.getClass().getName(), "메뉴 클릭을 했지만, 매핑된 부분이 없음!!!!!");
        }
        return true;
    }

    // 메모 리스트를 불러온 이후 실행.
    HttpCallback memoSelectListCallback = new HttpCallback() {
        @Override
        public void onResult(String result) {
            try
            {
                JSONObject memoSelectListResult = new JSONObject(result);

                int         code            = memoSelectListResult.getInt("code");
                String      message         = memoSelectListResult.getString("message");
                JSONArray memoJsonArray   = null;

                // 성공한 경우에만 data 를 가져옴.
                if(code == 0) { memoJsonArray = memoSelectListResult.getJSONArray("data"); }

                // 데이터를 잘 가져온 경우(code:0)
                if(code == 0)
                {
                    for(int i=0; i<memoJsonArray.length(); i++)
                    {
                        JSONObject memoInfo = memoJsonArray.getJSONObject(i);

                        int     seq         = memoInfo.getInt("seq");
                        String  contents    = memoInfo.getString("contents");
                        String  createDate  = memoInfo.getString("createDate");
                        String  updateDate  = memoInfo.getString("updateDate");
                        String  deleteDate  = memoInfo.getString("deleteDate");

                        linearLayout.addView((TextView) new TextViewCreator(memoListActivity).makeTextView(seq, contents));
                    }
                }
                // 더이상 데이터가 존재하지 않는 경우(api 호출에 성공했으나 데이터가 없는 경우(code:611))
                else if(code == 611)
                {
                    isExist = false;
                }
                // 데이터를 못 가져온 경우 '메모가 존재하지 않습니다.' 문구 노출 (api 호출에 실패한 경우 모두 포함)
                else
                {
                    isExist = false;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    };

    private void signOut() {
        // GoogleSignInOptions.DEFAULT_SIGN_IN 파라미터를 통해 기본적인 프로필 정보를 요청한다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // GoogleSignInClient 객체
        gsc = GoogleSignIn.getClient(MemoListActivity.this, gso);

        gsc.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}
