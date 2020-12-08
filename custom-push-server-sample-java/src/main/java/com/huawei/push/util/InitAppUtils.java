/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
 */
package com.huawei.push.util;

import java.util.ResourceBundle;

import com.huawei.push.messaging.HuaweiApp;
import com.huawei.push.messaging.HuaweiCredential;
import com.huawei.push.messaging.HuaweiOption;

public class InitAppUtils {
	/**
	 * @return HuaweiApp
	 */
	public static HuaweiApp initializeApp() {
		String appId = ResourceBundle.getBundle("config").getString("huawei_app_id");
		String appSecret = ResourceBundle.getBundle("config").getString("huawei_app_secret");
		// Create HuaweiCredential
		// This appId and appSecret come from Huawei Developer Alliance
		return initializeApp(appId, appSecret);
	}

	private static HuaweiApp initializeApp(String appId, String appSecret) {
		HuaweiCredential credential = HuaweiCredential.builder().setAppId(appId).setAppSecret(appSecret).build();

		// Create HuaweiOption
		HuaweiOption option = HuaweiOption.builder().setCredential(credential).build();

		// Initialize HuaweiApp
//        return HuaweiApp.initializeApp(option);
		return HuaweiApp.getInstance(option);
	}
}
