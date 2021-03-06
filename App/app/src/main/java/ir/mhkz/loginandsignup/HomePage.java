package ir.mhkz.loginandsignup;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//刘涛(主体)  蒋星宇(相册选择)  陈俊垚(拍照选择)

public class HomePage extends AppCompatActivity {
    private SearchView searchView;
    private static int REQ_1 = 1;
    String name="";
    String pwd="";
    String id="";
    boolean picChoise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageButton cameraBtn, homeDtlBtn;
        Button camera,gallery;

        cameraBtn = findViewById(R.id.cameraBtn);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        homeDtlBtn = findViewById(R.id.homeDtlBtn);

        final DrawerLayout sidemenu =findViewById(R.id.drawer_layout);
        final Intent intent = getIntent();
        name = intent.getStringExtra("username");
        pwd = intent.getStringExtra("pwd");
        id = intent.getStringExtra("id");
        //尝试修改用户信息显示
        NavigationView ngv=findViewById(R.id.nav_view_left);
        View smh = ngv.getHeaderView(0);
        TextView side_name = smh.findViewById(R.id.side_username);
        side_name.setText(name);
        TextView side_id = smh.findViewById(R.id.side_userid);
        side_id.setText(id);
        //给侧边栏菜单添加按钮监听器
        ngv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.account:
                        Intent intentAccount = new Intent(HomePage.this,Account.class);
                        intentAccount.putExtra("id",id);
                        intentAccount.putExtra("name",name);
                        intentAccount.putExtra("pwd",pwd);
                        startActivity(intentAccount);
                        break;
                    case R.id.healthdata:
                        Intent intentHealthD = new Intent(HomePage.this,HealthData.class);
                        intentHealthD.putExtra("id",id);
                        intentHealthD.putExtra("name",name);
                        intentHealthD.putExtra("pwd",pwd);
                        startActivity(intentHealthD);
                        break;
                    case R.id.weighthis:
                        Intent intentWH = new Intent(HomePage.this,WeightHis.class);
                        intentWH.putExtra("id",id);
                        intentWH.putExtra("name",name);
                        intentWH.putExtra("pwd",pwd);
                        startActivity(intentWH);
                        break;
                }
                return false;
            }
        });


        searchView = findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        //为SearchView设置监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单击搜索是激发该方法
            public boolean onQueryTextSubmit(String newtext) {
                //Toast.makeText(HomePage.this, "搜索框暂时不可用", Toast.LENGTH_SHORT).show();
                String serv = "http://203.195.155.114:3389/AlikeText?food="+newtext;
                HttpGet httpGet = new HttpGet(serv);
                HttpClient httpClient = new DefaultHttpClient();
                //发送请求
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    if (null == response) {
                        Toast.makeText(HomePage.this, "服务器无响应", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        InputStream inputStream = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String result  = reader.readLine();
                        if(null!=result) {
                            Intent intentRst = new Intent(HomePage.this,SearchResult.class);
                            intentRst.putExtra("result",result);
                            startActivity(intentRst);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            // 用户输入文字激发该方法
            public boolean onQueryTextChange(String newtext) {
                //todo
                return false;
            }
        });

        homeDtlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sidemenu.openDrawer(Gravity.LEFT);
            }
        });



//        cameraBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog picc = new AlertDialog.Builder(HomePage.this)
//                    .setMessage("请选择图片来源")
//                    .setNegativeButton("本机相册",new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            picChoise = true;
//                            Intent album = new Intent();
//                            album.setAction(Intent.ACTION_PICK);
//                            album.setType("image/*");
//                            startActivityForResult(album, REQ_1);
//                        }
//                    })
//                    .setPositiveButton("直接拍摄",new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            picChoise = false;
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用系统camera
//                            startActivityForResult(intent, REQ_1);
//                        }
//                    }).create();
//                picc.show();
//            }
//        });

        //陈俊垚
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //拍照选择
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用系统camera
                startActivityForResult(intent, REQ_1);
                picChoise=false;
            }
        });

        //蒋星宇
        gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //相册选择
                picChoise = true;
                Intent album = new Intent();
                album.setAction(Intent.ACTION_PICK);
                album.setType("image/*");
                startActivityForResult(album, REQ_1);
            }
        });



            //让主线程可以访问Internet
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    //蒋星宇(相册选择)  陈俊垚(拍照选择)   刘涛(整合与图片提交)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (picChoise) {
            if (resultCode == RESULT_OK) {
                //获取选中文件的定位符
                Uri uri = data.getData();
                Log.e("uri", uri.toString());
                //使用content的接口
                ContentResolver cr = this.getContentResolver();
                try {
                    //获取图片
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                //操作错误或没有选择图片
                Log.i("MainActivtiy", "operation error");
            }
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQ_1) {
                    Bundle bundle = data.getExtras();//获得图片的二进制流
                    bitmap = (Bitmap) bundle.get("data");

                }
            }
        }
        String pic="";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
            byte[] bytes = bos.toByteArray();
            pic = Base64.encodeToString(bytes, Base64.DEFAULT);
        }catch (Exception e)
        {
            Toast.makeText(HomePage.this,"取消图片选择",Toast.LENGTH_SHORT).show();
            return;
        }

        //将在这里添加图片上传
        String serv = "http://203.195.155.114:3389/Photo";
        //String serv = "http://203.195.155.114:8080/PatternRecognition";  //API测试
        HttpPost httpPost = new HttpPost(serv);
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,8000);//连接时间
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,10000);//数据传输时间
        try {
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            list.add(new BasicNameValuePair("id",id));
            list.add(new BasicNameValuePair("pic",pic));
            httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));// 设置请求参数
            Toast.makeText(HomePage.this, "发送中，请稍等", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e1) {
            Toast.makeText(HomePage.this, "发送失败\n"+e1.getMessage(), Toast.LENGTH_SHORT).show();

        }
        //发送请求
        try {
            HttpResponse response = null;
            response = httpClient.execute(httpPost);
            if (response == null) {
                Toast.makeText(HomePage.this, "服务器无响应", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String result = "";
                String line = reader.readLine();
                if((!line.equals("Failed")) && (!line.matches("<html>")) ) {
                    result+=line;
                    //Toast.makeText(HomePage.this, result, Toast.LENGTH_SHORT).show();
                    Intent intentRst = new Intent(HomePage.this,SearchResult.class);
                    intentRst.putExtra("spic",pic);
                    intentRst.putExtra("result",result);
                    startActivity(intentRst);
                }
                else{
                    Toast.makeText(HomePage.this, line, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HomePage.this,e.getMessage(),  Toast.LENGTH_SHORT).show();
        }
        return;
    }

}


