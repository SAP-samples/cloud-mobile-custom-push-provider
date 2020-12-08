/**
 * Copyright 2020 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sap.cloud.platform.mobile.services.custom.push.service.huawei;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huawei.push.android.AndroidNotification;
import com.huawei.push.android.BadgeNotification;
import com.huawei.push.android.ClickAction;
import com.huawei.push.apns.Alert;
import com.huawei.push.apns.ApnsHeaders;
import com.huawei.push.apns.ApnsHmsOptions;
import com.huawei.push.apns.Aps;
import com.huawei.push.exception.HuaweiMesssagingException;
import com.huawei.push.message.AndroidConfig;
import com.huawei.push.message.ApnsConfig;
import com.huawei.push.message.Message;
import com.huawei.push.message.Notification;
import com.huawei.push.messaging.HuaweiApp;
import com.huawei.push.messaging.HuaweiMessaging;
import com.huawei.push.model.Visibility;
import com.huawei.push.reponse.SendResponse;
import com.huawei.push.util.InitAppUtils;
import com.sap.cloud.platform.mobile.services.custom.push.service.PushAPI;
import com.sap.cloud.platform.mobile.services.custom.push.service.PushException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HuaweiPush implements PushAPI {

	private static final String HUAWEI_MESSAGING_KNOWN_ERROR = "known error";
	private HuaweiMessaging huaweiMessaging;

	public HuaweiPush() {
		HuaweiApp app = InitAppUtils.initializeApp();
		huaweiMessaging = HuaweiMessaging.getInstance(app);
		log.debug("Huawei Push initialization is complete");
	}

	@Override
	public String pushMessage(String token, String deviceModel, Map<String, Object> payload) throws PushException {
		try {
			log.debug("Token: {}", token);
			log.debug("Device Model: {}", deviceModel);
			log.debug("Payload: {}", payload);

			switch (deviceModel) {
			case "android":
				return pushToAndroid(token, payload);
			case "ios":
				return pushToApns(token, payload);
			default:
				throw new PushException("Device model is not supported: " + deviceModel);
			}
		} catch (Exception ex) {
			log.debug("Error happens when pushing message to device", ex);
			if (ex instanceof PushException) {
				throw (PushException) ex;
			}
			if (ex instanceof HuaweiMesssagingException) {
				if (StringUtils.equals(HUAWEI_MESSAGING_KNOWN_ERROR, ((HuaweiMesssagingException) ex).getErrorCode())) {
					throw new PushException(200, ex.getMessage());
				}
				throw new PushException(ex.getMessage());
			}
			throw new PushException(ex);
		}
	}

	private String pushToAndroid(String token, Map<String, Object> payload)
			throws HuaweiMesssagingException, PushException {
		log.debug("Push to android device...");

		String alert = (String) payload.get("alert");
		Integer badge = (Integer) payload.get("badge");
		String sound = (String) payload.get("sound");
		String data = (String) payload.get("data");
		String priority = (String) payload.get("priority");

		// Build message
		Map<String, Object> customNotification = (Map<String, Object>) payload.get("custom");
		log.debug("Custom parameters: {}", customNotification);

		String body = customNotification == null ? null : (String) customNotification.get("body");
		// Use alert value as body value when body is not specified
		if (body == null) {
			body = alert;
		}

		Notification notification = Notification.builder().setTitle(alert).setBody(body).build();

		AndroidNotification.Builder androidNotificationBuilder = AndroidNotification.builder().setTitle(alert)
				.setBody(body);
		if (sound != null) {
			androidNotificationBuilder = androidNotificationBuilder.setSound(sound);
		}
		if (badge == null) {
			badge = 1;
		}
		if (priority == null) {
			priority = "HIGH";
		}
		AndroidNotification androidNotification = androidNotificationBuilder.setDefaultSound(true)
				.setBadge(
						BadgeNotification.builder().setAddNum(badge).setSetNum(badge).setBadgeClass("Classic").build())
				.setClickAction(ClickAction.builder().setType(3).build()).setVisibility(Visibility.PUBLIC.getValue())
				.setForegroundShow(true).build();

		AndroidConfig androidConfig = AndroidConfig.builder().setUrgency(StringUtils.upperCase(priority))
				.setNotification(androidNotification).build();

		Message.Builder builder = Message.builder().setNotification(notification).setAndroidConfig(androidConfig);
		if (data != null) {
			builder = builder.setData(data);
		}
		Message message = builder.addToken(token).build();

		log.debug("Sending message...");
		SendResponse response = huaweiMessaging.sendMessage(message);
		log.debug("Request id: {}", response.getRequestId());

		return response.getRequestId();
	}

	private String pushToApns(String token, Map<String, Object> payload)
			throws HuaweiMesssagingException, PushException {
		log.debug("Push to apns device...");

		String alert = (String) payload.get("alert");
		Integer badge = (Integer) payload.get("badge");
		String sound = (String) payload.get("sound");
		String data = (String) payload.get("data");
		String priority = (String) payload.get("priority");

		Map<String, Object> customNotification = (Map<String, Object>) payload.get("custom");
		log.debug("Custom parameters: {}", customNotification);

		String body = customNotification == null ? null : (String) customNotification.get("body");
		// Use alert value as body value when body is not specified
		if (body == null) {
			body = alert;
		}
		String apnsId = customNotification == null ? null : (String) customNotification.get("apnsId");
		String image = customNotification == null ? null : (String) customNotification.get("image");

		if (badge == null) {
			badge = 1;
		}
		if (priority == null) {
			priority = "HIGH";
		}

		// Build message
		ApnsHeaders.Builder apnsHeadersBuilder = ApnsHeaders.builder();
		if (apnsId != null) {
			apnsHeadersBuilder = apnsHeadersBuilder.setApnsId(apnsId);
		}
		ApnsHeaders apnsHeaders = apnsHeadersBuilder.setApnsPriority(priority).build();

		Alert.Builder altertBuilder = Alert.builder().setTitle(alert).setBody(body);
		if (image != null) {
			altertBuilder = altertBuilder.setLaunchImage(image);
		}
		Alert apsAlert = altertBuilder.build();

		Aps.Builder apsBuilder = Aps.builder().setAlert(apsAlert).setBadge(badge);
		if (sound != null) {
			apsBuilder = apsBuilder.setSound(sound);
		}
		Aps aps = apsBuilder.build();

		ApnsHmsOptions apnsHmsOptions = ApnsHmsOptions.builder().build();

		ApnsConfig apns = ApnsConfig.builder().setApnsHeaders(apnsHeaders).addPayloadAps(aps)
				.setHmsOptions(apnsHmsOptions).build();

		Message.Builder messageBuilder = Message.builder().setApns(apns);
		if (data != null) {
			messageBuilder = messageBuilder.setData(data);
		}
		Message message = messageBuilder.addToken(token).build();

		log.debug("Sending message...");
		SendResponse response = huaweiMessaging.sendMessage(message);
		log.debug("Request id: {}", response.getRequestId());

		return response.getRequestId();
	}

}
