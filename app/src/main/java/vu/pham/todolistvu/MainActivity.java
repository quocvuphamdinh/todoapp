package vu.pham.todolistvu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtUsername, editPassword;
    Button btnLogin, btnSignUp;
    User user;
    ArrayList<User>userArrayList;
    int REQUEST_CODE_SIGNUP=123;
    String url_getUser="http://192.168.0.105:8080/todolist_vu/getdata_taikhoan.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhxa();
        GetUser(url_getUser);
        Events();

        Intent intentsignup=getIntent();
        if(intentsignup!=null){
            Bundle bundle=intentsignup.getBundleExtra("signup");
            if(bundle!=null){
                edtUsername.setText(bundle.getString("username"));
                editPassword.setText(bundle.getString("password"));
                GetUser(url_getUser);
            }
        }
    }

    private void GetUser(String url){
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                userArrayList.clear();
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject ojectUser=response.getJSONObject(i);
                        int id=Integer.parseInt(ojectUser.getString("id"));
                        String fullname=ojectUser.getString("fullname");
                        String userName=ojectUser.getString("username");
                        String passWord=ojectUser.getString("password");
                        userArrayList.add(new User(id, fullname, userName, passWord));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Lỗi: "+error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private void Events() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("userList", userArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_second);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=false;
                String username=edtUsername.getText().toString().trim();
                String password=editPassword.getText().toString().trim();
                if(username.length()>0 && password.length()>0){
                   for(int i=0;i<userArrayList.size();i++){
                       if((userArrayList.get(i).getUsername().equals(username)) && (userArrayList.get(i).getPassword().equals(password))){
                           check=true;
                           user.setId(userArrayList.get(i).getId());
                           user.setFullname(userArrayList.get(i).getFullname());
                           user.setUsername(userArrayList.get(i).getUsername());
                           user.setPassword(userArrayList.get(i).getPassword());
                           Toast.makeText(MainActivity.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();
                           break;
                       }
                   }
                   if(check==false){
                       Toast.makeText(MainActivity.this, "Đăng nhập thất bại vì tài khoản ko tồn tại hay gì đó !", Toast.LENGTH_SHORT).show();
                   }else{
                       Intent intent=new Intent(MainActivity.this, ToDoListActivity.class);
                       intent.putExtra("user", user);
                       startActivity(intent);
                       overridePendingTransition(R.anim.animation_enter, R.anim.animation_second);
                   }
                }else{
                    Toast.makeText(MainActivity.this, "Không được để trống thông tin !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode==REQUEST_CODE_SIGNUP && resultCode==RESULT_OK && data!=null){
//            GetUser(url_getUser);
//            Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void anhxa() {
        edtUsername=(EditText) findViewById(R.id.edittextUsername);
        editPassword=(EditText) findViewById(R.id.edittextPassword);
        btnLogin=(Button) findViewById(R.id.buttonLogin);
        btnSignUp=(Button) findViewById(R.id.buttonSignUp);
        user=new User(0, "", "", "");
        userArrayList=new ArrayList<>();
    }
}