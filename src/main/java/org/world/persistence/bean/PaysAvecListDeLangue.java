package org.world.persistence.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.world.persistence.entity.Langue;
import org.world.persistence.entity.Pays;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaysAvecListDeLangue {
    String nomPays;
    List<String> langues;

     public  PaysAvecListDeLangue(Collection<Langue> langues,String nom){
        nomPays=nom;
        this.langues=langues.stream().map(Langue::getNom).toList();
    }
    @SuppressWarnings("unused")
   public PaysAvecListDeLangue(Collection<Langue> langues, Pays pays){
        nomPays=pays.getNames().getFrench();
        this.langues=langues.stream().map(Langue::getNom).toList();
    }

}
