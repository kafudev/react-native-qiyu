package com.reactnativeqiyu;

import android.content.Context;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.qiyukf.nimlib.sdk.StatusBarNotificationConfig;
import com.qiyukf.nimlib.sdk.ServerAddresses;
import com.qiyukf.nimlib.sdk.RequestCallback;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.OnMessageItemClickListener;
import com.qiyukf.unicorn.api.ProductDetail;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.UICustomization;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnreadCountChangeListener;
import com.qiyukf.unicorn.api.YSFOptions;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.qiyukf.unicorn.api.lifecycle.SessionLifeCycleOptions;

public class QiyuModule extends ReactContextBaseJavaModule {
    private static ReactContext sContext;
    private static YSFOptions ysfOptions;
    private static UnreadChangeListener unreadChangeListener;

    public QiyuModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        sContext = reactApplicationContext;
        // init(appKey, appName);
        //Diagnosis.setDevServer(1);
    }

    @Override
    public String getName() {
        return "Qiyu";
    }

    private void init(String appKey, String appName) {
        Fresco.initialize(sContext);

        if (ysfOptions == null) {
            ysfOptions = new YSFOptions();
        }
        ysfOptions.statusBarNotificationConfig = new StatusBarNotificationConfig();
        // you can also use "new FrescoImageLoader()" or "new PicassoImageLoader()"
        Unicorn.init(sContext, appKey, ysfOptions, new GlideImageLoader(sContext));
    }

    @ReactMethod
    public void multiply(int a, int b, Promise promise) {
      promise.resolve(a * b);
    }

    @ReactMethod
    public void registerAppId(String appKey, String appName, Callback callback) {
        // 初始化方法不要限制进程，也就是说不要只在主进程中初始化
        if (ysfOptions == null) {
            ysfOptions = new YSFOptions();
        }
        Unicorn.config(sContext, appKey, ysfOptions, null);
        // 注册初始化
        Unicorn.initSdk();
        init(appKey, appName);
        callback.invoke(1);
    }

    @ReactMethod
    public void openServiceWindow(ReadableMap params) {
        ReadableMap sourceMap = RNUtils.optReadableMap(params, "source");
        ReadableMap commodityInfoMap = RNUtils.optReadableMap(params, "commodityInfo");

        String sourceTitle = RNUtils.optString(sourceMap, "sourceTitle");
        String sourceUrl = RNUtils.optString(sourceMap, "sourceUrl");
        String sourceCustomInfo = RNUtils.optString(sourceMap, "sourceCustomInfo");

        ProductDetail productDetail = null;
        if (commodityInfoMap != null) {
            String commodityInfoTitle = RNUtils.optString(commodityInfoMap, "commodityInfoTitle");
            String commodityInfoDesc = RNUtils.optString(commodityInfoMap, "commodityInfoDesc");
            String pictureUrl = RNUtils.optString(commodityInfoMap, "pictureUrl");
            String commodityInfoUrl = RNUtils.optString(commodityInfoMap, "commodityInfoUrl");
            String note = RNUtils.optString(commodityInfoMap, "note");
            boolean show = RNUtils.optBoolean(commodityInfoMap, "show", false);
            productDetail = new ProductDetail.Builder()
                    .setTitle(commodityInfoTitle)
                    .setDesc(commodityInfoDesc)
                    .setPicture(pictureUrl)
                    .setUrl(commodityInfoUrl)
                    .setNote(note)
                    .setShow(show ? 1 : 0)
                    .build();
        }

        String sessionTitle = RNUtils.optString(params, "sessionTitle");
        long groupId = RNUtils.optInt(params, "groupId");
        long staffId = RNUtils.optInt(params, "staffId");
        long robotId = RNUtils.optInt(params, "robotId");
        boolean robotFirst = RNUtils.optBoolean(params, "robotFirst", false);
        long faqTemplateId = RNUtils.optInt(params, "faqTemplateId");
        int vipLevel = RNUtils.optInt(params, "vipLevel");
        boolean showQuitQueue = RNUtils.optBoolean(params, "showQuitQueue", false);
        boolean showCloseSessionEntry = RNUtils.optBoolean(params, "showCloseSessionEntry", false);

        // 启动聊天界面
        ConsultSource source = new ConsultSource(sourceUrl, sourceTitle, sourceCustomInfo);
        source.productDetail = productDetail;
        source.groupId = groupId;
        source.staffId = staffId;
        source.robotId = robotId;
        source.robotFirst = robotFirst;
        source.faqGroupId = faqTemplateId;
        source.vipLevel = vipLevel;
        source.sessionLifeCycleOptions = new SessionLifeCycleOptions();
        source.sessionLifeCycleOptions.setCanQuitQueue(showQuitQueue);
        source.sessionLifeCycleOptions.setCanCloseSession(showCloseSessionEntry);
        Unicorn.openServiceActivity(sContext, sessionTitle, source);
    }

    @ReactMethod
    public void setCustomUIConfig(ReadableMap params) {
        // 会话窗口上方提示条中的文本字体颜色
        String sessionTipTextColor = RNUtils.optString(params, "sessionTipTextColor");
        // 会话窗口上方提示条中的文本字体大小
        int sessionTipTextFontSize = RNUtils.optInt(params, "sessionTipTextFontSize");
        // 访客文本消息字体颜色
        String customMessageTextColor = RNUtils.optString(params, "customMessageTextColor");
        // 客服文本消息字体颜色
        String serviceMessageTextColor = RNUtils.optString(params, "serviceMessageTextColor");
        // 消息文本消息字体大小
        int messageTextFontSize = RNUtils.optInt(params, "messageTextFontSize");
        // 提示文本消息字体颜色
        String tipMessageTextColor = RNUtils.optString(params, "tipMessageTextColor");
        // 提示文本消息字体大小
        int tipMessageTextFontSize = RNUtils.optInt(params, "tipMessageTextFontSize");
        // 输入框文本消息字体颜色
        String inputTextColor = RNUtils.optString(params, "inputTextColor");
        // 输入框文本消息字体大小
        int inputTextFontSize = RNUtils.optInt(params, "inputTextFontSize");
        // 客服聊天窗口背景图片
        String sessionBackgroundImage = RNUtils.optString(params, "sessionBackgroundImage");
        // 会话窗口上方提示条中的背景颜色
        String sessionTipBackgroundColor = RNUtils.optString(params, "sessionTipBackgroundColor");

        // 标题栏背景图
        int titleBackgroundResId = RNUtils.optInt(params, "titleBackgroundResId");
        // 标题栏背景颜色，如果同时设置 drawable 和颜色，优先使用 drawable
        String titleBackgroundColor = RNUtils.optString(params, "titleBackgroundColor");
        // 标题栏风格，影响标题和标题栏上按钮的颜色
        int titleBarStyle = RNUtils.optInt(params, "titleBarStyle");
        // 标题居中
        boolean titleCenter = RNUtils.optBoolean(params, "titleCenter", false);

        // 访客头像
        String customerHeadImage = RNUtils.optString(params, "customerHeadImage");
        // 客服头像
        String serviceHeadImage = RNUtils.optString(params, "serviceHeadImage");
        // 消息竖直方向间距
        float sessionMessageSpacing = (float) RNUtils.optDouble(params, "sessionMessageSpacing");
        // 是否显示头像
        boolean showHeadImage = RNUtils.optBoolean(params, "showHeadImage", true);
        // 显示发送语音入口，设置为false，可以修改为隐藏
        boolean showAudioEntry = RNUtils.optBoolean(params, "showAudioEntry", true);
        // 显示发送表情入口，设置为false，可以修改为隐藏
        boolean showEmoticonEntry = RNUtils.optBoolean(params, "showEmoticonEntry", true);
        // 进入聊天界面，是文本输入模式的话，会弹出键盘，设置为false，可以修改为不弹出
        boolean autoShowKeyboard = RNUtils.optBoolean(params, "autoShowKeyboard", true);

        UICustomization uiCustomization = null;
        if(ysfOptions == null){
            if(ysfOptions.uiCustomization == null){
                ysfOptions.uiCustomization = new UICustomization();
            }
            uiCustomization = ysfOptions.uiCustomization;
        }
        if (uiCustomization == null) {
            uiCustomization = ysfOptions.uiCustomization = new UICustomization();
        }
        uiCustomization.topTipBarTextColor = RNUtils.parseColor(sessionTipTextColor);
        uiCustomization.topTipBarTextSize = sessionTipTextFontSize;
        uiCustomization.textMsgColorRight = RNUtils.parseColor(customMessageTextColor);
        uiCustomization.textMsgColorLeft = RNUtils.parseColor(serviceMessageTextColor);
        uiCustomization.textMsgSize = messageTextFontSize;
        uiCustomization.tipsTextColor = RNUtils.parseColor(tipMessageTextColor);
        uiCustomization.tipsTextSize = tipMessageTextFontSize;
        uiCustomization.inputTextColor = RNUtils.parseColor(inputTextColor);
        uiCustomization.inputTextSize = inputTextFontSize;
        uiCustomization.msgBackgroundUri = RNUtils.getImageUri(sContext, sessionBackgroundImage);
        uiCustomization.topTipBarBackgroundColor = RNUtils.parseColor(sessionTipBackgroundColor);

        uiCustomization.titleBackgroundResId = (int)titleBackgroundResId;
        uiCustomization.titleBackgroundColor = RNUtils.parseColor(titleBackgroundColor);
        uiCustomization.titleBarStyle = (int)titleBarStyle;
        uiCustomization.titleCenter = titleCenter;

        uiCustomization.rightAvatar = RNUtils.getImageUri(sContext, customerHeadImage);
        uiCustomization.leftAvatar = RNUtils.getImageUri(sContext, serviceHeadImage);
        uiCustomization.msgListViewDividerHeight = (int) sessionMessageSpacing;
        uiCustomization.hideLeftAvatar = !showHeadImage;
        uiCustomization.hideRightAvatar = !showHeadImage;
        uiCustomization.hideAudio = !showAudioEntry;
        uiCustomization.hideEmoji = !showEmoticonEntry;
        uiCustomization.hideKeyboardOnEnterConsult = !autoShowKeyboard;
    }

    @ReactMethod
    public void setUrlClickWithEventName(String eventName) {
        ysfOptions.onMessageItemClickListener = new MessageClickListener(sContext, eventName);
    }

    @ReactMethod
    public void setUnreadCountWithEventName(String eventName) {
        if (unreadChangeListener != null) {
            Unicorn.addUnreadCountChangeListener(unreadChangeListener, false);
        }
        unreadChangeListener = new UnreadChangeListener(sContext, eventName);
        Unicorn.addUnreadCountChangeListener(unreadChangeListener, true);
    }

    @ReactMethod
    public void getUnreadCountCallback(Callback callback) {
        int count = Unicorn.getUnreadCount();
        callback.invoke(String.valueOf(count));
    }

    @ReactMethod
    public void setUserInfo(ReadableMap params, Callback callback) {
        String userId = RNUtils.optString(params, "userId");
        String data = RNUtils.optString(params, "data");
        YSFUserInfo userInfo = new YSFUserInfo();
        userInfo.userId = userId;
        userInfo.data = data;
        Unicorn.setUserInfo(userInfo);
        callback.invoke(1);
        // Unicorn.setUserInfo(userInfo, new RequestCallback<Void>() {
        //   @Override
        //   public void onSuccess(Void aVoid) {
        //     callback.invoke(1);
        //   }
        //   @Override
        //   public void onFailed(int errorCode) {
        //     callback.invoke(0);
        //   }
        //   @Override
        //   public void onException(Throwable throwable) {

        //   }
        // });
    }

    @ReactMethod
    public void logout() {
        Unicorn.setUserInfo(null);
    }

    @ReactMethod
    public void cleanCache() {
        Unicorn.clearCache();
    }

    private static class MessageClickListener implements OnMessageItemClickListener {
        private ReactContext reactContext;
        private String eventName;

        public MessageClickListener(ReactContext reactContext, String eventName) {
            this.reactContext = reactContext;
            this.eventName = eventName;
        }

        @Override
        public void onURLClicked(Context context, String url) {
            WritableMap params = Arguments.createMap();
            params.putString("url", url);
            sendEvent(reactContext, eventName, params);
        }
    }

    private static class UnreadChangeListener implements UnreadCountChangeListener {
        private ReactContext reactContext;
        private String eventName;

        public UnreadChangeListener(ReactContext reactContext, String eventName) {
            this.reactContext = reactContext;
            this.eventName = eventName;
        }

        @Override
        public void onUnreadCountChange(int count) {
            WritableMap params = Arguments.createMap();
            params.putInt("unreadCount", count);
            sendEvent(reactContext, eventName, params);
        }
    }

    private static void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}
