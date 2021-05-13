package com.example.moveuitemplate.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moveuitemplate.adapters.PhimYeuThichAdapter;
import com.example.moveuitemplate.R;
import com.example.moveuitemplate.models.Phim;
import com.example.moveuitemplate.utils.CheckConnection;
import com.example.moveuitemplate.utils.HttpParse;
import com.example.moveuitemplate.utils.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhimYeuThichActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
    PhimYeuThichAdapter phimYeuThichAdapter;
    ArrayList<Phim> mangPhim;

    String id_user = "";

    String idfavoritePhim;
    String idphim;
    String tenphim;

    //biến cho delete
    String urlDeleteFavoriteFilm = Server.urlDeleteFavoriteFilm;
    ProgressDialog progressDialog2;
    HashMap<String,String> hashMap = new HashMap<>();
    String finalResult;
    HttpParse httpParse = new HttpParse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phim_yeu_thich);

        Intent intent = getIntent();
        id_user = intent.getStringExtra("TenNguoiDung");
        anhxa();

        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            Log.d("1. ", "ket noi thanh cong");
//            GetPhim();
//            Log.d("2. ", "Get Phim thanh cong");
//            ActionToolbar();
//            Log.d("3. ", "toolbar thanh cong");
            GetData(id_user);
            Log.d("5. ", "Get data thanh cong");
        }
        else {
            CheckConnection.ShowToast(getApplicationContext(), "Kiểm tra lại kết nối!");
            finish();
        }


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PhimYeuThichActivity.this);
                alertDialogBuilder.setMessage("Bán có muốn xóa sản phẩm này!" + position);
                alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // xóa sp đang nhấn giữ

                        Phim p = (Phim) phimYeuThichAdapter.getItem(position);
                        idfavoritePhim = p.getID();
                        DeleteFilmFunction(id_user, idfavoritePhim);
                        mangPhim.remove(position);
                        //cập nhật lại listview
                        phimYeuThichAdapter.notifyDataSetChanged();

                    }
                });
                alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //không làm gì
                    }
                });
                alertDialogBuilder.show();
                return false;
            }
        });



    } // END OnCreate

    private void GetData(String id_user) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String duongDan = Server.ulrFavoriteFilm + String.valueOf(id_user);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, duongDan, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("4", " *** Kiem tra respone thanh cong: " + response);
                int id = 0;
                String id_Phim = "";
                String ten_Phim = "";

                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
//
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id = jsonObject.getInt("ID");
                            id_Phim = jsonObject.getString("IdPhim");
                            ten_Phim = jsonObject.getString("TenPhim");
                            mangPhim.add(new Phim(id_Phim, ten_Phim));
                            //phimYeuThichAdapter.notifyDataSetChanged();

                            Log.d("tagconvertstr", " *** ["+ id + "---" + id_Phim + ten_Phim +"]");
                            //thong bao notifyDataSetChange
                            Log.d("*** Trang thai: ", response);
                        }
                        phimYeuThichAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    phimYeuThichAdapter.notifyDataSetChanged();
                    //thong bao notifyDataSetChange
                    Log.println(Log.ERROR, "Loi", "Respone rong!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String, String>();
                param.put("id_film", String.valueOf(idphim));
                param.put("ten_phim", String.valueOf(tenphim));

                return param;
            }
        };
        requestQueue.add(stringRequest);



    }

//    private void ActionToolbar() {
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }

//    private void GetPhim() {
//        idfavoritePhim = getIntent().getIntExtra("id", 1);
//        idphim = getIntent().getIntExtra("id_user", -1);
//        tenphim = getIntent().getStringExtra("id_film");
//        Log.d("ID trong ds: ", idfavoritePhim +"");
//    }

    public void DeleteFilmFunction(final String iduser, final String idphim){

        class DeleteFavoriteFilmClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog2 = ProgressDialog.show(PhimYeuThichActivity.this,"Loading Data...",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                progressDialog2.dismiss();
                Toast.makeText(PhimYeuThichActivity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
            }

            @SuppressLint("LongLogTag")
            @Override
            protected String doInBackground(String... params) {

                //gửi IDUser và IDFilm qua $_POST
                hashMap.put("tendangnhap",params[0]);
                hashMap.put("idphim",params[1]);

                finalResult = httpParse.postRequest(hashMap, urlDeleteFavoriteFilm);

                Log.d(" - Kiem tra finalResult trong DeleteFavoriteFilmClass", finalResult.toString());

                return finalResult;
            }
        }

        DeleteFavoriteFilmClass deleteFavoriteFilmClass= new DeleteFavoriteFilmClass();

        deleteFavoriteFilmClass.execute(iduser, String.valueOf(idphim));
    }


    private void anhxa() {

        toolbar = findViewById(R.id.tb_phimyeuthich);
        listView = findViewById(R.id.lv_danhPhimSachYeuThich);

        mangPhim = new ArrayList<>();
        phimYeuThichAdapter = new PhimYeuThichAdapter(getApplicationContext(), mangPhim);
        listView.setAdapter(phimYeuThichAdapter);


    }
}