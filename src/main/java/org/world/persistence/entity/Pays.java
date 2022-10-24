package org.world.persistence.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.world.references.Continent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Accessors(chain = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pays")
@Entity
public class Pays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Continent continent;
    Long population;
    @Embedded
    Codes codes;
    @Embedded
    Names names;

    String capital;
    String nationality;
    Boolean independent;

    @ManyToMany(mappedBy = "pays",cascade = CascadeType.PERSIST)
    List<Langue> langues;

    public void addLangue(Langue langue){
        if(langue == null)
            return;

        if(langues==null){
            langues=new ArrayList<>();
        }
        langues.add(langue);

        if(langue.getPays()==null){
            langue.setPays(new ArrayList<>());
        }
        langue.getPays().add(this);
    }
    @Setter
    @Accessors(chain = true)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Codes {
        String alpha2;

        String alpha3;
        @Column(columnDefinition = "BIGINT(100)")
        Long numerical;
    }
    @Setter
    @Accessors(chain = true)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Names {
        String french;
        String english;
    }

    @Override
    public String toString() {
        return "Pays{" +
                "id=" + id +
                ", continent=" + continent +
                ", population=" + population +
                ", codes=" + codes +
                ", names=" + names +
                ", capital='" + capital + '\'' +
                ", nationality='" + nationality + '\'' +
                ", independent=" + independent +
                '}';
    }
}
