package uz.pdp.bot;

import java.util.regex.Pattern;

public class Checker {

    public boolean checkEmail(String email){
        return Pattern.matches(".+@[A-Za-z]+\\.[a-z]+",email);
    }


}
