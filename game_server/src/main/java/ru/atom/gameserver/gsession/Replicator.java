package ru.atom.gameserver.gsession;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.atom.gameserver.component.ConnectionHandler;
import ru.atom.gameserver.message.Message;
import ru.atom.gameserver.message.Topic;
import ru.atom.gameserver.model.GameObject;
import ru.atom.gameserver.util.JsonHelper;

import java.util.List;

public class Replicator {

    private final Long gameId;
    private final ConnectionHandler connectionHandler;

    public Replicator(Long gameId, ConnectionHandler connectionHandler) {
        this.gameId = gameId;
        this.connectionHandler = connectionHandler;
    }

    public void writePossess(int possess, int playersCount, String login) {
        ObjectNode objectNode = JsonHelper.nodeFactory.objectNode();
        objectNode.put("possess", possess);
        objectNode.put("playersCount", playersCount);
        connectionHandler.sendMessage(gameId, login,
                new Message(Topic.POSSESS, objectNode));
    }

    public void writeReplica(List<GameObject> objects) {
        ObjectNode node = getJsonNode(objects);
        Message message = new Message(Topic.REPLICA, node);
        connectionHandler.sendMessage(gameId, message);
    }

    public void writeGameOver(boolean hasWinner, Integer winnerId) {
        ObjectNode objectNode = JsonHelper.nodeFactory.objectNode();
        objectNode.put("hasWinner", hasWinner);
        objectNode.put("possess", winnerId);
        connectionHandler.sendGameOver(gameId, hasWinner ? winnerId : -1,
                new Message(Topic.END_MATCH, objectNode));
    }

    private ObjectNode getJsonNode(List<GameObject> objects) {
        ObjectNode rootObject = JsonHelper.nodeFactory.objectNode();
        ArrayNode jsonArrayNode = rootObject.putArray("objects");
        for (GameObject object : objects) {
            ObjectNode jsonObject = JsonHelper.getJsonNode(object);
            jsonObject.put("type", object.getClass().getSimpleName());
            jsonArrayNode.add(jsonObject);
        }
        return rootObject;
    }
}
