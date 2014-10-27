package ru.smirnov.volleyexample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


public class MyActivity extends Activity {

    private static final String TAG = "TAG";
    private static String REQUEST_TAG = "LOAD_JSON";
    private static final String LOG = "VOLLEY-SAMPLE";
    private MenuItem mRefreshMenu;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

        mTextView = (TextView) findViewById(R.id.text1);

        JsonObjectRequest request = new JsonObjectRequest(
                "http://cblunt.github.io/blog-android-volley/response.json", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTextView.setText(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText(error.toString());
            }
        });
        request.setTag(REQUEST_TAG);
        VolleyApplication.getInstance().getRequestQueue().add(request);


        startLoadingAnim();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (VolleyApplication.getInstance().getRequestQueue() != null) {
            VolleyApplication.getInstance().getRequestQueue().cancelAll(REQUEST_TAG);
        }
    }

    private void startLoadingAnim() {
        if (mRefreshMenu != null) {
            Log.i(LOG, "===== start loading");
            ImageView iv = (ImageView) mRefreshMenu.getActionView();

            if (iv != null) {
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
            }
        }
    }

    private void stopLoadingAnim() {
        if (mRefreshMenu != null) {
            Log.i(LOG, "===== stop loading");
            ImageView iv = (ImageView) mRefreshMenu.getActionView();
            iv.setImageResource(R.drawable.ic_action_refresh);
            iv.clearAnimation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        mRefreshMenu = menu.findItem(R.id.action_refresh);
        ImageView iv = (ImageView) mRefreshMenu.getActionView();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iv = (ImageView) inflater.inflate(R.layout.actionbar_refresh, null);
        iv.setId(R.id.action_refresh);
        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                VolleyApplication.getInstance().getRequestQueue().cancelAll(TAG);
                stopLoadingAnim();
                startLoadingAnim();
                // refreshDatas();
            }
        });
        mRefreshMenu.setActionView(iv);

        startLoadingAnim();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_refresh || super.onOptionsItemSelected(item);
    }
}
