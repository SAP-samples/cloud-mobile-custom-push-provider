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

package com.sap.cloud.platform.mobile.services.custom.push.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.platform.mobile.services.custom.push.service.PushAPI;
import com.sap.cloud.platform.mobile.services.custom.push.service.PushException;
import com.sap.cloud.platform.mobile.services.custom.push.service.PushProvider;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CustomPushController implements InitializingBean {

	@Autowired(required = false)
	@Qualifier("HuaweiPush")
	private PushAPI huaweiPush;

	@Autowired(required = false)
	@Qualifier("XiaomiPush")
	private PushAPI xiaomiPush;

	private PushAPI push;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("Setting the push provider...");
		String activePushProvider = ResourceBundle.getBundle("config").getString("active_push_provider");

		if (StringUtils.equalsIgnoreCase(activePushProvider, PushProvider.HUAWEI.name())) {
			push = huaweiPush;
		} else if (StringUtils.equalsIgnoreCase(activePushProvider, PushProvider.XIAOMI.name())) {
			push = xiaomiPush;
		} else {
			throw new IllegalArgumentException("Active push provider is not specified correctly");
		}

		if (push == null) {
			throw new IllegalArgumentException("Push provider is not initialized");
		}
	}

	@PostMapping("/push")
	public ResponseEntity<?> pushMessage(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> body) {
		log.debug("Push request body: {}", body);
		try {
			String token = (String) body.get("push_token");
			String deviceModel = (String) body.get("device_model");
			Map<String, Object> payload = (Map<String, Object>) body.get("payload");
			String requestId = push.pushMessage(token, deviceModel, payload);
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("message_id", requestId);
			log.debug("message_id: {}", requestId);
			return ResponseEntity.ok(result);
		} catch (PushException ex) {
			log.error("Error happens when pushing message", ex);
			Map<String, Object> result = new HashMap<>();
			result.put("error_message", ex.getMessage());
			if (ex.getCode() == 200) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}

	}

	@GetMapping("/")
	public ResponseEntity<?> indexPage() {
		return ResponseEntity.ok("");
	}
}