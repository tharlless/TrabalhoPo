package br.com.mvbos.lgj;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class Ranking {
    ArrayList<Jogadores> ranking = new ArrayList<>();

    public  void  addJogadores(Jogadores jogadores){
        ranking.add(jogadores);
        salvar_dado();
    }
    public void carregar_dados(){
        Path top10 = Paths.get("C:\\Users\\fernando\\Desktop\\Cap05\\TOP10.txt");
        try(BufferedReader ler = new BufferedReader(new FileReader(top10.toFile()))) {
            if(!Files.exists(top10)){
                Files.createFile(top10);
            }
            String Linha;
            while ((Linha = ler.readLine()) !=null){
                String[] NonPon = Linha.split(",");
                Jogadores jogadores = new Jogadores();
                jogadores.setNome(NonPon[0]);
                jogadores.setPontos(Integer.parseInt(NonPon[1]));
                ranking.add(jogadores);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void organizar(){
        Collections.sort(ranking);
    }
    public void tamanho_Lista_Top_10(){
        while (ranking.size()>10){
            ranking.remove(ranking.size()-1);
        }
    }
    public  void exibir_Top10() {
        JFrame exibir_Top10 = new JFrame("TOP 10");
        exibir_Top10.setSize(500,672);

        JTextArea Exibir_top10 = new JTextArea();
        // tudo isso para usar um Ç e um ~
        Font fonte_Usada = new Font("Arail",Font.PLAIN,12);
        Exibir_top10.setFont(fonte_Usada);
        //================================
        for (int i = 0; i < ranking.size(); i++) {
            Jogadores jogador = ranking.get(i);
            Exibir_top10.append((i + 1) + ") Nome: " + jogador.getNome() + " ---- Pontos: " + jogador.getPontos()+"\n");
            Exibir_top10.append("");
        }
        exibir_Top10.add(Exibir_top10);
        exibir_Top10.setVisible(true);
    }

    private void salvar_dado() {
        Path top10 = Paths.get("C:\\Users\\fernando\\Desktop\\Cap05\\TOP10.txt");
        try(BufferedWriter escrever = new BufferedWriter(new FileWriter(top10.toFile()))) {
            for(Jogadores jogadores : ranking){
                escrever.write(jogadores.getNome()+","+jogadores.getPontos()+"\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
