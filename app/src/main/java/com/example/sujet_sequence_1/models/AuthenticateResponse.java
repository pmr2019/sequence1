package com.example.sujet_sequence_1.models;

public class AuthenticateResponse {
    private boolean success;
    private int status;
    private String hash;

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Réponse : { success : " + success + ", status : " + status + ", hash : " + hash + "}";
    }
}

