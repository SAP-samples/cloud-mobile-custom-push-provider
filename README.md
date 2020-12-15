[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/cloud-platform-mobile-custom-push-provider)](https://api.reuse.software/info/github.com/SAP-samples/cloud-platform-mobile-custom-push-provider)
# Sample of Custom Push Provider
This repository will hold source code for Custom Push Provider apps and test code. We tested Custom Push for Huawei devices and Xiaomi devices.

#### [Custom Push Server Sample](./custom-push-server-sample-java)
custom-push-server-sample-java is a Java Springboot project which can perform as an endpoint in SAP Mobile Service Connectivity. It will work with SAP Mobile Service Push Notification. The customer should build its own Custom Push server function here. Once provided Huawei developer account or Xiaomi account information, this project can be a standalone server or deployed to Cloud Foundry. 

#### [Xiaomi device test](./xiaomi-push-clientdemo-android)
Xiaomi android studio device project

#### [Huawei device test](./hms-push-clientdemo-android)
Sample Huawei device push test app

License
-------
Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.
