package ru.atom.matchmaker.model;

import javax.persistence.*;

/**
 * Created by Alexandr on 25.11.2017.
 */
@Entity
@Table(name = "player", schema = "matchmaker")
public class Player {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "login", nullable = false, length = 30)
    private String login;

    public Integer getId() {
        return id;
    }

    public Player setId(Integer id) {
        this.id = id;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public Player setGame(Game game) {
        this.game = game;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public Player setLogin(String login) {
        this.login = login;
        return this;
    }

}
