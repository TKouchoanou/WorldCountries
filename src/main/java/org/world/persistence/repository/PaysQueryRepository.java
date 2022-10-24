package org.world.persistence.repository;

import org.world.persistence.bean.PaysAvecListDeLangue;
import org.world.persistence.bean.PaysNbLangue;
import org.world.persistence.entity.Langue;
import org.world.persistence.entity.Pays;

import java.util.List;
import java.util.Map;
//@SuppressWarnings("unused")

public interface PaysQueryRepository {
    Map<String,Integer> paysAvecLongueurDeNomFrancais();
    List<Pays> paysAuNomFrancaisLePlusLong();
    Pays premierPaysAuNomFrancaisLePlusLong();
    List<Pays> paysAvecPlusNbLettreDansLeurNomFrancais(Integer nb);
    List<Pays> paysParlantAumoinsUneLangue();
    List<Pays> paysParlantAumoinsUne(List<String> langues);
    List<Pays> paysParlantAlaFois(List<Langue> langues);
    List<Pays> paysParlant(String langue);
    List<PaysNbLangue> paysAvecNombreDeLangue();
    List<PaysAvecListDeLangue> paysAvecListDeLangue();
    List<Pays> paysDontLaLangueEstLaPlusParlee();
    List<Pays> paysDontLaLangueEstLaMoinsParlee();
    List<Pays> paysAvecLePlusDeLangue();
    List<Pays> paysAyantAuMoinsNbLangue(Long nb);

    List<Pays> paysAyantAuPlusNbLangue(Long nb);

    List<Pays> paysSansLangue();
    List<Pays> paysAvecLangue();
    List<Pays> toutLesPays();
    Pays paysAvecAlpha2(String codeAlpha2);
    Pays paysAvecAlpha3(String codeAlpha3);
    Pays paysAvecNomFrancais(String nomFrancais);
    Pays paysAvecNomAnglais(String nomAnglais);

    Long nombrePaysNeParlantPasALaFois(List<Langue> langues);

    Long nombrePaysNeParlantPas(List<Langue> langues);

     Long nombrePaysNeParlantPas(Langue lang);
    Long nombrePaysNeParlantPas(String langue);

    /*
      PREPARE stmt1 FROM
      'select count(pays0_.id) as col_0_0_ from pays pays0_ where (? in (select langues1_.langue_id from pays_langue langues1_ where pays0_.id=langues1_.pays_id)) and (? in (select langues2_.langue_id from pays_langue langues2_ where pays0_.id=langues2_.pays_id))';
       SET @a = 1;   SET @b = 2;
      EXECUTE stmt1 USING @a, @b;
     */

    Long nombrePaysParlant(List<Langue> langue);

    Long nombrePaysParlant(Langue langue);
    Long nombrePaysParlant(String langue);
    Long nombreDeLangueParlerPar(String paysName);
    Long nombrePaysSansLangue();
    Long nombrePaysAvecLangue();
    Integer nombreLanguePaysAvecLePlusDeLangue();
    List<Pays> findAll();
    List<Pays> loadAll();
}
