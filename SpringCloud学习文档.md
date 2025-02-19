# 前言

## dubbo的使用

### 一、什么是dubbo

dubbo是SOA（（Service-Oriented Architecture，面向服务架构）是一种将应用程序功能以“服务”的形式进行模块化设计的架构风格。）时代的产物，它的关注点主要在于服务的调用和治理。**是Java实现的**



RPC框架：*RPC*(Remote Procedure Call):远程过程调用

<font color=red>服务消费者通过zookeeper把服务注册的信息**拉取到本地**</font>

![](https://i-blog.csdnimg.cn/direct/b4435d613e3347a384f154daee1810cd.png)

### 二、dubbo的使用

#### 1、父工程和各子工程都引入依赖

```xml-dtd
<!-- dubbo -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-registry-zookeeper</artifactId>
    <version>3.1.0</version>
</dependency>
```

#### 2、服务提供者8081

配置文件：

```yaml
spring:
  application:
    name: provider
server:
  port: 8081

dubbo:
 application:
  name: dubbo-provider #当前应用名称
 protocol:
  name: dubbo #协议名称
  port: -1 #配置为-1，则会分配一个没有被占用的端口
 registry:
  address: zookeeper://127.0.0.1:2181 #注册中心地址，这里选择zookeeper作为注册中心，也是官方推荐的
  timeout: 60000 #连接到注册中心的超时时间10S，时间太多可能很容易注册失败，默认5s
```

代码：

```java
@SpringBootApplication
@EnableDubbo //@EnableDubbo是Dubbo框架中的一个重要注解，主要用于激活Dubbo的自动配置和启动类上，开启Dubbo的支持。
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}
-----------------------------------------------------------------
@DubboService //@DubboService是Dubbo框架中用于服务提供方的一个重要注解。它的主要作用是将服务接口的实现类注册到注册中心，使其能够被服务消费方发现和调用
public class ProviderServiceImpl implements IProviderService {
    @Override
    public String dogSay() {
        return "汪汪汪！";
    }

    @Override
    public String catSay() {
        return "喵喵喵！";
    }

    @Override
    public String justSay(String msg) {
        return msg;
    }
}
```

#### 3、服务消费者8082

<font color=red>可能需要把服务提供者的模块mvn install后在服务消费者的pom中引用</font>

配置文件：

```yaml
spring:
  application:
    name: comsumer
server:
  port: 8082

dubbo:
  application:
    name: dubbo-comsumer #当前应用名称
  protocol:
    name: dubbo #协议名称
    port: -1 #配置为-1，则会分配一个没有被占用的端口
  registry:
    address: zookeeper://127.0.0.1:2181 #注册中心地址，这里选择zookeeper作为注册中心，也是官方推荐的
    timeout: 60000 #连接到注册中心的超时时间10S，时间太多可能很容易注册失败，默认5s
```

代码：

```java
@SpringBootApplication
@EnableDubbo
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
-----------------------------------------------------------------
@RestController
@RequestMapping("/dubbo")
public class ConsumerController {

    @DubboReference
    private IProviderService providerService;

    @GetMapping("/dogSay")
    public String dogSay(){
        return providerService.dogSay();
    }

    @GetMapping("/otherSay/{msg}")
    public String otherSay(@PathVariable String msg){
        return providerService.justSay(msg);
    }
}
```

### 三、dubbo+zookeeper在Windows系统上的实践参考

https://www.bilibili.com/video/BV1XK411f7jL/?spm_id_from=333.337.search-card.all.click&vd_source=486ea2f9b831b9a1dc22cbbe98c55def

## zookeeper的使用

Linux环境的下载和安装：https://blog.csdn.net/QDNBD/article/details/142206926 （需要java环境的支持才能运行）

zookeeper端口：clientPort=**2181**

**zoo.cfg详情**

```xml-dtd
# The number of milliseconds of each tick
# zookeeper内部的基本单位，单位是毫秒，这个表示一个tickTime为2000毫秒，在zookeeper的其他配置中，都是基于tickTime来做换算的
tickTime=2000
# The number of ticks that the initial 
# synchronization phase can take
#集群中的follower服务器(F)与leader服务器(L)之间 初始连接 时能容忍的最多心跳数（tickTime的数量）。
initLimit=10
# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
#syncLimit：集群中的follower服务器(F)与leader服务器(L)之间 请求和应答 之间能容忍的最多心跳数（tickTime的数量）
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
# 数据存放文件夹，zookeeper运行过程中有两个数据需要存储，一个是快照数据（持久化数据）另一个是事务日志
dataDir=E:\\zookeeper\\apache-zookeeper-3.7.2-bin\\data #要改
dataLogDir=E:\\zookeeper\\apache-zookeeper-3.7.2-bin\\log #要加
# the port at which the clients will connect
# 客户端访问端口
clientPort=2181
admin.serverPort=2182 #要加
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the 
# administrator guide before turning on autopurge.
#
# http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1

## Metrics Providers
#
# https://prometheus.io Metrics Exporter
#metricsProvider.className=org.apache.zookeeper.metrics.prometheus.PrometheusMetricsProvider
#metricsProvider.httpPort=7000
#metricsProvider.exportJvmInfo=true
```





# SpringCloud

SpringBoot，新一代的JavaEE开发标准，自动装配



微服务框架的4个**核心问题**？

​	1.服务很多，客户端怎么访问？

​	2.这么多服务，服务之间如何通信？

​	3.这么多服务？如何治理？

​	4.服务挂了怎么办？



​	1.api（网关）

​	2.http，rpc（通信）

​	3.注册和发现（高可用）

​	4.熔断机制（服务降级）



解决方案：

​	Spring Cloud 是生态，很多技术的组合

​	1.Spring Cloud NetFlix 一站式解决方案

​		api网关，zuul组件

​		Feign ---HttpClient ---http通信方式，同步，阻塞（dubbo）

​		服务注册发现：Eureka

​		熔断机制：Hystrix

​		。。。

​	2.Apache Dubbo Zookeeper 半自动，需要整合别人的

​		api网关：没有，找第三方组件，或者自己实现

​		Dubbo

​		Zookeeper

​		熔断机制：没有，可以借助Hystrix

​		这个方案并不完善~



​	3.Spring Cloud Alibaba 一站式解决方案！更简单



新概念：服务网格~Server Mesh

​	istio



问题：因为网络不可靠，所以分布式会衍生出一系列问题

## 微服务概述	

- 就目前而言，对于微服务，业界并没有一个统一的，标准的定义。
- 但通常而言，微服务架构是一种架构模式，或者说是一种架构风格，**它提倡将单一的应用程序划分成一组小的服务**，每个服务运行在其独立的自己的进程内，服务之间互相协调，互相配置，为用户提供最终价值，服务之间采用轻量级的通信机制(**HTTP**)互相沟通，每个服务都围绕着具体的业务进行构建，并且能够被独立的部署到生产环境中，另外，应尽量避免统一的，集中式的服务管理机制，对具体的一个服务而言，应该根据业务上下文，选择合适的语言，工具(**Maven**)对其进行构建，可以有一个非常轻量级的集中式管理来协调这些服务，可以使用不同的语言来编写服务，也可以使用不同的数据存储

**再来从技术维度角度理解下：**

- 微服务化的核心就是将传统的一站式应用，根据业务拆分成一个一个的服务，彻底地去耦合，每一个微服务提供单个业务功能的服务，一个服务做一件事情，从技术角度看就是一种小而独立的处理过程，类似进程的概念，能够自行单独启动或销毁，拥有自己独立的数据库。

**微服务**强调的是服务的大小，它关注的是某一个点，是具体解决某一个问题/提供落地对应服务的一个服务应用，狭义的看，可以看作是IDEA中的一个个微服务工程，或者Module。



**微服务优缺点**
**优点**

- 单一职责原则；

- 每个服务足够内聚，足够小，代码容易理解，这样能聚焦一个指定的业务功能或业务需求；
  开发简单，开发效率高，一个服务可能就是专一的只干一件事；

- 微服务能够被小团队单独开发，这个团队只需2-5个开发人员组成；

- 微服务是松耦合的，是有功能意义的服务，无论是在开发阶段或部署阶段都是独立的；
  微服务能使用不同的语言开发；

- 易于和第三方集成，微服务允许容易且灵活的方式集成自动部署，通过持续集成工具，如jenkins，Hudson，bamboo；

- 微服务易于被一个开发人员理解，修改和维护，这样小团队能够更关注自己的工作成果，无需通过合作才能体现价值；

- 微服务允许利用和融合最新技术；

- 微服务只是业务逻辑的代码，不会和HTML，CSS，或其他的界面混合;
  每个微服务都有自己的存储能力，可以有自己的数据库，也可以有统一的数据库；

**缺点**

- 开发人员要处理分布式系统的复杂性；
- 多服务运维难度，随着服务的增加，运维的压力也在增大；
- 系统部署依赖问题；
- 服务间通信成本问题；
- 数据一致性问题；
- 系统集成测试问题；
- 性能和监控问题；

**微服务技术栈有那些？**

| 微服务技术条目                         | 落地技术                                                     |
| -------------------------------------- | ------------------------------------------------------------ |
| 服务开发                               | SpringBoot、Spring、SpringMVC等                              |
| 服务配置与管理                         | Netfix公司的Archaius、阿里的Diamond等                        |
| 服务注册与发现                         | Eureka、Consul、Zookeeper等                                  |
| 服务调用                               | Rest、PRC、gRPC                                              |
| 服务熔断器                             | Hystrix、Envoy等                                             |
| 负载均衡                               | Ribbon、Nginx等                                              |
| 服务接口调用(客户端调用服务的简化工具) | Fegin等                                                      |
| 消息队列                               | Kafka、RabbitMQ、ActiveMQ等                                  |
| 服务配置中心管理                       | SpringCloudConfig、Chef等                                    |
| 服务路由(API网关)                      | Zuul等                                                       |
| 服务监控                               | Zabbix、Nagios、Metrics、Specatator等                        |
| 全链路追踪                             | Zipkin、Brave、Dapper等                                      |
| 数据流操作开发包                       | SpringCloud Stream(封装与Redis，Rabbit，Kafka等发送接收消息) |
| 时间消息总栈                           | SpringCloud Bus                                              |
| 服务部署                               | Docker、OpenStack、Kubernetes等                              |

## SpringCloud入门概述

Spring官网：https://spring.io/

![](https://i-blog.csdnimg.cn/blog_migrate/94550f99df069dbd97a1193c7a873636.png)



![](https://i-blog.csdnimg.cn/blog_migrate/fd071717894059dcbd84d3f21810b8e9.png)

SpringCloud，基于SpringBoot提供了一套微服务解决方案，包括**服务注册与发现，配置中心，全链路监控，服务**
**网关，负载均衡，熔断器**等组件,除了基于NetFlix的开源组件做高度抽象封装之外，还有一些选型中立的开源组
件。
SpringCloud利用SpringBoot的开发便利性，巧妙地简化了分布式系统基础设施的开发，SpringCloud为开发人员
提供了快速构建分布式系统的一些工具，**包括配置管理，服务发现，断路器，路由，微代理，事件总线，全局锁，**
**决策竞选，分布式会话等等**，他们都可以用SpringBoot的开发风格做到一键启动和部署。

SpringCloud并没有重复造轮子，它只是将目前各家公司开发的比较成熟，经得起实际考研的服务框架组合起来,
通过SpringBoot风格进行再封装，屏蔽掉了复杂的配置和实现原理，**最终给开发者留出了一套简单易懂，易部署**
**和易维护的分布式系统开发工具包**。

SpringCloud是分布式微服务架构下的一站式解决方案，是各个微服务架构落地技术的集合体，俗称微服务全家
桶。

## SpringCloud和SpringBoot关系

- SpringBoot专注于快速方便地开发单个个体微服务； -jar
- SpringCloud是关注全局的微服务协调整理治理框架，它将SpringBoot开发的一个个单体微服务整合并管理起来，为各个微服务之间提供：配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等集成服务；
- SpringBoot可以离开SpringCloud独立使用，开发项目，但SpringCloud离不开SpringBoot，属于依赖关系；
- **SpringBoot专注于快速、方便地开发单个个体微服务，SpringCloud关注全局的服务治理框架；**



传统的较大型网站架构图:

![](https://i-blog.csdnimg.cn/blog_migrate/01c3c794b94f84c7aaa56ccd1ec79fa4.png)

## Dubbo和SpringCloud对比

**对比结果：**

|              | Dubbo         | SpringCloud                  |
| ------------ | ------------- | ---------------------------- |
| 服务注册中心 | Zookeeper     | Spring Cloud Netfilx Eureka  |
| 服务调用方式 | RPC           | REST API                     |
| 服务监控     | Dubbo-monitor | Spring Boot Admin            |
| 断路器       | 不完善        | Spring Cloud Netfilx Hystrix |
| 服务网关     | 无            | Spring Cloud Netfilx Zuul    |
| 分布式配置   | 无            | Spring Cloud Config          |
| 服务跟踪     | 无            | Spring Cloud Sleuth          |
| 消息总栈     | 无            | Spring Cloud Bus             |
| 数据流       | 无            | Spring Cloud Stream          |
| 批量任务     | 无            | Spring Cloud Task            |

**最大区别：Spring Cloud 抛弃了Dubbo的RPC通信，采用的是基于HTTP的REST方式**

## SpringCloud能干嘛

- Distributed/versioned configuration 分布式/版本控制配置
- Service registration and discovery 服务注册与发现
- Routing 路由
- Service-to-service calls 服务到服务的调用
- Load balancing 负载均衡配置
- Circuit Breakers 断路器
- Distributed messaging 分布式消息管理
- …

SpringCloud没有采用数字编号的方式命名版本号，而是采用了伦敦地铁站的名称，同时根据字母表的顺序来对应版本时间顺序，比如最早的Realse版本：Angel，第二个Realse版本：Brixton，然后是Camden、Dalston、Edgware，目前最新的是Hoxton SR4 CURRENT GA通用稳定版。



SpringCloud Netflix 中文文档：https://springcloud.cc/spring-cloud-netflix.html
SpringCloud 中文API文档(官方文档翻译版)：https://springcloud.cc/spring-cloud-dalston.html
SpringCloud中文网：https://springcloud.cc

## SpringCloud版本选择

**大版本说明**

| SpringBoot | SpringCloud             | 关系                                     |
| ---------- | ----------------------- | ---------------------------------------- |
| 1.2.x      | Angel版本(天使)         | 兼容SpringBoot1.2x                       |
| 1.3.x      | Brixton版本(布里克斯顿) | 兼容SpringBoot1.3x，也兼容SpringBoot1.4x |
| 1.4.x      | Camden版本(卡姆登)      | 兼容SpringBoot1.4x，也兼容SpringBoot1.5x |
| 1.5.x      | Dalston版本(多尔斯顿)   | 兼容SpringBoot1.5x，不兼容SpringBoot2.0x |
| 1.5.x      | Edgware版本(埃奇韦尔)   | 兼容SpringBoot1.5x，不兼容SpringBoot2.0x |
| 2.0.x      | Finchley版本(芬奇利)    | 兼容SpringBoot2.0x，不兼容SpringBoot1.5x |
| 2.1.x      | Greenwich版本(格林威治) |                                          |

4、SpringCloud项目主依赖

```xml
<!--打包方式-->
<packaging>pom</packaging>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.version>4.12</junit.version>
    <lombok.version>1.18.36</lombok.version>
    <log4j.version>1.2.17</log4j.version>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
</properties>

<dependencyManagement>
    <dependencies>
        <!--SpringCloud的依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Greenwich.SR5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!--SpringBoot的依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.1.4.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!--数据库-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.10</version>
        </dependency>
        <!--springboot 启动器-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.1</version>
        </dependency>
        <!--junit-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>
        <!--log4j-->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>${log4j.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 5、Eureka服务注册与发现

### 5.1 什么是Eureka

- 尤里卡
- Netflix在涉及Eureka时，遵循的就是AP原则。
- Eureka是Netflix的有个子模块，也是核心模块之一。Eureka是基于REST的服务，用于定位服务，以实现云端中间件层服务发现和故障转移，服务注册与发现对于微服务来说是非常重要的，有了服务注册与发现，只需要使用服务的标识符，就可以访问到服务，而不需要修改服务调用的配置文件了，功能类似于Dubbo的注册中心，比如Zookeeper。

### 5.2 原理讲解

- Eureka基本的架构
  - Springcloud 封装了Netflix公司开发的Eureka模块来实现服务注册与发现 (对比Zookeeper)。
  - Eureka采用了C-S的架构设计，EurekaServer作为服务注册功能的服务器，他是服务注册中心。
  - 而系统中的其他微服务，使用Eureka的客户端连接到EurekaServer并维持心跳连接。这样系统的维护人员就可以通过EurekaServer来监控系统中各个微服务是否正常运行，Springcloud 的一些其他模块 (比如Zuul) 就可以通过EurekaServer来发现系统中的其他微服务，并执行相关的逻辑。

![](https://i-blog.csdnimg.cn/blog_migrate/6db11f60df4e59a2d76369d501844476.png#pic_center)



- 和Dubbo架构对比
![](https://i-blog.csdnimg.cn/blog_migrate/55a1e4622d346842a51627c76a3583fe.png#pic_center)


- Eureka 包含两个组件：Eureka Server 和 Eureka Client。
- Eureka Server 提供服务注册，各个节点启动后，会在EurekaServer中进行注册，这样Eureka Server中的服务注册表中将会储存所有课用服务节点的信息，服务节点的信息可以在界面中直观的看到。
- Eureka Client 是一个Java客户端，用于简化EurekaServer的交互，客户端同时也具备一个内置的，使用轮询负载算法的负载均衡器。在应用启动后，将会向EurekaServer发送心跳 (默认周期为30秒) 。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除掉 (默认周期为90s)。




- 三大角色

  - Eureka Server：提供服务的注册与发现
  - Service Provider：服务生产方，将自身服务注册到Eureka中，从而使服务消费方能够找到
  - Service Consumer：服务消费方，从Eureka中获取注册服务列表，从而找到消费服务

**springcloud-eureka-7001关键代码**

pom：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

application.yml:

```yml
server:
  port: 7001

#Eureka配置
eureka:
  instance:
    hostname: localhost #Eureka服务端的实例名称
  client:
    register-with-eureka: false #表示是否向eureka注册中心注册自己（本module是服务端，所以不用）
    fetch-registry: false #表示是否从eureka中获取注册信息；fetch-registry如果为false。则表示自己为注册中心
    service-url:  #监控页面~
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

主启动类：

```java
package com.luo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer //服务端的启动类，可以接受别人注册进来~
public class EurekaServer_7001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer_7001.class, args);
    }
}
```

### 5.3 EureKa自我保护机制：好死不如赖活着

一句话总结就是：**某时刻某一个微服务不可用，eureka不会立即清理，依旧会对该微服务的信息进行保存！**

- 默认情况下，当eureka server在一定时间内没有收到某个微服务实例的心跳，eureka server便会把该实例从注册表中删除（**默认是90秒**），但是，如果短时间内丢失大量的实例心跳，便会触发eureka server的自我保护机制，比如在开发测试时，需要频繁地重启微服务实例，但是我们很少会把eureka server一起重启（因为在开发过程中不会修改eureka注册中心），**当一分钟内收到的心跳数大量减少时，会触发该保护机制**。可以在eureka管理界面看到Renews threshold和Renews(last min)，当后者（最后一分钟收到的心跳数）小于前者（心跳阈值）的时候，触发保护机制，会出现红色的警告：<font color=red>EMERGENCY!EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEGING EXPIRED JUST TO BE SAFE.</font>从警告中可以看到，eureka认为虽然收不到实例的心跳，但它认为实例还是健康的，eureka会保护这些实例，不会把它们从注册表中删掉。
- 该保护机制的目的是避免网络连接故障，在发生网络故障时，微服务和注册中心之间无法正常通信，但服务本身是健康的，不应该注销该服务，如果eureka因网络故障而把微服务误删了，那即使网络恢复了，该微服务也不会重新注册到eureka server了，因为只有在微服务启动的时候才会发起注册请求，后面只会发送心跳和服务列表请求，这样的话，该实例虽然是运行着，但永远不会被其它服务所感知。所以，eureka server在短时间内丢失过多的客户端心跳时，会进入自我保护模式，该模式下，eureka会保护注册表中的信息，不在注销任何微服务，当网络故障恢复后，eureka会自动退出保护模式。自我保护模式可以让集群更加健壮。
- 在自我保护模式中，EurekaServer会保护服务注册表中的信息， 不再注销任何服务实例。当它收到的心跳数
  重新恢复到阈值以上时，该EurekaServer节点就会自动退出自我保护模式。它的设计哲学就是宁可保留错误
  的服务注册信息，也不盲目注销任何可能健康的服务实例。一句话: 好死不如赖活着。
- 综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务(健康的
  微服务和不健康的微服务都会保留)， 也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka
  集群更加的健壮和稳定。
- 但是我们在开发测试阶段，需要频繁地重启发布，如果触发了保护机制，则旧的服务实例没有被删除，这时请求有可能跑到旧的实例中，而该实例已经关闭了，这就导致请求错误，影响开发测试。所以，在开发测试阶段，我们可以把自我保护模式关闭，只需在eureka server配置文件中加上如下配置即可：==eureka.server.enable-self-preservation=false==【不推荐关闭自我保护机制】

**springcloud-provider-dept-8001关键代码**

pom：

```xml
<!--Eureka服务提供者-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<!--actuator完善监控信息-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

application.yml:

```yaml
server:
  port: 8001

#mybatis配置
mybatis:
  type-aliases-package: com.luo.pojo
  #config-location: classpath:mybatis-config.xml
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

#spring的配置
spring:
  application:
    name: springcloud-provider-dept
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource #数据源
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db01?useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123

#Eureka的配置，服务注册到哪里
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka/
  instance:
    instance-id: springcloud-provider-dept8001 #修改eureka上的默认描述信息！

#info配置
info:
  app.name: lws-springcloud
  company.name: blog.luostudy.com
```

主启动类：

```java
package com.luo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient //在服务启动后自动注册到Eureka中
public class DeptProvider_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProvider_8001.class, args);
    }
}
```

### 5.4 Eureka集群

https://www.bilibili.com/video/BV1jJ411S7xr?spm_id_from=333.788.videopod.episodes&vd_source=486ea2f9b831b9a1dc22cbbe98c55def&p=8

它是每每相互关联，平级关系，不存在主从关系

**主要代码：**

Eureka服务器节点：application.yml（3个节点示例，每个节点的yaml写其他两个节点的地址）

```yaml
eureka:
 client:
  service-url: #监控页面~      #重写Eureka的默认端口以及访问路径 --- # 集群（关联）：7001关联7002、7003
   defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
```

服务提供者：application.yml

```yaml
eureka:
 client:
  service-url:
    defaultZone: http://localhost:7001/eureka/,http://localhost:7002/eureka/,http://localhost:7003/eureka/ 
```

- 都启动后，会在每一个Eureka节点显示其他2个节点的地址，以及服务注册的实例。
- 当一个节点挂掉，不影响其他两个节点运行，即Eureka服务仍会正常运行。

### 5.5 对比和Zookeeper区别

关系型数据库 RDBMS (MySQL 、Oracle 、sqlServer) ===> ACID

非关系型数据库 NoSQL (Redis 、MongoDB) ===> CAP

**ACID是什么？**

- A (Atomicity) 原子性
- C (Consistency) 一致性
- I (Isolation) 隔离性
- D (Durability) 持久性


**CAP是什么?**

- C (Consistency) 强一致性
- A (Availability) 可用性
- P (Partition tolerance) 分区容错性

CAP的三进二：CA、AP、CP

**CAP理论的核心**

- 一个分布式系统不可能同时很好的满足一致性，可用性和分区容错性这三个需求
- 根据CAP原理，将NoSQL数据库分成了满足CA原则，满足CP原则和满足AP原则三大类
  - CA：单点集群，满足一致性，可用性的系统，通常可扩展性较差
  - CP：满足一致性，分区容错的系统，通常性能不是特别高
  - AP：满足可用性，分区容错的系统，通常可能对一致性要求低一些

**作为分布式服务注册中心，Eureka比Zookeeper好在哪里？**
著名的CAP理论指出，一个分布式系统不可能同时满足C (一致性) 、A (可用性) 、P (容错性)，

**由于分区容错性P在分布式系统中是必须要保证的**，因此我们只能再A和C之间进行权衡。

- Zookeeper 保证的是 CP —> 满足一致性，分区容错的系统，通常性能不是特别高

- Eureka 保证的是 AP —> 满足可用性，分区容错的系统，通常可能对一致性要求低一些

  

**Zookeeper保证的是CP**

**服务注册功能对一致性的要求要高于可用性。**

zookeeper会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时，剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30-120s，且选举期间整个zookeeper集群是不可用的，这就导致在选举期间注册服务瘫痪（不可用了）。在云部署的环境下，因为网络问题使得zookeeper集群失去master节点是较大概率发生的事件，虽然服务最终能够恢复，但是，漫长的选举时间导致注册长期不可用，是不可容忍的。

**Eureka保证的是AP**

Eureka看明白了这一点，因此在设计时就优先保证可用性。**Eureka各个节点都是平等的**，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册时，如果发现连接失败，则会自动切换至其他节点，只要有一台Eureka还在，就能保住注册服务的可用性，只不过查到的信息可能不是最新的，除此之外，Eureka还有之中自我保护机制，如果在15分钟内超过85%的节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：

- Eureka不在从注册列表中移除因为长时间没收到心跳而应该过期的服务

- Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上 (即保证当前节点依然可用)

- 当网络稳定时，当前实例新的注册信息会被同步到其他节点中

**因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪。**



## 6、Ribbon：负载均衡(基于客户端)

### 6.1 Ribbon介绍

- Spring Cloud Ribbon 是基于Netflix Ribbon 实现的一套 **==客户端负载均衡的工具==**。
- 简单的说，Ribbon 是 Netflix 发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将 Netflix 的中间层服务连接在一起。Ribbon 的客户端组件提供一系列完整的配置项，如：连接超时、重试等。简单的说，就是在配置文件中列出LoadBalancer (简称LB：负载均衡)**后面所有的机器，Ribbon 会自动的帮助你基于某种规则 (如简单轮询，随机连接等等) 去连接这些机器。我们也容易使用 Ribbon 实现自定义的负载均衡算法！

**Ribbon能干嘛？**

- LB，即负载均衡 (LoadBalancer) ，在微服务或分布式集群中经常用的一种应用。
- **负载均衡**简单的说就是**将用户的请求平摊的分配到多个服务上，从而达到系统的HA** (高可用)。
- 常见的负载均衡软件有 Nginx、Lvs(中国人开发的，已经放在Linux核心代码上了)等等。
- Dubbo、SpringCloud 中均给我们提供了负载均衡，**SpringCloud 的负载均衡算法可以自定义**。
- 负载均衡简单分类：
  - 集中式LB
    - 即在服务的提供方和消费方之间使用独立的LB设施，如**Nginx(反向代理服务器)**，由该设施负责把访问请求通过某种策略转发至服务的提供方！
  - 进程式 LB
    - 将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选出一个合适的服务器。
    - **==Ribbon 就属于进程内LB==**，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址！



**springcloud-consumer-dept-80关键代码**

pom：

```xml
<!--Ribbon(其实下面那个依赖已经包含了ribbon)-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
<!--Eureka客户端-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

添加代码给ConfigBean(发送请求的配置)：

```java
@Configuration
public class ConfigBean { //@Configuration -- spring applicationContext.xml

    //配置负载均衡实现RestTemplate
    @Bean
    @LoadBalanced //Ribbon
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
```

按ribbon要求修改Controller给服务提供者发送请求的地址：

```java
//private static final String REST_URL_PREFIX = "http://localhost:8001/dept";
    //Ribbon。由于eureka是集群，我们这里的地址，应该是一个变量，通过服务名来访问（SPRINGCLOUD-PROVIDER-DEPT）
    private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT/dept";
```

由于ribbon要和eureka一起用，所以要配置eureka：

```yaml
#Eureka配置
eureka:
  client:
    register-with-eureka: false #表示不向eureka注册中心注册自己
    service-url:
      defaultZone: http://localhost:7001/eureka/
```

```java
@SpringBootApplication
@EnableEurekaClient //Eureka启动配置
public class DeptConsumer_80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_80.class, args);
    }
}
```

### 6.2 使用Ribbon实现负载均衡

==实线就是ribbon做的事情==

![](https://i-blog.csdnimg.cn/blog_migrate/3c615bdb42799ac5cbdf0530e9a7d704.png#pic_center)

**Ribbon负载均衡算法：**

实现IRule接口，示例如下

- RoundRobinRule 轮询（**默认**）
- RandomRule 随机
- AvailabilityFilteringRule 会先过滤掉，跳闸，访问故障的服务，对剩下的进行轮询
- RetryRule 会先按照轮询获取服务，如果服务获取失败，则会在指定时间内进行重试

自选规则：

```java
@Configurationpublic 
class ConfigBean {//@Configuration -- spring  applicationContext.xml    @Bean    
   public IRule myRule() {        
       return new RandomRule();//使用随机策略        
       //return new RoundRobinRule();//使用轮询策略        
       //return new AvailabilityFilteringRule();//使用轮询策略
       //return new RetryRule();//使用轮询策略    
   }
}
```

自己写规则由于用的不多看视频或百度吧

## 7、Feign：负载均衡

### 7.1 Feign介绍

Ribbon基于客户端。

Feign是声明式Web Service客户端，它让微服务之间的调用变得更简单，类似controller调用service。

SpringCloud集成了Ribbon和Eureka，也可以使用Feigin提供负载均衡的http客户端。

（Feign就是在Ribbon的基础上加了一层，将使用方式转变为面向接口变成的方式）

**只需要创建一个接口，然后添加注解即可**



Feign，主要是社区版，大家都习惯面向接口编程。这个是很多开发人员的规范。调用微服务访问两种方法

1. 微服务名字 【ribbon 是通过微服务名字去访问】
2. 接口和注解 【feign】



**Feign能干什么？**

- Feign旨在使编写Java Http客户端变得更容易
- 前面在使用Ribbon + RestTemplate时，利用RestTemplate对Http请求的封装处理，形成了一套模板化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一个客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步的封装，由他来帮助我们定义和实现依赖服务接口的定义，==在Feign的实现下，我们只需要创建一个接口并使用注解的方式来配置它 (类似以前Dao接口上标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解)，==即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon 时，自动封装服务调用客户端的开发量。

**Feign默认集成了Ribbon**，<font color=red>在ribbon上加了一层性能会变低</font>

- 利用**Ribbon**维护了MicroServiceCloud-Dept的服务列表信息，并且通过轮询实现了客户端的负载均衡，而与**Ribbon**不同的是，通过**Feign**只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。

### 7.2 代码示例

0.在客户端和api端的pom加入Feign依赖

```xml
<!--Feign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

1.在springcloud-api层面按Feign风格增加service层代码

```java
@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT")
@RequestMapping("/dept")
public interface DeptClientService {
    @PostMapping("/add")
    boolean addDept(Dept dept);

    @GetMapping("/get/{id}")
    Dept queryById(@PathVariable("id") Long id);

    @GetMapping("/list")
    List<Dept> queryAll();
}
```

2.在客户端(springcloud-consumer-dept-feign)按Feign风格代码

```java
@RestController
@RequestMapping("/consumer/dept")
public class DeptConsumerController {

    @Resource
    private DeptClientService deptClientService;

    @GetMapping("/get/{id}")
    public Dept get(@PathVariable("id") Long id){
        return deptClientService.queryById(id);
    }

    @PostMapping("/add")
    public boolean add(Dept dept){
        return deptClientService.addDept(dept);
    }

    @GetMapping("/list")
    public List list(){
        return deptClientService.queryAll();
    }
}
```

3.在客户端启动类上添加Feign配置注解

```java
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.luo.service"})
public class FeignDeptConsumer_80 {
    public static void main(String[] args) {
        SpringApplication.run(FeignDeptConsumer_80.class, args);
    }
}
```

## 8、Hystrix：服务熔断

**分布式系统面临的问题**

复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免失败！

### 8.1 服务雪崩

多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，这就是所谓的“扇出”，如果扇出的链路上**某个微服务的调用响应时间过长，或者不可用**，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”。

对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几十秒内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障，**这些都表示需要对故障和延迟进行隔离和管理，以达到单个依赖关系的失败而不影响整个应用程序或系统运行。**

我们需要，**弃车保帅**！

### 8.2 什么是Hystrix？

​	**Hystrix**是一个应用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时，异常等，**Hystrix** 能够保证在一个依赖出问题的情况下，不会导致整个体系服务失败，避免级联故障，以提高分布式系统的弹性。

​	“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控 (类似熔断保险丝) ，**向调用方返回一个服务预期的，可处理的备选响应 (FallBack) ，而不是长时间的等待或者抛出调用方法无法处理的异常，这样就可以保证了服务调用方的线程不会被长时间，**不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

### 8.3 Hystrix能干嘛？

- 服务降级
- 服务熔断
- 服务限流
- 接近实时的监控
- …

### 8.4 服务熔断（提供者操作）

**熔断机制是赌对应雪崩效应的一种微服务链路保护机制**。

当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，==进而熔断该节点微服务的调用，快速返回错误的响应信息。==检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阀值缺省是5秒内20次调用失败，就会启动熔断机制。熔断机制的注解是：@HystrixCommand。

**关键代码**

1.在服务端的pom中添加Hystrix的依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

2.编写controller层的代码

```java
//提供restful服务
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Resource
    private IDeptService deptService;

    @PostMapping("/add")
    public boolean addDept(@RequestBody Dept dept){
        return deptService.addDept(dept);
    }

    @GetMapping("/list")
    public List<Dept> queryAll(){
        return deptService.queryAll();
    }

    @GetMapping("/get/{id}")
    @HystrixCommand(fallbackMethod = "hystrixQueryById")
    public Dept queryById(@PathVariable("id") Long id){
        Dept dept = deptService.queryById(id);

        if(dept == null){
            throw new RuntimeException("id=>" + id + ",不存在该用户，或者信息无法找到~");
        }

        return dept;
    }

    //备选方案
    public Dept hystrixQueryById(@PathVariable("id") Long id){
        return new Dept()
                .setDeptNo(id)
                .setName("id=>" + id + "没有对应信息，null--@Hystrix")
                .setDbSource("no this database in MYSQL");
    }

}
```

3.在主启动类中添加开启Hystrix断路器配置注解

```java
@SpringBootApplication
@EnableEurekaClient //在服务启动后自动注册到Eureka中
@EnableCircuitBreaker //添加对熔断的支持
public class DeptProviderHystrix_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProviderHystrix_8001.class, args);
    }
}
```

### 8.5 服务降级（消费者操作with feign）

服务降级是指：**当服务器压力剧增的情况下，根据实际业务情况及流量，对一些服务和页面有策略的不处理，或换种简单的方式处理，从而释放服务器资源以保证核心业务正常运作或高效运作。**说白了，**就是尽可能的把系统资源让给优先级高的服务。**

资源有限，而请求是无限的。如果在并发高峰期，不做服务降级处理，一方面肯定会影响整体服务的性能，严重的话可能会导致宕机某些重要的服务不可用。所以，一般在高峰期，为了保证核心功能服务的可用性，都要对某些服务降级处理。比如当双11活动时，把交易无关的服务统统降级，如查看蚂蚁深林，查看历史订单等等。

**服务降级主要用于什么场景呢？**当整个微服务架构整体的负载超出了预设的上限阈值或即将到来的流量预计将会超过预设的阈值时，为了保证重要或基本的服务能正常运行，可以将一些 不重要 或 不紧急 的服务或任务进行服务的 延迟使用 或 暂停使用。

降级的方式可以根据业务来，可以延迟服务，比如延迟给用户增加积分，只是放到一个缓存中，等服务平稳之后再执行 ；或者在粒度范围内关闭服务，比如关闭相关文章的推荐。

**tips**：

- 服务熔断会先调用原服务方法，调用失败后才执行降级方法；服务降级会直接调用服务降级方法（狂神说反了）

- 熔断是提供者的操作，降级是在消费者的操作

**代码示例**

1.在api服务的service，配置feign接口代码同一目录下，添加DeptClientServiceFallbackFactory，用于生成服务降级的服务类

```java
//降级
@Component
public class DeptClientServiceFallbackFactory implements FallbackFactory {
    @Override
    public DeptClientService create(Throwable throwable) {
        return new DeptClientService() {
            @Override
            public boolean addDept(Dept dept) {
                return false;
            }

            @Override
            public Dept queryById(Long id) {
                return new Dept()
                        .setDeptNo(id)
                        .setName("id=>" + id + "没有对应的信息，客户端提供了降级的信息，这个服务现在已经被关闭")
                        .setDbSource("没有数据~");
            }

            @Override
            public List<Dept> queryAll() {
                return null;
            }
        };
    }
}
```

2.在Feign的配置接口类DeptClientService中，将写好的用于生成服务降级的服务类的工厂类配置一下

```java
@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT", fallbackFactory = DeptClientServiceFallbackFactory.class) //配置一下
@RequestMapping("/dept")
public interface DeptClientService {
    @PostMapping("/add")
    boolean addDept(Dept dept);

    @GetMapping("/get/{id}")
    Dept queryById(@PathVariable("id") Long id);

    @GetMapping("/list")
    List<Dept> queryAll();
}
```

3.在客户端服务的配置文件中开启Hystrix服务降级的支持

```yaml
# 开启降级feign.hystrix
feign:
  hystrix:
    enabled: true
```

### 8.6 Dashboard 流监控

**代码示例**

1.监控面板微服务springcloud-consumer-hystrix-dashboard-9001

1.1 pom

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-test</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
</dependencies>
```

1.2 application.yml要配置好端口

1.3 主启动类

```java
@SpringBootApplication
@EnableHystrixDashboard //开启监控页面，要保证服务提供者要有spring-boot-starter-actuator的监控包
public class DeptConsumerDashboard_9001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumerDashboard_9001.class, args);
    }
}
```

1.4 其他服务提供者，除了要pom有actuator和hystrix的包，主启动类还得配置被监控的servlet

```java
@SpringBootApplication
@EnableEurekaClient //在服务启动后自动注册到Eureka中
@EnableCircuitBreaker //添加对熔断的支持
public class DeptProviderHystrix_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProviderHystrix_8001.class, args);
    }

    //增加一个Servlet，能被hystrixBoard监控到的比较官方的配置
    @Bean
    public ServletRegistrationBean hystrixMetricsStreamServlet(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet()); //访问该页面就是监控页面
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        return registrationBean;
    }
}
```

## 9、Zuul 路由网关

### 9.1 概述

**什么是zuul?**

Zuul包含了对请求的**路由**(用来跳转的)和**过滤**两个最主要功能：

​		其中**路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础**，（之前我们在测试过程中，地址都是http://localhost:8001/dept/get/1 ，当然端口号还有8002、8003，但是实际我们应该把localhost:8001 这一部分隐藏起来，真实服务的地址不应该暴露出去，例如我们可以设置为：http://www.wlw.com/dept/get/1，做一个统一的访问地址。）
​		而**过滤器功能则负责对请求的处理过程进行干预，是实现请求校验，服务聚合等功能的基础**。Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。==（zuul管理这些服务，然后zuul注册到Eureka中）==

​		**注意**：Zuul 服务最终还是会注册进 Eureka

​		**提供**：代理 + 路由 + 过滤 三大功能！

**springcloud-zuul-9527代码示例：**

1.pom

```xml
<dependencies>
    <!--Zuul-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
    <!--Eureka服务提供者-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

2.主启动类

```java
@SpringBootApplication
@EnableZuulProxy //开启网关
public class ZuulApplication_9527 {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication_9527.class, args);
    }
}
```

==此时==

```xquery
此时直接访问一个微服务资源地址：http://localhost:8001/dept/get/1

**等于**

通过zuul网关访问（**微服务名称要小写**）：http://localhost:9527/springcloud-provider-dept/dept/get/1
```

==tips：idea大小写转换快捷键：Shift+Ctrl+u==

3.application.yml配置zuul

```yaml
server:
  port: 9527

spring:
  application:
    name: springcloud-zuul

#Eureka的配置，服务注册到哪里
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka/
  instance:
    instance-id: zuul9527.com #修改eureka上的默认描述信息！
    prefer-ip-address: true #显示完整的ip地址，而不是localhost

#info配置
info:
  app.name: lws-springcloud
  company.name: blog.luostudy.com

#zuul配置
zuul:
  routes:
    myMap: #自定义map名称 将微服务名称替换
      serviceId: springcloud-provider-dept #key-value
      path: /mydept/**
  #ignored-services: springcloud-provider-dept #忽略某服务名访问路径，或一些服务名访问路径
  ignored-services: "*" #忽略所有的服务名访问路径
  prefix: /luo #设置公共的前缀
```

## 10、Spring Cloud Config 分布式配置

### 10.1 概述

**分布式系统面临的–配置文件问题**

微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务，由于每个服务都需要必要的配置信息才能运行，所以一套集中式的，动态的配置管理设施是必不可少的。springCloud提供了configServer来解决这个问题，我们每一个微服务自己带着一个application.yml，那上百个的配置文件修改起来，令人头疼！

**什么是SpringCloud config分布式配置中心**

![](https://i-blog.csdnimg.cn/blog_migrate/e7934ad8f4d205e3a3572380a17fef86.png#pic_center)

​		spring cloud config 为微服务架构中的微服务提供集中化的外部支持，配置服务器为各个不同微服务应用的所有环节提供了一个**中心化的外部配置**。

​		spring cloud config 分为**服务端**和**客户端**两部分。

​		服务端也称为 **分布式配置中心**，它是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密，解密信息等访问接口。

​		客户端则是**通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息**。配置服务器默认采用git来存储配置信息，这样就有助于对环境配置进行版本管理。并且可用通过git客户端工具来方便的管理和访问配置内容。



**spring cloud config 分布式配置中心能干嘛**

- 集中式管理配置文件
- 不同环境，不同配置，动态化的配置更新，分环境部署，比如 /dev /test /prod /beta /release
- 运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
- 当配置发生变动时，服务不需要重启，即可感知到配置的变化，并应用新的配置
- 将配置信息以REST接口的形式暴露



**spring cloud config 分布式配置中心与GitHub整合**

​		由于spring cloud config 默认使用git来存储配置文件 (也有其他方式，比如自持SVN 和本地文件)，但是最推荐的还是git ，而且使用的是 http / https 访问的形式。

### 10.2 spring cloud config服务端

**代码示例**

1.pom

```xml
<!--spring-cloud-config-server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

2.application.yml

```yaml
server:
  port: 3344
spring:
  application:
    name: springcloud-config-server

    # 连接远程仓库
  cloud:
    config:
      server:
        git:
          uri: https://github.com/LuoWenSa/springcloud-config.git
          skip-ssl-validation: true
```

3.主启动类Config_Server_3344

```java
@SpringBootApplication
@EnableConfigServer
public class Config_Server_3344 {
    public static void main(String[] args) {
        SpringApplication.run(Config_Server_3344.class, args);
    }
}
```

正常启动后就可以访问git上的配置文件了

```yaml
spring:
  profiles:
    active: dev
---
spring:
  profiles: dev
  application:
    name: springcloud-config-dev
---
spring:
  profiles: test
  application:
    name: springcloud-config-test
```

访问格式：

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

label指分支

例如测试访问http://localhost:3344/application-dev.yml，或http://localhost:3344/master/application-dev.yml成功

### 10.3 spring cloud config客户端

**代码示例**

1.pom

```xml
<!-- spring-cloud-starter-config -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

2.bootstrap.yml

```yaml
#系统级别的配置
spring:
  cloud:
    config:
      name: config-client #需要从git上读取的资源名称，不需要后缀
      profile: test
      label: master
      uri: http://localhost:3344
#这样写等价于server访问git：http://localhost:3344/master/config-client-dev.yml
```

3.application.yml

```yaml
#用户级别的配置
spring:
  application:
    name: springcloud-config-client-3355
```

4.主启动类(没变化)

```java
@SpringBootApplication
public class ConfigClient_3355 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigClient_3355.class, args);
    }
}
```

5.测试远程配置是否已被读取到的controller

```java
@RestController
public class ConfigClientController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${eureka.client.service-url.defaultZone}")
    private String eurekaServer;

    @Value("${server.port}")
    private String port;

    @GetMapping("/config")
    public String getConfig(){
        return "applicationName:" + applicationName +
                " eurekaServer:" + eurekaServer +
                " port:" + port;
    }
}
```

修改bootstrap.yml中的profile可以切换对应配置