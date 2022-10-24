package org.world.persistence.repository;

import org.world.persistence.entity.Langue;

import java.util.List;
@SuppressWarnings("unused")
public interface LangQueryRepository {
    List<Langue> langueLaPlusParler();
    List<Langue> langueLaMoinsParler();
    List<Langue> langueParlerParPlusNbPays(Integer nb);
    Long nombrePaysParlant(String nomLangue);

    Langue charge(String nomLangue);
    List<Langue> findAll();
}
