package com.espressif.iot.esptouch.demo_activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * WiFi管理类
 */
public class EspWifiAdminSimple {

	private final Context mContext;

	public EspWifiAdminSimple(Context context) {
		mContext = context;
	}


	//获取当前WiFi_SSID数据;
	public String getWifiConnectedSsid() {
		WifiInfo mWifiInfo = getConnectionInfo();
		String ssid = null;
		if (mWifiInfo != null && isWifiConnected()) {
			int len = mWifiInfo.getSSID().length();
			if (mWifiInfo.getSSID().startsWith("\"")
					&& mWifiInfo.getSSID().endsWith("\"")) {
				ssid = mWifiInfo.getSSID().substring(1, len - 1);
			} else {
				ssid = mWifiInfo.getSSID();
			}

		}
		return ssid;
	}

	//获取连接的WiFi
	public String getWifiConnectedSsidAscii(String ssid) {
		final long timeout = 100;//超时
		final long interval = 20;//
		String ssidAscii = ssid;//ssid;

		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();//开启扫描;

		boolean isBreak = false;//是否断开
		long start = System.currentTimeMillis();
		do {
			try {
				Thread.sleep(interval);//休眠0.02秒;
			} catch (InterruptedException ignore) {
				isBreak = true;
				break;
			}
			List<ScanResult> scanResults = wifiManager.getScanResults();
			for (ScanResult scanResult : scanResults) {
				if (scanResult.SSID != null && scanResult.SSID.equals(ssid)) {//配对当前ssid成功
					isBreak = true;//断开
					try {//反射1.属性wifiSsid;
						Field wifiSsidfield = ScanResult.class
								.getDeclaredField("wifiSsid");
						wifiSsidfield.setAccessible(true);
						Class<?> wifiSsidClass = wifiSsidfield.getType();//获取当前字段的类型;
						Object wifiSsid = wifiSsidfield.get(scanResult);// WiFi字段获取当前值
						Method method = wifiSsidClass
								.getDeclaredMethod("getOctets");//wifissidclass类获取方法getOctets
						byte[] bytes = (byte[]) method.invoke(wifiSsid);//返回一个二进制数据;
						ssidAscii = new String(bytes, "ISO-8859-1");//
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		} while (System.currentTimeMillis() - start < timeout && !isBreak);

		return ssidAscii;
	}
	
	public String getWifiConnectedBssid() {//获取bssid
		WifiInfo mWifiInfo = getConnectionInfo();
		String bssid = null;
		if (mWifiInfo != null && isWifiConnected()) {
			bssid = mWifiInfo.getBSSID();
		}
		return bssid;
	}

	// get the wifi info which is "connected" in wifi-setting
	private WifiInfo getConnectionInfo() {//获取WiFiINFo链接信息;
		WifiManager mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return wifiInfo;
	}

	private boolean isWifiConnected() {//判断当前WiFi是否链接;
		NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
		boolean isWifiConnected = false;
		if (mWiFiNetworkInfo != null) {
			isWifiConnected = mWiFiNetworkInfo.isConnected();
		}
		return isWifiConnected;
	}

	private NetworkInfo getWifiNetworkInfo() {//获取当前网络信息类NetworkInfo
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo;
	}
}
