package ru.atom.matchmaker.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexandr on 25.11.2017.
 */
@Entity
@Table(name = "game", schema = "matchmaker")
public class Game {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "start_time", nullable = false, updatable = false)
    private Date startTime;

    @OneToMany(mappedBy = "gameId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Player> players;

    public Long getId() {
        return id;
    }

    public Game setId(Long id) {
        this.id = id;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Game setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Game setPlayers(List<Player> players) {
        this.players = players;
        return this;
    }
}
