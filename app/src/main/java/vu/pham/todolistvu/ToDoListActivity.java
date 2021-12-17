package vu.pham.todolistvu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoListActivity extends AppCompatActivity {

    TextView txtWelcome;
    EditText edtAddToDoList;
    Button btnAddToDoList;
    ListView lsvToDoList;
    ArrayList<ToDoModel>toDoModelArrayList;
    ToDoListAdapter adapter;
    int indexColor=0;
    User user2;
    String url_getTodolist="http://192.168.0.105:8080/todolist_vu/getdata_todolist.php";
    String url_insertTodolist="http://192.168.0.105:8080/todolist_vu/insertdata_todolist.php";
    String url_updateTodolist="http://192.168.0.105:8080/todolist_vu/updatedata_todolist.php";
    String url_deleteTodolist="http://192.168.0.105:8080/todolist_vu/deletedata_todolist.php";
    ArrayList<String>historyToDoLists;
    ArrayAdapter<String>adapterHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        anhxa();

        Intent intent=getIntent();
        user2=intent.getParcelableExtra("user");
        String welcome="Welcome, "+user2.getFullname()+"\n Here is your to do list";
        Spannable spannable=new SpannableString(welcome);
        spannable.setSpan(new ForegroundColorSpan(Color.YELLOW), 9, ("Welcome, "+user2.getFullname()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtWelcome.setText(spannable, TextView.BufferType.SPANNABLE);

        KhoiTaoToDoList();
        Events();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void creatDialogHistory(){
        Dialog dialog=new Dialog(ToDoListActivity.this);
        dialog.setTitle("Những việc đã hoàn thành");
        dialog.setContentView(R.layout.custom_dialog_history);
        ListView lsvHistory=dialog.findViewById(R.id.listviewHistory);
        historyToDoLists.clear();
        for(int i=0;i<toDoModelArrayList.size();i++){
            if(toDoModelArrayList.get(i).isTrangThai()){
                historyToDoLists.add(toDoModelArrayList.get(i).getNoiDung());
            }
        }
        adapterHistory.notifyDataSetChanged();
        lsvHistory.setAdapter(adapterHistory);
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_LogOut:
                createDialogSignOut();
                break;
            case R.id.menu_History:
                creatDialogHistory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void createDialogSignOut(){
        Dialog dialog=new Dialog(ToDoListActivity.this);
        dialog.setTitle("Bạn có chắc không?");
        dialog.setContentView(R.layout.custom_diaglog_signout);
        TextView txtDiaglogSignout=(TextView) dialog.findViewById(R.id.textviewDialogSignOut);
        Button btnYes=(Button) dialog.findViewById(R.id.buttonYesDialogSignOut);
        Button btnNo=(Button) dialog.findViewById(R.id.buttonNoDialogSignOut);
        txtDiaglogSignout.setText("Bạn có muốn thoát khỏi "+user2.getFullname()+" ?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void updateStateTodolist(ToDoModel toDoModel){
        updateTodolist_database(url_updateTodolist, toDoModel, user2);
    }
    private void GetToDoList(String url){
        RequestQueue requestQueue= Volley.newRequestQueue(ToDoListActivity.this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                toDoModelArrayList.clear();
                for(int i=0;i<response.length();i++){
                    try {
                        if(Integer.parseInt(response.getJSONObject(i).getString("taikhoanid")) == user2.getId()){
                            JSONObject ojectToDoList=response.getJSONObject(i);
                            int id=Integer.parseInt(ojectToDoList.getString("id"));
                            String noidung=ojectToDoList.getString("noidung");
                            Boolean trangthai=false;
                            int state=Integer.parseInt(ojectToDoList.getString("trangthai"));
                            if(state==0){
                                trangthai=false;
                            }else if(state==1){
                                trangthai=true;
                            }
                            toDoModelArrayList.add(new ToDoModel(id, noidung, trangthai, indexColor));
                            indexColor++;
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ToDoListActivity.this, "Lỗi: "+error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private void Events() {
        btnAddToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noidung=edtAddToDoList.getText().toString().trim();
                if(noidung.length()>0){
                    edtAddToDoList.getText().clear();
                    addToDoList_Database(url_insertTodolist, noidung, user2);
                }else{
                    Toast.makeText(ToDoListActivity.this, "Nội dung không được để trống !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addToDoList_Database(String url, String noidung, User userInsert){
        RequestQueue requestQueue=Volley.newRequestQueue(ToDoListActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success")){
                    Toast.makeText(ToDoListActivity.this, "Thêm thành công !", Toast.LENGTH_SHORT).show();
                    GetToDoList(url_getTodolist);
                }else{
                    Toast.makeText(ToDoListActivity.this, "Thêm thất bại !"+"\nLỗi: "+response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ToDoListActivity.this, "Lỗi hệ thống !"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params=new HashMap<>();
                params.put("noidung", noidung);
                params.put("trangthai", String.valueOf(0));
                params.put("taikhoanid", String.valueOf(userInsert.getId()));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateTodolist_database(String url, ToDoModel toDoModelUpdate, User userUpdate){
        RequestQueue requestQueue=Volley.newRequestQueue(ToDoListActivity.this);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success")){
                    Toast.makeText(ToDoListActivity.this, "Cập nhật thành công !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ToDoListActivity.this, "Cập nhật thất bại !"+"\nLỗi: "+response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ToDoListActivity.this, "Lỗi hệ thống !"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params=new HashMap<>();
                params.put("id", String.valueOf(toDoModelUpdate.getId()));
                params.put("noidung", toDoModelUpdate.getNoiDung());
                params.put("trangthai", toDoModelUpdate.isTrangThai() ? String.valueOf(1) : String.valueOf(0));
                params.put("taikhoanid", String.valueOf(userUpdate.getId()));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void editToDoList(ToDoModel toDoModel, int position){
        Dialog dialog=new Dialog(ToDoListActivity.this);
        dialog.setTitle("Chỉnh sửa "+toDoModel.getNoiDung());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_dialog_edit);

        EditText edtEditDialog=(EditText) dialog.findViewById(R.id.edittextDialogEdit);
        Button btnOk=(Button) dialog.findViewById(R.id.buttonOkDialogEdit);
        Button btnCancel=(Button) dialog.findViewById(R.id.buttonCancelDialogEdit);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noidungmoi=edtEditDialog.getText().toString().trim();
                if(noidungmoi.length()>0){
                    toDoModel.setNoiDung(noidungmoi);
                    toDoModelArrayList.set(position, toDoModel);
                    adapter.notifyDataSetChanged();
                    updateTodolist_database(url_updateTodolist, toDoModel, user2);
                    dialog.cancel();
                }else{
                    Toast.makeText(ToDoListActivity.this, "Nội dung chỉnh sửa không được để trống !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    private void deleteTodolist_database(String url, int id){
        RequestQueue requestQueue=Volley.newRequestQueue(ToDoListActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success")){
                    Toast.makeText(ToDoListActivity.this, "Xóa thành công !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ToDoListActivity.this, "Xóa thất bai !", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ToDoListActivity.this, "Lỗi: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params=new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void deleteTodoList(int position){
        indexColor--;
        int id=toDoModelArrayList.get(position).getId();
        toDoModelArrayList.remove(position);
        deleteTodolist_database(url_deleteTodolist, id);
        adapter.notifyDataSetChanged();
    }

    private void KhoiTaoToDoList() {
        toDoModelArrayList=new ArrayList<>();
        GetToDoList(url_getTodolist);
        adapter=new ToDoListAdapter(ToDoListActivity.this, R.layout.to_do_list_row, toDoModelArrayList);
        lsvToDoList.setAdapter(adapter);
    }

    private void anhxa() {
        edtAddToDoList=(EditText) findViewById(R.id.edittextAddToDoList);
        btnAddToDoList=(Button) findViewById(R.id.buttonAddToDoList);
        lsvToDoList=(ListView) findViewById(R.id.listviewToDoList);
        txtWelcome=(TextView) findViewById(R.id.textviewWelcomeToDoList);
        historyToDoLists=new ArrayList<>();
        adapterHistory=new ArrayAdapter<String>(ToDoListActivity.this, android.R.layout.simple_list_item_1, historyToDoLists);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}