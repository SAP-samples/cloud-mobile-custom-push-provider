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

public class PushException extends Exception {

	private static final long serialVersionUID = 3460671240949998970L;

	private int code = 500;

	public PushException(Exception ex) {
		super(ex);
	}

	public PushException(String message) {
		super(message);
	}

	public PushException(int code, Exception ex) {
		super(ex);
		this.code = code;
	}

	public PushException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
