package com.mx.sy.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mx.sy.R;
import com.mx.sy.activity.ServiceDetailedActivity;
import com.mx.sy.adapter.ServiceAdapter;
import com.mx.sy.api.ApiConfig;
import com.mx.sy.base.BaseFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceFragment extends BaseFragment implements OnClickListener {
    private LinearLayout lin_nomanage, lin_processed;
    private TextView tv_nomanage, tv_processed;
    private View viw_nomanage, viw_processed;

    private ListView lv_service;
    private List<HashMap<String, String>> dateList;
    private ServiceAdapter serviceAdapter;

    RefreshLayout mPullToRefreshView;
    private int selectBtnFlag = 0;
    private SharedPreferences preferences;

    int page = 1;

    int totalnum;

    View view_nodate;

    @Override
    protected int setLayoutResouceId() {
        // TODO Auto-generated method stub
        return R.layout.fragment_service;
    }

    @Override
    protected void loadDate() {
        // TODO Auto-generated method stub
        super.loadDate();
        mPullToRefreshView = getActivity().findViewById(
                R.id.pullrefresh_service);

        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                page = 1;
                dateList.clear();
                if (selectBtnFlag == 0) {
                    getSertice(0);
                } else if (selectBtnFlag == 1) {
                    getSertice(1);
                }
            }
        });
        mPullToRefreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                if (dateList.size() == totalnum) {
                    Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_LONG)
                            .show();
                } else {
                    if (selectBtnFlag == 0) {
                        getSertice(0);
                    } else if (selectBtnFlag == 1) {
                        getSertice(1);
                    }
                }
            }
        });


    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        view_nodate = findViewById(R.id.view_nodate);
        lin_nomanage = findViewById(R.id.lin_nomanage);
        lin_nomanage.setOnClickListener(this);
        lin_processed = findViewById(R.id.lin_processed);
        lin_processed.setOnClickListener(this);

        tv_nomanage = findViewById(R.id.tv_nomanage);
        tv_processed = findViewById(R.id.tv_processed);

        viw_nomanage = findViewById(R.id.viw_nomanage);
        viw_processed = findViewById(R.id.viw_processed);

        lv_service = findViewById(R.id.lv_service);
//        lv_service.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//                                    long arg3) {
//                // TODO Auto-generated method stub
//                if (selectBtnFlag == 0) {
//                    Intent intent = new Intent(getActivity(), ServiceDetailedActivity.class);
//                    intent.putExtra("service_id", dateList.get(position).get("service_id"));
//                    intent.putExtra("service_state", dateList.get(position).get("status"));
//                    intent.putExtra("content", "餐桌:" + dateList.get(position).get("table_name") + "    服务:" + dateList.get(position).get("service_content"));
//                    startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    protected void initData(Bundle arguments) {
        // TODO Auto-generated method stub
        super.initData(arguments);
    }

    @Override
    protected void onLazyLoad() {
        // TODO Auto-generated method stub
        super.onLazyLoad();

        preferences = getActivity().getSharedPreferences("userinfo",
                getActivity().MODE_PRIVATE);

        dateList = new ArrayList<HashMap<String, String>>();
        serviceAdapter = new ServiceAdapter(getActivity(), dateList,
                R.layout.item_servicemanage);

    }

    @Override
    public void onResume() {
        if (dateList.size() > 0) {
            dateList.clear();
            serviceAdapter.notifyDataSetChanged();
        }
        if (selectBtnFlag == 0) {
            getSertice(0);
        } else if (selectBtnFlag == 1) {
            getSertice(1);
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.lin_nomanage:
                serviceAdapter = new ServiceAdapter(getActivity(), dateList,
                        R.layout.item_servicemanage);
                selectBtnFlag = 0;
                changeBtnBg(selectBtnFlag);
                page = 1;
                dateList.clear();
                getSertice(0);
                break;
            case R.id.lin_processed:
                serviceAdapter = new ServiceAdapter(getActivity(), dateList,
                        R.layout.item_serviceprocess);
                selectBtnFlag = 1;
                changeBtnBg(selectBtnFlag);
                dateList.clear();
                page = 1;
                getSertice(1);
                break;
            default:
                break;
        }
    }


    private void changeBtnBg(int selectTag) {
        switch (selectTag) {
            case 0:
                tv_nomanage.setTextColor(Color.rgb(79, 145, 244));
                viw_nomanage.setBackgroundResource(R.color.main_bg_color);

                tv_processed.setTextColor(Color.rgb(0, 0, 0));
                viw_processed.setBackgroundResource(R.color.sweet_dialog_bg_color);
                break;
            case 1:
                tv_nomanage.setTextColor(Color.rgb(0, 0, 0));
                viw_nomanage.setBackgroundResource(R.color.sweet_dialog_bg_color);

                tv_processed.setTextColor(Color.rgb(79, 145, 244));
                viw_processed.setBackgroundResource(R.color.main_bg_color);
                break;
            default:
                break;
        }
    }

    // 获取Service
    public void getSertice(final int servicestate) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("key", preferences.getString("loginkey", ""));
        client.addHeader("id", preferences.getString("userid", ""));
        String url = ApiConfig.API_URL + ApiConfig.SELECTSERVICELIST;
        RequestParams params = new RequestParams();
        params.put("waiter_id", preferences.getString("business_id", ""));
        params.put("page_no", page);
        params.put("shop_id", preferences.getString("shop_id", ""));
        params.put("status", servicestate);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (arg0 == 200) {
                    try {
                        String response = new String(arg2, "UTF-8");
                        com.orhanobut.logger.Logger.d(response);
                        JSONObject jsonObject = new JSONObject(response);
                        String CODE = jsonObject.getString("CODE");
                        if (CODE.equals("1000")) {
                            totalnum = Integer.parseInt(jsonObject
                                    .getString("totalnum"));
                            JSONArray jsonArray = new JSONArray(jsonObject
                                    .getString("DATA"));
                            if (jsonArray.length()==0){
                                view_nodate.setVisibility(View.VISIBLE);
                                lv_service.setVisibility(View.GONE);
                            }else {
                                view_nodate.setVisibility(View.GONE);
                                lv_service.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String service_id = object
                                            .getString("service_id");
                                    String service_content = object
                                            .getString("service_content");
                                    String status = object.getString("status");
                                    String create_time = object.getString("create_time");
                                    String receive_time = "";
                                    JSONObject waiterobj = null;
                                    String name = "";
                                    if (servicestate == 1) {
                                        receive_time = object.getString("receive_time");
                                        waiterobj = new JSONObject(object.getString("waiter"));
                                        name = waiterobj.getString("name");
                                    }
                                    JSONObject tableobj = new JSONObject(object.getString("table"));
                                    String table_name = tableobj.getString("table_name");
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    if (servicestate == Integer.parseInt(status)) {
                                        map.put("service_id", service_id);
                                        map.put("service_content", service_content);
                                        map.put("status", status);
                                        map.put("create_time", create_time);
                                        map.put("receive_time", receive_time);
                                        map.put("table_name", table_name);
                                        map.put("name", name);
                                        dateList.add(map);
                                    }
                                }
                                if (page == 1) {
                                    lv_service.setAdapter(serviceAdapter);
                                } else {
                                    serviceAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(),
                                    jsonObject.getString("MESSAGE"),
                                    Toast.LENGTH_SHORT).show();
                            view_nodate.setVisibility(View.GONE);
                            lv_service.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "服务器异常",
                                Toast.LENGTH_SHORT).show();
                        view_nodate.setVisibility(View.GONE);
                        lv_service.setVisibility(View.VISIBLE);
                        dissmissDilog();
                    }
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "服务器异常", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
