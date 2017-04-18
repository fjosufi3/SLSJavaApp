package app.java.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

import app.java.model.ConnectionConfiguration;
import app.java.model.FormValidation;
import app.java.model.POI;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Created by mcw0805 on 4/14/17.
 */
public class ViewPOIController implements Initializable {

    @FXML
    private Label pageHeader;
    @FXML
    private TableView<POI> poiTableView;
    @FXML
    private TableColumn<POI, String> columnLocation;
    @FXML
    private TableColumn<POI, String> columnCity;
    @FXML
    private TableColumn<POI, String> columnState;
    @FXML
    private TableColumn<POI, String> columnZipCode;
    @FXML
    private TableColumn<POI, Integer> columnFlag;
    @FXML
    private TableColumn<POI, Date> columnDate;
    @FXML
    private ComboBox poi_loc_box;
    @FXML
    private ComboBox city_view_poi_box;
    @FXML
    private ComboBox state_view_poi_box;
    @FXML
    private TextField zip_view_poi;
    @FXML
    private CheckBox flagged_checkBox;
    @FXML
    private DatePicker dateStart_view_poi;
    @FXML
    private DatePicker dateEnd_view_poi;
    @FXML
    private Button apply_filter_view_poi_btn;
    @FXML
    private Button reset_filter_view_poi_btn;
    @FXML
    private Button backBtn_view_poi;
    @FXML
    private Label zip_valid_label;

    private ObservableList<String> cityList = FXCollections.observableArrayList();
    private ObservableList<String> stateList = FXCollections.observableArrayList();
    private ObservableList<String> poiLocList = FXCollections.observableArrayList();

    private ObservableList<POI> data = FXCollections.observableArrayList();
    private Connection con = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        con = ConnectionConfiguration.getConnection();
        setCellTable();
        //loadFromDB(); //temporary
        loadDropDown();

    }

    @FXML
    private void loadFromDB() {
        data = FXCollections.observableArrayList();
        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM POI");

            while (rs.next()) {
                data.add(new POI(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getDate(6)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        poiTableView.setItems(null);
        poiTableView.setItems(data);
    }

    private void setCellTable() {
        columnLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
        columnZipCode.setCellValueFactory(new PropertyValueFactory<>("zip"));
        columnFlag.setCellValueFactory(new PropertyValueFactory<>("flagString"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("dateFlagged"));



    }

    @FXML
    private void loadDropDown() {

        try {
            PreparedStatement pst1 = con.prepareStatement("SELECT DISTINCT City FROM CITY_STATE ORDER BY City");
            ResultSet rs1 = pst1.executeQuery();


            while(rs1.next()) {
                //data.add(new CityState(rs1.getString(1), rs1.getString(2)));

                cityList.add(rs1.getString(1));
            }

            PreparedStatement pst2 = con.prepareStatement("SELECT DISTINCT State FROM CITY_STATE ORDER BY State");
            ResultSet rs2 = pst2.executeQuery();


            while(rs2.next()) {

                stateList.add(rs2.getString(1));
            }


            PreparedStatement pst3 = con.prepareStatement("SELECT Location_Name FROM POI ORDER BY Location_Name");
            ResultSet rs3 = pst3.executeQuery();

            while(rs3.next()) {
                poiLocList.add(rs3.getString(1));
            }

            city_view_poi_box.setItems(cityList);
            state_view_poi_box.setItems(stateList);
            poi_loc_box.setItems(poiLocList);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onApplyFilter() { //IN PROGRESS ---> a lot of clean up to do
        boolean zipNotEmpty = FormValidation.textFieldNotEmpty(zip_view_poi);
        boolean isValidZip = zipNotEmpty && FormValidation.isValidZipCode(zip_view_poi.getText());
        boolean isSelectedLocation = !(poi_loc_box.getSelectionModel().isEmpty());
        boolean isSelectedCity = !(city_view_poi_box.getSelectionModel().isEmpty());
        boolean isSelectedState = !(state_view_poi_box.getSelectionModel().isEmpty());
        boolean isFlagged = flagged_checkBox.isSelected();
        boolean isValidStartDate = dateStart_view_poi.getValue() != null;
        boolean isValidEndDate = dateEnd_view_poi.getValue() != null;

        if (!zipNotEmpty && !isSelectedLocation && !isSelectedCity && !isSelectedState
                && !isFlagged && !isValidStartDate && !isValidEndDate) {
            System.out.println("enter a field");
        }

        if ((isValidStartDate && !isValidEndDate) || (!isValidEndDate && isValidEndDate)) {
            System.out.println("invalid"); // fix later with labels
        }


        String POILocation = null;
        String city = null;
        String state = null;
        String flag = null;
        String zipCode = null;
        String dateStart = null;
        String dateEnd = null;

        if (isValidZip) {
            zipCode = "Zip_Code = " + zip_view_poi.getText();
            //zipCode = zip_view_poi.getText();
        }
        if (isSelectedLocation) {
            POILocation = "Location_Name = \'" + poi_loc_box.getSelectionModel().getSelectedItem().toString() + "\'";
            //POILocation = poi_loc_box.getSelectionModel().getSelectedItem().toString();

        }
        if (isSelectedCity) {
            city = "City = \'" + city_view_poi_box.getSelectionModel().getSelectedItem().toString() + "\'";
        }
        if (isSelectedState) {
            state = "State = \'" + state_view_poi_box.getSelectionModel().getSelectedItem().toString() + "\'";
        }
        if (isFlagged) {
            flag = "Flag = TRUE";
            //flag = "TRUE";
        }
        if (isValidStartDate) {
            dateStart = "Date_Flagged >= \'" + dateStart_view_poi.getValue().toString() + "\'";
            //dateStart = dateStart_view_poi.getValue().toString();
        }
        if (isValidEndDate) {
            dateEnd = "Date_Flagged <= \'" + dateEnd_view_poi.getValue().toString() + "\'";
            //dateEnd = dateEnd_view_poi.getValue().toString();
        }


        System.out.println(POILocation);
        System.out.println(city);
        System.out.println(state);
        System.out.println(flag);
        System.out.println(zipCode);
        System.out.println(dateStart);
        System.out.println(dateEnd);

        System.out.println(generateCondition(POILocation, city, state, flag, zipCode, dateStart, dateEnd));
        poiTableView.getItems().remove(data); //prevents loading data twice or more


        if (POILocation == null && city == null && state == null && flag == null
                && zipCode == null && dateStart == null && dateEnd == null) {
            System.out.println("enter a field");

        } else if ((dateStart == null && dateEnd != null) || (dateStart != null && dateEnd == null)) {
            System.out.println("invalid"); // fix later with labels
        } else {
            try {
                PreparedStatement pst = con.prepareStatement("SELECT Location_Name, City, State, Zip_Code, Flag, Date_Flagged " +
                        "FROM POI " +
                        "WHERE " + generateCondition(POILocation, city, state, flag, zipCode, dateStart, dateEnd));


                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    data.add(new POI(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getInt(5), rs.getDate(6)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        poiTableView.setItems(data);


//        SELECT  Location_Name, City, State, Zip_Code, Flag, Date_Flagged FROM POI
//        WHERE Location_Name = ? AND City = ? AND State = ? AND Zip_Code = ?
//        AND Date_Flagged >= ? AND Date_Flagged <= ? AND Flag = ?;




    }

    private String generateCondition(String POILoc, String city, String state, String flag,
                                     String zip, String startDate, String endDate) {
        //guaranteed that at least one is not null

        String whereClause = "";

        String [] paramArray = {POILoc, city, state, flag, zip, startDate, endDate};

        for (int i = 0; i < paramArray.length; i++) {
            if (paramArray[i] != null) {
                whereClause += paramArray[i] + " AND ";
            }
        }

        //remove "AND" from the end of the string
        if (whereClause.endsWith("AND ")) {
            whereClause = whereClause.substring(0, whereClause.length() - 4);
        }

        return whereClause;
    }

    @FXML
    private void onResetFilter() {

        //clear table
        poiTableView.getItems().removeAll(data);

        //clear combo boxes
        poi_loc_box.getSelectionModel().clearSelection();
        city_view_poi_box.getSelectionModel().clearSelection();
        state_view_poi_box.getSelectionModel().clearSelection();

        //clear TextField
        zip_view_poi.clear();

        //clear check box
        flagged_checkBox.setSelected(false);

        //clear dates
        dateStart_view_poi.getEditor().clear();
        dateEnd_view_poi.getEditor().clear();
    }

    @FXML
    private void onClickBack(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) backBtn_view_poi.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/main/app/java/view/welcome_official.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


}
