package com.example.ajoan.utils;

/**
 * Created by Joan on 01/04/2017.
 */

public interface Rules {

    public final static String USERNAME_RULE="((?=.*[a-z])^[a-zA-Z](\\w{2,}))";

    public final static String USERNMAIL_RULE=".+@.+"; //todo change

    public final static String PASS_RULE = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";

    public final static String NON_EMPTY_RULE = ".+";

}
