package org.world.references;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.Collator;
import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Continent {
    AFRIQUE("Africa", 10),
    AMERIQUE("America",11),
    ASIE("Asia",12),
    EUROPE("Europe",13),
    OCEANIE("Oceanie",14)
    ;

    String nom;
    Integer code;
    public boolean sameNom(String nom){
        return Objects.equals(this.nom.toLowerCase(), nom.toLowerCase());
    }

  public static Continent from(String name) {

      if(name.contains("rique")){
          name= Continent.AFRIQUE.name().equals(name.toUpperCase())?Continent.AFRIQUE.name():Continent.AMERIQUE.name();
      }
      final String nom =name;
      final Collator instance = Collator.getInstance();
      instance.setStrength(Collator.NO_DECOMPOSITION);
     return  Arrays.stream(Continent.values()).filter(cont->instance.compare(nom,cont.name())==0).findFirst().orElse(null);
  }
    public static Continent of(String nomContinent) {
        return  Arrays.stream(Continent.values()).filter(c->c.sameNom(nomContinent)).findFirst().orElse(null);
    }


}
