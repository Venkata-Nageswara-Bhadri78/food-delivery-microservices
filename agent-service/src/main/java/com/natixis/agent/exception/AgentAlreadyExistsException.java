package com.natixis.agent.exception;

public class AgentAlreadyExistsException extends RuntimeException {
    public AgentAlreadyExistsException(String message){
        super(message);
    }
}
