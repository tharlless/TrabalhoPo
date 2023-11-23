package br.com.mvbos.lgj;

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
    public void organizar(){
        Collections.sort(ranking);
    }
    public void tamanho_Lista_Top_10(){
        while (ranking.size()>10){
            ranking.remove(ranking.size()-1);
        }
    }

    public void Limpar_Lista(){

    }
    public  void exibir_Top10(){
        for (int i = 0; i<ranking.size();i++){
            Jogadores jogador = ranking.get(i);
            System.out.println((i + 1) + ". " + jogador.getNome() + " - Pontuação: " + jogador.getPontos());
        }
    }
    public void carregar_dados(){
        //copia a pasta e coloca o caminho que você vai usar no final coloca // TOP10.txt. Neste ambiente não consigo criar.
        Path top10 = Paths.get("C:\\Users\\fernando\\Desktop\\Ciencias da Comp\\2 semestre\\Programação orient objetos\\trabalho_Tharlles_e_Eu\\TrabalhoPo\\TOP10.txt");
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
    private void salvar_dado() {
        //copia a pasta e coloca o caminho que você vai usar no final coloca // TOP10.txt. Neste ambiente não consigo criar.
        Path top10 = Paths.get("C:\\Users\\fernando\\Desktop\\Ciencias da Comp\\2 semestre\\Programação orient objetos\\trabalho_Tharlles_e_Eu\\TrabalhoPo\\TOP10.txt");
        try(BufferedWriter escrever = new BufferedWriter(new FileWriter(top10.toFile()))) {
           for(Jogadores jogadores : ranking){
               escrever.write(jogadores.getNome()+","+jogadores.getPontos()+"\n");
           }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
