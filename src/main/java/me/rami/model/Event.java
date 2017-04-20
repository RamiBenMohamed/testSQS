package me.rami.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Event {

    private Long taskId;
    private Type type;
    private Status status;
   
    public enum Status {
        STARTED, SUCCESSFUL, FAILED
    }

}
