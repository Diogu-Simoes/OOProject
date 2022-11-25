package src;

abstract class Mercado extends Mercearia{
    private double areaCorredores;

    public Mercado(String nome, Localizacao local, String distrito, double faturacaoMedia, double custoLimpezaAnual, double areaCorredores) {
        super(nome, local, distrito, faturacaoMedia, custoLimpezaAnual);
        this.areaCorredores = areaCorredores;
        this.tipo = "Mercado";
        this.subCategoria = "Mercado";
    }

    public double getAreaCorredores() {
        return areaCorredores;
    }

    public void setAreaCorredores(double areaCorredores) {
        this.areaCorredores = areaCorredores;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public double receitaAnual() {
        return areaCorredores*getFaturacaoMedia();}//faturacaoMedia é a faturacao media anual por m^2

}
