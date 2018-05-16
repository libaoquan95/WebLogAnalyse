Spark 实践——基于 Spark Streaming 的实时日志分析系统

本文基于《Spark 最佳实践》第6章 Spark 流式计算。
运行环境：Ubuntu 16.04 + JDK 1.8 + Scala 2.11.12 + Spark 2.11

> 我们知道网站用户访问流量是不间断的，基于网站的访问日志，即 Web log 分析是典型的流式实时计算应用场景。比如百度统计，它可以做流量分析、来源分析、网站分析、转化分析。另外还有特定场景分析，比如安全分析，用来识别 CC 攻击、 SQL 注入分析、脱库等。这里我们简单实现一个类似于百度分析的系统。

## 1.模拟生成 web log 记录
在日志中，每行代表一条访问记录，典型格式如下：
```
46.156.87.72 - - [2018-05-15 06:00:30] "GET /upload.php HTTP/1.1" 200 0 "-" "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)" "-"
```
分别代表：访问 ip，时间戳，访问页面，响应状态，搜索引擎索引，访问 Agent。

简单模拟一下数据收集和发送的环节，用一个 Python 脚本随机生成 Nginx 访问日志，为了方便起见，不使用 HDFS，使用单机文件系统。

首先，新建文件夹用于存放日志文件
```
$ mkdir Documents/nginx
$ mkdir Documents/nginx/log
$ mkdir Documents/nginx/log/tmp
```

然后，使用 Python 脚本随机生成 Nginx 访问日志，并为脚本设置执行权限, 代码见 [sample_web_log.py](https://github.com/libaoquan95/WebLogAnalyse/blob/master/sample_web_log.py)
```
$ chmod +x sample_web_log.py
```

之后，编写 bash 脚本，自动生成日志记录，并赋予可执行权限，代码见 [genLog.sh](https://github.com/libaoquan95/WebLogAnalyse/blob/master/genLog.sh)
```
$ chmod +x genLog.sh
```
执行 genLog.sh 查看效果，输入 ctrl+c 终止。
```
$ ./genLog.sh
```

## 2.流式分析
创建 Scala 脚本，代码见 [genLog.sh](https://github.com/libaoquan95/WebLogAnalyse/blob/master/WebLogAnalyse.scala)

## 3.执行
同时开启两个终端，分别执行 genLog.sh 生成日志文件和执行 WebLogAnalyse.scala 脚本进行流式分析。

执行 genLog.sh
```
$ ./genLog.sh
```

执行 WebLogAnalyse.scala, 使用 spark-shell 执行 scala 脚本
```
$ spark-shell --executor-memory 5g --driver-memory 1g --master local  < WebLogAnalyse.scala 
```