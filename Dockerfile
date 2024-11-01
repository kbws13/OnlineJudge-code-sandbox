# 创建 Ubuntu 镜像
FROM ubuntu:20.04

# 修改默认终端为 bash
SHELL ["/bin/bash", "-c"]

# 设置为中国国内源
RUN sed -i s@/ports.ubuntu.com/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN sed -i s@/archive.ubuntu.com/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN sed -i s@/security.ubuntu.com/@/mirrors.aliyun.com/@g /etc/apt/sources.list

RUN apt-get clean

# 更新镜像到最新的包
RUN apt-get update
RUN apt-get update -y

# 安装需要的包
RUN apt-get install software-properties-common -y
RUN apt-get install zip unzip curl wget tar -y

# 安装 Python
RUN apt-get install python python3-pip -y

# 安装 C
RUN apt-get install gcc -y

# 安装 C++
RUN apt-get install g++ -y

# 安装 Java
RUN apt-get install default-jdk -y
RUN apt-get install default-jre -y

# 安装 Node.js
RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get install nodejs -y

# 安装 Go
RUN apt-get install golang -y
ENV GOCACHE /box
ENV GOTMPDIR /box

# 更新包
RUN apt-get clean -y
RUN apt-get autoclean -y
RUN apt-get autoremove -y

# 设置默认工作目录
WORKDIR /box