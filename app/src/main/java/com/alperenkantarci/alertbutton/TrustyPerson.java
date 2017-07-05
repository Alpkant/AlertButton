package com.alperenkantarci.alertbutton;

import static com.alperenkantarci.alertbutton.R.id.country_code;

/**
 * Created by Alperen Kantarci on 15.06.2017.
 */

class TrustyPerson {
    private String name;
    private String surname;

    private String telephone_number;
    private String email;

    TrustyPerson(String name, String surname, String telephone_number, String email) {
        this.name = name;
        this.surname = surname;

        this.telephone_number = telephone_number;
        this.email = email;
    }

    TrustyPerson() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getSurname() {
        return surname;
    }

    void setSurname(String surname) {
        this.surname = surname;
    }

    String getTelephone_number() {
        return telephone_number;
    }

    void setTelephone_number(String telephone_number) {
        this.telephone_number = telephone_number;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }
}
