# Java大数据工具包

##　一.日志记录
SLF4J(Simple logging Facade for Java)不是一个真正的日志实现，而是一个抽象层（ abstraction layer），
它允许你在后台使用任意一个日志类库。如果是在编写供内外部都可以使用的API或者通用类库，

大家知道Java的日志记录是稍微有点乱的，于是采用logbcak进行日志记录

    org.slf4j:slf4j-api:1.6.1  //接口
    
    log4j:log4j:1.2.16　//排除掉，使用logback-classic
    slf4j-log4j12　//排除掉，使用logback-classic
    
    //slf4j-log4j12:链接slf4j-api和log4j中间的适配器。
    //它实现了slf4j-apiz中StaticLoggerBinder接口，从而使得在编译时绑定的是slf4j-log4j12的getSingleton()方法
    //log4j:这个是具体的日志系统。通过slf4j-log4j12初始化Log4j，达到最终日志的输出。
    
    ch.qos.logback:logback-classic:1.0.13
    ch.qos.logback:logback-core:1.0.13
    
    org.slf4j:log4j-over-slf4j:1.6.6　//旧式日志API的第三方类库或旧代码的日志调用转到slfj
    
参考：http://www.slf4j.org/legacy.html