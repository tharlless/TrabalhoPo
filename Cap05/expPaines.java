import javax.swing.*;
import java.awt.*;

public class expPaines extends JFrame {
    public expPaines() {
        // Configuração do JFrame
        setTitle("JFrame com Tamanhos Específicos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Criando os painéis
        JPanel painelEsquerdo = new JPanel();
        JPanel painelMeio = new JPanel();
        JPanel painelDireito = new JPanel();

        // Definindo cores diferentes para cada painel
        painelEsquerdo.setBackground(Color.BLUE);
        painelMeio.setBackground(Color.RED);
        painelDireito.setBackground(Color.GREEN);

        // Definindo tamanhos específicos para os painéis esquerdo e direito
        painelEsquerdo.setPreferredSize(new Dimension(100, getHeight())); // largura de 100 pixels
        painelDireito.setPreferredSize(new Dimension(100, getHeight())); // largura de 100 pixels

        // Adicionando componentes aos painéis
        painelEsquerdo.add(new JLabel("Painel Esquerdo"));
        painelMeio.add(new JLabel("Painel do Meio"));
        painelDireito.add(new JLabel("Painel Direito"));

        // Criando um painel principal com GridLayout
        JPanel painelPrincipal = new JPanel(new GridLayout(1, 3));
        
        painelPrincipal.add(painelEsquerdo);
        painelPrincipal.add(painelMeio);
        painelPrincipal.add(painelDireito);

        // Adicionando o painel principal ao JFrame
        add(painelPrincipal);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            expPaines exemplo = new expPaines();
            exemplo.setVisible(true);
        });
    }
}
