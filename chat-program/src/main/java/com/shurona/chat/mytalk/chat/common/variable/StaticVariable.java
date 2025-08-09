package com.shurona.chat.mytalk.chat.common.variable;

public abstract class StaticVariable {

    /**
     * 안 읽은 채팅을 업데이트 하는 구독 URL, + "userId"
     */
    public final static String sendUnreadCount = "/topic/room/${room}/read/user/";

    /**
     * 카프카
     */
    public static final String KAFKA_CHAT_GROUP_ID = "chat.processor.group";
    public static final String KAFKA_CHAT_READ_TOPIC_ID = "coupon.read";
    public static final String KAFKA_CHAT_WRITTEN_TOPIC_ID = "coupon.written";

    public static final String KAFKA_CHAT_READ_TOPIC_DLT_ID = "coupon.read.dlt";


    /**
     * socket chat room endpoint prefix
     */
    public static final String chatRoomDestinationPrefix = "/topic/room/";
}
