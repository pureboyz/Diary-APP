package pw.pureboyz.questiondiary.element;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pw.pureboyz.questiondiary.MainActivity;
import pw.pureboyz.questiondiary.memo.MemoListActivity;
import pw.pureboyz.questiondiary.memo.MemoWriteActivity;
import pw.pureboyz.questiondiary.R;
import pw.pureboyz.questiondiary.http.HttpCallback;
import pw.pureboyz.questiondiary.http.HttpRequester;
import pw.pureboyz.questiondiary.util.GlobalVariables;

public class TextViewCreator
{
    private MemoListActivity memoListActivity;

    private static final int MARGIN_TOP     = 25;
    private static final int MARGIN_BOTTOM  = 25;
    private static final int HEIGHT         = 300;

    private int id;

    public TextViewCreator(MemoListActivity memoListActivity)
    {
        this.memoListActivity = memoListActivity;
    }

    @SuppressLint("ResourceType")
    public TextView makeTextView(int id, String text)
    {
        this.id = id;

        TextView textView = new TextView(memoListActivity);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                 ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        param.topMargin     = MARGIN_TOP;
        param.bottomMargin  = MARGIN_BOTTOM;
        param.height        = HEIGHT;

        textView.setId(id);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18); // 18dp
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.shape_textview_corner);
        textView.setLayoutParams(param);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("seq", String.valueOf(id));
                new HttpRequester().request(GlobalVariables.getInstance().getUrlApi()+"/memo/Select", hashMap, memoSelectCallback);
            }
        });

        return textView;
    }

    HttpCallback memoSelectCallback = new HttpCallback() {
        @Override
        public void onResult(String result)
        {
            try
            {
                JSONObject memoSelectListResult = new JSONObject(result);

                int         code        = memoSelectListResult.getInt("code");
                String      message     = memoSelectListResult.getString("message");
                JSONObject  memoInfo    = memoSelectListResult.getJSONObject("data");

                Intent intent = new Intent(memoListActivity.getApplicationContext(), MemoWriteActivity.class);
                if(code == 0) { intent.putExtra("memoInfo", memoInfo.toString()); }

                memoListActivity.startActivity(intent);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };


}
