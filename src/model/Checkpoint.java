package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Checkpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    private String address;
    private boolean longStop;
    private LocalDate dateArrived;

    @ManyToOne(cascade = {CascadeType.REMOVE})
    private Destination assignedDestination;

    public String toString() {
        return "Checkpoint address: " + address + ", long stop: " + longStop + ", date arrived: " + dateArrived;
    }

    public Checkpoint(String title, boolean longStop, LocalDate dateArrived) {
        this.address = title;
        this.longStop = longStop;
        this.dateArrived = dateArrived;
    }
}
