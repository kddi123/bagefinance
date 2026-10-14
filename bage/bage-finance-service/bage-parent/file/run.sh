#!/bin/bash

# 进入上级目录
cd ..
# 清理并安装依赖
mvn clean install

cd bage-common
mvn clean install -DskipTests

cd ../bage-mybatis
mvn clean install -DskipTests

cd ../bage-wx
mvn clean install -DskipTests

cd ../bage-finance-biz -DskipTests
mvn clean install

cd ../bage-finance-admin-api
mvn clean package -DskipTests

mvn spring-boot:run -DskipTests -Dspring-boot.run.profiles=local
