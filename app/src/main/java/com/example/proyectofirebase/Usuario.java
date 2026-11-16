package com.example.proyectofirebase;

public class Usuario {
    public String username;
    public String email;
    public String profile_picture;

    public Usuario() { }  // Firebase lo necesita vacío

    public Usuario(String username, String email, String profile_picture) {
        this.username = username;
        this.email = email;
        this.profile_picture = profile_picture;
    }
}