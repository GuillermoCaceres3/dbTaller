package org.example.dbtaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;

public class HelloController {

    private static String URL = "jdbc:mysql://localhost:3306/tallerdb";
    private static String user = "Guill";
    private static String pass = "iker2013";

    private static Connection connection;

    @FXML
    private TextField nombre;
    @FXML
    private TextField apellido;
    @FXML
    private ComboBox<String> comboTarea;
    @FXML
    private ComboBox<String> comboEmpleado;
    @FXML
    private DatePicker dateTarea;
    @FXML
    private TextField precio;
    @FXML
    private ComboBox<String> comboEmpleadoConsulta;
    @FXML
    private DatePicker fecha1;
    @FXML
    private DatePicker fecha2;
    @FXML
    private ListView<String> listaTareas;

    @FXML
    public void initialize(){
        setConnection();
        llenarComboTarea();
        llenarComboEmpleado();
    }


    private void setConnection(){
        try{
            connection = DriverManager.getConnection(URL,user,pass);
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    @FXML
    public void agregarEmpleado(){
        String insert = "Insert into Empleado (nombre,apellido) values (?,?)";
        String name = nombre.getText();
        String lastname = apellido.getText();

        if (name.isEmpty() || lastname.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Debe digitar su nombre y apellido");
            alert.showAndWait();
            return;
        }
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,lastname);
            int rows = preparedStatement.executeUpdate();
            if (rows > 0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Realizado");
                alert.setHeaderText(null);
                alert.setContentText("Se ha agregado el empleado");
                alert.showAndWait();
                nombre.clear();
                apellido.clear();
            }

        }catch(Exception e){
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo agregar al empleado");
            alert.showAndWait();
        }
        }

        public void llenarComboTarea(){
        try{
        Statement statement = connection.createStatement();
        ResultSet resultset = statement.executeQuery("Select * from tarea");
        while(resultset.next()){
            String tarea = resultset.getString("tipo");
            comboTarea.getItems().add(tarea);
        }
        } catch (SQLException e){
            System.out.println(e);
        }
        }

    public void llenarComboEmpleado(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("Select * from Empleado");
            while(resultset.next()){
                String empleado = resultset.getString("nombre");
                comboEmpleado.getItems().add(empleado);
                comboEmpleadoConsulta.getItems().add(empleado);
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    @FXML
    public void agregarRegistro(){
        String insert = "Insert into registro (fecha,precio,id_empleado,id_tarea) values (?,?,?,?)";
        LocalDate fecha = dateTarea.getValue();
        String strprecio = precio.getText();
        String TareaNombre = comboTarea.getSelectionModel().getSelectedItem();
        String EmpleadoNombre = comboEmpleado.getSelectionModel().getSelectedItem();

        try{
            float precioVal = Float.parseFloat(strprecio);
            String getIDTarea = "Select id from tarea where tipo = ?";
            PreparedStatement psTarea = connection.prepareStatement(getIDTarea);
            psTarea.setString(1,TareaNombre);
            ResultSet rsTarea = psTarea.executeQuery();
            rsTarea.next();
            int TareaId = rsTarea.getInt("id");
            String getIDEmpleado = "Select id from empleado where nombre = ?";
            PreparedStatement psEmpleado = connection.prepareStatement(getIDEmpleado);
            psEmpleado.setString(1,EmpleadoNombre);
            ResultSet rsEmpleado = psEmpleado.executeQuery();
            rsEmpleado.next();
            int EmpleadoId = rsEmpleado.getInt("id");

            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setDate(1,Date.valueOf(fecha));
            preparedStatement.setFloat(2,precioVal);
            preparedStatement.setInt(3,EmpleadoId);
            preparedStatement.setInt(4,TareaId);
            preparedStatement.executeUpdate();

        } catch(SQLException e){
            System.out.println(e);
        }
    }

    @FXML
    public void consultaFechas(){
        String empleadoNombre = comboEmpleadoConsulta.getSelectionModel().getSelectedItem();
        LocalDate fechaIn = this.fecha1.getValue();
        LocalDate fechaF = this.fecha2.getValue();

        try{
            String getIDEmpleado = "Select id from empleado where nombre = ?";
            PreparedStatement psEmpleado = connection.prepareStatement(getIDEmpleado);
            psEmpleado.setString(1,empleadoNombre);
            ResultSet rsEmpleado = psEmpleado.executeQuery();
            rsEmpleado.next();
            int empleadoID = rsEmpleado.getInt("id");


            String consulta = "select e.nombre as Nombre ,t.tipo as Tarea,r.precio as Precio, r.fecha as Fecha from registro r inner join tarea t on r.id_tarea = t.id inner join empleado e on r.id_empleado = e.id where e.id = ? and r.fecha between ? and ?";
            PreparedStatement preparedStatement = connection.prepareStatement(consulta);
            preparedStatement.setInt(1,empleadoID);
            preparedStatement.setDate(2,Date.valueOf(fechaIn));
            preparedStatement.setDate(3,Date.valueOf(fechaF));
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> tareas = FXCollections.observableArrayList();
            while(resultSet.next()){
                String tarea = "Nombre: " + resultSet.getString("Nombre") + ", Tarea : " + resultSet.getString("Tarea" )+  ", Precio: " + resultSet.getFloat("Precio")+ ", Fecha: " + resultSet.getDate("Fecha") ;
                tareas.add(tarea);
            }
            listaTareas.setItems(tareas);
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    }

