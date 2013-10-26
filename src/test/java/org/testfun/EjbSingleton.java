package org.testfun;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class EjbSingleton {

    private String answer;

    public String whoAmI() {
        return answer;
    }

    @PostConstruct
    public void setup() {
        answer = "me";
    }



}
