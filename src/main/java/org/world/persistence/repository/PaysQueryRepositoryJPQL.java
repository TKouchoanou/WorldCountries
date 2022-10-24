package org.world.persistence.repository;

import org.world.persistence.JpaUtils;
import org.world.persistence.bean.PaysAvecListDeLangue;
import org.world.persistence.bean.PaysNbLangue;
import org.world.persistence.entity.Langue;
import org.world.persistence.entity.Pays;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaysQueryRepositoryJPQL implements PaysQueryRepository {
    @Override
    public Map<String, Integer> paysAvecLongueurDeNomFrancais() {
        return JpaUtils.doInReadOnlyTransaction( em-> em.createQuery("select p.names.french as nom, length(p.names.french) as longueur from Pays p", Tuple.class)
                        .getResultList()
                        .stream()
                        .collect(Collectors.toMap(e -> (String) e.get(0), v -> (Integer) v.get(1),(e1,e2)->e2)));
    }

    @Override
    public List<Pays> paysAuNomFrancaisLePlusLong() {
        return JpaUtils.doInReadOnlyTransaction(em->{
            int maxLength= em.createQuery("select p from Pays p ORDER BY LENGTH(p.names.french) DESC",Pays.class).setMaxResults(1).getSingleResult().getNames().getFrench().length();
            return em.createQuery("select p from Pays p where LENGTH(p.names.french)=:max", Pays.class)
                    .setParameter("max",maxLength).getResultList();
        });
    }
    @Override
    public Pays premierPaysAuNomFrancaisLePlusLong() {
        return JpaUtils.doInReadOnlyTransaction(em-> em.createQuery("select p from Pays p ORDER BY LENGTH(p.names.french) DESC", Pays.class).setMaxResults(1).getSingleResult());
    }

    @Override
    public List<Pays> paysAvecPlusNbLettreDansLeurNomFrancais(Integer nb) {
         return JpaUtils.doInReadOnlyTransaction(em-> em.createQuery("select p from Pays p where LENGTH(p.names.french) > "+nb, Pays.class).getResultList());
    }

    public List<Pays> paysParlantAumoinsUneLangue() {
        return JpaUtils.doInReadOnlyTransaction(em -> em.createQuery("select p from Pays p where p.langues.size > 0", Pays.class).getResultList());
    }

    @Override
    public List<Pays> paysParlantAumoinsUne(List<String> langues) {

        List<Langue> langueList=JpaUtils.doInReadOnlyTransaction(em->em
                .createQuery("select l from Langue l where l.nom IN :langues", Langue.class)
                .setParameter("langues",langues)
                .getResultList());

        Map<String,Langue> params= JpaUtils.toMap(langueList);
        String whereClause= params.isEmpty()?"":"where "+JpaUtils.fromMapToCondition(params,"MEMBER OF p.langues", JpaUtils.Clause.OR);
        return JpaUtils.doInReadOnlyTransaction(em-> {
            TypedQuery<Pays> typedQuery=em.createQuery("select p from Pays p "+whereClause, Pays.class);
            params.forEach(typedQuery::setParameter);
            return  typedQuery.getResultList();
        });
    }

    @Override
    public List<Pays> paysParlantAlaFois(List<Langue> langues) {
        return JpaUtils.doInReadOnlyTransaction((em,langs)->{
            Map<String,Langue> params= langs.stream().collect(Collectors.toMap(l->"langue"+langs.indexOf(l)+1, Function.identity()));
            String conditions=params.keySet().stream().map(key->":"+key+" MEMBER OF p.langues ").collect(Collectors.joining(" AND "));
            String whereClause=!conditions.isEmpty()?"where "+conditions:"";
            String queryString= "select p from Pays p "+whereClause;
            TypedQuery<Pays> query=em.createQuery(queryString, Pays.class);
            params.forEach(query::setParameter);
            return query.getResultList();
        },langues);
    }

    @Override
    public List<Pays> paysParlant(String langue) {
         return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select p from Pays p join fetch p.langues pl where pl.nom=:nom",Pays.class)
                    .setParameter("nom",nom)
                    .getResultList();
        },langue);
    }

    @Override
    public List<PaysNbLangue> paysAvecNombreDeLangue() {
        return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select new org.world.persistence.bean.PaysNbLangue(p.names.french,p.langues.size) from Pays p", PaysNbLangue.class).getResultList());
    }

    @Override
    public List<PaysAvecListDeLangue> paysAvecListDeLangue() {
        return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select distinct p from Pays p left join fetch p.langues", Pays.class)
                .getResultList()
                .stream()
                .map(pays -> new PaysAvecListDeLangue(pays.getLangues(),pays.getNames().getFrench()))
                .toList());
        //JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select new org.world.persistence.bean.PaysAvecListDeLangue(p.langues,p.names.french) from Pays p", PaysAvecListDeLangue.class).getResultList());
    }
    @Override
    public List<Pays> paysDontLaLangueEstLaPlusParlee() {

        //SELECT pl.langue_id,l.nom, sum(p.`population`) as pop FROM `pays` as p left outer join pays_langue as pl ON p.id=pl.pays_id left join langue l on pl.langue_id=l.id group by pl.langue_id order by pop desc
        return JpaUtils.doInReadOnlyTransaction(em->{
            Long maxPop=em.createQuery("select sum(lp.population) from Langue l left join l.pays lp group by l ORDER BY sum(lp.population) DESC", Long.class).setMaxResults(1).getSingleResult();
            return em.createQuery("select distinct  p from Pays p left join p.langues pl where pl in (select l from Langue l left join l.pays lp group by l having sum(lp.population)=:maxPop)",Pays.class)
                    .setParameter("maxPop",maxPop).getResultList();

           /*
           fausse requête car considérant qu'il n'y a qu'un seul pays dont la langue est la plus parlé. Forte hypothèse sur les données
            Langue maxLangue= em.createQuery("select l from Langue l left join l.pays as lp GROUP BY l ORDER BY sum(lp.population) DESC", Langue.class).setMaxResults(1).getSingleResult();
            return em.createQuery("select p from Pays p where :langue MEMBER OF p.langues", Pays.class)
                    .setParameter("langue",maxLangue)
                    .getResultList();*/
        });

    }

    @Override
    public List<Pays> paysDontLaLangueEstLaMoinsParlee() {
        return JpaUtils.doInReadOnlyTransaction(em-> {
            Long minPop=em.createQuery("select sum(lp.population) from Langue l left join l.pays lp group by l HAVING sum(lp.population)>0 ORDER BY sum(lp.population)", Long.class).setMaxResults(1).getSingleResult();
            return em.createQuery("select distinct  p from Pays p left join p.langues pl where pl in (select l from Langue l left join l.pays lp group by l having sum(lp.population)=:minPop)",Pays.class)
                    .setParameter("minPop",minPop).getResultList();
        });
    }

    @Override
    public List<Pays> paysAvecLePlusDeLangue() {
        //native sql select * from pays p where p.id IN (select mpg.pid from (select pid,max(nb) maxNb from (select p.id as pid, count(p.id) as nb from pays p left join pays_langue pl on p.id=pl.pays_id group by p.id ) as pg) as mpg)
        //requête imbriqué supportée seulement dans la clause where en JPQL
        return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select distinct p from Pays p left join fetch p.langues where p.langues.size = (select max(p.langues.size) from Pays p )", Pays.class)
                    .getResultList();},"");
    }

    @Override
    public List<Pays> paysAyantAuMoinsNbLangue(Long nb) {
        return JpaUtils.doInReadOnlyTransaction((em,n)->{
            return em.createQuery("select p from Pays p left join fetch p.langues where p.id IN (select p.id from Pays p left join p.langues group by p.id having count(p.id) >=:n)",Pays.class)
                    .setParameter("n",n)
                    .getResultList();
        },nb);
    }

    @Override
    public List<Pays> paysAyantAuPlusNbLangue(Long nb) {
        return JpaUtils.doInReadOnlyTransaction((em,n)->{
            return em.createQuery("select p from Pays p left join fetch p.langues where p.id IN (select p.id from Pays p left join p.langues group by p.id having count(p.id) <=:n)",Pays.class)
                    .setParameter("n",n)
                    .getResultList();
        },nb);
    }

    @Override
    public List<Pays> paysSansLangue() {
        return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select p from Pays p where p.langues IS EMPTY",Pays.class).getResultList();
        },"");
    }

    @Override
    public List<Pays> paysAvecLangue() {
        return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select p from Pays p where p.langues IS NOT EMPTY",Pays.class).getResultList();
        },"");
    }

    @Override
    public List<Pays> toutLesPays() {
        return findAll();
    }

    @Override
    public Pays paysAvecAlpha2(String codeAlpha2) {
        return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select p from Pays p left join fetch p.langues  where p.codes.alpha2=:nom",Pays.class)
                    .setParameter("nom",nom)
                    .getSingleResult();
        },codeAlpha2);
    }

    @Override
    public Pays paysAvecAlpha3(String codeAlpha3) {
         return JpaUtils.doInReadOnlyTransaction((em,nom)->{
            return em.createQuery("select p from Pays p left join fetch p.langues where p.codes.alpha3=:nom",Pays.class)
                    .setParameter("nom",nom)
                    .getSingleResult();
        },codeAlpha3);
    }

    @Override
    public Pays paysAvecNomFrancais(String nomFrancais) {
        return JpaUtils.doInReadOnlyTransaction((em,frenchName)->{
            return em.createQuery("select p from Pays p left join fetch p.langues pl where p.names.french=:frenchName",Pays.class)
                    .setParameter("frenchName",frenchName)
                    .getSingleResult();
        },nomFrancais);
    }

    @Override
    public Pays paysAvecNomAnglais(String nomAnglais) {
        return JpaUtils.doInReadOnlyTransaction((em,englishName)->{
            return em.createQuery("select p from Pays p left join fetch p.langues pl where p.names.english=:englishName",Pays.class)
                    .setParameter("englishName",englishName)
                    .getSingleResult();
        },nomAnglais);
    }

    @Override
    public Long nombrePaysNeParlantPasALaFois(List<Langue> langues) {

        return JpaUtils.doInReadOnlyTransaction((em,langs)->{
            Map<String,Langue> params=langs.stream().collect(Collectors.toMap(l->"langue"+langs.indexOf(l), Function.identity()));
            String conditions=params.keySet().stream().map(key->":"+key+" MEMBER OF p.langues ").collect(Collectors.joining(" AND "));
            String whereClause=!conditions.isEmpty()?"where "+conditions:"";
            String select1= "select p from Pays p "+whereClause;
            TypedQuery<Long> query=em.createQuery("select count(p) from Pays p where p NOT IN ("+select1+")", Long.class);
            params.forEach(query::setParameter);
            return query.getSingleResult();
        },langues);
    }
    @SuppressWarnings("unused")
    public Long nombrePaysNeParlantPasALaFoisBis(List<Langue> langues) {
        return JpaUtils.doInReadOnlyTransaction((em,langs)->{
            Map<String,Langue> params=langs.stream().collect(Collectors.toMap(l->"langue"+langs.indexOf(l), Function.identity()));
            String conditions=params.keySet().stream().map(key->":"+key+" NOT MEMBER OF p.langues ").collect(Collectors.joining(" OR "));
            String whereClause=!conditions.isEmpty()?"where "+conditions:"";
            String queryString= "select count(p) from Pays p "+whereClause;
            TypedQuery<Long> query=em.createQuery(queryString, Long.class);
            params.forEach(query::setParameter);
            return query.getSingleResult();
        },langues);
    }

    @Override
    public Long nombrePaysNeParlantPas(List<Langue> langues) {
        return JpaUtils.doInReadOnlyTransaction((em,langs)->{
            Map<String,Langue> params=langs.stream().collect(Collectors.toMap(l->"langue"+langs.indexOf(l), Function.identity()));
            String conditions=params.keySet().stream().map(key->":"+key+" NOT MEMBER OF p.langues ").collect(Collectors.joining(" AND "));
            String whereClause=!conditions.isEmpty()?"where "+conditions:"";
            String queryString= "select count(p) from Pays p "+whereClause;
            TypedQuery<Long> query=em.createQuery(queryString, Long.class);
            params.forEach(query::setParameter);
            return query.getSingleResult();
        },langues);
    }


    @Override
    public Long nombrePaysNeParlantPas(Langue lang) {
        /*
        pas besoin de distinct en jpql pour selectionner toute une entité, car on utilise des PK sur une entité et chaque ligne est distinct grâce à l'identifiant
         select pays.french from pays where pays.id NOT IN (select pays.id from pays left join pays_langue pl on pays.id=pl.pays_id WHERE pl.langue_id=(select id from langue where nom="Anglais") )
        */
        return JpaUtils.doInReadOnlyTransaction((em,langue)->{
            return em.createQuery("select count(p) from Pays p where p.id NOT IN  (select p from Pays p join p.langues pl where pl=:lang)", Long.class)
                    .setParameter("lang",langue)
                    .getSingleResult();
        },lang);
    }
    @Override
    public Long nombrePaysNeParlantPas(String lang) {
        /*
         select pays.french from pays where pays.id NOT  IN (select pays.id from pays left join pays_langue pl on pays.id=pl.pays_id WHERE pl.langue_id=(select id from langue where nom="Anglais") )
        */
        EntityManager em= em();
        em.getTransaction().begin();
        Long nbPaysAvecLangues =  em.createQuery("select count(p) from Pays p where p.id NOT IN  (select p.id from Pays p join p.langues pl where pl.nom=:lang)", Long.class)
                .setParameter("lang",lang)
                .getSingleResult();

        em.getTransaction().rollback();
        em.close();
        return nbPaysAvecLangues;
    }

      /*
        PREPARE stmt1 FROM
        'select count(pays0_.id) as col_0_0_ from pays pays0_ where (? in (select langues1_.langue_id from pays_langue langues1_ where pays0_.id=langues1_.pays_id)) and (? in (select langues2_.langue_id from pays_langue langues2_ where pays0_.id=langues2_.pays_id))';
         SET @a = 1;   SET @b = 2;
        EXECUTE stmt1 USING @a, @b;
       */

    @Override
    public Long nombrePaysParlant(List<Langue> langues) {
        return JpaUtils.doInReadOnlyTransaction((em,langs)->{
            Map<String,Langue> params=langs.stream().collect(Collectors.toMap(l->"langue"+langs.indexOf(l)+1, Function.identity()));
            String conditions=params.keySet().stream().map(key->":"+key+" MEMBER OF p.langues ").collect(Collectors.joining(" AND "));
            String whereClause=!conditions.isEmpty()?"where "+conditions:"";
            String queryString= "select count(p) from Pays p "+whereClause;
            TypedQuery<Long> query=em.createQuery(queryString, Long.class);
            params.forEach(query::setParameter);
            return query.getSingleResult();
        },langues);
    }


    @Override
    public Long nombrePaysParlant(Langue langue) {
       return JpaUtils.doInReadOnlyTransaction((em,lang)->{
            return em.createQuery("select count(p) from Pays p join p.langues pl where pl=:langue", Long.class)
                    .setParameter("langue",lang)
                    .getSingleResult();
        },langue);

    }

    @Override
    public Long nombrePaysParlant(String langue) {
        /*
        SQL select pays.id from pays left join pays_langue pl on pays.id=pl.pays_id WHERE pl.langue_id=(select id from langue where nom="Anglais")
        */
        EntityManager em= em();
        em.getTransaction().begin();
        Long nbPaysAvecLangues =em.createQuery("select count(p) from Pays p join p.langues pl where pl.nom=:langue", Long.class)
                .setParameter("langue",langue)
                .getSingleResult();
        em.getTransaction().rollback();
        em.close();
        return nbPaysAvecLangues;
    }

    @Override
    public Long nombreDeLangueParlerPar(String paysFrenchName) {
        EntityManager em= em();
        em.getTransaction().begin();
        Long nbPaysAvecLangues =em.createQuery("select count(p) from Pays p left join p.langues pl where p.names.french=:paysFrenchName group by p.names.french", Long.class)
                .setParameter("paysFrenchName",paysFrenchName)
                .getSingleResult();
        em.getTransaction().rollback();
        em.close();
        return nbPaysAvecLangues;
    }

    @Override
    public Long nombrePaysSansLangue() {
        EntityManager em= em();
        em.getTransaction().begin();
        Long nbPaysSansLangues =em.createQuery("select count(distinct p) from Pays p left join p.langues pl where p.langues IS EMPTY ", Long.class)
                .getSingleResult();
        em.getTransaction().rollback();
        em.close();
        return nbPaysSansLangues;
    }

    @Override
    public Long nombrePaysAvecLangue() {
        EntityManager em= em();
        em.getTransaction().begin();
        Long nbPaysAvecLangues =em.createQuery("select count(distinct p) from Pays p join p.langues pl ", Long.class)
                        .getSingleResult();
        em.getTransaction().rollback();
        em.close();
        return nbPaysAvecLangues;
    }

    @Override
    public Integer nombreLanguePaysAvecLePlusDeLangue() {
        return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select max(p.langues.size) from Pays p", Integer.class).getSingleResult());
    }

    private EntityManager em(){
        return JpaUtils.getEmF().createEntityManager();
    }

    @Override
    public List<Pays> findAll() {
        return JpaUtils.doInReadOnlyTransaction((em,langue)->{
            return  em.createQuery("select p from Pays p",Pays.class)
                    .getResultList();
        },"");
    }

    @Override
    public List<Pays> loadAll() {
     return JpaUtils.doInReadOnlyTransaction(em->em.createQuery("select distinct p from Pays p left join fetch p.langues", Pays.class).getResultList());
    }


    /*
      SELECT count(*) total FROM pays;

       SELECT count(*) nbpsl FROM (
            SELECT p.french, count(pl.langue_id) as nbl FROM `pays` as p LEFT JOIN `pays_langue` as pl ON p.id=pl.pays_id GROUP BY p.french HAVING nbl<1 ORDER BY nbl
            )

            as pays_withoutlang
     */
}
