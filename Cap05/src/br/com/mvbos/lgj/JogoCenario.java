package br.com.mvbos.lgj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.swing.*;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Texto;

public class JogoCenario extends CenarioPadrao {

	enum Estado {
		JOGANDO, GANHOU, PERDEU
	}

	private static final int ESPACAMENTO = 2;

	private static final int ESPACO_VAZIO = -1;

	private static final int LINHA_COMPLETA = -2;

	private int largBloco, altBloco; // largura bloco e altura bloco

	private int ppx, ppy; // Posicao peca x e y

	private final int[][] grade = new int[10][16];

	private int temporizador = 0;

	private Texto texto = new Texto(20);

	private Random rand = new Random();

	private int idPeca = -1;
	private int idPrxPeca = -1;

	private int idPrxPeca2 = -1;
	private int idPrxPeca3 = -1;
	private Color corPeca;
	private int[][] peca;
	private int cont_L1;
	private int cont_L2;
	private int cont_R1;
	private int cont_R2;
	private int cont_Q;
	private int cont_T;
	private int cont_R;

	private int nivel = Jogo.nivel;
	private static int pontos;
	private int linhasFeistas;

	//Boolean para pausar SOM #######Criado
	private boolean somPausado = false;

	private boolean animar;
	private boolean depurar;

	private Estado estado = Estado.JOGANDO;

	//som que criei
	private Clip clipMusicaFundo;
	// Som
	private AudioInputStream as;

	private Clip clipAdicionarPeca;

	private Clip clipMarcarLinha;

	public static int getPontos() {
		return pontos;
	}

	public JogoCenario(){
		super();
	}

	public JogoCenario(int largura, int altura) {
		super(largura, altura);
	}

	@Override
	public void carregar() {

// aqui tem mudança ====================================================================================================
		pontos = 0;
//######################################################################################################################
		largBloco = largura / grade.length;
		altBloco = altura / grade[0].length;

		for (int i = 0; i < grade.length; i++) {
			for (int j = 0; j < grade[0].length; j++) {
				grade[i][j] = ESPACO_VAZIO;
			}
		}

		Type[] audioFileTypes = AudioSystem.getAudioFileTypes();
		for (Type t : audioFileTypes) {
			System.out.println(t.getExtension());
		}
//########################################################### Sons auterados #################################################
		try {
			//carrega musica que criei 10 minutos
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("som/TetrisBgSong.wav"));
			clipMusicaFundo = AudioSystem.getClip();
			clipMusicaFundo.open(audioInputStream);

			clipMusicaFundo.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						event.getLine().close();
						clipMusicaFundo.setFramePosition(0);
						clipMusicaFundo.start();
					}
				}
			});

			//codigo anterior do som das peças
			as = AudioSystem.getAudioInputStream(new File("som/FixedPeace.wav"));
			clipAdicionarPeca = AudioSystem.getClip();
			clipAdicionarPeca.open(as);

			as = AudioSystem.getAudioInputStream(new File("som/800Points.wav"));
			clipMarcarLinha = AudioSystem.getClip();
			clipMarcarLinha.open(as);

			// Inicia a reprodução da musica que criei
			clipMusicaFundo.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		adicionaPeca();
	}
	//######################################################################### Temino dos Sons ##################################################
	@Override
	public void descarregar() {

		if (clipAdicionarPeca != null) {
			clipAdicionarPeca.stop();
			clipAdicionarPeca.close();
		}

		if (clipMarcarLinha != null) {
			clipMarcarLinha.stop();
			clipMarcarLinha.close();
		}
		if (clipMusicaFundo != null) {
			clipMusicaFundo.stop();
			clipMusicaFundo.close();
		}
	}

	// Método para pausar ou retomar o som de fundo ########################################## mudança para pausar musica
	private void pausarOuRetomarSomFundo() {
		if (clipMusicaFundo != null) {
			if (somPausado) {
				// Se o som estiver pausado, retoma a reprodução
				clipMusicaFundo.start();
				somPausado = false;
			} else {
				// Se o som estiver sendo reproduzido, pausa a reprodução
				clipMusicaFundo.stop();
				somPausado = true;
			}
		}
	}

	@Override
	public void atualizar() {

		if (estado != Estado.JOGANDO) {
			return;
		}

		if (Jogo.controleTecla[Jogo.Tecla.ENTER.ordinal()]) {
			// Tecla "Enter" pressionada, pausa ou retoma o som
			pausarOuRetomarSomFundo();
		}

		if (Jogo.controleTecla[Jogo.Tecla.ESQUERDA.ordinal()]) {
			if (validaMovimento(peca, ppx - 1, ppy))
				ppx--;

		} else if (Jogo.controleTecla[Jogo.Tecla.DIREITA.ordinal()]) {
			if (validaMovimento(peca, ppx + 1, ppy))
				ppx++;
		}

		if (Jogo.controleTecla[Jogo.Tecla.CIMA.ordinal()]) {
			girarReposicionarPeca(true);

		} else if (Jogo.controleTecla[Jogo.Tecla.BAIXO.ordinal()]) {
			if (validaMovimento(peca, ppx, ppy + 1)){
				ppy++;
				pontos +=1;
			}

		} else if (Jogo.controleTecla[Jogo.Tecla.Z.ordinal()]) {
			girarReposicionarPeca(false);

		} else if (Jogo.controleTecla[Jogo.Tecla.SPACE.ordinal()]) {
			while (validaMovimento(peca, ppx, ppy + 1)){
				ppy++;
				pontos +=2;
			}
		}

		if (depurar && Jogo.controleTecla[Jogo.Tecla.BC.ordinal()]) {
			if (++idPeca == Peca.PECAS.length)
				idPeca = 0;

			peca = Peca.PECAS[idPeca];
			corPeca = Peca.Cores[idPeca];
		}

		Jogo.liberaTeclas();

		if (animar && temporizador >= 5) {
			animar = false;

			descerColunas();
			adicionaPeca();

		} else if (temporizador >= 20) {
			temporizador = 0;

			if (colidiu(ppx, ppy + 1)) {

				if (clipAdicionarPeca != null) {
					clipAdicionarPeca.setFramePosition(0);
					clipAdicionarPeca.start();
				}

				if (!parouForaDaGrade()) {
					adicionarPecaNaGrade();
					animar = marcarLinha();

					peca = null;

					if (!animar)
						adicionaPeca();
// Eu mechi aqui =======================================================================================================
				} else {
					estado = Estado.PERDEU;
					Dados_Jogadores();

//======================================================================================================================
				}

			} else
				ppy++;

		} else
			temporizador += nivel;
	}
	// Eu mechi aqui ===================================================================================================
	private void Dados_Jogadores() {

		Jogadores jogador = new Jogadores();
		Ranking ranking = new Ranking();

		int pontosDoJogo = getPontos();

		jogador.setPontos(pontosDoJogo);

		String nomeDoJogador = JOptionPane.showInputDialog("Digite seu nome:");

		jogador.setNome(nomeDoJogador);

		ranking.carregar_dados();
		ranking.addJogadores(jogador);
		ranking.organizar();

		int posicao_Ultimo_Jogador = ranking.ranking.indexOf(jogador) + 1;
		JOptionPane.showMessageDialog(null,  posicao_Ultimo_Jogador+" no Ranking / Nome: " + jogador.getNome() + " / Pontos: " + jogador.getPontos());

		ranking.salvar_dado();
		ranking.tamanho_Lista_Top_10();
		ranking.exibir_Top10();
	}
	//==================================================================================================================
	// AQUI GERA AS PEÇAS esta funcionando agora gera todas em ordem ===================================================
	public void adicionaPeca() {

		ppy = -2;
		ppx = grade.length / 2 - 1;

		// Primeira chamada
		if (idPeca == -1) {
			idPeca = rand.nextInt(Peca.PECAS.length);
			idPrxPeca = rand.nextInt(Peca.PECAS.length);
			if (idPeca == idPrxPeca)
				idPrxPeca = rand.nextInt(Peca.PECAS.length);
		}else{
			idPeca = idPrxPeca;
		}

		// /Segunda chamada
		if (idPrxPeca2 ==-1 ){
			idPrxPeca2 = rand.nextInt(Peca.PECAS.length);
		}else {
			idPrxPeca = idPrxPeca2;
			idPrxPeca2 = idPrxPeca3;
		}
		idPrxPeca3 = rand.nextInt(Peca.PECAS.length);
		if (idPrxPeca3 == idPrxPeca2 || idPrxPeca3 == idPrxPeca || idPrxPeca3 == idPeca)
			idPrxPeca3 = rand.nextInt(Peca.PECAS.length);

		peca = Peca.PECAS[idPeca];
		corPeca = Peca.Cores[idPeca];

		if (idPeca == 0){
			cont_L1+=1;
		} else if (idPeca == 1) {
			cont_L2+=1;
		}else if (idPeca == 2) {
			cont_T+=1;
		}else if (idPeca == 3) {
			cont_R1+=1;
		}else if (idPeca == 4) {
			cont_R2+=1;
		}else if (idPeca == 5) {
			cont_Q+=1;
		}else if (idPeca == 6) {
			cont_R+=1;
		}

	}
	// modificado ate aqui =============================================================================================
	private void adicionarPecaNaGrade() {

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {

				if (peca[lin][col] != 0) {

					grade[col + ppx][lin + ppy] = idPeca;

				}
			}
		}
	}

	private boolean validaMovimento(int[][] peca, int px, int py) {

		if (peca == null)
			return false;

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {
				if (peca[lin][col] == 0)
					continue;

				int prxPx = col + px; // Proxima posicao peca x
				int prxPy = lin + py; // Proxima posicao peca y

				if (prxPx < 0 || prxPx >= grade.length)
					return false;

				if (prxPy >= grade[0].length)
					return false;

				if (prxPy < 0)
					continue;

				// Colidiu com uma peca na grade
				if (grade[prxPx][prxPy] > ESPACO_VAZIO)
					return false;

			}
		}

		return true;
	}

	private boolean parouForaDaGrade() {

		if (peca == null)
			return false;

		for (int lin = 0; lin < peca.length; lin++) {
			for (int col = 0; col < peca[lin].length; col++) {
				if (peca[lin][col] == 0)
					continue;
				// Fora da grade
				if (lin + ppy < 0)
					return true;
			}
		}

		return false;
	}

	private boolean colidiu(int px, int py) {

		if (peca == null)
			return false;

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {
				if (peca[lin][col] == 0)
					continue;

				int prxPx = col + px;
				int prxPy = lin + py;

				if (depurar) {
					if (prxPx < 0 || prxPx >= grade.length)
						return false;
				}
				// Chegou na base da grade
				if (prxPy == grade[0].length)
					return true;

				// Fora da grade
				if (prxPy < 0)
					continue;

				// Colidiu com uma peca na grade
				if (grade[prxPx][prxPy] > ESPACO_VAZIO)
					return true;
			}
		}

		return false;
	}
	// FIZ UMA ALTERAÇÃO AQUI MEU PARCEIRO =============================================================================
	//Chamar Nivel a direita
	private boolean marcarLinha() {
		int multPontos = 0;

		for (int lin = grade[0].length - 1; lin >= 0; lin--) {
			boolean linhaCompleta = true;

			for (int col = grade.length - 1; col >= 0; col--) {
				if (grade[col][lin] == ESPACO_VAZIO) {
					linhaCompleta = false;
					break;
				}
			}
			if (linhaCompleta) {
				multPontos++;
				for (int col = grade.length - 1; col >= 0; col--) {
					grade[col][lin] = LINHA_COMPLETA;
				}
			}
		}
		switch (multPontos) {
			case 1:
				pontos += 100 * nivel;
				break;
			case 2:
				pontos += 300 * nivel;
				break;
			case 3:
				pontos += 500 * nivel;
				break;
			case 4:
				pontos += 800 * nivel;
				break;
			default:
				pontos += 0;
				break;
		}
		linhasFeistas += multPontos;

		if (linhasFeistas >= 10) {
			nivel++;
			linhasFeistas = 0;
		}
		return multPontos > 0;
	}
	// FIZ UMA ALTERAÇÃO ATÉ AQUI MEU PARCEIRO =========================================================================
	//==================================================================================================================
	private void descerColunas() {
		for (int col = 0; col < grade.length; col++) {
			for (int lin = grade[0].length - 1; lin >= 0; lin--) {

				if (grade[col][lin] == LINHA_COMPLETA) {
					int moverPara = lin;
					int prxLinha = lin - 1;

					for (; prxLinha > -1; prxLinha--) {
						if (grade[col][prxLinha] == LINHA_COMPLETA)
							continue;
						else
							break;

					}

					for (; moverPara > -1; moverPara--, prxLinha--) {

						if (prxLinha > -1)
							grade[col][moverPara] = grade[col][prxLinha];
						else
							grade[col][moverPara] = ESPACO_VAZIO;

					}
				}
			}
		}

		if (clipMarcarLinha != null) {
			clipMarcarLinha.setFramePosition(0);
			clipMarcarLinha.start();
		}

	}

	protected void girarPeca(boolean sentidoHorario) {
		if (peca == null)
			return;

		final int[][] temp = new int[peca.length][peca.length];

		for (int i = 0; i < peca.length; i++) {
			for (int j = 0; j < peca.length; j++) {
				if (sentidoHorario)
					temp[j][peca.length - i - 1] = peca[i][j];
				else
					temp[peca.length - j - 1][i] = peca[i][j];
			}
		}

		System.out.println("Antes:");
		imprimirArray(peca);
		System.out.println("Depois:");
		imprimirArray(temp);

		if (validaMovimento(temp, ppx, ppy)) {
			peca = temp;
		}
	}

	private void imprimirArray(int[][] arr) {
		for (int lin = 0; lin < arr.length; lin++) {
			for (int col = 0; col < arr[lin].length; col++) {
				System.out.print(arr[lin][col] + "\t");
			}

			System.out.println();
		}
	}

	private void girarReposicionarPeca(boolean sentidoHorario) {
		if (peca == null)
			return;

		int tempPx = ppx;
		final int[][] tempPeca = new int[peca.length][peca.length];

		for (int i = 0; i < peca.length; i++) {
			for (int j = 0; j < peca.length; j++) {
				if (sentidoHorario)
					tempPeca[j][peca.length - i - 1] = peca[i][j];
				else
					tempPeca[peca.length - j - 1][i] = peca[i][j];
			}
		}

		// Reposiciona peca na tela
		for (int i = 0; i < tempPeca.length; i++) {
			for (int j = 0; j < tempPeca.length; j++) {
				if (tempPeca[j][i] == 0) {
					continue;
				}

				int prxPx = i + tempPx;

				if (prxPx < 0)
					tempPx = tempPx - prxPx;

				else if (prxPx == grade.length)
					tempPx = tempPx - 1;

			}
		}

		if (validaMovimento(tempPeca, tempPx, ppy)) {
			peca = tempPeca;
			ppx = tempPx;
		}
	}

	@Override
	public void desenhar(Graphics2D g) {

		for (int col = 0; col < grade.length; col++) {
			for (int lin = 0; lin < grade[0].length; lin++) {
				int valor = grade[col][lin];

				if (valor == ESPACO_VAZIO)
					continue;

				if (valor == LINHA_COMPLETA)
					g.setColor(Color.RED);
				else
					g.setColor(Peca.Cores[valor]);

				int x = 250 + col * largBloco + ESPACAMENTO;
				int y = lin * altBloco + ESPACAMENTO;

				g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

			}
		}

		if (peca != null) {
			g.setColor(corPeca);

			for (int col = 0; col < peca.length; col++) {
				for (int lin = 0; lin < peca[col].length; lin++) {
					if (peca[lin][col] != 0) {

						int x = 250 + (col + ppx) * largBloco + ESPACAMENTO;
						int y = (lin + ppy) * altBloco + ESPACAMENTO;

						g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

					} else if (depurar) {
						g.setColor(Color.PINK);
						int x = 250 + (col + ppx) * largBloco + ESPACAMENTO;
						int y = (lin + ppy) * altBloco + ESPACAMENTO;

						g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

						g.setColor(corPeca);
					}
				}
			}
		}
		// FIZ UMA ALTERAÇÃO AQUI MEU PARCEIRO =========================================================================
		// mas não deu certo ainda mostra as 3 mas felizmente sem sucesso.
		int miniatura = largBloco / 4;
		int[][] prxPeca = Peca.PECAS[idPrxPeca];
		int[][] prxPeca2 = Peca.PECAS[idPrxPeca2];
		int[][] prxPeca3 = Peca.PECAS[idPrxPeca3];


		g.setColor(Peca.Cores[idPrxPeca]);
		for (int col = 0; col < prxPeca.length; col++) {
			for (int lin = 0; lin < prxPeca[col].length; lin++) {
				if (prxPeca[lin][col] == 0)
					continue;

				int x = col * miniatura + ESPACAMENTO + 850;
				int y = lin * miniatura + ESPACAMENTO;

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);

			}
		}
		// isso garante que as miniaturas estejam uma debaixo da outra
		g.setColor(Peca.Cores[idPrxPeca2]);
		for (int col = 0; col < prxPeca2.length; col++) {
			for (int lin = 0; lin < prxPeca2[col].length; lin++) {
				if (prxPeca2[lin][col] == 0)
					continue;

				int x = col * miniatura + ESPACAMENTO + 850;
				int y = (lin + prxPeca.length+1) * miniatura + ESPACAMENTO;

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		g.setColor(Peca.Cores[idPrxPeca3]);
		for (int col = 0; col < prxPeca3.length; col++) {
			for (int lin = 0; lin < prxPeca3[col].length; lin++) {
				if (prxPeca3[lin][col] == 0)
					continue;

				int x = col * miniatura + ESPACAMENTO + 850;
				int y = (lin + prxPeca2.length+ prxPeca.length+2) * miniatura + ESPACAMENTO;

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		//==============================================================================================================
		//Aqui deixa todos os dados
		texto.setCor(Color.WHITE);
		texto.desenha(g, "Level: " + nivel, 0, 20);
		texto.desenha(g,"Linha: "+linhasFeistas,0,40);
		texto.desenha(g,"Pontos: "+ String.valueOf(pontos), 0, 60);
		texto.desenha(g," X "+ String.valueOf(cont_L1), 40, 130);
		texto.desenha(g," X "+ String.valueOf(cont_L2), 40, 180);
		texto.desenha(g," X "+ String.valueOf(cont_R1), 40, 280);
		texto.desenha(g," X "+ String.valueOf(cont_R2), 40, 330);
		texto.desenha(g," X "+ String.valueOf(cont_Q), 40, 380);
		texto.desenha(g," X "+ String.valueOf(cont_R), 40, 430);
		texto.desenha(g," X "+ String.valueOf(cont_T), 40, 230);

		int miniatura_fixada = largBloco / 4;
		int[][] LD = Peca.PECAS[0];
		g.setColor(Peca.Cores[0]);
		for (int col = 0; col < LD.length; col++) {
			for (int lin = 0; lin < LD[col].length; lin++) {
				if (LD[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 100 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] LE = Peca.PECAS[1];
		g.setColor(Peca.Cores[1]);
		for (int col = 0; col < LE.length; col++) {
			for (int lin = 0; lin < LE[col].length; lin++) {
				if (LE[lin][col] == 0)
					continue;
				int x = 5 + col * miniatura + ESPACAMENTO;
				int y = 150 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] T = Peca.PECAS[2];
		g.setColor(Peca.Cores[2]);
		for (int col = 0; col < T.length; col++) {
			for (int lin = 0; lin < T[col].length; lin++) {
				if (T[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 200 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] RE = Peca.PECAS[3];
		g.setColor(Peca.Cores[3]);
		for (int col = 0; col < RE.length; col++) {
			for (int lin = 0; lin < RE[col].length; lin++) {
				if (RE[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 250 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] RD = Peca.PECAS[4];
		g.setColor(Peca.Cores[4]);
		for (int col = 0; col < RD.length; col++) {
			for (int lin = 0; lin < RD[col].length; lin++) {
				if (RD[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 300 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] Q = Peca.PECAS[5];
		g.setColor(Peca.Cores[5]);
		for (int col = 0; col < Q.length; col++) {
			for (int lin = 0; lin < Q[col].length; lin++) {
				if (Q[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 350 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}
		int[][] R = Peca.PECAS[6];
		g.setColor(Peca.Cores[6]);
		for (int col = 0; col < R.length; col++) {
			for (int lin = 0; lin < R[col].length; lin++) {
				if (R[lin][col] == 0)
					continue;
				int x = 10 + col * miniatura + ESPACAMENTO;
				int y = 400 + lin * miniatura + ESPACAMENTO;
				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);
			}
		}


		Color cor_linha = new Color(252, 4, 226);
		g.setColor(cor_linha);
		g.drawLine(250,0,250,672);
		g.drawLine(750,0,750,672);
		g.drawLine(0,70,250,70);
		g.drawLine(750,145,1000,145);
		g.drawLine(840,145,840,0);
		g.drawLine(900,145,900,0);

		if (estado != Estado.JOGANDO) {
			texto.setCor(Color.WHITE);

			if (estado == Estado.GANHOU)
				texto.desenha(g, "Nunca vai acontecer =] !", 180, 180);
			else
				texto.desenha(g, "Deu ruim!", 180, 180);
		}
	}

}
