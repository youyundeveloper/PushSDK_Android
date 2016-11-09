# **Youyun Android PUSH_SDK 集成**

## 一、前期准备
在您阅读此文档之前，我们假定您已具备基础的 Android 应用开发经验，并能够理解相关基础概念。

### 1、注册开发者帐号
开发者在集成游云PUSH功能前，需前往 [游云官方网站](http://www.17youyun.com) 注册创建游云开发者帐号。

### 2、下载 SDK

您可以到 [游云Github平台](https://github.com/youyundeveloper/PushSDK_Android/tree/push) 下载游云 SDK。下载包中分为如下两部分：

- PUSH Lib - 游云 PUSH库和相关库
- PUSH Demo

 Demo简单集成了PUSH功能，供您参考,您可以在开发者平台进行测试。
 
### 3、创建应用

您要进行应用开发之前，需要先在游云开发者平台创建应用。如果您已经注册了游云开发者帐号，请前往 [游云开发者平台](http://www.17youyun.com) 创建应用。

您创建完应用后，首先需要了解的是 App ClientID / Secret，它们是游云 SDK 连接服务器所必须的标识，每一个 App 对应一套 App ClientID / Secret。

### 4、如小米平台需使用小米PUSH则需要此步骤：
##### 1、注册小米开发者帐号
开发者在集成小米PUSH功能前，需前往[小米开放平台](http://dev.xiaomi.com/)注册创建小米开发者帐号。 
##### 2、创建应用并在游云平台配置
在小米开发者平台创建应用并启用,将得到小米AppID、AppKey、AppSecret，在游云放平台的[推送管理](http://17youyun.com/web/push/pushAdvanceConfig)中进行配置。并在应用的res/values/string里面添加AppId和AppKey

```
<string name="MiPushAppId">AppId</string>
<string name="MiPushAppKey">AppKey</string>
```

具体参见 [小米开发者文档](http://dev.xiaomi.com/doc/?p=1621/)


## 二、集成开发

### 1、将您下载的jar包导入您项目的libs目录，包括PUSH能力基础包两个和http请求包volley，如果您的应用有volley包可不用导入此volley包，如下：

```
youyun-push-android-1.1.0.jar
youyun-protobuf-java-2.4.1.jar
volley.jar
```
#### 注：如小米平台需使用小米PUSH则需加入小米PUSH jar包：

```
MiPush_SDK_Client_3_0_3.jar
```

### 2、在您项目的AndroidManifest.xml文件中加入以下权限

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.VIBRATE"/>
```
#### 注：如小米平台需使用小米PUSH则需额外加入如下权限：

```
<permission android:name="com.xiaomi.mipushdemo.permission.MIPUSH_RECEIVE" android:protectionLevel="signature" />
<!--这里com.xiaomi.mipushdemo改成app的包名-->   
<uses-permission android:name="com.xiaomi.mipushdemo.permission.MIPUSH_RECEIVE" />
<!--这里com.xiaomi.mipushdemo改成app的包名-->
```


### 3、在您项目的AndroidManifest.xml文件中加入以下服务和广播

```
<service 
    android:name="com.weimi.push.service.WeimiPushService"
    android:exported="true"
    android:process=":push">
        <intent-filter>
            <action android:name="me.weimi.PushService.BIND" />
        </intent-filter>
</service>

<receiver
    android:name="com.weimi.push.WeimiPushReceiver"
    android:exported="false">
        <intent-filter>
            <action android:name="me.weimi.push.action.10001" />
            <!-- me.weimi.push.action.第三方的appId -->
        </intent-filter>
</receiver>
```
#### 注：如小米平台需使用小米PUSH则需额外加入如下服务和广播：

```
<service
    android:name="com.xiaomi.push.service.XMJobService"
    android:enabled="true"
    android:exported="false"
    android:permission="android.permission.BIND_JOB_SERVICE"
    android:process=":pushservice" />
<service
    android:name="com.xiaomi.push.service.XMPushService"
    android:enabled="true"
    android:process=":pushservice" />
<service
    android:name="com.xiaomi.mipush.sdk.PushMessageHandler"
    android:enabled="true"
    android:exported="true" />
<service
    android:name="com.xiaomi.mipush.sdk.MessageHandleService"
    android:enabled="true" />

<receiver
    android:name="com.weimi.push.MiPushMsgReceiver"
    android:exported="true">
        <intent-filter>
            <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
        </intent-filter>
        <intent-filter>
            <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
        </intent-filter>
        <intent-filter>
            <action android:name="com.xiaomi.mipush.ERROR" />
        </intent-filter>
</receiver>
<receiver
    android:name="com.xiaomi.push.service.receivers.NetworkStatusReceiver"
    android:exported="true">
        <intent-filter>
            <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
</receiver>
<receiver
    android:name="com.xiaomi.push.service.receivers.PingReceiver"
    android:exported="false"
    android:process=":pushservice">
        <intent-filter>
            <action android:name="com.xiaomi.push.PING_TIMER" />
        </intent-filter>
</receiver>
```

### 4、新建类PushMsgReceiver继承ClickReceiver ,在此接收PUSH消息。其中集成游云PUSH接收到PUSH消息后回回调onYouYunReceiveMessage()方法,集成小米PUSH收到PUSH消息没有回调,当点击通知栏消息时都会回调onNotificationClicked()方法。

```
public class PushMsgReceiver extends ClickReceiver {

    /**
     *  集成游云PUSH的接收消息会回调
     * @param context
     * @param payLoadMessage 消息数据
     */
    @Override
    protected void onYouYunReceiveMessage(Context context, PayLoadMessage payLoadMessage) {
        
    }
    
    /**
     * 点击回调
     * @param context
     * @param message 消息数据
     */
    @Override
    protected void onNotificationClicked(Context context, Object message) {
        if(message instanceof PayLoadMessage) {
            Log.d("Youyun Click");
        } else if(message instanceof MiPushMessage) {
            Log.d("Xiaomi Click");
        }
    }
    
}
```
### 5、在您项目的AndroidManifest.xml文件中加入以下广播(ClickReceiver 的类)

```
<receiver android:name=".PushMsgReceiver">
             <intent-filter>
                <action android:name="com.weimi.push.ClickReceiver"></action>
            </intent-filter>
</receiver>
```

### 6、调用SDK接口即可开发

```
YouyunInstance.getInstance().xxx();
```
## 三、接口文档

### 1、初始化SDK

```
/**
* @param context 必填
* @param clientId cliendid 必填
* @param secret 密钥 必填
* @param udid 用户唯一标识, udid+clientId+secret生成唯一一个uid 必填
* @param isOnline true:线上平台 必填
* @param callback 回调 {"apistatus":1,"result":"12345"} apistatus,1:成功 0:失败 result,成功返回uid,失败返回失败原因
* @param timeout
*/
void initSDK(Context context, String clientId, String secret, String udid, boolean isOnline, YouYunHttpCallback callback, int timeout);
```

### 2、启动PUSH服务
```
/**
 * @param context 必填
 * @param isOnline true:线上平台 必填
 * @param isLogEnable true:显示日志
 * @return
 */
boolean startPush(Context context, boolean isOnline, boolean isLogEnable);
```
### 3、设置push提醒时段,第一次生成用户时会上报设备信息,所以第一次启动PUSH后必须调用此方法,
```
/**
 * @param context 必填
 * @param startTime 开始时间 startTime和endTime不填时会默认为上次设置的时间,上次没有设置则默认0~24
 * @param endTime 结束时间
 * @param callback 回调
 * @param timeout 超时时间
 * @param tag 用于取消请求
 */
void pushCreate(Context context, String startTime, String endTime, YouYunHttpCallback callback, int timeout, Object tag);
```
### 4、查询PUSH注册信息
```
/**
 * @param context 必填
 * @param callback
 * @param timeout
 * @param tag 用于取消请求
 */
void pushShowUsers(Context context, YouYunHttpCallback callback, int timeout, Object tag);
```
### 5、注销PUSH信息
```
/**
 * @param context 必填
 * @param callback
 * @param timeout
 * @param tag
 */
void pushCancle(Context context, YouYunHttpCallback callback, int timeout, Object tag);
```
## 四、测试

#### 在游云开发平台的 [推送管理](http://17youyun.com/web/push/pushManage) 中进行测试

## 五、自定义通知

#### 1、自定义游云通知样式

##### 新建YouYunPushReceiver继承WeimiPushReceiver，在onReceiveMessage()中自定义通知样式及点击事件，并在AndroidManifest中覆盖WeimiPushReceiver广播

```
public class YouYunPushReceiver extends WeimiPushReceiver {

    @Override
    protected void onReceiveMessage(Context context, PayLoadMessage payLoadMessage) {

    }
}
```
#### 2、自定义小米Receiver
##### 新建XiaoMiPushReceiver继承PushMessageReceiver，并在onReceiveRegisterResult()中设置别名，并在AndroidManifest中覆盖MiPushMsgReceiver广播

```
public class YouYunPush extends PushMessageReceiver {

    private String mRegId;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceivePassThroughMessage(context, miPushMessage);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageClicked(context, miPushMessage);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageArrived(context, miPushMessage);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        String command = miPushCommandMessage.getCommand();
        List<String> arguments = miPushCommandMessage.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        if(MiPushClient.COMMAND_REGISTER.equals(command)) {
            if(miPushCommandMessage.getResultCode() == ErrorCode.SUCCESS) {
                this.mRegId = cmdArg1;
                // 设置别名
                MiPushClient.setAlias(context.getApplicationContext(), Constants.MiPush + OpenUDIDManager.getOpenUDID(), null);
            }
        } else {
            String log = miPushCommandMessage.getReason();
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onCommandResult(context, miPushCommandMessage);
    }
}
```
#### 3、重写了WeimiPushReceiver和PushMessageReceiver后ClickReceiver将失去作用，需要用户自己上报点击行为

```
/**
 * @param context 必填
 * @param apiPushId pushId,收到消息会返回 必填
 * @param isOnline 必填
 * @param httpCallback
 * @param timeout
 * @param tag
 */
void pushClick(Context context, String apiPushId, boolean isOnline, YouYunHttpCallback httpCallback, int timeout, Object tag);
```




