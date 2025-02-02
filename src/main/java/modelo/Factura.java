package modelo;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class Factura {
    private IntegerProperty id;
    private ObjectProperty<Timestamp> fecha;
    private DoubleProperty montoFinal;
    private StringProperty tipo;

    public Factura(int id, Timestamp fecha, Double montoFinal, String tipo){
        this.id = new SimpleIntegerProperty(id);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.montoFinal = new SimpleDoubleProperty(montoFinal);
        this.tipo = new SimpleStringProperty(tipo);
    }


    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<Timestamp> fechaProperty() { return fecha; }
    public DoubleProperty montoFinalProperty() { return montoFinal; }
    public StringProperty tipoProperty() { return tipo; }

    public int getId() { return id.get(); }
    public Timestamp getFecha() { return fecha.get(); }
    public Double getMontoFinal() { return montoFinal.get(); }
    public String getTipo() { return tipo.get(); }


}
