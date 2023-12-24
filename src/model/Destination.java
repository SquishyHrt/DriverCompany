package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    private String startCity;
    private String endCity;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private LocalDate departureDate;
    private LocalDate arrivalDate;

    @OneToMany(mappedBy = "assignedDestination", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Checkpoint> checkpointList;

    @OneToMany(mappedBy = "assignedDestination", cascade = {CascadeType.PERSIST})
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Cargo> cargoList;

    @OneToOne()
    private Truck truck;
    @ManyToOne()
    private Driver driver;
    @ManyToOne()
    private Manager responsibleManager;

    public String toString() {
        // Departure @city - @departureDate day/month | Arrival @city - @arrivalDate day/month
        return "Departure " + startCity + " - " + departureDate.getDayOfMonth() + "/" + departureDate.getMonth() + " | Arrival " + endCity + " - " + arrivalDate.getDayOfMonth() + "/" + arrivalDate.getMonth();
    }

    public Destination(String startCity, String endCity, Manager responsibleManager, Truck truck, Driver driver, LocalDate departureDate, LocalDate arrivalDate) {
        this.startCity = startCity;
        this.endCity = endCity;
        this.truck = truck;
        this.driver = driver;
        this.responsibleManager = responsibleManager;
        this.dateCreated = LocalDate.now();
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.cargoList = new ArrayList<>();
        this.checkpointList = new ArrayList<>();
    }
}
