package ru.atom.gameserver.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Message {
    private final Topic topic;
    private final String data;
    private int playerId;

    public Message(Topic topic, String data, int playerId) {
        this.topic = topic;
        this.data = data;
        this.playerId = playerId;
    }

    @JsonCreator
    public Message(@JsonProperty("topic") Topic topic, @JsonProperty("data") JsonNode data) {
        this.topic = topic;
        this.data = data.toString();
    }

    public Topic getTopic() {
        return topic;
    }

    public String getData() {
        return data;
    }

    public int getPlayerId() { return playerId; }
}
