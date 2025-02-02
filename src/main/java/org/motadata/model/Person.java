package org.motadata.model;

public class Person {
    protected String name;
    protected String id;

    public Person(String name, String id, String username, String password, int securityQuestionIndex, String securityAnswer) {
        this.name = name;
        this.id = id;
    }
}