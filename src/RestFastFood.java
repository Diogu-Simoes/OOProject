package src;

public class RestFastFood extends Restaurante {
    private double numMedClientesDrive;
    private double FaturacaoMediaPClienteDrive;

    public RestFastFood(String nome, Localizacao local, String distrito, double faturacaoMedia, int numEmpMesa, double salarioMedAnual, double numMedClientesDiario, int numDiasFuncAnual, int numMesasInteriores, double numMedClientesDrive, double faturacaoMediaPClienteDrive) {
        super(nome, local, distrito, faturacaoMedia, numEmpMesa, salarioMedAnual, numMedClientesDiario, numDiasFuncAnual, numMesasInteriores);
        this.numMedClientesDrive = numMedClientesDrive;
        this.FaturacaoMediaPClienteDrive = faturacaoMediaPClienteDrive;
        this.tipo = "Restaurante Fast-Food";
    }
    public double getFaturacaoMediaPClienteDrive() {
        return FaturacaoMediaPClienteDrive;
    }
    public void setFaturacaoMediaPClienteDrive(double faturacaoMediaPClienteDrive) {
        this.FaturacaoMediaPClienteDrive = faturacaoMediaPClienteDrive;
    }
    public double getNumMedClientesDrive() {
        return numMedClientesDrive;
    }
    public void setNumMedClientesDrive(double numMedClientesDrive) {
        this.numMedClientesDrive = numMedClientesDrive;
    }
    @Override
    public double receitaAnual() {
        return (getNumMesasInteriores()*getFaturacaoMedia()+numMedClientesDrive*FaturacaoMediaPClienteDrive)*getNumDiasFuncAnual();
    }//FaturacaoMedia por cada mesa por dia
    @Override
    public double despesaAnual() {return getNumEmpMesa()*getSalarioMedAnual();}

}
