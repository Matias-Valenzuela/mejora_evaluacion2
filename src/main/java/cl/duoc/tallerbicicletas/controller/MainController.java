package cl.duoc.tallerbicicletas.controller;

import cl.duoc.tallerbicicletas.dao.BicicletaDAO;
import cl.duoc.tallerbicicletas.dao.JsonBicicletaDao;
import cl.duoc.tallerbicicletas.model.Bicicleta;
import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final BicicletaDAO dao = new JsonBicicletaDao();

    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtAnio;
    @FXML private TextField txtPeso;
    @FXML private TextField txtAutonomia;
    @FXML private CheckBox checkBateria;
    @FXML private CheckBox checkGarantia;
    @FXML private TextField txtSuspensiones;
    @FXML private Label labelMensaje;
    @FXML private TableView<Bicicleta> tablaBicicletas;
    @FXML private TableColumn<Bicicleta, String> colTipo;
    @FXML private TableColumn<Bicicleta, String> colCodigo;
    @FXML private TableColumn<Bicicleta, Integer> colAnio;
    @FXML private TableColumn<Bicicleta, Double> colPeso;
    @FXML private TableColumn<Bicicleta, Double> colCosto;

    @FXML
    public void initialize() {
        labelMensaje.setText("");
        comboTipo.getItems().addAll("Bicicleta Eléctrica", "Bicicleta de Montaña");

        txtAutonomia.setDisable(true);
        checkBateria.setDisable(true);
        checkGarantia.setDisable(true);
        txtSuspensiones.setDisable(true);

        // Enlace de columnas con los getters del modelo Bicicleta
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioFabricacion"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colCosto.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().calcularCostoMantencion())
        );


        tablaBicicletas.getItems().addAll(dao.cargarElectricas());
        tablaBicicletas.getItems().addAll(dao.cargarMontanya());
    }

    @FXML
    void cambiarTipoBicicleta(ActionEvent event) {
        String seleccion = comboTipo.getValue();
        if (seleccion == null) {
            return;
        }

        if (seleccion.equals("Bicicleta Eléctrica")) {
            txtAutonomia.setDisable(false);
            checkBateria.setDisable(false);
            checkGarantia.setDisable(false);

            txtSuspensiones.setDisable(true);
            txtSuspensiones.clear();
        } else if (seleccion.equals("Bicicleta de Montaña")) {
            txtSuspensiones.setDisable(false);

            txtAutonomia.setDisable(true);
            txtAutonomia.clear();
            checkBateria.setDisable(true);
            checkBateria.setSelected(false);
            checkGarantia.setDisable(true);
            checkGarantia.setSelected(false);
        }
    }

    @FXML
    void registrarBicicleta(ActionEvent event) {
        try {
            String tipo = comboTipo.getValue();
            if (tipo == null || tipo.equals("Seleccione..")) {
                mostrarMensaje("Error: Seleccione un tipo de bicicleta.", Color.RED);
                return;
            }

            String codigo = txtCodigo.getText();
            int anio = Integer.parseInt(txtAnio.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());

            if (tipo.equals("Bicicleta Eléctrica")) {
                int autonomiaKm = Integer.parseInt(txtAutonomia.getText().trim());
                boolean bateria = checkBateria.isSelected();
                boolean garantia = checkGarantia.isSelected();

                BicicletaElectrica electrica = new BicicletaElectrica(codigo, anio, peso, autonomiaKm, bateria);
                if (garantia) {
                    electrica.activarGarantiaExtendida();
                }
                tablaBicicletas.getItems().add(electrica);

            } else if (tipo.equals("Bicicleta de Montaña")) {
                int suspensiones = Integer.parseInt(txtSuspensiones.getText().trim());

                BicicletaMontanya montanya = new BicicletaMontanya(codigo, anio, peso, suspensiones);
                tablaBicicletas.getItems().add(montanya);
            }

            actualizarArchivosJSON();
            mostrarMensaje("Bicicleta registrada y guardada con éxito.", Color.GREEN);
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Ingrese valores numéricos válidos en Año, Peso, Autonomía o Suspensiones.", Color.RED);
        } catch (IllegalArgumentException e) {
            mostrarMensaje("Error: " + e.getMessage(), Color.RED);
        }
    }

    @FXML
    void eliminarBicicleta(ActionEvent event) {
        Bicicleta seleccionada = tablaBicicletas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Error: Seleccione una bicicleta de la tabla para eliminar.", Color.RED);
            return;
        }

        tablaBicicletas.getItems().remove(seleccionada);
        actualizarArchivosJSON();
        mostrarMensaje("Bicicleta eliminada correctamente.", Color.GREEN);
    }

    private void actualizarArchivosJSON() {
        List<BicicletaElectrica> electricas = new ArrayList<>();
        List<BicicletaMontanya> montanya = new ArrayList<>();

        for (Bicicleta b : tablaBicicletas.getItems()) {
            if (b instanceof BicicletaElectrica) {
                electricas.add((BicicletaElectrica) b);
            } else if (b instanceof BicicletaMontanya) {
                montanya.add((BicicletaMontanya) b);
            }
        }

        dao.guardarElectricas(electricas);
        dao.guardarMontanya(montanya);
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtAnio.clear();
        txtPeso.clear();
        txtAutonomia.clear();
        txtSuspensiones.clear();
        checkBateria.setSelected(false);
        checkGarantia.setSelected(false);
    }

    private void mostrarMensaje(String texto, Color color) {
        labelMensaje.setText(texto);
        labelMensaje.setTextFill(color);
    }
}