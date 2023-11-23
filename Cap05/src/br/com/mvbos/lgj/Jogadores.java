package br.com.mvbos.lgj;

import javax.swing.*;

public class Jogadores implements Comparable<Jogadores>{

    private String Nome;
    private  int Pontos;

    public void setNome(String nome) {
        Nome = nome;
    }
    public String getNome() {
        return Nome;
    }

    public int getPontos() {
        return Pontos;
    }
    public void setPontos(int pontos) {
        Pontos = pontos;
    }

    @Override
    public int compareTo(Jogadores o) {
        return o.getPontos()-Pontos;
    }
}


