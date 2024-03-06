# 使用 openjdk 镜像的 8-jdk 版本作为基础镜像
FROM openjdk:8-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/oj-code-sandbox-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8090

# 在镜像中运行命令，更新软件包列表并安装 Python3
RUN apt-get update && apt-get install -y python3

# 设置容器的时区为 Asia/Shanghai，并将其复制到 /etc/localtime 文件，同时设置时区信息到 /etc/timezone 文件
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

# 启动命令
ENTRYPOINT ["java","-jar","/app/oj-backend-user-service-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
