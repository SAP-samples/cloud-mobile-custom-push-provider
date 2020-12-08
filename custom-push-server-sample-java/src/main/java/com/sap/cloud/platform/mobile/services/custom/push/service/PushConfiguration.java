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

package com.sap.cloud.platform.mobile.services.custom.push.service;

import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.platform.mobile.services.custom.push.service.huawei.HuaweiPush;
import com.sap.cloud.platform.mobile.services.custom.push.service.xiaomi.XiaomiPush;

@Configuration
public class PushConfiguration {

	private String activePushProvider = ResourceBundle.getBundle("config").getString("active_push_provider");

	@Bean("HuaweiPush")
	public PushAPI createHuaweiPush() {
		if (StringUtils.equalsIgnoreCase(activePushProvider, PushProvider.HUAWEI.name())) {
			return new HuaweiPush();
		}
		return null;
	}

	@Bean("XiaomiPush")
	public PushAPI createXiaomiPush() {
		if (StringUtils.equalsIgnoreCase(activePushProvider, PushProvider.XIAOMI.name())) {
			return new XiaomiPush();
		}
		return null;
	}
}
