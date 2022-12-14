package src;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.io.*;

/**
 * Classe UserInterface
 * Contém toda a interface gráfica do programa, bem como as operações
 * que ocorrem quando se interage com a mesma
 * @author Diogo Simões e Gustavo Alves
 * @version 1.0
 */

public class UserInterface extends JFrame {
    /**
     * Irá atuar pela interface ao longo do programa
     */
    private GestorEmpresas gestor;
    /**
     * Guarda as opções da interface alteradas pelo utilizador
     */
    private Opcoes opcoesGuardadas;
    /**
     *  Guarda o posicionamento dos elementos na interface antes de serem adicionados
     */
    private GridBagConstraints posicao;
    /**
     * Contém os respetivos componentes dessa parte da interface
     */
    private JPanel menu, baseDados, opcoes, caixasOpcoes, filtrar, listar,  gerir, criarEditar, caixasDados, voltarBD,
                    voltarOpc, voltarCE;
    /**
     * Botão
     */
    private JButton botaoBaseDados, botaoOpcoes, botaoSair, botaoCriar, botaoApagar, botaoDetalhes,
                    botaoEditar, botaoGuardar, botaoVoltarBD, botaoVoltarOpc, botaoVoltarCE, botaoTerminarCriar,botaoTerminarEditar;
    /**
     * Checkbox
     */
    private JCheckBox caixaConfirmar,caixaAutoGuardar, caixaFullscreen;
    /**
     * Campo de texto
     */
    private JTextField campoNome, campoDistrito, campoFaturacaoMedia, campoIntUm, campoIntDois, campoIntTres, campoIntQuatro,
                        campoDoubleUm, campoDoubleDois, campoDoubleTres, campoDoubleQuatro;
    /**
     * Texto
     */
    private JLabel textoIntUm, textoIntDois, textoIntTres, textoIntQuatro, textoDoubleUm, textoDoubleDois,
                    textoDoubleTres, textoDoubleQuatro;
    /**
     * ComboBox
     */
    private JComboBox<String> caixaFiltrar, caixaOrdenar, caixaEstilo, caixaTema, caixaTipo;
    /**
     * ComboBox
     */
    private JComboBox<Integer> caixaHorasLat, caixaMinutosLat,caixaSegundosLat, caixaHorasLong, caixaMinutosLong, caixaSegundosLong;
    /**
     * ComboBox
     */
    private JComboBox<Character> caixaDirecaoLat, caixaDirecaoLong;
    /**
     * ComboBox
     */
    private JTable tabela;
    /**
     * Contém os elementos que estão atualmente adicionados à tabela da base de dados
     */
    private DefaultTableModel elementos;
    /**
     * Listener para as ComboBox
     */
    private InteracoesCaixa selecElemento;
    /**
     * Listener para os botões
     */
    private InteracoesBotao premirBotao;
    /**
     * Listener para os campos de texto
     */
    private InteracoesCampo escreverCampo;
    /**
     * Permite a interface saber quando há alterações por guardar ou não
     */
    private boolean alteracoesPorGuardar;

    /**
     * Construtor da classe UserInterface
     * Carrega os dados necessários e inicializa todos os componentes
     * da interface gráfica pela respectiva ordem, divididindo as várias
     * etapas deste processo em métodos separados
     */

    public UserInterface() {
        // criar o listener para os clicks nos botões
        premirBotao = new InteracoesBotao();
        // criar o listener para as comboBox
        selecElemento = new InteracoesCaixa();
        // criar o listener para quando escreve num campo de texto
        escreverCampo = new InteracoesCampo();
        // criar a variável que guarda as definições de layout para cada componente antes de ser adicionado
        posicao = new GridBagConstraints();
        // criar o gestor que vai funcionar através da GUI e tenta carregar dados a partir dos ficheiros
        gestor = new GestorEmpresas();
        // inicar variavel que verifica se houve alterações aos ficheiros deste o último save
        alteracoesPorGuardar = false;
        // carregar as opções guardadas da última sessão, se não exisitirem cria as default
        carregarOpcoes();
        // construir a aparencia da janela com as opções carregadas
        construirAparencia();
        // utiliza o gestor para carregar os dados
        carregarDados();
        //construir o painel do menu
        construirMenu();
        // construir os paines com os diferentes botões para voltar ao menu
        construirVoltar();
        // criar o painel base de dados que contém todos os anteriores
        construirBaseDados();
        // criar o painel onde se podem configurar as opções do programa
        construirOpcoes();
        // fazer todos os popups serem brancos independentemente do tema
        UIManager.put("Panel.background",Color.WHITE);
        // uma vez que o construtor apenas é chamado quando a frame
        // é criada pela primeira vez sabemos que podemos mostrar
        // logo o menu depois de estar tudo construído
        add(menu,BorderLayout.CENTER);
    }

    /**
    * Nested Class InteracoesBotao
    * Listener personalizado para as ações com os vário botões e checkBox que o implementam
    */
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
            }
            if(evento.getSource() == botaoEditar) {
                int indexLinha = tabela.getSelectedRow();
                if(indexLinha == -1) {
                    JOptionPane.showMessageDialog(null, "Deve selecionar uma empresa da tabela para efetuar esta operação!",null, JOptionPane.WARNING_MESSAGE);
                }
                else{
                    mostrarEditar(indexLinha);
                }
            }
            if(evento.getSource() == botaoTerminarCriar) {
                String tipo = caixaTipo.getSelectedItem().toString();
                Coordenada latitude = new Coordenada(caixaHorasLat.getSelectedIndex(),caixaMinutosLat.getSelectedIndex(),caixaSegundosLat.getSelectedIndex(),caixaDirecaoLat.getSelectedItem().toString().charAt(0));
                Coordenada longitude = new Coordenada(caixaHorasLong.getSelectedIndex(),caixaMinutosLong.getSelectedIndex(),caixaSegundosLong.getSelectedIndex(),caixaDirecaoLong.getSelectedItem().toString().charAt(0));
                Localizacao local = new Localizacao(latitude,longitude);
                switch(tipo){
                    case "Cafe" -> {
                        gestor.addCafe(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleTres.getText()),Double.parseDouble(campoDoubleDois.getText()),Integer.parseInt(campoIntUm.getText()));
                    }
                    case "Pastelaria" -> {
                        gestor.addPastelaria(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleTres.getText()),Double.parseDouble(campoDoubleDois.getText()),Integer.parseInt(campoIntUm.getText()));
                    }
                    case "Restaurante Fast-Food" -> {
                        gestor.addRestFastFood(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Integer.parseInt(campoIntUm.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleDois.getText()),Integer.parseInt(campoIntDois.getText()),Integer.parseInt(campoIntTres.getText()),Double.parseDouble(campoDoubleTres.getText()),Double.parseDouble(campoDoubleQuatro.getText()));
                    }
                    case "Restaurante Local" -> {
                        gestor.addRestLocal(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Integer.parseInt(campoIntUm.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleDois.getText()),Integer.parseInt(campoIntDois.getText()),Integer.parseInt(campoIntTres.getText()),Integer.parseInt(campoIntQuatro.getText()),Double.parseDouble(campoDoubleTres.getText()));
                    }
                    case "Frutaria" -> {
                        gestor.addFrutaria(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()), Double.parseDouble(campoDoubleUm.getText()),Integer.parseInt(campoIntUm.getText()));
                    }
                    case "Minimercado" -> {
                        gestor.addMiniMercado(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleDois.getText()));
                    }
                    case "Supermercado" -> {
                        gestor.addSuperMercado(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleDois.getText()));
                    }
                    case "Hipermercado"-> {
                        gestor.addHiperMercado(campoNome.getText(),local,campoDistrito.getText(),Double.parseDouble(campoFaturacaoMedia.getText()),Double.parseDouble(campoDoubleUm.getText()),Double.parseDouble(campoDoubleDois.getText()));
                    }
                    default -> {}
                }
                autoGuardar();
                recarregarTabela();
                mostrarBaseDados();
                if(caixaAutoGuardar.isSelected())
                    JOptionPane.showMessageDialog(null, "A empresa foi criada com sucesso e guardada automaticamente!",null, JOptionPane.WARNING_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "A empresa foi criada com sucesso, não se esqueça de guardar!",null, JOptionPane.WARNING_MESSAGE);
            }
            if(evento.getSource() == botaoTerminarEditar) {
                Empresa empresa = gestor.getEmpresas().get(tabela.getSelectedRow());
                empresa.setNome(campoNome.getText());
                Coordenada latitude = new Coordenada(caixaSegundosLat.getSelectedIndex(),caixaMinutosLat.getSelectedIndex(),caixaHorasLat.getSelectedIndex(),caixaDirecaoLat.getSelectedItem().toString().charAt(0));
                Coordenada longitude = new Coordenada(caixaSegundosLong.getSelectedIndex(),caixaMinutosLong.getSelectedIndex(),caixaHorasLong.getSelectedIndex(),caixaDirecaoLong.getSelectedItem().toString().charAt(0));
                Localizacao local = new Localizacao(latitude,longitude);
                empresa.setLocal(local);
                empresa.setDistrito(campoDistrito.getText());
                empresa.setFaturacaoMedia(Double.parseDouble(campoFaturacaoMedia.getText()));
                String tipo = caixaTipo.getSelectedItem().toString();
                switch(tipo){
                    case "Cafe" -> {
                        Cafe cafe = (Cafe)empresa;
                        cafe.setNumEmpMesa(Integer.parseInt(campoIntUm.getText()));
                        cafe.setSalarioMedAnual(Double.parseDouble(campoDoubleUm.getText()));
                        cafe.setNumMedClientesDiario(Double.parseDouble(campoDoubleDois.getText()));
                        cafe.setNumMedCafes(Double.parseDouble(campoDoubleTres.getText()));
                    }
                    case "Pastelaria" -> {
                        Pastelaria pastelaria = (Pastelaria)empresa;
                        pastelaria.setNumEmpMesa(Integer.parseInt(campoIntUm.getText()));
                        pastelaria.setSalarioMedAnual(Double.parseDouble(campoDoubleUm.getText()));
                        pastelaria.setNumMedClientesDiario(Double.parseDouble(campoDoubleDois.getText()));
                        pastelaria.setNumMedBolos(Double.parseDouble(campoDoubleTres.getText()));
                    }
                    case "Restaurante Fast-Food" -> {
                        RestFastFood restFastFood = (RestFastFood)empresa;
                        restFastFood.setNumEmpMesa(Integer.parseInt(campoIntUm.getText()));
                        restFastFood.setSalarioMedAnual(Double.parseDouble(campoDoubleUm.getText()));
                        restFastFood.setNumMedClientesDiario(Double.parseDouble(campoDoubleDois.getText()));
                        restFastFood.setNumDiasFuncAnual(Integer.parseInt(campoIntDois.getText()));
                        restFastFood.setNumMesasInteriores(Integer.parseInt(campoIntUm.getText()));

                    }
                    case "Restaurante Local" -> {
                        RestLocal restLocal = (RestLocal)empresa;
                        restLocal.setNumEmpMesa(Integer.parseInt(campoIntUm.getText()));
                        restLocal.setSalarioMedAnual(Double.parseDouble(campoDoubleUm.getText()));
                        restLocal.setNumMedClientesDiario(Double.parseDouble(campoDoubleDois.getText()));
                        restLocal.setNumDiasFuncAnual(Integer.parseInt(campoIntDois.getText()));
                        restLocal.setNumMesasInteriores(Integer.parseInt(campoIntUm.getText()));

                    }
                    case "Frutaria" -> {
                        Frutaria frutaria = (Frutaria)empresa;
                        frutaria.setCustoLimpezaAnual(Double.parseDouble(campoDoubleUm.getText()));
                        frutaria.setNumProdutos(Integer.parseInt(campoIntUm.getText()));

                    }
                    case "Minimercado" -> {
                        Minimercado minimercado = (Minimercado)empresa;
                        minimercado.setCustoLimpezaAnual(Double.parseDouble(campoDoubleUm.getText()));
                        minimercado.setAreaCorredores(Double.parseDouble(campoDoubleDois.getText()));

                    }
                    case "Supermercado" -> {
                        Supermercado supermercado = (Supermercado)empresa;
                        supermercado.setCustoLimpezaAnual(Double.parseDouble(campoDoubleUm.getText()));
                        supermercado.setAreaCorredores(Double.parseDouble(campoDoubleDois.getText()));

                    }
                    case "Hipermercado"-> {
                        Hipermercado hipermercado = (Hipermercado)empresa;
                        hipermercado.setCustoLimpezaAnual(Double.parseDouble(campoDoubleUm.getText()));
                        hipermercado.setAreaCorredores(Double.parseDouble(campoDoubleDois.getText()));

                    }
                    default -> {}
                }
                autoGuardar();
                recarregarTabela();
                mostrarBaseDados();
                if(caixaAutoGuardar.isSelected())
                    JOptionPane.showMessageDialog(null, "As alterações à empresa foram aplicadas com sucesso e guardadas automaticamente!",null, JOptionPane.WARNING_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "As alterações à empresa foram aplicadas com sucesso, não se esqueça de guardar!",null, JOptionPane.WARNING_MESSAGE);
            }
            if(evento.getSource() == botaoApagar) {
                int indexLinha = tabela.getSelectedRow();
                if(indexLinha == -1) {
                    JOptionPane.showMessageDialog(null, "Deve selecionar uma coluna da tabela para efetuar esta operação!",null, JOptionPane.WARNING_MESSAGE);
                }
                else{
                    if(JOptionPane.showConfirmDialog(null, "Tem a certeza que pretende apagar a empresa selecionada?", null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        gestor.remove(indexLinha);
                        autoGuardar();
                        recarregarTabela();
                        if(caixaAutoGuardar.isSelected())
                            JOptionPane.showMessageDialog(null, "A empresa foi apagada com sucesso e a alteração foi guardada automaticamente!",null, JOptionPane.WARNING_MESSAGE);
                        else
                            JOptionPane.showMessageDialog(null, "A empresa foi apagada com sucesso, não se esqueça de guardar!",null, JOptionPane.WARNING_MESSAGE);
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
                    JOptionPane.showMessageDialog(null, informacao,null, JOptionPane.ERROR_MESSAGE);
                else {
                    alteracoesPorGuardar = false;
                    botaoGuardar.setEnabled(false);
                    botaoGuardar.setToolTipText("Não há alterações para guardar!");
                }
            }
            if(evento.getSource() == botaoVoltarBD || evento.getSource() == botaoVoltarOpc) {
                mostrarMenu();
            }
            if(evento.getSource() == botaoVoltarCE) {
                mostrarBaseDados();
            }
            if(evento.getSource() == botaoSair) {
                sair();
            }
            if(evento.getSource() == caixaConfirmar)
            {
                if (caixaConfirmar.isSelected() && getWindowListeners().length == 0) {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evento) {
                            sair();
                        }
                    });
                }
                else if (getWindowListeners().length != 0){
                    removeWindowListener(getWindowListeners()[0]);
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                opcoesGuardadas.setConfirmarSair(caixaConfirmar.isSelected());
                guardarOpcoes();
            }
            if(evento.getSource() == caixaAutoGuardar) {
                opcoesGuardadas.setAutoGuardar(caixaAutoGuardar.isSelected());
                guardarOpcoes();
            }
            if(evento.getSource() == caixaFullscreen) {
                boolean estado = caixaFullscreen.isSelected();
                UserInterface.this.dispose();
                UserInterface.this.setUndecorated(estado);
                UserInterface.this.setVisible(true);
                opcoesGuardadas.setFullscreen(estado);
                guardarOpcoes();
            }
        }
    }

    /**
    * Nested Class InteracoesCampo
    * Listener personalizado para as ações efetudaos nas caixas de texto que o implementam
    */
    private class InteracoesCampo implements DocumentListener{
        public void changedUpdate(DocumentEvent evento) {}
        // quando o utilizador escreve ou apaga nas caixas de texto da secção
        // criar/editar verifica se todos os campos que o tipo de empresa ncessita
        // estão corretamente preenchidos
        public void removeUpdate(DocumentEvent evento) {
            if(verificaCampos(caixaTipo.getSelectedItem().toString()) == 0) {
                botaoTerminarEditar.setEnabled(true);
                botaoTerminarCriar.setEnabled(true);
            } else {
                botaoTerminarEditar.setEnabled(false);
                botaoTerminarCriar.setEnabled(false);
            }
        }
        public void insertUpdate(DocumentEvent evento) {
            if(verificaCampos(caixaTipo.getSelectedItem().toString()) == 0) {
                botaoTerminarEditar.setEnabled(true);
                botaoTerminarCriar.setEnabled(true);
            } else {
                botaoTerminarEditar.setEnabled(false);
                botaoTerminarCriar.setEnabled(false);
            }
        }
    }

    /**
    * Nested Class InteracoesCaixa
    * Listener personalizado para as ações com as várias comboBox que o implementam
    */
    private class InteracoesCaixa implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evento) {
            requestFocusInWindow();
            if(evento.getSource() == caixaOrdenar) {
                int caixaSelect = caixaOrdenar.getSelectedIndex();
                if(caixaSelect == 10) {
                    caixaFiltrar.setSelectedIndex(1);
                }
                gestor.ordenarLista(caixaSelect);
                recarregarTabela();
            }
            if(evento.getSource() == caixaFiltrar) {
                recarregarTabela();
            }
            if(evento.getSource() == caixaTipo) {
                recarregarCriarEditar();
            }
            if(evento.getSource() == caixaEstilo) {
                int estilo = caixaEstilo.getSelectedIndex();
                mudarEstilo(estilo,false);
            }
            if(evento.getSource() == caixaTema) {
                int tema = caixaTema.getSelectedIndex();
                mudarTema(tema,false);
            }
            if(evento.getSource() == caixaTipo || evento.getSource() == caixaHorasLat || evento.getSource() == caixaMinutosLat ||
            evento.getSource() == caixaMinutosLat || evento.getSource() == caixaDirecaoLat || evento.getSource() == caixaHorasLong ||
            evento.getSource() == caixaMinutosLong || evento.getSource() == caixaSegundosLong || evento.getSource() == caixaDirecaoLong) {
                botaoTerminarEditar.setEnabled(true);
            }
        }
    }

    /**
    * Método que retira todos outros painéis de onde é possivel voltar ao menu
    * e adiciona o painel do menu
    */
    private void mostrarMenu() {
        remove(baseDados);
        remove(opcoes);
        add(menu,BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
    * Método que retira todos os paineis de onde é possível aceder à base de dados
    * e adiciona o painel da base de dados
    */
    private void mostrarBaseDados() {
        remove(menu);
        remove(criarEditar);
        add(baseDados,BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
    * Método que retira to painel de onde é possível aceder às opções (menu) e adiciona o painel
    * das opções
    */
    private void mostrarOpcoes() {
        remove(menu);
        add(opcoes,BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
    * Método que retira o painel de onde é possível aceder à secção de criar/editar (base de dados)
    * e adiciona o painel da secção de criar  além de também atribuir os valores default aos
    * campos de texto para criar a empresa e adicionar o respetivo botão de terminar a criação à secçãp
    */
    private void mostrarCriar()  {
        tabela.clearSelection();
        caixasDados.remove(botaoTerminarEditar);
        caixaTipo.setSelectedIndex(0);
        caixaTipo.setEnabled(true);
        campoNome.setText("A minha empresa");
        campoDistrito.setText("O meu distrito");
        campoIntUm.setText("1");
        campoIntDois.setText("1");
        campoIntTres.setText("1");
        campoIntQuatro.setText("1");
        campoDoubleUm.setText("1");
        campoDoubleDois.setText("1");
        campoDoubleTres.setText("1");
        campoDoubleQuatro.setText("1");
        caixaHorasLat.setSelectedIndex(0);
        caixaMinutosLat.setSelectedIndex(0);
        caixaSegundosLat.setSelectedIndex(0);
        caixaDirecaoLat.setSelectedIndex(0);
        caixaHorasLong.setSelectedIndex(0);
        caixaMinutosLong.setSelectedIndex(0);
        caixaSegundosLong.setSelectedIndex(0);
        caixaDirecaoLong.setSelectedIndex(0);
        campoFaturacaoMedia.setText("1");
        recarregarCriarEditar();
        posicao.gridx = 1;
        posicao.gridy = 99; // garantir que o botão fica no fim
        posicao.insets = new Insets(0,0,25,0);
        caixasDados.add(botaoTerminarCriar,posicao);
        remove(baseDados);
        add(criarEditar,BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
    * Método que retira o painel de onde é possível aceder à secção de criar/editar (base de dados)
    * e adiciona o painel da secção de editar além de também atribuir os valores atuais do tipo da empresa
    * que se escolheu editar aos campos de texto e adicionar o respetivo botão de terminar a edição à secção
    */
    private void mostrarEditar(int indexEmpresa) {
        Empresa empresa = gestor.getEmpresas().get(indexEmpresa);
        caixasDados.remove(botaoTerminarCriar);
        caixaTipo.setSelectedItem(empresa.getTipo());
        campoNome.setText(empresa.getNome());
        campoDistrito.setText(empresa.getDistrito());
        caixaHorasLat.setSelectedItem(empresa.getLocal().getLatitude().getHoras());
        caixaMinutosLat.setSelectedItem(empresa.getLocal().getLatitude().getMinutos());
        caixaSegundosLat.setSelectedItem(empresa.getLocal().getLatitude().getSegundos());
        caixaDirecaoLat.setSelectedItem(empresa.getLocal().getLatitude().getDirecao());
        caixaHorasLong.setSelectedItem(empresa.getLocal().getLongitude().getHoras());
        caixaMinutosLong.setSelectedItem(empresa.getLocal().getLongitude().getMinutos());
        caixaSegundosLong.setSelectedItem(empresa.getLocal().getLongitude().getSegundos());
        caixaDirecaoLong.setSelectedItem(empresa.getLocal().getLongitude().getDirecao());
        campoFaturacaoMedia.setText(String.valueOf(empresa.getFaturacaoMedia()));
        String tipo = empresa.getTipo();
        switch(tipo){
            case "Cafe" -> {
                Cafe cafe = (Cafe)empresa;
                campoIntUm.setText(String.valueOf(cafe.getNumEmpMesa()));
                campoDoubleUm.setText(String.valueOf(cafe.getSalarioMedAnual()));
                campoDoubleDois.setText(String.valueOf(cafe.getNumMedClientesDiario()));
                campoDoubleTres.setText(String.valueOf(cafe.getNumMedCafes()));
            }
            case "Pastelaria" -> {
                Pastelaria pastelaria = (Pastelaria)empresa;
                campoIntUm.setText(String.valueOf(pastelaria.getNumEmpMesa()));
                campoDoubleUm.setText(String.valueOf(pastelaria.getSalarioMedAnual()));
                campoDoubleDois.setText(String.valueOf(pastelaria.getNumMedClientesDiario()));
                campoDoubleTres.setText(String.valueOf(pastelaria.getNumMedBolos()));
            }
            case "Restaurante Fast-Food" -> {
                RestFastFood restFastFood = (RestFastFood)empresa;
                campoIntUm.setText(String.valueOf(restFastFood.getNumEmpMesa()));
                campoIntDois.setText(String.valueOf(restFastFood.getNumDiasFuncAnual()));
                campoIntTres.setText(String.valueOf(restFastFood.getNumMesasInteriores()));
                campoDoubleUm.setText(String.valueOf(restFastFood.getSalarioMedAnual()));
                campoDoubleDois.setText(String.valueOf(restFastFood.getNumMedClientesDiario()));
                campoDoubleTres.setText(String.valueOf(restFastFood.getNumMedClientesDrive()));
                campoDoubleQuatro.setText(String.valueOf(restFastFood.getFaturacaoMediaPClienteDrive()));
            }
            case "Restaurante Local" -> {
                RestLocal restLocal = (RestLocal)empresa;
                campoIntUm.setText(String.valueOf(restLocal.getNumEmpMesa()));
                campoIntDois.setText(String.valueOf(restLocal.getNumDiasFuncAnual()));
                campoIntTres.setText(String.valueOf(restLocal.getNumMesasInteriores()));
                campoIntQuatro.setText(String.valueOf(restLocal.getNumMesasEsplanadas()));
                campoDoubleUm.setText(String.valueOf(restLocal.getSalarioMedAnual()));
                campoDoubleDois.setText(String.valueOf(restLocal.getNumMedClientesDiario()));
                campoDoubleTres.setText(String.valueOf(restLocal.getCustoLiceAnualMesaEsp()));
            }
            case "Frutaria" -> {
                Frutaria frutaria = (Frutaria)empresa;
                campoIntUm.setText(String.valueOf(frutaria.getNumProdutos()));
                campoDoubleUm.setText(String.valueOf(frutaria.getCustoLimpezaAnual()));
            }
            case "Minimercado" -> {
                Minimercado minimercado = (Minimercado)empresa;
                campoDoubleUm.setText(String.valueOf(minimercado.getCustoLimpezaAnual()));
                campoDoubleDois.setText(String.valueOf(minimercado.getAreaCorredores()));
            }
            case "Supermercado" -> {
                Supermercado supermercado = (Supermercado)empresa;
                campoDoubleUm.setText(String.valueOf(supermercado.getCustoLimpezaAnual()));
                campoDoubleDois.setText(String.valueOf(supermercado.getAreaCorredores()));
            }
            case "Hipermercado"-> {
                Hipermercado hipermercado = (Hipermercado)empresa;
                campoDoubleUm.setText(String.valueOf(hipermercado.getCustoLimpezaAnual()));
                campoDoubleDois.setText(String.valueOf(hipermercado.getAreaCorredores()));
            }
            default -> {}
        }
        recarregarCriarEditar();
        posicao.gridx = 1;
        posicao.gridy = 99; // garantir que o botão fica no fim
        posicao.insets = new Insets(0,0,25,0);
        botaoTerminarEditar.setEnabled(false);
        caixasDados.add(botaoTerminarEditar,posicao);
        remove(baseDados);
        add(criarEditar,BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
    * Método que atualiza a quantidade e nome dos campos de texto da secção criar/editar em relação
    * ao que é necessário para o tipo de empresa que se encontra escolhido na comboBox
    */
    private void recarregarCriarEditar() {
        campoDoubleUm.setVisible(false);
        campoDoubleDois.setVisible(false);
        campoDoubleTres.setVisible(false);
        campoDoubleQuatro.setVisible(false);
        campoIntUm.setVisible(false);
        campoIntDois.setVisible(false);
        campoIntTres.setVisible(false);
        campoIntQuatro.setVisible(false);
        textoIntUm.setVisible(false);
        textoIntDois.setVisible(false);
        textoIntTres.setVisible(false);
        textoIntQuatro.setVisible(false);
        textoDoubleUm.setVisible(false);
        textoDoubleDois.setVisible(false);
        textoDoubleTres.setVisible(false);
        textoDoubleQuatro.setVisible(false);
        String tipo = caixaTipo.getSelectedItem().toString();
        switch(tipo){
            case "Cafe":
                textoIntUm.setText("Número de empregados de mesa:");
                textoDoubleUm.setText("Salário médio anual:");
                textoDoubleDois.setText("Número de clientes médio diário:");
                textoDoubleTres.setText("Número médio de cafés por dia:");
                textoIntUm.setVisible(true);
                textoDoubleUm.setVisible(true);
                textoDoubleDois.setVisible(true);
                textoDoubleTres.setVisible(true);
                campoIntUm.setVisible(true);
                campoDoubleUm.setVisible(true);
                campoDoubleDois.setVisible(true);
                campoDoubleTres.setVisible(true);
            break;
            case "Pastelaria":
                textoIntUm.setText("Número de empregados de mesa:");
                textoDoubleUm.setText("Salário médio anual:");
                textoDoubleDois.setText("Número de clientes médio diário:");
                textoDoubleTres.setText("Número médio de bolos por dia:");
                textoIntUm.setVisible(true);
                textoDoubleUm.setVisible(true);
                textoDoubleDois.setVisible(true);
                textoDoubleTres.setVisible(true);
                campoIntUm.setVisible(true);
                campoDoubleUm.setVisible(true);
                campoDoubleDois.setVisible(true);
                campoDoubleTres.setVisible(true);

            break;
            case "Restaurante Fast-Food":
                textoIntUm.setText("Número de empregados de mesa:");
                textoIntDois.setText("Número de dias de funcionamento anual:");
                textoIntTres.setText("Número de mesas interiores:");
                textoDoubleUm.setText("Salário médio anual:");
                textoDoubleDois.setText("Número de clientes médio diário:");
                textoDoubleTres.setText("Número de clientes médio drive:");
                textoDoubleQuatro.setText("Faturação média por cliente drive:");
                campoIntUm.setVisible(true);
                campoIntDois.setVisible(true);
                campoIntTres.setVisible(true);
                campoDoubleUm.setVisible(true);
                campoDoubleDois.setVisible(true);
                campoDoubleTres.setVisible(true);
                campoDoubleQuatro.setVisible(true);
                textoDoubleUm.setVisible(true);
                textoDoubleDois.setVisible(true);
                textoDoubleTres.setVisible(true);
                textoDoubleQuatro.setVisible(true);
                textoIntUm.setVisible(true);
                textoIntDois.setVisible(true);
                textoIntTres.setVisible(true);
            break;
            case "Restaurante Local":
                textoIntUm.setText("Número de empregados de mesa:");
                textoIntDois.setText("Número de dias de funcionamento anual:");
                textoIntTres.setText("                      Número de mesas interiores:");
                textoIntQuatro.setText("                Número de mesas de esplanada:");
                textoDoubleUm.setText("Salário médio anual:");
                textoDoubleDois.setText("Número de clientes médio diário:");
                textoDoubleTres.setText("Custo da licensa anual de esplanada:");
                campoIntUm.setVisible(true);
                campoIntDois.setVisible(true);
                campoIntTres.setVisible(true);
                campoIntQuatro.setVisible(true);
                campoDoubleUm.setVisible(true);
                campoDoubleDois.setVisible(true);
                campoDoubleTres.setVisible(true);
                textoDoubleUm.setVisible(true);
                textoDoubleDois.setVisible(true);
                textoDoubleTres.setVisible(true);
                textoIntUm.setVisible(true);
                textoIntDois.setVisible(true);
                textoIntTres.setVisible(true);
                textoIntQuatro.setVisible(true);
            break;
            case "Frutaria":
                textoDoubleUm.setText("Custo da limpeza anual:");
                textoIntUm.setText("                    Número de Produtos:");
                textoIntUm.setVisible(true);
                textoDoubleUm.setVisible(true);
                campoIntUm.setVisible(true);
                campoDoubleUm.setVisible(true);
            break;
            case "Minimercado":
            case "Supermercado":
            case "Hipermercado":
                textoDoubleUm.setText("Custo da limpeza anual:");
                textoDoubleDois.setText("                   Área de corredores:");
                campoDoubleUm.setVisible(true);
                campoDoubleDois.setVisible(true);
                textoDoubleUm.setVisible(true);
                textoDoubleDois.setVisible(true);
            break;
            default: break;
        }
        criarEditar.validate();
    }

    /**
    * Método que atualiza os elementos da tabela de acordo com o filtro selecionado.
    * Também é utilizado após ser escolhida uma opção de ordernar para atualizar a ordenação
    * das empresas demonstrada na tabela.
    */
    private void recarregarTabela() {
        int caixaSelect = caixaFiltrar.getSelectedIndex();
        ArrayList<Empresa> registo = gestor.getEmpresas();
        elementos.setRowCount(0);
        String[] tipos = {"Todas","Restauração","Pastelaria","Cafe","Restaurante","Restaurante Fast-Food","Restaurante Local","Mercearia","Frutaria","Mercado","Minimercado","Supermercado","Hipermercado"};
        if(caixaSelect != 1 && caixaOrdenar.getSelectedIndex() == 10) // se tentar mudar o tipo de filtração para outro que não restauração
        {                                                             // enquanto está selecionada a ordenação por num médio clientes dia
            caixaOrdenar.setSelectedIndex(0);               // remova essa ordenação e volta a ser por nome (pois o num medio de
            gestor.ordenarLista(0);                         // clientes apenas se aplica à restauração)
        }
        else if(caixaSelect==0) { // sem filtro, mostrar todas
            for (Empresa empresa : registo)
                if (empresa.lucro() > 0)
                    elementos.addRow(new Object[]{empresa.getNome(), empresa.getTipo(), empresa.getDistrito(), empresa.despesaAnual(), empresa.receitaAnual(), "Sim"});
                else
                    elementos.addRow(new Object[]{empresa.getNome(), empresa.getTipo(), empresa.getDistrito(), empresa.despesaAnual(), empresa.receitaAnual(), "Não"});
        }
        else{
            for (Empresa empresa : registo) { // com filtro, apenas mostrar as que a categoria, subcategoria, ou tipo, seja o selecionado
                if(empresa.getTipo().equals(tipos[caixaSelect]) || empresa.getCategoria().equals(tipos[caixaSelect]) || empresa.getSubCategoria().equals(tipos[caixaSelect])) {
                    if (empresa.lucro()>0)
                        elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),"Sim"});
                    else
                        elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),"Não"});
                }
            }
        }
    }

    /**
    * Método que define a aparência da janela bem como o estilo dos
    * componentes, a partir das opções guardadas
    */
    private void construirAparencia() {
        // definir o estilo da janela
        setTitle("StarThrive");
        setSize(720, 720);
        setUndecorated(opcoesGuardadas.isFullscreen());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setIconImage(new ImageIcon("src/resources/icon.png").getImage());
        // criar um listener personalizado para chamar a confirmação quando o utilizador tenta fechar, caso a opção esteja ativa
        if(opcoesGuardadas.isConfirmarSair()){
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent evento) {
                    sair();
                }
            });
        }
        else{
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        // criar cores e modelos personalizados para toda a interface através do UIManager
        int estilo = opcoesGuardadas.getEstilo();
        int tema =  opcoesGuardadas.getTema();
        mudarEstilo(estilo, true);
        mudarTema(tema, true);
        personalizarUI();
    }

    /**
    * Método que altera o estilo dos componentes
    * @param estilo O estilo a aplicar
    * @param serConstruida Se está a ser chamado durante a construção da interface
    * (se não estiver, é necessário atualizar certos componentes para mostrar as alterações)
    */
    private void mudarEstilo(int estilo, boolean serConstruida) {
        try {
            if(estilo == 0)
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            else
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if(!serConstruida){
                SwingUtilities.updateComponentTreeUI(UserInterface.this);
                SwingUtilities.updateComponentTreeUI(menu);
                SwingUtilities.updateComponentTreeUI(baseDados);
                SwingUtilities.updateComponentTreeUI(opcoes);
                SwingUtilities.updateComponentTreeUI(criarEditar);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException| UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível implementar o estilo, será utilizado o default!",null, JOptionPane.WARNING_MESSAGE);
            opcoesGuardadas.setEstilo(0);
        }
        opcoesGuardadas.setEstilo(estilo);
        guardarOpcoes();
    }

    /**
    * Método que altera o tema da interface
    * @param tema O tema a aplicar
    * @param serConstruida Se está a ser chamado durante a construção da interface
    * (se não estiver, é necessário recriar certos componentes para mostrar as alterações)
    */
    private void mudarTema(int tema, boolean serConstruida) {
        Color cinza = new Color(33,33,33);
        if(tema == 0) {
            UIManager.put("Panel.background",Color.WHITE); // light mode
            UIManager.put("Label.foreground",cinza);
            UIManager.put("CheckBox.background",Color.WHITE);
            UIManager.put("CheckBox.foreground",cinza);
            UIManager.put("Table.background",Color.WHITE);
            UIManager.put("Table.foreground",Color.BLACK);
        }
        else {
            UIManager.put("Panel.background",cinza); //dark mode
            UIManager.put("Label.foreground",Color.WHITE);
            UIManager.put("CheckBox.background",cinza);
            UIManager.put("CheckBox.foreground",Color.WHITE);
            UIManager.put("Table.background",cinza);
            UIManager.put("Table.foreground",Color.WHITE);
        }
        opcoesGuardadas.setTema(tema);
        guardarOpcoes();
        if(!serConstruida)  // reconstrói tudo para aplicar o tema
        {
            construirMenu();
            construirVoltar();
            construirBaseDados();
            remove(opcoes);
            construirOpcoes();
            add(opcoes,BorderLayout.CENTER);
            UIManager.put("Panel.background",Color.WHITE);
            validate();
            repaint();
        }
    }

    /**
    * Método que aplica os partes do UI personalizadas que são comuns a todos
    * os temas e estilos à interface
    */
    private void personalizarUI() {
        Color invisivel = new Color(0,0,0,0);
        Color azul =  new Color(118,221,221);
        UIManager.put("Button.focus",invisivel);
        UIManager.put("Button.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("Label.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("CheckBox.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("CheckBox.focus",invisivel);
        UIManager.put("ComboBox.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("TextField.font",new Font("Arial", Font.BOLD, 15));
        UIManager.put("ComboBox.selectionBackground",new Color(118,221,221));
        UIManager.put("ComboBox.selectionForeground",Color.BLACK);
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
        UIManager.put("Table.selectionBackground",azul);
        UIManager.put("Table.selectionForeground",Color.BLACK);
        UIManager.put("Table.focusCellBackground",azul);
        UIManager.put("Table.focusCellForeground",Color.BLACK);
    }

    /**
    * Método que verifica se a opção de autoguardar está ativa, se sim guarda os dados
    * se não, atualiza que existem alterações por guardar e ativa o botão de guardar para o permitir
    */
    private void autoGuardar() {
        if(caixaAutoGuardar.isSelected()) {
            gestor.guardarDados();
        }
        else
        {
            alteracoesPorGuardar = true;
            botaoGuardar.setEnabled(true);
            botaoGuardar.setToolTipText(null);
        }
    }

    /**
    * Método que carrega os dados das empresas do ficheiro de objetos utilizando o método carregarDadosObjeto da classe Gestor
    * e mostra um popup com a mensagem de erro devolvida pelo mesmo caso ocorra um
    * @see GestorEmpresas#carregarDadosObjeto()
    */
    private void carregarDados() {
        String informacao = gestor.carregarDadosObjeto();
        // Se os dados forem carregados de um ficheiro objeto não se mostra a mensagem pois esta é a situação ótima de
        // carregamento dos dados, sendo a que aconteceria mais vezes durante o uso normal do programa iria se tornar irritante
        if(informacao.compareTo("Os dados foram carregados do ficheiro de objetos com sucesso!") != 0) {
            UIManager.put("Panel.background",Color.WHITE);
            JOptionPane.showMessageDialog(null, informacao,null, JOptionPane.ERROR_MESSAGE);
            if(opcoesGuardadas.getTema() != 0)
            UIManager.put("Panel.background",new Color(33,33,33));
        }
    }

    /**
    * Método que cria o painel do menu e todos os seus componentes
    */
    private void construirMenu() {
        menu = new JPanel();
        menu.setLayout(new GridBagLayout());
        JLabel textoTitulo1 = new JLabel("St");
        JLabel textoTituloImagem = new JLabel(new ImageIcon("src/resources/star.gif"));
        JLabel textoTitulo2 = new JLabel("rThrive");
        textoTitulo1.setFont(new Font("Arial", Font.BOLD, 100));
        textoTitulo2.setFont(new Font("Arial", Font.BOLD, 100));
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,40,400);
        menu.add(textoTitulo1, posicao);
        posicao.insets = new Insets(0,0,70,200);
        menu.add(textoTituloImagem, posicao);
        posicao.insets = new Insets(0,230,40,0);
        menu.add(textoTitulo2, posicao);
        posicao.insets = new Insets(0,0,40,0);
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

    /**
    * Método que cria os painéis com os respetivos botões para voltar
    * às secções anteriores
    */
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

    /**
    * Método que cria o painel com os respetivos botões para efetuar
    * operações sobre as empresas da tabela
    */
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


    /**
    * Método que cria o painel e a respetiva tabela para a secção da base de dados
    */
    private void construirListar() {
        listar = new JPanel();
        listar.setLayout(new GridBagLayout());
        // por default criamos a tabela a mostrar todas as empresas na primeira vez que o utlizadar aceder à base de dados
        String[] colunas = {"Nome", "Tipo", "Distrito", "Despesa", "Receita", "Lucro"};
        elementos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int fila, int coluna) {
                return false; //return (coluna == 0 || coluna == 1 || coluna == 2 || coluna == 3 || coluna == 4);
            }
        };
        ArrayList<Empresa> registo = gestor.getEmpresas();
        gestor.ordenarLista(0);
        for (Empresa empresa : registo) {
            if (empresa.lucro()> 0)
                elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),"Sim"});
            else
                elementos.addRow(new Object[]{empresa.getNome(),empresa.getTipo(),empresa.getDistrito(),empresa.despesaAnual(),empresa.receitaAnual(),"Não"});
        }
		tabela = new JTable(elementos) {
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
        {
            // Fazer um clique na tabela selecionar a linha toda e não apenas uma célula
            JComponent Jcomponente = (JComponent)super.prepareRenderer(renderer, row, column);
            if (isRowSelected(row))
                Jcomponente.setBorder(null);
            return Jcomponente;
        }};
        tabela.setFillsViewportHeight(true);
        DefaultTableCellRenderer justificarCentro = new DefaultTableCellRenderer();
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setOpaque(false);
        header.setBackground(new Color(225,225,225));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        justificarCentro.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<6;x++) {
            tabela.getColumnModel().getColumn(x).setCellRenderer(justificarCentro);
        }
        tabela.setFont(new Font("Arial", Font.PLAIN, 15));
        tabela.setRowHeight(25);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroller = new JScrollPane(tabela);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.fill = GridBagConstraints.BOTH;
        posicao.weightx = 1;
        posicao.insets = new Insets(0,0,0,0);
        listar.add(scroller,posicao);
        posicao.fill = GridBagConstraints.CENTER;
    }

    /**
    * Método que cria o painel com os respetivos botões para filtrar
    * e ordenar as empresas da tabela
    */
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
        caixaFiltrar = new JComboBox<>(filtros);
        caixaFiltrar.addActionListener(selecElemento);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,10,40,0);
        JLabel textoFiltrar = new JLabel("Filtrar:");
		textoFiltrar.setLabelFor( caixaFiltrar );
        filtrar.add(caixaFiltrar,posicao);
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,42,0);
        filtrar.add(textoFiltrar,posicao);
        String[] ordem = {"Nome ▲","Nome ▼","Distrito ▲","Distrito ▼","Despesa anual ▲","Despesa anual ▼","Receita anual ▲","Receita anual ▼","Lucro ▲","Lucro ▼","Média de clientes por dia ▼"};        caixaOrdenar = new JComboBox<>(ordem);
        caixaOrdenar.addActionListener(selecElemento);
        posicao.gridx = 3;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,10,40,75);
        JLabel textoOrdenar = new JLabel("Ordenar:");
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
        botaoGuardar.setToolTipText("Não há alterações para guardar!");
        posicao.insets = new Insets(0,40,40,0);
        botaoGuardar.addActionListener(premirBotao);
        filtrar.add(botaoGuardar, posicao);
    }

    /**
    * Método que cria o painel com os respetivos campos de textos e comboBox
    * para a criação e edição das empresas
    */
    private void construirCriarEditar(){
        criarEditar = new JPanel();
        criarEditar.setLayout(new GridBagLayout());
        caixasDados = new JPanel();
        caixasDados.setLayout(new GridBagLayout());
        JLabel textoTipo = new JLabel("Tipo:");
        String[] tipos = {"Cafe","Pastelaria","Restaurante Fast-Food","Restaurante Local","Frutaria","Minimercado","Hipermercado","Supermercado"};
        caixaTipo = new JComboBox<String>(tipos);
        caixaTipo.addActionListener(selecElemento);
        textoTipo.setLabelFor(caixaTipo);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,20,275);
        caixasDados.add(textoTipo,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(caixaTipo,posicao);
        JLabel textoNome = new JLabel("Nome:");
        campoNome = new JTextField(14);
        campoNome.getDocument().addDocumentListener(escreverCampo);
        textoNome.setLabelFor(campoNome);
        posicao.gridx = 1;
        posicao.gridy = 1;
        posicao.insets = new Insets(0,0,20,275);
        caixasDados.add(textoNome,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoNome,posicao);
        JLabel textoDistrito = new JLabel("Distrito:");
        campoDistrito = new JTextField(14);
        campoDistrito.getDocument().addDocumentListener(escreverCampo);
        textoDistrito.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 2;
        posicao.insets = new Insets(0,0,20,275);
        caixasDados.add(textoDistrito,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoDistrito,posicao);
        Integer[] lista0a59 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,25,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59};
        Integer[] lista0a179 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,25,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,125,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179};
        caixaHorasLong = new JComboBox<Integer>(lista0a179);
        caixaMinutosLong = new JComboBox<Integer>(lista0a59);
        caixaSegundosLong = new JComboBox<Integer>(lista0a59);
        Character[] listaDirecoesLong = {'W','E'};
        caixaDirecaoLong = new JComboBox<Character>(listaDirecoesLong);
        Integer[] lista0a89 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,25,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89};
        caixaHorasLat = new JComboBox<Integer>(lista0a89);
        caixaMinutosLat = new JComboBox<Integer>(lista0a59);
        caixaSegundosLat = new JComboBox<Integer>(lista0a59);
        Character[] listaDirecoesLat = {'N','S'};
        caixaDirecaoLat = new JComboBox<Character>(listaDirecoesLat);
        caixaHorasLong.addActionListener(selecElemento);
        caixaMinutosLong.addActionListener(selecElemento);
        caixaSegundosLong.addActionListener(selecElemento);
        caixaDirecaoLong.addActionListener(selecElemento);
        caixaHorasLat.addActionListener(selecElemento);
        caixaMinutosLat.addActionListener(selecElemento);
        caixaSegundosLat.addActionListener(selecElemento);
        caixaDirecaoLat.addActionListener(selecElemento);
        posicao.gridx = 1;
        posicao.gridy = 3;
        JLabel textoLocalizacao = new JLabel("Localização:");
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(textoLocalizacao,posicao);
        posicao.gridx = 1;
        posicao.gridy = 4;
        posicao.insets = new Insets(0,0,20,240);
        caixasDados.add(caixaHorasLong,posicao);
        JLabel textoHorasLong= new JLabel("°");
        textoHorasLong.setFont(new Font("Arial", Font.BOLD, 25));
        textoHorasLong.setLabelFor(caixaHorasLong);
        posicao.insets = new Insets(0,0,20,170);
        caixasDados.add(textoHorasLong,posicao);
        JLabel textoMinutosLong= new JLabel("'");
        textoMinutosLong.setFont(new Font("Arial", Font.BOLD, 25));
        textoMinutosLong.setLabelFor(caixaMinutosLong);
        posicao.insets = new Insets(0,0,20,20);
        caixasDados.add(textoMinutosLong,posicao);
        posicao.insets = new Insets(0,0,20,80);
        caixasDados.add(caixaMinutosLong,posicao);
        JLabel textoSegundosLong= new JLabel("\"");
        textoSegundosLong.setFont(new Font("Arial", Font.BOLD, 25));
        textoSegundosLong.setLabelFor(caixaSegundosLong);
        posicao.insets = new Insets(0,140,20,0);
        caixasDados.add(textoSegundosLong,posicao);
        posicao.insets = new Insets(0,80,20,0);
        caixasDados.add(caixaSegundosLong,posicao);
        JLabel textoDirecaoLong= new JLabel("→ Longitude");
        textoDirecaoLong.setLabelFor(caixaDirecaoLong);
        posicao.insets = new Insets(0,385,20,0);
        caixasDados.add(textoDirecaoLong,posicao);
        posicao.insets = new Insets(0,240,20,0);
        caixasDados.add(caixaDirecaoLong,posicao);
        posicao.gridx = 1;
        posicao.gridy = 5;
        posicao.insets = new Insets(0,0,20,240);
        caixasDados.add(caixaHorasLat,posicao);
        JLabel textoHorasLat = new JLabel("°");
        textoHorasLat.setFont(new Font("Arial", Font.BOLD, 25));
        textoHorasLat.setLabelFor(caixaHorasLat);
        posicao.insets = new Insets(0,0,20,180);
        caixasDados.add(textoHorasLat,posicao);
        JLabel textoMinutosLat = new JLabel("'");
        textoMinutosLat.setFont(new Font("Arial", Font.BOLD, 25));
        textoMinutosLat.setLabelFor(caixaMinutosLat);
        posicao.insets = new Insets(0,0,20,20);
        caixasDados.add(textoMinutosLat,posicao);
        posicao.insets = new Insets(0,0,20,80);
        caixasDados.add(caixaMinutosLat,posicao);
        JLabel textoSegundosLat = new JLabel("\"");
        textoSegundosLat.setFont(new Font("Arial", Font.BOLD, 25));
        textoSegundosLat.setLabelFor(caixaSegundosLat);
        posicao.insets = new Insets(0,140,20,0);
        caixasDados.add(textoSegundosLat,posicao);
        posicao.insets = new Insets(0,80,20,0);
        caixasDados.add(caixaSegundosLat,posicao);
        JLabel textoDirecaoLat = new JLabel("→ Latitude");
        textoDirecaoLat.setLabelFor(caixaDirecaoLat);
        posicao.insets = new Insets(0,365,20,0);
        caixasDados.add(textoDirecaoLat,posicao);
        posicao.insets = new Insets(0,240,20,0);
        caixasDados.add(caixaDirecaoLat,posicao);
        JLabel textoFaturacaoMedia = new JLabel("Faturação média:");
        campoFaturacaoMedia = new JTextField(14);
        campoFaturacaoMedia.getDocument().addDocumentListener(escreverCampo);
        textoFaturacaoMedia.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 6;
        posicao.insets = new Insets(5,0,20,350);
        caixasDados.add(textoFaturacaoMedia,posicao);
        posicao.insets = new Insets(5,0,20,0);
        caixasDados.add(campoFaturacaoMedia,posicao);
        textoDoubleUm = new JLabel("");
        campoDoubleUm = new JTextField(14);
        campoDoubleUm.getDocument().addDocumentListener(escreverCampo);
        textoDoubleUm.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 7;
        posicao.insets = new Insets(0,0,20,390);
        caixasDados.add(textoDoubleUm,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoDoubleUm,posicao);
        textoDoubleDois = new JLabel("");
        campoDoubleDois = new JTextField(14);
        campoDoubleDois.getDocument().addDocumentListener(escreverCampo);
        textoDoubleDois.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 8;
        posicao.insets = new Insets(0,0,20,475);
        caixasDados.add(textoDoubleDois,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoDoubleDois,posicao);
        textoDoubleTres = new JLabel("");
        campoDoubleTres = new JTextField(14);
        campoDoubleTres.getDocument().addDocumentListener(escreverCampo);
        textoDoubleTres.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 9;
        posicao.insets = new Insets(0,0,20,485);
        caixasDados.add(textoDoubleTres,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoDoubleTres,posicao);
        textoDoubleQuatro = new JLabel("");
        campoDoubleQuatro = new JTextField(14);
        campoDoubleQuatro.getDocument().addDocumentListener(escreverCampo);
        textoDoubleQuatro.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 10;
        posicao.insets = new Insets(0,0,20,475);
        caixasDados.add(textoDoubleQuatro,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoDoubleQuatro,posicao);
        textoIntUm = new JLabel("");
        campoIntUm = new JTextField(14);
        campoIntUm.getDocument().addDocumentListener(escreverCampo);
        textoIntUm.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 11;
        posicao.insets = new Insets(0,0,20,470);
        caixasDados.add(textoIntUm,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoIntUm,posicao);
        textoIntDois = new JLabel("");
        campoIntDois = new JTextField(14);
        campoIntDois.getDocument().addDocumentListener(escreverCampo);
        textoIntDois.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 12;
        posicao.insets = new Insets(0,0,20,550);
        caixasDados.add(textoIntDois,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoIntDois,posicao);
        textoIntTres = new JLabel("");
        campoIntTres = new JTextField(14);
        campoIntTres.getDocument().addDocumentListener(escreverCampo);
        textoIntTres.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 13;
        posicao.insets = new Insets(0,0,20,550);
        caixasDados.add(textoIntTres,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoIntTres,posicao);
        textoIntQuatro = new JLabel("");
        campoIntQuatro = new JTextField(14);
        campoIntQuatro.getDocument().addDocumentListener(escreverCampo);
        textoIntQuatro.setLabelFor(campoDistrito);
        posicao.gridx = 1;
        posicao.gridy = 14;
        posicao.insets = new Insets(0,0,20,550);
        caixasDados.add(textoIntQuatro,posicao);
        posicao.insets = new Insets(0,0,20,0);
        caixasDados.add(campoIntQuatro,posicao);
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,0,0);
        criarEditar.add(voltarCE,posicao);
        posicao.gridx = 1;
        posicao.gridy = 0;
        posicao.anchor = GridBagConstraints.WEST;
        criarEditar.add(caixasDados,posicao);
        posicao.anchor = GridBagConstraints.CENTER;
        botaoTerminarCriar = new JButton("Criar");
        botaoTerminarCriar.addActionListener(premirBotao);
        botaoTerminarEditar = new JButton("Aplicar alterações");
        botaoTerminarEditar.addActionListener(premirBotao);
    }

    /**
    * Método que verifica se um campo de texto contém informação válida (não estar vazio), se não
    * coloca o a vermelho e define uma mensagem a avisar o utilizador caso este
    * coloque o rato em cima do campo
    * @param campo O campo a verificar
    * @return 0 se o campo estiver válido, 1 se estiver inválido
    */
    private int verificaCampoString(JTextField campo){
        if(campo.getText().isEmpty()){
            campo.setToolTipText("Este campo não pode ficar vazio!");
            campo.setBackground(new Color(255,114,111));
            return 1;
        } else {
            campo.setBackground(Color.WHITE);
            campo.setToolTipText(null);
            return 0;
        }
    }

    /**
    * Método que verifica se um campo de texto contém informação válida (ser um double), se não
    * coloca o a vermelho e define uma mensagem a avisar o utilizador caso este
    * coloque o rato em cima do campo
    * @param campo O campo a verificar
    * @return 0 se o campo estiver válido, 1 se estiver inválido
    */
    private int verificaCampoDouble(JTextField campo){
        try{
            double valor = Double.parseDouble(campo.getText());
            if(valor >= 0) {
                campo.setBackground(Color.WHITE);
                campo.setToolTipText(null);
                return 0;
            }
            campo.setToolTipText("Este campo apenas aceita valores númericos iguais ou superiores a zero!");
            campo.setBackground(new Color(255,114,111));
            return 1;
        } catch (NumberFormatException ex) {
            campo.setToolTipText("Este campo apenas aceita valores númericos iguais ou superiores a zero!");
            campo.setBackground(new Color(255,114,111));
            return 1;
        }
    }

    /**
    * Método que verifica se um campo de texto contém informação válida (ser um inteiro), se não
    * coloca o a vermelho e define uma mensagem a avisar o utilizador caso este
    * coloque o rato em cima do campo
    * @param campo O campo a verificar
    * @return 0 se o campo estiver válido, 1 se estiver inválido
    */
    private int verificaCampoInt(JTextField campo){
        try{
            int valor = Integer.parseInt(campo.getText());
            if(valor >= 0) {
                campo.setBackground(Color.WHITE);
                campo.setToolTipText(null);
                return 0;
            }
            campo.setToolTipText("Este campo apenas aceita valores númericos inteiros iguais ou superiores a zero!");
            campo.setBackground(new Color(255,114,111));
            return 1;
        } catch (NumberFormatException ex) {
            campo.setToolTipText("Este campo apenas aceita valores númericos inteiros iguais ou superiores a zero!");
            campo.setBackground(new Color(255,114,111));
            return 1;
        }
    }

    /**
    * Método que verifica se todos os campos que este tipo de empresa necessita estão
    * corretamente preenchidos utilizando os verificadores para os 3 diferentes tipos
    * @see #verificaCampoString
    * @see #verificaCampoDouble
    * @see #verificaCampoInt
    * @param tipoSelecionado O tipo de empresa selecionado na caixa
    * @return O número de campos inválidos (devolve 0 se nenhum)
    */
    private int verificaCampos(String tipoSelecionado){
        switch (tipoSelecionado) {
            case "Cafe":
            case "Pastelaria":
                return verificaCampoDouble(campoFaturacaoMedia) + verificaCampoString(campoNome) + verificaCampoString(campoDistrito) +  verificaCampoDouble(campoDoubleUm) + verificaCampoDouble(campoDoubleDois) + verificaCampoDouble(campoDoubleTres) +
                verificaCampoInt(campoIntUm);

            case "Restaurante Fast-Food":
                return verificaCampoDouble(campoFaturacaoMedia) + verificaCampoString(campoNome) + verificaCampoString(campoDistrito) +  verificaCampoDouble(campoDoubleUm) + verificaCampoDouble(campoDoubleDois) + verificaCampoDouble(campoDoubleTres) +
                verificaCampoDouble(campoDoubleQuatro) + verificaCampoInt(campoIntUm) + verificaCampoInt(campoIntDois) +
                verificaCampoInt(campoIntTres) + verificaCampoInt(campoIntQuatro);

            case "Restaurante Local":
                return verificaCampoDouble(campoFaturacaoMedia) + verificaCampoString(campoNome) + verificaCampoString(campoDistrito) +  verificaCampoDouble(campoDoubleUm) + verificaCampoDouble(campoDoubleDois) + verificaCampoDouble(campoDoubleTres) +
                verificaCampoInt(campoIntUm) + verificaCampoInt(campoIntDois) + verificaCampoInt(campoIntTres) +
                verificaCampoInt(campoIntQuatro);

            case "Frutaria":
                return verificaCampoDouble(campoFaturacaoMedia) + verificaCampoString(campoNome) + verificaCampoString(campoDistrito) +  verificaCampoDouble(campoDoubleUm) + verificaCampoInt(campoIntUm);

            case "Minimercado":
            case "Supermercado":
            case "Hipermercado":
                return verificaCampoDouble(campoFaturacaoMedia) + verificaCampoString(campoNome) + verificaCampoString(campoDistrito) +  verificaCampoDouble(campoDoubleUm) + verificaCampoDouble(campoDoubleDois);

            default:
                return 0;
        }
    }

     /**
    * Método que cria o painel da base de dados utilizando os outros painéis
    * que a constitutem
    * @see #construirFiltrar
    * @see #construirGerir
    * @see #construirListar
    * @see #construirCriarEditar
    */
    private void construirBaseDados() {
        // construir o painel com a tabela
        construirFiltrar();
        // construir o painel com os botões gerir as empresas (criar/editar/apagar)
        construirGerir();
        // construir o painel onde irá ficar a interface para criar ou editar uma empresa
        construirListar();
        // construir o painel para filtrar as empresas com as diferentes opções do enunciado e o botao de guardar
        construirCriarEditar();
        // constroi o painel com todos os outros, execeto o CriarEditar, que apenas é chamado quando se escolhe criar ou editar a partir da bd
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
        posicao.fill = GridBagConstraints.NONE;
    }

     /**
    * Método que carrega as opções da interface personalizadas pelo utlizador
    * para a variável criada para as guardar, caso ocorra um erro ou o ficheiro
    * não existe (nunca foram alteradas as opções) utiliza as default
    * @see #opcoesGuardadas
    */
    private void carregarOpcoes() {
        File ficheiro = new File("src/data/Opcoes.dat");
        if(ficheiro.exists() && ficheiro.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(ficheiro);
                ObjectInputStream ois = new ObjectInputStream(fis);
                opcoesGuardadas = (Opcoes)ois.readObject();
                ois.close();
            } catch (IOException | ClassNotFoundException ex ) {
                opcoesGuardadas = new Opcoes();
            }
        }
        else
            opcoesGuardadas = new Opcoes();
    }

    /**
     * Método que guarda as opções da interface personalizadas pelo utlizador
     * no respetivo ficheiro de objetos, caso ocorra um erro mostraa um popup
     * com uma mensagem a descrevê-lo
     */
    private void guardarOpcoes() {
        File ficheiro = new File("src/data/Opcoes.dat");
        String informacao;
        try {
            FileOutputStream fos = new FileOutputStream(ficheiro);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(opcoesGuardadas);
            oos.close();
            return; //"As alterações foram guardadas com sucesso!";
        } catch (FileNotFoundException ex) {
            informacao = "Não foi possível guardar as mudanças nas opções, ocorreu um erro a criar o ficheiro!";
        } catch (IOException ex) {
            informacao = "Não foi possível guardar as mudanças nas opções, ocorreu um erro durante a escrita para o ficheiro!";
        }
        JOptionPane.showMessageDialog(null, informacao,null, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Método que constroi o Painel com os respetivos botões, comboBox e
     * checkBox para personalizar as opções da interface
     */
    private void construirOpcoes()
    {
        opcoes = new JPanel();
        opcoes.setLayout(new GridBagLayout());
        caixasOpcoes = new JPanel();
        caixasOpcoes.setLayout(new GridBagLayout());
        JLabel textoEstilo = new JLabel("Estilo:");
        String[] estilos = {"Clássico","Nativo do Sistema"};
        caixaEstilo = new JComboBox<>(estilos);
        caixaEstilo.setSelectedIndex(opcoesGuardadas.getEstilo());
        caixaEstilo.addActionListener(selecElemento);
        textoEstilo.setLabelFor(caixaEstilo);
        posicao.gridx = 1;
        posicao.gridy = 1;
        posicao.insets = new Insets(0,5,25,350);
        posicao.anchor = GridBagConstraints.WEST;
        caixasOpcoes.add(textoEstilo,posicao);
        posicao.insets = new Insets(0,70,25,0);
        caixasOpcoes.add(caixaEstilo,posicao);
        JLabel textoTema = new JLabel("Tema:");
        String[] temas = {"Claro","Escuro"};
        caixaTema = new JComboBox<>(temas);
        caixaTema.setSelectedIndex(opcoesGuardadas.getTema());
        caixaTema.addActionListener(selecElemento);
        textoTema.setLabelFor(caixaTema);
        posicao.gridx = 1;
        posicao.gridy = 2;
        posicao.insets = new Insets(0,5,25,350);
        caixasOpcoes.add(textoTema,posicao);
        posicao.insets = new Insets(0,70,25,300);
        caixasOpcoes.add(caixaTema,posicao);
        caixaConfirmar = new JCheckBox("Confirmar antes de sair:   ");
        caixaConfirmar.setHorizontalTextPosition(SwingConstants.LEFT);
        caixaConfirmar.setSelected(opcoesGuardadas.isConfirmarSair());
        caixaConfirmar.setSize(new Dimension(50, HEIGHT));
        caixaConfirmar.addActionListener(premirBotao);
        posicao.gridx = 1;
        posicao.gridy = 3;
        posicao.insets = new Insets(0,0,25,300);
        caixasOpcoes.add(caixaConfirmar,posicao);
        caixaAutoGuardar = new JCheckBox("Guardar automaticamente depois de criar, editar ou apagar:   ");
        caixaAutoGuardar.setHorizontalTextPosition(SwingConstants.LEFT);
        caixaAutoGuardar.setSelected(opcoesGuardadas.isAutoGuardar());
        caixaAutoGuardar.addActionListener(premirBotao);
        posicao.gridx = 1;
        posicao.gridy = 4;
        posicao.insets = new Insets(0,0,25,300);
        caixasOpcoes.add(caixaAutoGuardar,posicao);
        caixaFullscreen = new JCheckBox("Fullscreen:   ");
        caixaFullscreen.setHorizontalTextPosition(SwingConstants.LEFT);
        caixaFullscreen.setSelected(opcoesGuardadas.isFullscreen());
        caixaFullscreen.addActionListener(premirBotao);
        posicao.gridx = 1;
        posicao.gridy = 5;
        posicao.insets = new Insets(0,0,25,300);
        caixasOpcoes.add(caixaFullscreen,posicao);
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.insets = new Insets(0,0,0,0);
        posicao.anchor = GridBagConstraints.CENTER;
        opcoes.add(voltarOpc,posicao);
        posicao.gridx = 1;
        posicao.gridy = 0;
        opcoes.add(caixasOpcoes,posicao);
    }

     /**
     * Método chamado pelo listener, caso esteja ativa a confirmação ao sair, mostra
     * um popup diferente dependendo se o utilizador tiver ou não alterações por guardar
     * e permite ainda guardar, não guardar e cancelar a ação de sair antes de terminar
     */
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
