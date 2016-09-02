package kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;

/**
 * Created by hunterhug on 16-8-17.
 * Kafka生产者测试
 * http://kafka.apache.org/documentation.html#introduction
 * http://blog.csdn.net/hmsiwtv/article/details/46960053
 */
public class KafkaProducetest {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Producer<String, String> producer;
    public final static String TOPIC = "clicki_track_topic";

    private KafkaProducetest() {
        Properties props = new Properties();
        //此处配置的是kafka的端口
        props.put("metadata.broker.list", "192.168.11.73:9092");

        //配置value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置key的序列化类
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //0表示不确认主服务器是否收到消息,马上返回,低延迟但最弱的持久性,数据可能会丢失
        //1表示确认主服务器收到消息后才返回,持久性稍强,可是如果主服务器死掉,从服务器数据尚未同步,数据可能会丢失
        //-1表示确认所有服务器都收到数据,完美!
        props.put("request.required.acks", "-1");

        //异步生产,批量存入缓存后再发到服务器去
//        props.put("producer.type", "async");

        //填充配置,初始化生产者
        producer = new Producer<String, String>(new ProducerConfig(props));
    }

    void produce() {
        int messageNo = 1000;
        final int COUNT = 2003;
        Random ra = new Random();
        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String[] table = new String[]{"infospot",
                    "info_site_user"};
            int i = ra.nextInt(table.length);

            String data1 = "{\"c\":" + messageNo + ",\"i\":" + messageNo + ",\"n\":\"http:/icki.html\",\"s\":" + messageNo + ",\"sid\":0,\"t\":\"" + table[i] + "\",\"tid\":" + messageNo + ",\"unix\":0,\"viewId\":0}";

            // 发送消息
            producer.send(new KeyedMessage<String, String>(TOPIC, data1));

            // 消息类型key:value
//            producer.send(new KeyedMessage<String, String>(TOPIC, key, data1));
            logger.info(messageNo + ".." + data1);
            messageNo++;
        }
//        producer.close();//必须关闭
    }

    public static void main(String[] args) {
        new KafkaProducetest().produce();

    }
}