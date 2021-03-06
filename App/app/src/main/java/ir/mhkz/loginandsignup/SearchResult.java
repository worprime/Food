package ir.mhkz.loginandsignup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//刘涛

public class SearchResult extends AppCompatActivity {
    ImageView searchPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent homepage = getIntent();
        String []rstarr = new String[0];
        String result = homepage.getStringExtra("result");
        String spic = homepage.getStringExtra("spic");
        Bitmap srpic = null;
        try {
            byte[] bitmapArray = Base64.decode(spic, Base64.DEFAULT);
            srpic = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchPic=findViewById(R.id.SearchPic);
        if (srpic!=null)
            searchPic.setImageBitmap(srpic);
        if(!result.equals("failed")) {
            try {
                JSONArray ja = new JSONArray(result);
                rstarr = new String[ja.length()];
                for(int i=0;i<ja.length();i++)
                {
                    JSONObject jo = ja.getJSONObject(i);
                    rstarr[i] = "\u3000"+jo.getString("name")+"\n\u3000"+jo.getString("heat");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            final ListView lv = findViewById(R.id.listView);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_item_style, rstarr);//新建并配置ArrayAapeter
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 0:
                        default:
                            //Toast.makeText(SearchResult.this, "(测试用功能)你点击了" + i + "项", Toast.LENGTH_SHORT).show();
                            //二次查询
                            TextView item = (TextView) adapter.getView(i,view,lv);
                            String foodname =item.getText().toString();
                            foodname = foodname.split("\n")[0];
                            foodname = foodname.replace("\u3000","");
                            foodname = foodname.replace(" ","%20");

                            String serv = "http://203.195.155.114:3389/Text?foodname="+foodname;
                            //发送请求
                            try {
                                HttpGet httpGet = new HttpGet(serv);
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpResponse response = httpClient.execute(httpGet);
                                try {
                                    InputStream inputStream = response.getEntity().getContent();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    String result = "";
                                    String show="";
                                    String []res={};
                                    result = reader.readLine();
                                    if((null!=result ) && !result.matches("<html>")) {
                                        try {
                                                res = result.split("[}]]");
                                                result = res[0].substring(1)+"}";
                                                JSONObject jo = new JSONObject(result);
                                                show = "<font color=#ff0000>名称：</font>"+jo.getString("name")
                                                        +"<br><font color=#ff0000>种类：</font>"+jo.getString("type")
                                                        +"<br><font color=#ff0000>热量：</font>"+jo.getString("heat")
                                                        +"<br><font color=#ff0000>成分：</font>"+jo.getString("material")
                                                        +"<br><font color=#ff0000>建议：</font>"+jo.getString("advise")
                                                        +"<br><font color=#ff0000>制作：</font>"+jo.getString("method")
                                                ;

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchResult.this);
                                        LayoutInflater inflater = getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.detail, null);
                                        dialog.setView(dialogView);
                                        TextView t1 = dialogView.findViewById(R.id.dtltex);
                                        t1.setText(Html.fromHtml(show));
                                        ImageView pic = dialogView.findViewById(R.id.dtlpic);
                                        Bitmap bitmap = null;
                                        try {
                                            byte[] bitmapArray = Base64.decode(res[1].substring(4), Base64.DEFAULT);
                                            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        pic.setImageBitmap(bitmap);
                                        final AlertDialog dia = dialog.show();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(SearchResult.this, "服务器无响应", Toast.LENGTH_SHORT).show();

                            }
                            break;
                    }
                }
            });
        }
        else{
            Toast.makeText(SearchResult.this,"无结果",Toast.LENGTH_SHORT).show();
        }

        ImageButton back=findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResult.this.finish();
            }
        });
    }
}
