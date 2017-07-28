package com.zwhd.appbase.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.IUrconstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.MainActivity;
import com.zwhd.appbase.adapter.GameShowAdapter;
import com.zwhd.appbase.download.AdvModel;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelUtil;
import com.zwhd.appbase.download.CateModel;
import com.zwhd.appbase.http.HttpResponseModel;
import com.zwhd.appbase.util.Logger;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zaoxin on 2015/12/3.
 */
public class GamePageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    CateModel cate;
    RecyclerView recyclerView;
    GameShowAdapter adapter;
    Handler handler;
    boolean isRefresh = false;
    EndlessRecyclerOnScrollListener scrollListener;
    UpdateDownloadStatusReciver statusReciver = new UpdateDownloadStatusReciver();
    GridLayoutManager manager;
    JSONArray data = null;

    public GamePageFragment() {
        super();
    }

    public static GamePageFragment newInstance(CateModel cate) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.BUNDLE_PARAMS, cate);
        GamePageFragment fragment = new GamePageFragment();
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.cate = (CateModel) getArguments().getSerializable(IConstant.BUNDLE_PARAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_game, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new GridLayoutManager(activity, 3);
        scrollListener = new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int current_page) {

            }
        };
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });

        recyclerView = ViewUtil.findViewById(view, R.id.reclye_view);
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new GameShowAdapter(activity, cate, new ArrayList<AppModel>(), new ArrayList<AppModel>());
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(manager);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // swipeRefreshLayout.setRefreshing(false);
                switch (msg.what) {
                    case 0://加载数据
                        try {
                            List<AppModel> models = null;

                            String name = ViewUtil.getContent(activity, R.id.search_game_edit);
                            int start = adapter.labels.size();
                            if (isRefresh) start = 0;
                            if (StringUtil.isEmpty(name))
                                models =
                                        AppModelUtil.queryByPage(AppConstant.app.getContentResolver(), cate.getcId(), start, IConstant.REQUEST_PAGE_SIZE);
                            else
                                models = AppModelUtil.queryByCateIdandName(AppConstant.app.getContentResolver(), cate.getcId(), name);
                            Logger.log(models.size());
                            if (models != null && models.size() > 0) {
                                if (isRefresh) adapter.labels.clear();
                                adapter.labels.addAll(models);
                                adapter.notifyDataSetChanged();
                            }
                            activity.app.setSize(adapter.labels.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                if (position == activity.app.getSize() && cate.getcId() == 1000)
                                    return manager.getSpanCount();
                                return adapter.isHeader(position) ? manager.getSpanCount() : 1;

                            }
                        });
                        if (cate.getcId()==1000)
                            handler.sendEmptyMessageDelayed(2, 500);
                        break;
                    case 1://更新状态
                        try {
                            List<AppModel> models = AppModelUtil.queryByPage(AppConstant.app.getContentResolver(), cate.getcId(), 0, adapter.labels.size());
                            if (models != null && models.size() > 0) {
                                adapter.labels.clear();
                                adapter.labels.addAll(models);
                                adapter.notifyDataSetChanged();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:
                        List<AppModel> model = null;
                        model = AppModelUtil.queryByPage(AppConstant.app.getContentResolver(), 1001, 0, IConstant.REQUEST_PAGE_SIZE);
                        if (model != null && model.size() > 0) {
                            adapter.labels.addAll(model);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };

        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageLoader.getInstance().clearMemoryCache();
        if (adapter.labels.size() > 0) {
            handler.sendEmptyMessage(1);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION);
        activity.registerReceiver(statusReciver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(statusReciver);
    }

    void loadData() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("cateid", String.valueOf(cate.getcId()));
        params.put("start", String.valueOf(adapter.labels.size()));
        if (isRefresh) params.put("start", String.valueOf(0));
        params.put("num", String.valueOf(10));
        params.put("keyword", ViewUtil.getContent(activity, R.id.search_game_edit));
        if (cate.getcId() == 1000) {
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.GAME_HOT_URL, params, fragmenthttpHandler, cate.getcId());
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.GAME_NEW_URL, params, fragmenthttpHandler, 1001);
        } else {
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.GET_SOFTWARE_URL, params, fragmenthttpHandler, cate.getcId());
        }
        if (!isRefresh) {
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.LOAD_ADV_URL, params, fragmenthttpHandler, cate.getcId());
        }
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        JSONObject jsonObject = new JSONObject(new String(message.getResponse()));

        data = jsonObject.optJSONArray("data");
        if (data != null) {
            String url = message.getUrl();
            if (url.equals(IUrconstant.LOAD_ADV_URL)) {//收到广告
                List<AppModel> models = new ArrayList<>();
                for (int i = 0, leg = data.length(); i < leg && i < 3; i++) {
                    long aId;
                    int aCateId;
                    int aVersion;
                    String aVersionName;
                    String aUpdateTime;
                    String aSize;
                    String aContainSystem;
                    String aLogo;
                    String aGrade;
                    String aRecomment;
                    String aName;
                    String aComments;
                    String aVisitors;
                    String aDown;
                    String aDesc;
                    String aDwonloadUrl;
                    String packageName;
                    int openway;
                    String aTitle;
                    String advurl;
                    JSONObject item = data.getJSONObject(i);
                    aId = item.optInt("appid");
                    aCateId = item.optInt("cateid");
                    aVersion = item.optInt("app_ver_num");
                    aVersionName = item.optString("app_version");
                    aUpdateTime = item.optString("app_update_time");
                    aSize = item.optString("app_size");
                    aContainSystem = item.optString("app_system");
                    aLogo = item.optString("logo");
                    aGrade = item.optString("app_grade");
                    aRecomment = item.optString("app_recomment");
                    aName = item.optString("uname");
                    aComments = item.optString("app_comments");
                    aVisitors = item.optString("app_visitors");
                    aDown = item.optString("app_down");
                    aDesc = item.optString("app_desc");
                    aDwonloadUrl = item.optString("down_url");
                    packageName = item.optString("packagename");
                    openway = item.optInt("openway");
                    aTitle = item.optString("app_title");
                    advurl = item.optString("ad_url");
                    AppModel model = new AppModel();
                    model.setaId(aId);
                    model.setaCateId(aCateId);
                    model.setaVersion(aVersion);
                    model.setaVersionName(aVersionName);
                    model.setaUpdateTime(aUpdateTime);
                    model.setaSize(aSize);
                    model.setaContainSystem(aContainSystem);
                    model.setaLogo(aLogo);
                    model.setaGrade(aGrade);
                    model.setaRecomment(aRecomment);
                    model.setaName(aName);
                    model.setaComments(aComments);
                    model.setaVisitors(aVisitors);
                    model.setaDown(aDown);
                    model.setaDesc(aDesc);
                    model.setaDwonloadUrl(aDwonloadUrl);
                    model.setPackageName(packageName);
                    model.setOpenway(openway);
                    model.setaTitle(aTitle);
                    model.setSavePath(model.getGeneralPath());
                    model.setAdvurl(advurl);
                    models.add(model);
                }
                adapter.advModels.clear();
                adapter.advModels.addAll(models);
                adapter.notifyDataSetChanged();
            } else if (url.equals(IUrconstant.GET_SOFTWARE_URL) || url.equals(IUrconstant.GAME_HOT_URL)) {
                List<AppModel> models = AppModelUtil.jsnToMdl(data, cate.getcId());
                AppModelUtil.insert(AppConstant.app.getContentResolver(), cate.getcId(), models);
                handler.sendEmptyMessageDelayed(0, 500);
            } else if (url.equals(IUrconstant.GAME_NEW_URL)) {
                List<AppModel> models = AppModelUtil.jsnToMdl(data, 1001);
                AppModelUtil.insert(AppConstant.app.getContentResolver(), 1001, models);
            }
        }
    }

    @Override
    public void onRefresh() {
        search();
    }

    public void search() {
        isRefresh = true;
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        ImageLoader.getInstance().clearMemoryCache();
    }

    class UpdateDownloadStatusReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((MainActivity) activity).onResume();
            //handler.sendEmptyMessage(1);
        }
    }
}
