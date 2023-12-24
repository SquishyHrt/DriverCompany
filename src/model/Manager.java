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
public class Manager extends User {
    private String email;
    private LocalDate employmentDate;
    private boolean isAdmin;

    @OneToMany(mappedBy = "responsibleManager", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Destination> destinationList;

    public String toString() {
        return "Manager: " + this.getName() + " " + this.getSurname();
    }

    public Manager(String login, String password, String name, String surname, LocalDate birthDate, String phoneNum, String email) {
        super(login, password, name, surname, birthDate, phoneNum);
        this.email = email;
        this.employmentDate = LocalDate.now();
        this.destinationList = new ArrayList<>();
    }

    public Manager(String login, String password, String name, String surname, LocalDate birthDate, String phoneNum, String email, boolean isAdmin) {
        super(login, password, name, surname, birthDate, phoneNum);
        this.email = email;
        this.employmentDate = LocalDate.now();
        this.isAdmin = isAdmin;
        this.destinationList = new ArrayList<>();
    }
}
