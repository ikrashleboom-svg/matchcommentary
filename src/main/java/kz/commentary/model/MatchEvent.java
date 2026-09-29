package kz.commentary.model;
public record MatchEvent(int minute, EventType type, String team, String player) { }
