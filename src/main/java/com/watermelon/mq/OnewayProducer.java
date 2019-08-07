package com.watermelon.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class OnewayProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("sync-producer-group-name");

        producer.setNamesrvAddr("192.168.31.11:9876;192.168.31.11:9877");

        producer.start();

        for (int i = 0; i < 3; i++) {
            try {
                Message msg = new Message("async_bbb_topic", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.setSendMsgTimeout(6000);
                producer.sendOneway(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Thread.sleep(10000);

        producer.shutdown();
    }

}
