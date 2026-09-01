package com.govtech.messaging.realtime.protocol;

import com.govtech.messaging.message.MessageResponse;

public record ServerEvent(Type type, MessageResponse message, String error) {
    public enum Type { CONNECTED, MESSAGE, ERROR }

    public static ServerEvent connected() { return new ServerEvent(Type.CONNECTED, null, null); }
    public static ServerEvent message(MessageResponse message) { return new ServerEvent(Type.MESSAGE, message, null); }
    public static ServerEvent error(String error) {
        return new ServerEvent(Type.ERROR, null, error == null ? "Command failed" : error);
    }
}
