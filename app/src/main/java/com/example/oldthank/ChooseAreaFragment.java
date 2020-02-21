package com.example.oldthank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.oldthank.DB.City;
import com.example.oldthank.DB.County;
import com.example.oldthank.DB.Province;
import com.example.oldthank.util.Fruit;
import com.example.oldthank.util.HttpUtil;
import com.example.oldthank.util.LogUtil;
import com.example.oldthank.util.Utility;
import org.litepal.LitePal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     * */
    private List<Province> provinceList;

    /**
     * 市列表
     * */
    private List<City> cityList;

    /**
     * 县列表
     * */
    private List<County> countyList;

    /**
     * 选中的省份
     * */
    private Province selectedProvice;

    /**
     * 选中的城市
     * */
    private City selectCity;

    /**
     * 当前选中的级别
     * */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvice = provinceList.get(position);
                    quertyCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectCity = cityList.get(position);
                    quertyCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof  MainActivity){
                        LogUtil.v("weatherId" ,weatherId);
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requrstWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    quertyCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        //开始加载省级数据
        queryProvince();
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * */
    private void queryFromServer(String addrss,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(addrss, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.v("12",String.valueOf(response));
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    LogUtil.v("省级",responseText);
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    LogUtil.v("市级",responseText);
                    result = Utility.handleCityResponse(responseText,selectedProvice.getId());
                }else if ("county".equals(type)){
                    LogUtil.v("县级",responseText);
                    result = Utility.handleCountyResponse(responseText,selectCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProfressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                quertyCities();
                            }else if ("county".equals(type)){
                                quertyCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProfressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    /**
     * 查询全国所有的省，优先冲数据库查询，如果没有查询到再去服务器上查询
     * */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//隐藏按钮
        //从数据库中读取省级数据，如果存在直接显示出来
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0){
            //从列表中删除所有元素
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            //刷新ViewList
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中的省内所有城市，优先从数据库查询，如果没有查询到再去服务器上查询
     * */
    private void quertyCities() {
        titleText.setText(selectedProvice.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?",String.valueOf(selectedProvice.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvice.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库中查询，如果没有查询到再去服务器上查询
     * */
    private void quertyCounties(){
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvice.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 显示进度对话框
     * */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     * */
    private void closeProfressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();

        }
    }
}
