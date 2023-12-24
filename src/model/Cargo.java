package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    private String title;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private double weight;
    private String description;
    @Enumerated
    private CargoType cargoType;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Destination assignedDestination;

    public String toString() {
        return "Cargo: " + title + " description: " + description;
    }

    public Cargo(String title, double weight, CargoType cargoType, String description) {
        this.title = title;
        this.weight = weight;
        this.cargoType = cargoType;
        this.description = description;
        this.dateCreated = LocalDate.now();
    }
}
