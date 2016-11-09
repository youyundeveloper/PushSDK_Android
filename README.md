# **Youyun Android IM PUSH_SDK 集成**

## 一、前期准备
在您阅读此文档之前，我们假定您已具备基础的 Android 应用开发经验，并能够理解相关基础概念。

## 注：此PUSH服务依赖Wchat_SDK,此文档必须是集成了IM后.如果您只需要PUSH服务可以查看[游云PUSH文档]文档

### 1、下载 SDK

您可以到 [游云Github平台](https://github.com/youyundeveloper/PushSDK_Android/tree/youyun_mi) 下载游云 SDK。下载包中分为如下两部分：

- PUSH Lib - 游云 PUSH库和相关库
- PUSH Demo

 Demo简单集成了PUSH功能，供您参考,您可以在开发者平台进行测试。

### 2、如小米平台需使用小米PUSH则需要此步骤：
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

### 1、将您下载的jar包导入您项目的libs目录，PUSH基础包

```
youyun-push-android-1.0.1.jar
```
#### 注：如小米平台需使用小米PUSH则需加入小米PUSH jar包：

```
MiPush_SDK_Client_3_0_3.jar
```

### 2、如小米平台需使用小米PUSH则需额外加入如下权限：

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

### 4、新建class PushMsgReceiver继承ClickReceiver ,在此接收PUSH消息。其中集成游云PUSH接收到PUSH消息后回回调onYouYunReceiveMessage()方法,集成小米PUSH收到PUSH消息则没有回调次方法,当点击通知栏消息时都会回调onNotificationClicked()方法。

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
WeimiInstance.getInstance().xxx();
```
## 三、接口文档

### 注：下面接口是在登录成功IM后调用

### 1、启动PUSH服务
```
@param context
@param pushServerHost WeimiPush.pushServerIp or WeimiPush.testPushServerIp
@param isLogEnable 是否打开日志
boolean connect(Context context, String pushServerHost,
boolean isLogEnable)

启动PUSH接口方法：WeimiPush.connect();
```
### 2、注册PUSH信息
```
@param startTime 勿扰时段开始时间
@param endTime 勿扰时段结束时间
@param httpCallback 回调 {"code":"200"}
@param timeout 超时 单位秒
@return boolean
boolean shortPushCreate(String startTime, String endTime, HttpCallback httpCallback, int timeout);
```
### 3、查询PUSH注册信息
```
@param httpCallback 回调 {"code":"200","msg":{"device_token":"android-168de9ab8f3a5baf","display":7,"start_time":0,"end_time":24,"data_type":63,"user_id":10001}}
@param timeout 超时 单位秒
@return boolean
boolean shortPushShowUser(HttpCallback httpCallback, int timeout);
```
### 4、注销PUSH信息
```
@param httpCallback 回调 {"code":"200"}
@param timeout 超时 单位秒
@return boolean
boolean shortPushCancel(HttpCallback httpCallback, int timeout);
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
 * push消息点击量
 * @param context
 * @param isTest 正式服务器 or 测试服务器
 * @param apiPushId 消息id
 * @param httpCallback
 * @param timeout
 * @return
 */
boolean pushClick(Context context, String apiPushId, boolean isTest, HttpCallback httpCallback, int timeout);
```




