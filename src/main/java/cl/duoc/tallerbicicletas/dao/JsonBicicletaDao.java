package cl.duoc.tallerbicicletas.dao;

import cl.duoc.tallerbicicletas.model.BicicletaElectrica;
import cl.duoc.tallerbicicletas.model.BicicletaMontanya;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonBicicletaDao implements BicicletaDAO {

    private final Gson gson;
    private final Path RUTA_ELECTRICAS = Path.of("bicicletas_electricas.json");
    private final Path RUTA_MONTANYA = Path.of("bicicletas_montanya.json");

    public JsonBicicletaDao() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void guardarElectricas(List<BicicletaElectrica> lista) {
        try (Writer writer = Files.newBufferedWriter(RUTA_ELECTRICAS, StandardCharsets.UTF_8)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    @Override
    public List<BicicletaElectrica> cargarElectricas() {
        if (!Files.exists(RUTA_ELECTRICAS, LinkOption.NOFOLLOW_LINKS)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(RUTA_ELECTRICAS, StandardCharsets.UTF_8)) {
            Type tipoLista = new TypeToken<List<BicicletaElectrica>>(){}.getType();
            List<BicicletaElectrica> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            System.err.println("Error al leer: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void guardarMontanya(List<BicicletaMontanya> lista) {
        try (Writer writer = Files.newBufferedWriter(RUTA_MONTANYA, StandardCharsets.UTF_8)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    @Override
    public List<BicicletaMontanya> cargarMontanya() {
        if (!Files.exists(RUTA_MONTANYA, LinkOption.NOFOLLOW_LINKS)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(RUTA_MONTANYA, StandardCharsets.UTF_8)) {
            Type tipoLista = new TypeToken<List<BicicletaMontanya>>(){}.getType();
            List<BicicletaMontanya> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            System.err.println("Error al leer: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}