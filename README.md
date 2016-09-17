# Java大数据工具包

>Java进行网站应用系统日志解析，达到记录访客来源，一般采用数据流式处理。一个流接着一个流，进行分段处理，达到处理速度快的效果。
一般使用Ngnix配置日志进行网站行为收集，或者开发收数接口，使用js实现用户行为响应时能带参数链接get到服务器进行收数，收数结果
扔到kafka消息队列,kafka消息通过topic来读取，zookeeper作为消息offset的记录着，另外的程序读取kafka数据，采用多线程或者跑
storm任务进行连续流处理，处理结果可以再次扔回kafka，也可存入cassandra或者hbase等列式数据库，复杂查询结果存到mysql或者
mongodb，session记录/经常访问的数据等热缓存到redis数据库。

>记录此大数据库主要是工作总结，不想浪费，仅作记录，提供数据工具代码示例，软件安装等请到网站 http://www.lenggirl.com 寻找。
有问题可联系本Github.

>方便处理，我也实例了一个日志记录框架，请看下面。

##　一.日志记录
SLF4J(Simple logging Facade for Java)不是一个真正的日志实现，而是一个抽象层（abstraction layer），
它允许你在后台使用任意一个日志类库。如果是在编写供内外部都可以使用的API或者通用类库，

大家知道Java的日志记录是稍微有点乱的，于是采用logbcak进行日志记录

    slf4j-api  //接口
    
    log4j　//排除掉，使用logback-classic
    slf4j-log4j12　//排除掉，使用logback-classic
    
    //slf4j-log4j12:链接slf4j-api和log4j中间的适配器。它实现了slf4j-api中StaticLoggerBinder接口，从而使得在编译时绑定的是slf4j-log4j12的getSingleton()方法
    //log4j:这个是具体的日志系统。通过slf4j-log4j12初始化Log4j，达到最终日志的输出。
    
    logback-classic
    logback-core
    
    log4j-over-slf4j　//旧式日志API的第三方类库或旧代码的日志调用转到slfj

最后使用的Maven配置是：

        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.13</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.0.13</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.5</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>log4j-over-slf4j</artifactId>
            <version>1.6.6</version>
            <scope>compile</scope>
        </dependency>
    
参考：http://www.slf4j.org/legacy.html

## 二.具体介绍
1. 采用Maven管理第三方库，请看pom.xml文件
2. Kafka生产消费代码示例
3. Hbase代码示例

```
请先建表，包括命名空间，表名，列族，分区算法，分区数
    create_namespace 'namespace'
    create 'namespace:visitor', 'col',{SPLITALGO => 'HexStringSplit',NUMREGIONS => 20}
```

4. Cassandra代码示例
