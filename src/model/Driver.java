package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver extends User implements Serializable {
    private LocalDate medCertificateDate;
    private String medCertificateNumber;
    private String driverLicense;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Destination> myDestinations;

    public String toString() {
        return "Driver: " + this.getName() + " " + this.getSurname();
    }

    public Driver(String login, String password, String name, String surname, LocalDate birthDate, String phoneNum, LocalDate medCertificate, String medCertificateNumber, String driverLicense) {
        super(login, password, name, surname, birthDate, phoneNum);
        this.medCertificateDate = medCertificate;
        this.medCertificateNumber = medCertificateNumber;
        this.driverLicense = driverLicense;
        this.myDestinations = new ArrayList<>();
    }
}
