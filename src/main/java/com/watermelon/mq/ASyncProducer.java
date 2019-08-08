package com.watermelon.mq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ASyncProducer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("async-producer-group-name");

        producer.setNamesrvAddr("192.168.31.11:9876");

        producer.setVipChannelEnabled(false);

        producer.start();

        producer.setRetryTimesWhenSendAsyncFailed(0);

        final long startTime = System.currentTimeMillis();
        int messageCount = 2;
        final CountDownLatch latch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            Message msg = new Message("async_bbb_topic", "TagA", "", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            try {
                producer.send(msg, new SendCallback() {
                    public void onSuccess(SendResult sendResult) {
                        latch.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                        System.out.println("time:" + (System.currentTimeMillis() - startTime));
                    }

                    public void onException(Throwable e) {
                        latch.countDown();
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        latch.await(50, TimeUnit.SECONDS);
        producer.shutdown();
    }

}
