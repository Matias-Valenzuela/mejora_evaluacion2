package cl.duoc.tallerbicicletas.dao;

import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import java.util.List;

public interface BicicletaDAO {

    // Métodos para bicicletas eléctricas
    List<BicicletaElectrica> cargarElectricas();
    void guardarElectricas(List<BicicletaElectrica> lista);

    // Métodos para bicicletas de montaña
    List<BicicletaMontanya> cargarMontanya();
    void guardarMontanya(List<BicicletaMontanya> lista);
}