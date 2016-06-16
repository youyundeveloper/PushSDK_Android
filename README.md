# **Youyun Android PUSH_SDK 集成**

## 前期准备
在您阅读此文档之前，我们假定您已具备基础的 Android 应用开发经验，并能够理解相关基础概念。

### 1、注册开发者帐号
开发者在集成游云PUSH功能前，需前往 [游云官方网站](http://www.17youyun.com) 注册创建游云开发者帐号。

### 2、下载 SDK

您可以到 [游云官方网站](http://www.17youyun.com) 下载游云 SDK。下载包中分为如下两部分：

- PUSH Lib - 游云 PUSH库和相关库
- PUSH Dome 

 Dome简单集成了PUSH功能，供您参考,您可以在开发者平台进行测试。
 
### 3、创建应用

您要进行应用开发之前，需要先在游云开发者平台创建应用。如果您已经注册了游云开发者帐号，请前往 [游云开发者平台](http://www.17youyun.com) 创建应用。

您创建完应用后，首先需要了解的是 App ClientID / Secret，它们是游云 SDK 连接服务器所必须的标识，每一个 App 对应一套 App ClientID / Secret。

## 集成开发

### 1、将您下载的jar包导入您项目的libs目录，PUSH需要如下jar包

```
commons-fileupload-1.2.1.jar
commons-httpclient-3.1.jar
commons-lang-2.6.jar
youyun-protobuf-java-2.4.1.jar
youyun-push-android-1.0.1.jar
youyun-wchat-android-1.0.1.jar
```
### 2、在您项目的AndroidManifest.xml文件中加入以下权限

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
### 3、在您项目的AndroidManifest.xml文件中加入以下服务

```
<service 
    android:name="com.weimi.push.service.WeimiPushService"
    android:exported="true"
    android:process=":push">
        <intent-filter>
            <action android:name="me.weimi.PushService.BIND" />
        </intent-filter>
</service>
```
### 4、新建class PushMsgReceiver继承WeimiPushReceiver ,在此接收PUSH消息

```
public class PushMsgReceiver extends WeimiPushReceiver {

	@Override
	public void onMessage(Context context, PayLoadMessage payLoadMessage) {
	    // 在此接收PUSH消息
	    String alertMsg = payLoadMessage.alert;
    }
}
```
### 5、在您项目的AndroidManifest.xml文件中加入以下广播(PushMsgReceiver即上面实现WeimiPushReceiver 的类)

```
<receiver android:name=".PushMsgReceiver"
          android:exported="false">
             <intent-filter>
                <action android:name="me.weimi.push.action.10001" />
                <!-- me.weimi.push.action.第三方的appId -->
            </intent-filter>
</receiver>
```

### 6、调用SDK接口即可开发

```
WeimiInstance.getInstance().xxx();
```
## 一、接口文档

### 1、初始化SDK

```
@param context 上下文
@param udid 设备唯一标识
@param clientId ClientId
@param secret 密钥
@param timeout 超时 单位秒
@return AuthResultData{boolean success, String userInfo} 
AuthResultData registerApp(Context context, String udid, String clientId, String secret, int timeout);
```
### 2、初始化SDK（测试）
```
@param context 上下文
@param udid 设备唯一标识
@param clientId ClientId
@param secret 密钥
@param timeout 超时 单位秒
@return AuthResultData{boolean success, String userInfo}
AuthResultData testRegisterApp(Context context, String udid, String clientId, String secret, int timeout);
```
### 3、获取用户Id

```
@return 用户Id
String getUID();
```
### 4、注销

```
@return true or false
boolean logout();
```
### 5、启动PUSH服务
```
@param context
@param pushServerHost WeimiPush.pushServerIp or WeimiPush.testPushServerIp
@param isLogEnable 是否打开日志
boolean connect(Context context, String pushServerHost,
boolean isLogEnable)

启动PUSH接口方法：WeimiPush.connect();
```
### 6、注册PUSH信息
```
@param startTime 勿扰时段开始时间
@param endTime 勿扰时段结束时间
@param httpCallback 回调 {"code":"200"}
@param timeout 超时 单位秒
@return boolean
boolean shortPushCreate(String startTime, String endTime, HttpCallback httpCallback, int timeout);
```
### 7、查询PUSH注册信息
```
@param httpCallback 回调 {"code":"200","msg":{"device_token":"android-168de9ab8f3a5baf","display":7,"start_time":0,"end_time":24,"data_type":63,"user_id":10001}}
@param timeout 超时 单位秒
@return boolean
boolean shortPushShowUser(HttpCallback httpCallback, int timeout);
```
### 8、注销PUSH信息
```
@param httpCallback 回调 {"code":"200"}
@param timeout 超时 单位秒
@return boolean
boolean shortPushCancel(HttpCallback httpCallback, int timeout);
```
