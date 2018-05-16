import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

val batch = 10  // 计算周期（秒）
//val conf = new SparkConf().setAppName("WebLogAnalyse").setMaster("local")
//val ssc = new StreamingContext(conf, Seconds(batch))
val ssc = new StreamingContext(sc, Seconds(batch))
val input = "file:///home/libaoquan/Documents/nginx/log"  // 文件流
val lines = ssc.textFileStream(input)

// 计算总PV
lines.count().print()

// 各个ip的pv
lines.map(line => (line.split(" ")(0), 1)).reduceByKey(_+_).print()

// 获取搜索引擎信息
val urls = lines.map(_.split("\"")(3))

// 先输出搜索引擎和查询关键词，避免统计搜索关键词时重复计算
// 输出(host, query_keys)
val searchEnginInfo = urls.map( url  => {
  // 搜索引擎对应的关键字索引
  val searchEngines = Map(
  "www.google.cn" -> "q",
  "www.yahoo.com" -> "p",
  "cn.bing.com" -> "q",
  "www.baidu.com" -> "wd",
  "www.sogou.com" -> "query"
  )
  val temp = url.split("/")
  // Array(http:, "", www.baidu.com, s?wd=hadoop)
  if(temp.length > 2){
  val host = temp(2)
  if(searchEngines.contains(host)){
    val q = url.split("//?")
    if(q.length > 0) {
      val query = q(1)
      val arr_search_q = query.split('&').filter(_.indexOf(searchEngines(host) + "=") == 0)
      if (arr_search_q.length > 0) {
        (host, arr_search_q(0).split('=')(1))
      } else {
        (host, "")
      }
    } else{
      ("", "")
    }
  } else{
    ("", "")
  }
  } else{
  ("", "")
  }
})

// 搜索引擎pv
searchEnginInfo.filter(_._1.length > 0).map(i => (i._1, 1)).reduceByKey(_+_).print()

// 关键字pv
searchEnginInfo.filter(_._2.length > 0).map(i => (i._2, 1)).reduceByKey(_+_).print()

// 终端pv
lines.map(_.split("\"")(5)).map(agent => {
  val types = Seq("iPhone", "Android")
  var r = "Default"
  for (t <- types) {
  if (agent.indexOf(t) != -1)
    r = t
  }
  (r, 1)
}).reduceByKey(_ + _).print()

// 各页面pv
lines.map(line => (line.split("\"")(1).split(" ")(1), 1)).reduceByKey(_+_).print()

ssc.start()
ssc.awaitTermination()