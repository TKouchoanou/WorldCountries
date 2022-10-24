package org.world.persistence.repository;

import org.world.persistence.JpaUtils;
import org.world.persistence.entity.Langue;

import java.util.List;

public class LangQueryRepositoryJPQL implements LangQueryRepository {

    @Override
    public List<Langue> langueLaPlusParler() {
        Long maxPop=  JpaUtils.doInReadOnlyTransaction(em->
                em.createQuery("select sum(lp.population) from Langue l left join l.pays lp group by l ORDER BY sum(lp.population) DESC", Long.class)
                        .setMaxResults(1)
                        .getSingleResult());
        return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select distinct l from Langue l left join fetch l.pays lp group by l having sum(lp.population)=:maxPop", Langue.class)
                .setParameter("maxPop",maxPop)
                .getResultList());
    }

    @Override
    public List<Langue> langueLaMoinsParler() {
        Long minPop=  JpaUtils.doInReadOnlyTransaction(em->
                em.createQuery("select sum(lp.population) from Langue l  left join l.pays lp group by l having sum(lp.population)>0 ORDER BY sum(lp.population)", Long.class)
                        .setMaxResults(1)
                        .getSingleResult());
        return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select l from Langue l join fetch l.pays lp group by l having sum(lp.population)=:minPop", Langue.class)
                .setParameter("minPop",minPop)
                .getResultList());
    }

    @Override
    public List<Langue> langueParlerParPlusNbPays(Integer nb) {
        return JpaUtils.doInReadOnlyTransaction(em->em
                .createQuery("select distinct l from Langue l  left join fetch l.pays lp where l.pays.size> :nb ", Langue.class)
                .setParameter("nb",nb)
                .getResultList());
    }

    @Override
    public Long nombrePaysParlant(String nomLangue) {
      return   JpaUtils.doInReadOnlyTransaction((em,langue)-> {
                 return em.createQuery("select count(l) from Langue l left join l.pays p where l.nom=:langue group by l.nom", Long.class)
                          .setParameter("langue", langue)
                          .getSingleResult();
              }
              ,nomLangue);
    }

    @Override
    public Langue charge(String nomLangue) {
        return JpaUtils.doInReadOnlyTransaction((em,langue)->{
           return  em.createQuery("select l from Langue l fetch join fetch l.pays pl where l.nom=:langue",Langue.class)
                   .setParameter("langue",langue)
                   .getSingleResult();
        },nomLangue);
    }

    @Override
    public List<Langue> findAll() {
        return JpaUtils.doInReadOnlyTransaction((em,langue)->{
            return  em.createQuery("select l from Langue l",Langue.class)
                    .getResultList();
        },"");
    }
}
