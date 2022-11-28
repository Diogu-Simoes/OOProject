package src;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.*;


public class UserInterface extends JFrame {
    private GestorEmpresas gestor;
    private GridBagConstraints posicao;
    private JPanel menu, baseDados, opcoes, filtrar, listar,  gerir, criarEditar, voltarBD, voltarOpc, voltarCE;
    private JButton botaoBaseDados, botaoOpcoes, botaoSair, botaoCriar, botaoApagar, botaoDetalhes,
    botaoEditar, botaoGuardar, botaoVoltarBD, botaoVoltarOpc, botaoVoltarCE,botaoTerminarCriar,botaoTerminarEditar;
    private JCheckBox caixaConfirmar,caixaAutoGuardar;
    private JComboBox<String> caixaFiltrar, caixaOrdenar, caixaTema, caixaTipo;
    private JTable tabela;
    private DefaultTableModel elementos;
    private InteracoesCaixa selecElemento;
    private InteracoesBotao premirBotao;
    private boolean alteracoesPorGuardar;

    public UserInterface() {
        // criar o listener para os clicks nos botões
        premirBotao = new InteracoesBotao();
        // criar o listener para as comboBox
        selecElemento = new InteracoesCaixa();
        // criar a variável que guarda as definições de layout para cada componente antes de ser adicionado
        posicao = new GridBagConstraints();
        // criar o gestor que vai funcionar através da GUI e tenta carregar dados a partir dos ficheiros
        gestor = new GestorEmpresas();
        // inicar variavel que verifica se houve alterações aos ficheiros deste o último save
        alteracoesPorGuardar = false;
        // construir a aparencia da janela
        construirAparencia();
        // utiliza o gestor para carregar os dados e informa do output do carregamento
        carregarDados();
        //construir o painel do menu
        construirMenu();
        // construir os paines com os diferentes botões para voltar ao menu
        construirVoltar();
        // criar o painel base de dados que contém todos os anteriores
        construirBaseDados();
        // criar o painel onde se podem configurar as opções do programa
        construirOpcoes();
        // uma vez que o construtor apenas é chamado quando a frame
        // é criada pela primeira vez sabemos que podemos mostrar
        // logo o menu depois de estar tudo construído
        add(menu);
    }

    private class InteracoesBotao implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evento) {

            if(evento.getSource() == botaoBaseDados) {
                mostrarBaseDados();
            }
            if(evento.getSource() == botaoOpcoes) {
                mostrarOpcoes();
            }
            if(evento.getSource() == botaoCriar) {
                mostrarCriar();
                alteracoesPorGuardar = true;
                botaoGuardar.setEnabled(true);
            }
            if(evento.getSource() == botaoEditar) {
                int indexLinha = tabela.getSelectedRow();
                if(indexLinha == -1) {
                    JOptionPane.showMessageDialog(null, "Deve selecionar uma empresa da tabela para efetuar esta operação!",null, JOptionPane.WARNING_MESSAGE);
                }
                else{
                    mostrarEditar(indexLinha);
                    alteracoesPorGuardar = true;
                    botaoGuardar.setEnabled(true);
                }
            }
            if(evento.getSource() == botaoApagar) {
                int indexLinha = tabela.getSelectedRow();
                if(indexLinha == -1) {
                    JOptionPane.showMessageDialog(null, "Deve selecionar uma coluna da tabela para efetuar esta operação!",null, JOptionPane.WARNING_MESSAGE);
                }
                else{
                    if(JOptionPane.showConfirmDialog(null, "Tem a certeza que pretende apagar a empresa selecionada?", null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        gestor.remove(indexLinha);
                        recarregarTabela();
                        if(caixaAutoGuardar.isSelected()) {
                            gestor.guardarDados();
                        }
                        else
                        {
                            alteracoesPorGuardar = true;
                            botaoGuardar.setEnabled(true);
                        }
                    }
                }
            }
            if(evento.getSource() == botaoDetalhes) {
                int indexLinha = tabela.getSelectedRow();
                if(indexLinha == -1) {
                    JOptionPane.showMessageDialog(null, "Deve selecionar uma coluna da tabela para efetuar esta operação!",null, JOptionPane.WARNING_MESSAGE);
                }
                else{
                    String detalhes = gestor.getEmpresas().get(indexLinha).toString();
                    JOptionPane.showMessageDialog(null, detalhes, null, JOptionPane.PLAIN_MESSAGE, null);
                }
            }
            if(evento.getSource() == botaoGuardar) {
                String informacao = gestor.guardarDados();
                if(informacao.compareTo("As alterações foram guardadas com sucesso!") != 0)
                    JOptionPane.showMessageDialog(null, informacao,null, JOptionPane.OK_OPTION);
                else {
                    alteracoesPorGuardar = false;
                    botaoGuardar.setEnabled(false);
                }
            }
            if(evento.getSource() == botaoVoltarBD || evento.getSource() == botaoVoltarOpc) {
                mostrarMenu();
            }
            if(evento.getSource() == botaoVoltarCE) {
                mostrarBaseDados();
            }
            if(evento.getSource() == botaoSair) {
                // confirmar saída e terminar
                sair();
            }
            if(evento.getSource() == caixaConfirmar)
            {
                if (caixaConfirmar.isSelected() && getWindowListeners().length == 0) {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            sair();
                        }
                    });
                }
                else if (getWindowListeners().length != 0){
                    removeWindowListener(getWindowListeners()[0]);
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        }
    }

    private class InteracoesCaixa implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evento) {
            requestFocusInWindow();
            if(evento.getSource() == caixaOrdenar) {
                Integer caixaSelect = caixaOrdenar.getSelectedIndex();
                gestor.ordenarLista(caixaSelect);
                recarregarTabela();
            }
            if(evento.getSource() == caixaFiltrar) {
                recarregarTabela();
            }
            if(evento.getSource() == caixaTema) {
                Integer caixaSelect = caixaTema.getSelectedIndex();
                try{
                    if(caixaSelect==0)
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    if(caixaSelect==1)
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    personalizarUI();
                    SwingUtilities.updateComponentTreeUI(menu);
                    SwingUtilities.updateComponentTreeUI(baseDados);
                    SwingUtilities.updateComponentTreeUI(opcoes);
                }
                catch(Exception e) {
                    JOptionPane.showMessageDialog(null, "Não foi possível implementar o tema selecionado, irá permanecer o atual!",null, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void mostrarMenu() {
        remove(baseDados);
        remove(opcoes);
        add(menu,BorderLayout.CENTER);
        validate();
        repaint();
    }

    private void mostrarBaseDados() {
        remove(menu);
        remove(criarEditar);
        add(baseDados,BorderLayout.CENTER);
        validate();
        repaint();
    }

    private void mostrarOpcoes() {
        remove(menu);
        add(opcoes,BorderLayout.CENTER);
        validate();
        repaint();
    }

    private void mostrarCriar()  {
        remove(baseDados);
        criarEditar.remove(botaoTerminarEditar);
        posicao.gridx = 1;
        posicao.gridy = 1;
        posicao.fill = GridBagConstraints.NONE;
        posicao.insets = new Insets(0,0,23,0);
        criarEditar.add(botaoTerminarCriar,posicao);
        caixaTipo.setSelectedIndex(-1);
        add(criarEditar,BorderLayout.CENTER);
        validate();
        repaint();
    }

    private void mostrarEditar(int indexEmpresa) {
        remove(baseDados);
        criarEditar.remove(botaoTerminarCriar);
        posicao.gridx = 1;
        posicao.gridy = 1;
        posicao.fill = GridBagConstraints.NONE;
        posicao.insets = new Insets(0,0,23,0);
        String tipo = gestor.getEmpresas().get(indexEmpresa).getTipo();
        switch(tipo){
            case "Cafe" -> {
                caixaTipo.setSelectedIndex(0);
            }
            case "Pastelaria" -> {
                caixaTipo.setSelectedIndex(1);
            }
            case "Restaurante Fast-Food" -> {
                caixaTipo.setSelectedIndex(2);
            }
            case "Restaurante Local" -> {
                caixaTipo.setSelectedIndex(3);
            }
            case "Frutaria" -> {
                caixaTipo.setSelectedIndex(4);
            }
            case "Minimercado" -> {
                caixaTipo.setSelectedIndex(5);
            }
            case "Supermercado" -> {
                caixaTipo.setSelectedIndex(6);
            }
            case "Hipermercado" -> {
                caixaTipo.setSelectedIndex(7);
            }
        }
        criarEditar.add(botaoTerminarEditar,posicao);
        add(criarEditar,BorderLayout.CENTER);
        validate();
        repaint();
    }

    private void recarregarTabela() {
        Integer caixaSelect = caixaFiltrar.getSelectedIndex();
        ArrayList<Empresa> registo = gestor.getEmpresas();
        elementos.setRowCount(0);
        String[] tipos = {"Todas","Restauração","Pastelaria","Cafe","Restaurante","Restaurante Fast-Food","Restaurante Local","Mercearia","Frutaria","Mercado","Minimercado","Supermercado","Hipermercado"};
        if(caixaSelect==0) {
            for (Empresa empresa : registo) {
                elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),empresa.lucroSimNao()});
            }
        }
        else{
            for (Empresa empresa : registo) {
                if(empresa.getTipo().equals(tipos[caixaSelect]) || empresa.getCategoria().equals(tipos[caixaSelect]) || empresa.getSubCategoria().equals(tipos[caixaSelect])) {
                    elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),empresa.lucroSimNao()});
                }
            }
        }
    }

    private void construirAparencia() {
        // definir o estilo da janela
        setTitle("StarThrive");
        setSize(720, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // cria um listener personalizado para chamar a confirmação quando o utilizador tenta fechar o programa de qualquer forma
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sair();
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon("src/resources/icon.png").getImage());

        // criar cores e modelos personalizados para toda a interface através do UIManager
        personalizarUI();
    }

    private void personalizarUI() {
        Color invisivel = new Color(0,0,0,0);
        UIManager.put("Panel.background",Color.WHITE);
        UIManager.put("Button.focus",invisivel);
        UIManager.put("Button.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("CheckBox.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("CheckBox.focus",invisivel);
        UIManager.put("CheckBox.background",Color.WHITE);
        UIManager.put("ComboBox.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("OptionPane.background",Color.WHITE);
        UIManager.put("OptionPane.yesButtonText","Sim");
        UIManager.put("OptionPane.noButtonText","Não");
        UIManager.put("OptionPane.cancelButtonText","Cancelar");
        UIManager.put("OptionPane.yesButtonText","Sim");
        UIManager.put("OptionPane.noButtonText","Não");
        UIManager.put("OptionPane.cancelButtonText","Cancelar");
        UIManager.put("OptionPane.okButtonMnemonic", "0");
        UIManager.put("OptionPane.cancelButtonMnemonic", "0");
        UIManager.put("OptionPane.noButtonMnemonic", "0");
        UIManager.put("OptionPane.questionIcon",new ImageIcon("src/resources/question.gif"));
        UIManager.put("OptionPane.warningIcon",new ImageIcon("src/resources/warning.gif"));
        UIManager.put("OptionPane.errorIcon",new ImageIcon("src/resources/error.gif"));
    }

    private void carregarDados() {
        String informacao = gestor.carregarDadosObjeto();
        // Se os dados forem carregados de um ficheiro objeto não se mostra a mensagem pois esta é a situação ótima de
        // carregamento dos dados, sendo a que aconteceria mais vezes durante o uso normal do programa iria se tornar irritante
        if(informacao.compareTo("Os dados foram carregados do ficheiro de objetos com sucesso!") != 0)
            JOptionPane.showMessageDialog(null, informacao,null, JOptionPane.OK_OPTION);
    }

    private void construirMenu() {
        menu = new JPanel();
        menu.setLayout(new GridBagLayout());
        JLabel textoTitulo = new JLabel("<html>St<img src=" + getClass().getResource("resources/star.gif").toString() + "></FONT>rThrive</html>");
        textoTitulo.setFont(new Font("Arial", Font.BOLD, 100));
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,40,0);
        menu.add(textoTitulo, posicao);
        botaoBaseDados = new JButton("Base de Dados");
        posicao.gridx = 0;
        posicao.gridy = 1;
        botaoBaseDados.addActionListener(premirBotao);
        menu.add(botaoBaseDados, posicao);
        botaoOpcoes = new JButton("Opções");
        posicao.gridx = 0;
        posicao.gridy = 2;
        botaoOpcoes.addActionListener(premirBotao);
        menu.add(botaoOpcoes, posicao);
        botaoSair = new JButton("Sair do Programa");
        posicao.gridx = 0;
        posicao.gridy = 3;
        botaoSair.addActionListener(premirBotao);
        menu.add(botaoSair,posicao);
    }

    private void construirVoltar() {
        voltarBD = new JPanel();
        voltarBD.setLayout(new GridBagLayout());
        posicao.gridx = 0;
        posicao.gridy = 0;
        botaoVoltarBD = new JButton("◄");
        posicao.insets = new Insets(0,0,0,0);
        botaoVoltarBD.addActionListener(premirBotao);
        voltarBD.add(botaoVoltarBD, posicao);
        voltarOpc = new JPanel();
        voltarOpc.setLayout(new GridBagLayout());
        posicao.gridx = 0;
        posicao.gridy = 0;
        botaoVoltarOpc = new JButton("◄");
        posicao.insets = new Insets(0,0,0,0);
        botaoVoltarOpc.addActionListener(premirBotao);
        voltarOpc.add(botaoVoltarOpc, posicao);
        voltarCE = new JPanel();
        voltarCE.setLayout(new GridBagLayout());
        posicao.gridx = 0;
        posicao.gridy = 0;
        botaoVoltarCE = new JButton("◄");
        posicao.insets = new Insets(0,0,0,0);
        botaoVoltarCE.addActionListener(premirBotao);
        voltarCE.add(botaoVoltarCE, posicao);
    }

    private void construirGerir() {
        gerir = new JPanel();
        gerir.setLayout(new GridBagLayout());
        botaoCriar = new JButton("Criar");
        posicao.gridx = 0;
        posicao.gridy = 1;
        botaoCriar.addActionListener(premirBotao);
        gerir.add(botaoCriar, posicao);
        botaoEditar = new JButton("Editar");
        posicao.gridx = 0;
        posicao.gridy = 2;
        botaoEditar.addActionListener(premirBotao);
        gerir.add(botaoEditar, posicao);
        botaoApagar = new JButton("Apagar");
        posicao.gridx = 0;
        posicao.gridy = 3;
        botaoApagar.addActionListener(premirBotao);
        gerir.add(botaoApagar, posicao);
        botaoDetalhes = new JButton("Detalhes");
        posicao.gridx = 0;
        posicao.gridy = 4;
        botaoDetalhes.addActionListener(premirBotao);
        gerir.add(botaoDetalhes, posicao);
    }


    private void construirListar() {
        listar = new JPanel();
        listar.setLayout(new GridBagLayout());
        // por default criamos a tabela a mostrar todas as empresas na primeira vez que o utlizadar aceder à base de dados
        String[] colunas = {"Nome", "Tipo", "Distrito", "Despesa", "Receita", "Lucro / Diferença"};
        elementos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int fila, int coluna) {
                return false; //return (coluna == 0 || coluna == 1 || coluna == 2 || coluna == 3 || coluna == 4);
            }
        };
        ArrayList<Empresa> registo = gestor.getEmpresas();
        gestor.ordenarLista(0);
        for (Empresa empresa : registo) {
            elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),empresa.lucroSimNao()});
        }
		tabela = new JTable(elementos) {
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
        {
            // Fazer um clique na tabela selecionar a linha toda (remove a borda de foco no elemento da coluna selecionado)
            JComponent Jcomponente = (JComponent)super.prepareRenderer(renderer, row, column);
            if (isRowSelected(row))
                Jcomponente.setBorder(null);
            return Jcomponente;
        }};
        tabela.setFillsViewportHeight(true);
        DefaultTableCellRenderer justificarCentro = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer)tabela.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        justificarCentro.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<6;x++) {
            tabela.getColumnModel().getColumn(x).setCellRenderer(justificarCentro);
        }
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tabela.setFont(new Font("Arial", Font.PLAIN, 15));
        tabela.setRowHeight(25);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroller = new JScrollPane(tabela);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.fill = GridBagConstraints.BOTH;
        posicao.weightx = 1;
        posicao.weighty = 0;
        posicao.insets = new Insets(0,0,0,0);
        listar.add(scroller,posicao);
    }

    private void construirFiltrar() {
        filtrar = new JPanel();
        filtrar.setLayout(new GridBagLayout());
        String[] filtros = {"Todas", // 0
                            "Restauração (Categoria)", // 1
                            "  Pastelaria", // 2
                            "  Cafe", // 3
                            "  Restaurante (Sub-Categoria)", // 4
                            "      Restaurante Fast-Food", // 5
                            "      Restaurante Local", // 6
                            "Mercearia (Categoria)", // 7
                            "  Frutaria", // 8
                            "  Mercado (Sub-Categoria)", // 9
                            "    Minimercado", // 10
                            "    Hipermercado", // 11
                            "    Supermercado"}; // 12
        caixaFiltrar = new JComboBox<String>(filtros);
        caixaFiltrar.addActionListener(selecElemento);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,10,40,0);
        JLabel textoFiltrar = new JLabel("Filtrar:");
        textoFiltrar.setFont(new Font("Arial", Font.BOLD, 15));
		textoFiltrar.setLabelFor( caixaFiltrar );
        filtrar.add(caixaFiltrar,posicao);
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,42,0);
        filtrar.add(textoFiltrar,posicao);
        String[] ordem = {"Nome ↓","Nome ↑","Distrito ↓","Distrito ↑","Despesa anual ↓","Despesa anual ↑","Receita anual ↓","Receita anual ↑","Lucro ↓","Lucro ↑"};
        caixaOrdenar = new JComboBox<String>(ordem);
        caixaOrdenar.addActionListener(selecElemento);
        posicao.gridx = 3;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,10,40,75);
        JLabel textoOrdenar = new JLabel("Ordenar:");
        textoOrdenar.setFont(new Font("Arial", Font.BOLD, 15));
		textoOrdenar.setLabelFor( caixaOrdenar );
        filtrar.add(caixaOrdenar,posicao);
        posicao.gridx = 2;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,40,42,0);
        filtrar.add(textoOrdenar,posicao);
        posicao.gridx = 4;
        posicao.gridy = 0;
        botaoGuardar = new JButton("Guardar");
        botaoGuardar.setEnabled(false);
        posicao.insets = new Insets(0,40,40,0);
        botaoGuardar.addActionListener(premirBotao);
        filtrar.add(botaoGuardar, posicao);
    }

    private void construirCriarEditar(){
        criarEditar = new JPanel();
        criarEditar.setLayout(new GridBagLayout());
        JLabel textoTipo = new JLabel("Tipo:");
        textoTipo.setFont(new Font("Arial", Font.BOLD, 15));
        String[] tipos = {"Cafe","Pastelaria","Restaurante Fast-Food","Restaurante Local","Frutaria","Minimercado","Hipermercado","Supermercado"};
        caixaTipo = new JComboBox<String>(tipos);
        caixaTipo.addActionListener(selecElemento);
        textoTipo.setLabelFor(caixaTipo);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.fill = GridBagConstraints.NONE;
        posicao.insets = new Insets(0,0,23,350);
        criarEditar.add(textoTipo,posicao);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,23,0);
        criarEditar.add(caixaTipo,posicao);
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,0,0);
        criarEditar.add(voltarCE,posicao);
        botaoTerminarCriar = new JButton("Criar");
        posicao.gridx = 1;
        posicao.gridy = 1;
        botaoTerminarCriar.addActionListener(premirBotao);
        posicao.insets = new Insets(0,0,23,0);
        botaoTerminarEditar = new JButton("Aplicar alterações");
        posicao.gridx = 0;
        posicao.gridy = 1;
        botaoTerminarEditar.addActionListener(premirBotao);
        posicao.insets = new Insets(0,0,23,0);
    }

    private void construirBaseDados() {
        // construir o painel com a tabela
        construirFiltrar();
        // construir o painel com os botões gerir as empresas (criar/editar/apagar)
        construirGerir();
        // construir o painel onde irá ficar a interface para criar ou editar uma empresa
        construirListar();
        // construir o painel para filtrar as empresas com as diferentes opções do enunciado e o botao de guardar
        construirCriarEditar();
        // constroi o painel com todos os outros, execeto o CriarEditar, que apenas é chamado quando se escolhe criar ou editar
        baseDados = new JPanel();
        baseDados.setLayout(new GridBagLayout());
        posicao.fill = GridBagConstraints.BOTH;
        posicao.insets = new Insets(0,0,0,0);
        posicao.gridx = 1;
        posicao.gridy = 0;
        baseDados.add(filtrar,posicao);
        posicao.gridx = 0;
        posicao.gridy = 1;
        baseDados.add(voltarBD,posicao);
        posicao.gridx = 2;
        posicao.gridy = 1;
        baseDados.add(gerir,posicao);
        posicao.gridx = 1;
        posicao.gridy = 1;
        baseDados.add(listar,posicao);
    }

    private void construirOpcoes()
    {
        opcoes = new JPanel();
        opcoes.setLayout(new GridBagLayout());
        posicao.fill = GridBagConstraints.NONE;
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,0,0);
        opcoes.add(voltarOpc,posicao);
        JLabel textoTema = new JLabel("Tema:");
        textoTema.setFont(new Font("Arial", Font.BOLD, 15));
        String[] temas = {"Original","Nativo do Sistema (Experimental)"};
        caixaTema = new JComboBox<String>(temas);
        caixaTema.addActionListener(selecElemento);
        textoTema.setLabelFor(caixaTema);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,23,350);
        opcoes.add(textoTema,posicao);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,23,0);
        opcoes.add(caixaTema,posicao);
        caixaConfirmar = new JCheckBox("Confirmar antes de sair:   ");
        caixaConfirmar.setHorizontalTextPosition(SwingConstants.LEFT);
        caixaConfirmar.setSelected(true);
        caixaConfirmar.setSize(new DimensionUIResource(50, HEIGHT));
        caixaConfirmar.addActionListener(premirBotao);
        posicao.gridx = 1;
        posicao.gridy = 1;
        posicao.insets = new Insets(0,0,23,0);
        opcoes.add(caixaConfirmar,posicao);
        caixaAutoGuardar = new JCheckBox("Guardar automaticamente depois de criar, editar ou apagar:   ");
        caixaAutoGuardar.setHorizontalTextPosition(SwingConstants.LEFT);
        caixaAutoGuardar.setSelected(false);
        caixaAutoGuardar.setSize(new DimensionUIResource(50, HEIGHT));
        caixaAutoGuardar.addActionListener(premirBotao);
        posicao.gridx = 1;
        posicao.gridy = 2;
        posicao.insets = new Insets(0,0,23,0);
        opcoes.add(caixaAutoGuardar,posicao);
    }

    private void sair() {
        int resposta;
        if(alteracoesPorGuardar) {
            resposta = JOptionPane.showConfirmDialog(null, "Existem alterações por guardar.\nDeseja guardar antes de sair?\n ", null, JOptionPane.YES_NO_CANCEL_OPTION);
            if(resposta==JOptionPane.YES_OPTION) {
                gestor.guardarDados();
                System.exit(0);
            }
            if(resposta==JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
        else{
            resposta = JOptionPane.showConfirmDialog(null, "Tem a certeza que pretende sair?", null, JOptionPane.YES_NO_OPTION);
            if(resposta==JOptionPane.YES_OPTION)
                System.exit(0);
        }
    }
}
