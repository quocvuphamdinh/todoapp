package vu.pham.todolistvu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    String url_insertTaikhoan="http://192.168.0.105:8080/todolist_vu/insertdata_taikhoan.php";
    EditText edtFullName, edtUserNameSignUp, edtPasswordSignUp;
    Button btnSignUp2;
    ArrayList<User>users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        anhxa();
        getUserList();
        events();
    }

    private void events() {
        btnSignUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=true;
                String fullname=edtFullName.getText().toString().trim();
                String username=edtUserNameSignUp.getText().toString().trim();
                String password=edtPasswordSignUp.getText().toString().trim();
                if(fullname.length() >0 && username.length()>0 && password.length()>0){
                    for(int i=0;i<users.size();i++){
                        if(users.get(i).getUsername().equals(username) || users.get(i).getPassword().equals(password)){
                            Toast.makeText(SignUpActivity.this, "Tên tài khoản hoặc mật khẩu này đã tồn tại !", Toast.LENGTH_SHORT).show();
                            check=false;
                            break;
                        }
                    }
                    if(check==true){
                        insertUserToDatabase(url_insertTaikhoan, fullname, username, password);
                        Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("password", password);
                        intent.putExtra("signup", bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Thông tin đăng ký không được bỏ trống !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertUserToDatabase(String url, String fullName, String userName, String passWord){
        RequestQueue requestQueue= Volley.newRequestQueue(SignUpActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success")){
                    Toast.makeText(SignUpActivity.this, "Đăng ký tài khoản thành công !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignUpActivity.this, "Đăng ký tài khoản thất bại !", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUpActivity.this, "Lỗi: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params=new HashMap<>();
                params.put("fullname", fullName);
                params.put("username", userName);
                params.put("password", passWord);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getUserList() {
        Bundle bundle=getIntent().getExtras();
        users=new ArrayList<>();
        users=bundle.getParcelableArrayList("userList");
    }

    private void anhxa() {
        edtFullName=(EditText) findViewById(R.id.edittextFullnameSignUp);
        edtUserNameSignUp=(EditText) findViewById(R.id.edittextUsernameSignUp);
        edtPasswordSignUp=(EditText) findViewById(R.id.edittextPasswordSignUp);
        btnSignUp2=(Button) findViewById(R.id.buttonSignUp2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}