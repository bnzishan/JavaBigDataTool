# Java大数据工具包

>Java进行网站应用系统日志解析，达到记录访客来源，一般采用数据流式处理。一个流接着一个流，进行分段处理，达到处理速度快的效果。
一般使用Ngnix配置日志进行网站行为收集，或者开发收数接口，使用js实现用户行为响应时能带参数链接get到服务器进行收数，收数结果
扔到kafka消息队列,kafka消息通过topic来读取，zookeeper作为消息offset的记录着，另外的程序读取kafka数据，采用多线程或者跑
storm任务进行连续流处理，处理结果可以再次扔回kafka，也可存入cassandra或者hbase等列式数据库，复杂查询结果存到mysql或者
mongodb，session记录/经常访问的数据等热缓存到redis数据库。

>记录此大数据库主要是工作总结，不想浪费，仅作记录，提供数据工具代码示例,
有问题可联系本Github.

>方便处理，我也实例了一个日志记录框架，请看下面。

## 一.日志记录

SLF4J(Simple logging Facade for Java)不是一个真正的日志实现，而是一个抽象层（abstraction layer），
它允许你在后台使用任意一个日志类库。如果是在编写供内外部都可以使用的API或者通用类库，

大家知道Java的日志记录是稍微有点乱的，于是采用logbcak进行日志记录


```
    slf4j-api  //接口，以下两种选择均实现该接口
    
 /*
    第一种用法：
    log4j
        这个是具体的日志系统。通过slf4j-log4j12初始化Log4j，达到最终日志的输出。
    slf4j-log4j12:
        链接slf4j-api和log4j中间的适配器。
        它实现了slf4j-api中StaticLoggerBinder接口，从而使得在编译时绑定的是slf4j-log4j12的getSingleton()方法
 */
    log4j　//排除掉，使用logback-classic
    slf4j-log4j12　//排除掉，使用logback-classic
    
 
 /*
    第二种用法：
    引入下面三个!
    替代上面第一种用法
 */
    logback-classic
    logback-core
    
    log4j-over-slf4j　//旧式日志API的第三方类库或旧代码的日志调用转到slfj
```

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

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.lenggirl.datautil</groupId>
    <artifactId>datautil</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.10</artifactId>
            <version>0.8.2.0</version>

            <!-- use provided scope, so users can pull in whichever scala version they choose -->
            <exclusions>
                <exclusion>
                    <groupId>org.apache.zookeeper</groupId>
                    <artifactId>zookeeper</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.8</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.mongodb.morphia</groupId>
            <artifactId>morphia</artifactId>
            <version>0.109</version>
        </dependency>

        <dependency>
            <groupId>com.datastax.cassandra</groupId>
            <artifactId>cassandra-driver-core</artifactId>
            <version>2.1.7</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.hbase/hbase-shaded-client -->
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-shaded-client</artifactId>
            <version>1.2.2</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!--使用storm的log吗？可以直接抽出来-->
        <dependency>
            <groupId>org.apache.storm</groupId>
            <artifactId>storm-core</artifactId>
            <version>0.9.4</version>
        </dependency>

        <dependency>
            <groupId>org.apache.storm</groupId>
            <artifactId>storm-kafka</artifactId>
            <version>0.9.4</version>
        </dependency>

        <!--来吧，日志们-->
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

    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <excludes>
                </excludes>
            </resource>
            <resource>
                <directory>conf</directory>
                <excludes>
                </excludes>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

2. Kafka生产消费代码示例

```
生产消费两个示例请见代码，已经有详细注释
```

3. Hbase代码示例

```
请先建表，包括命名空间，表名，列族，分区算法，分区数
    create_namespace 'namespace'
    create 'namespace:visitor', 'col',{SPLITALGO => 'HexStringSplit',NUMREGIONS => 20}
   
代码已有详细注释
```

4. Cassandra代码示例

```
代码已有详细注释
```

5. 基本爬虫包

Java不适合写爬虫，比较笨重，建议数据流用Java处理，如把爬到的数据打到KAFKA，让Java接力，Golang/Python比较擅长高并发写爬虫.

6. 网站开发

Java并不适合快速网站开发，但是可以使用Spring，目前Spring Boot已经简化了开发，较简单，此不引入。MYSQL ORM不建议使用Hibernate或MyBatis,由于转换SQL语句相对耗时，且由于目前有其他数据库可替代，所以建议原生，带上连接池即可。
