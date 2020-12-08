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

package com.sap.cloud.platform.mobile.services.custom.push.service.xiaomi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

import com.sap.cloud.platform.mobile.services.custom.push.service.PushAPI;
import com.sap.cloud.platform.mobile.services.custom.push.service.PushException;
import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Region;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XiaomiPush implements PushAPI {

	private static String APP_SECRET_KEY = ResourceBundle.getBundle("config").getString("xiaomi_app_secret_key");
	private static String APP_PACKAGE_NAME = ResourceBundle.getBundle("config").getString("xiaomi_app_package_name");

	private Sender sender;

	public XiaomiPush() {
		sender = new Sender(APP_SECRET_KEY, Region.Other);
		Constants.useOfficial();
		log.debug("Xiaomi Push initialization is complete");
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
				throw new Exception("Device model is not supported: " + deviceModel);
			}
		} catch (Exception ex) {
			log.debug("Error happens when pushing message to device", ex);
			if (ex instanceof PushException) {
				throw (PushException) ex;
			}
			throw new PushException(ex);
		}
	}

	private String pushToAndroid(String registrationId, Map<String, Object> payload) throws PushException, IOException {
		log.debug("Push to android device...");

		String alert = (String) payload.get("alert");

		Map<String, Object> customNotification = (Map<String, Object>) payload.get("custom");
		log.debug("Custom parameters: {}", customNotification);

		String description = customNotification == null ? null : (String) customNotification.get("description");
		// Use alert value as description value when description is not specified
		if (description == null) {
			description = alert;
		}

		String body = customNotification == null ? null : (String) customNotification.get("body");
		// Use alert value as body value when body is not specified
		if (body == null) {
			body = alert;
		}

		Result result = null;
		// Build message
		Message message = new Message.Builder().title(alert).description(description).payload(body).notifyType(1)
				.restrictedPackageName(APP_PACKAGE_NAME).build();

		log.debug("Sending message...");
		result = sender.unionSend(message, Arrays.asList(registrationId), 3);
		log.debug("Push result: {}", result);

		if (result != null) {
			if (result.getErrorCode().getValue() == ErrorCode.Success.getValue()) {
				String messageID = result.getMessageId();
				return messageID;
			}
			throw new PushException("Sending message to device failed, errorCode: " + result.getErrorCode().toString()
					+ ", reason: " + result.getReason());
		}
		throw new PushException("Sending message to device failed, the result is null.");
	}

	private String pushToApns(String registrationId, Map<String, Object> payload) throws PushException, IOException {
		log.debug("Push to apns device...");

		String alert = (String) payload.get("alert");
		Integer badge = (Integer) payload.get("badge");
		String sound = (String) payload.get("sound");

		Map<String, Object> customNotification = (Map<String, Object>) payload.get("custom");
		log.debug("Custom parameters: {}", customNotification);

		String description = customNotification == null ? null : (String) customNotification.get("description");
		// Use alert value as description value when description is not specified
		if (description == null) {
			description = alert;
		}

		Result result = null;
		// Build message
		Message.IOSBuilder builder = new Message.IOSBuilder().title(alert).description(description);
		if (sound != null) {
			builder = builder.soundURL(sound);
		}
		if (badge != null) {
			builder = builder.badge(badge);
		}
		Message message = builder.build();

		log.debug("Sending message...");
		result = sender.unionSend(message, Arrays.asList(registrationId), 3);
		log.debug("Push result: {}", result);

		if (result != null) {
			if (result.getErrorCode().getValue() == ErrorCode.Success.getValue()) {
				String messageID = result.getMessageId();
				return messageID;
			}
			throw new PushException("Sending message to device failed, errorCode: " + result.getErrorCode().toString()
					+ ", reason: " + result.getReason());
		}
		throw new PushException("Sending message to device failed, the result is null.");
	}
}
