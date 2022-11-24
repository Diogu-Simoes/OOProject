package src;

public abstract class Empresa {
    private String nome;
    protected String tipo;
    protected String subCategoria;
    protected String categoria;
    private Localizacao local;
    private String distrito;
    private float faturacaoMedia;

    public Empresa(String nome,Localizacao local, String distrito, float faturacaoMedia) {
        this.nome = nome;
        this.tipo = "Empresa";
        this.subCategoria = "Nenhuma";
        this.categoria = "Nenhuma";
        this.local = local;
        this.distrito = distrito;
        this.faturacaoMedia = faturacaoMedia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public Localizacao getLocal() {
        return local;
    }

    public void setLocal(Localizacao local) {
        this.local = local;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public float getFaturacaoMedia() {
        return faturacaoMedia;
    }

    public void setFaturacaoMedia(float faturacaoMedia) {
        this.faturacaoMedia = faturacaoMedia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    abstract public float despesaAnual();
    abstract public float receitaAnual();

    public float lucro(){
        return receitaAnual() - despesaAnual();
    }

    public String lucroSimNao()
    {
        Float lucro = receitaAnual() - despesaAnual();
        if(lucro > 0)
            return "Sim | " + lucro;
        else
            return "Não | " + lucro;
    }

    @Override
    public String toString() {
        return nome;
    }
}