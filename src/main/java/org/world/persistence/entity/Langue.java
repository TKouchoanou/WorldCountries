package org.world.persistence.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter
@Accessors(chain = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "langue")
@Entity
public class Langue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true,nullable = false)
    String nom;
    @ManyToMany
    @JoinTable(name = "pays_langue",joinColumns ={ @JoinColumn(name = "langue_id",referencedColumnName = "id")},
    inverseJoinColumns = { @JoinColumn(name = "pays_id",referencedColumnName = "id")})
    List<Pays> pays;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Langue langue)) {
            return false;
        }

         if(id!=null && langue.id!=null){
             return Objects.equals(id, langue.id) && Objects.equals(nom, langue.nom);
         }

        return Objects.equals(nom, langue.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }


    @Override
    public String toString() {
        return "Langue{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
/*

== Observations ==
On remarque avec cette liste que nombre de régions ou pays sont bilingues voire multilingues. Aussi, les 5 premières langues listées sont également, avec le chinois mandarin, des langues officielles de l'[[Organisation des Nations unies|ONU]].

Ne sont pas listées les langues pratiquées officiellement dans 4 pays ou moins, car il n'est pas possible de les départager vraiment. Et les langues pratiquées dans 2 pays sont également très nombreuses et ignorées volontairement, pour ne retenir que les langues les plus internationales.

== Autres langues internationales ==

=== Liste de 4 pays ===

* le [[Malais (langue)|malais]] ({{Brunei}}, {{Indonésie}}, {{Malaisie}}, {{Singapour}})
* l'[[italien]] ({{Italie}}, {{Vatican}}, {{Saint-Marin}}, {{Suisse}})
* l'[[amazighe]] ({{Maroc}}, {{Algérie}}, {{Niger}}, {{Mali}})
* le [[serbe]] ({{Serbie}}, {{Bosnie-Herzégovine}}, {{Kosovo}}, {{Monténégro}})
* le [[kurde (langue)|kurde]] ({{turquie}}, {{irak}}, {{iran}}, {{syrie}})

=== Liste de 3 pays ===

* le [[Mandarin (langue)|mandarin]] ({{Chine}}, {{Singapour}}, {{Taïwan}}),
* le [[persan]] ({{Iran}}, {{Tadjikistan}}, {{Afghanistan}}),
* l'[[hindi]] ({{Inde}}, {{Népal}}, {{Fidji}})
* le [[kikongo]] ({{Angola}}, {{République démocratique du Congo}}, {{République du Congo}})
*l'[[albanais]] ({{Albanie}}, {{Kosovo}}, {{Macédoine du Nord}})
*le [[quechua]] ({{Bolivie}}, {{Équateur}}, {{Pérou}})
*le [[tamoul]] ({{Inde}}, {{Singapour}}, {{Sri Lanka}})

== Articles connexes ==

* [[Langues officielles de l'Organisation des Nations unies]]
* [[Liste des langues officielles]]
*[[Liste de langues par nombre de continents]]

== Sources ==
<references />

{{Palette|Listes de langues|Liste langue officielle|Distribution des langues}}

{{Portail|linguistique|langues}}

[[Catégorie:Liste de pays|Langues officielles, liste des]]
[[Catégorie:Liste de langues officielles|*]]
[[Catégorie:Langue internationale ou mondiale]]
 */