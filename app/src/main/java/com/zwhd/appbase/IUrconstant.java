package com.zwhd.appbase;

/**
 * Created by zaoxin on 2015/12/9.
 */
public final class IUrconstant {
    /**
     * 基础地址
     */
    public final static String BASE_URL = "http://www.gamefly.in/index.php?tpl=api&act=app_recommend";
    /**
     * 分类地址
     */
    public final static String CATE_GORY_URL = BASE_URL + "tpl=api&act=category";
    /**
     * 获取软件列表地址
     */
    public final static String GET_SOFTWARE_URL = BASE_URL + "tpl=api&act=apps";
    /**
     * 拉取广告列表
     */
    public final static String LOAD_ADV_URL = BASE_URL + "tpl=api&act=getads";
    /**
     * 下载成功上报
     */
    public final static String DOWNLOAD_SUCCESS_UPLOAD_URL = BASE_URL + "tpl=api&act=downsuccess";
    /**
     * 安装成功上报
     */
    public final static String INSTALL_SUCCESS_UPLOAD_URL = BASE_URL + "tpl=api&act=installsuccess";
    /**
     * 游戏详情地址
     */
    public final static String GAME_DETAIL_URL = "http://www.gamefly.in/index.php?tpl=app_detail&id=";

    /**
     * 热门游戏
     */
    public final static String GAME_HOT_URL = BASE_URL + "tpl=api&act=app_hot";
    /**
     * 推荐游戏
     */
    public final static String GAME_NEW_URL = BASE_URL + "tpl=api&act=app_recommend";

}
