package org.world.persistence;

import lombok.Getter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JpaUtils {

    static EntityManagerFactory emf;

    public  static EntityManagerFactory getEmF(){
        if(emf==null){
            emf= Persistence.createEntityManagerFactory("WorldPersistenceUnit");
        }
        return emf;
    }
    public  static EntityManagerFactory newEmfInstance(){
        return Persistence.createEntityManagerFactory("WorldPersistenceUnit");
    }

    public static void close(){
        if(emf!=null){
            emf.close();
            emf=null;
        }
    }

    public static <T> void doInReadOnlyTransaction(BiConsumer<EntityManager,T> consumer, T param){
        EntityManager em= JpaUtils.getEmF().createEntityManager();
        em.getTransaction().begin();
        consumer.accept(em,param);
        em.getTransaction().rollback();
    }
    public static <T,R> R doInReadOnlyTransaction(BiFunction<EntityManager,T,R> function, T param){
        EntityManager em= JpaUtils.getEmF().createEntityManager();
        em.getTransaction().begin();
        R result= function.apply(em,param);
        em.getTransaction().rollback();
        return result;
    }
    public static <R> R doInReadOnlyTransaction(Function<EntityManager,R> function){
        EntityManager em= JpaUtils.getEmF().createEntityManager();
        em.getTransaction().begin();
        R result= function.apply(em);
        em.getTransaction().rollback();
        return result;
    }

    public static <R,T> Map<R,T> toMap(Function<T,R> keyMapper, List<T> tList){
        return  tList.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }
    public static <T> Map<String,T> toMap(List<T> tList){
        return  toMap(l->"langue"+tList.indexOf(l)+1,tList);
    }
    public static <T>String fromMapToCondition(Map<String,T> params,String condition,Clause clause){
        return  params.keySet().stream().map(key->":"+key+wrapWithSpace(condition)).collect(Collectors.joining(clause.wrapWithSpace()));
    }
    public static String wrapWithSpace(String value){
        return " "+value+" ";
    }
    @Getter
    public enum Clause{
        OR("OR"),AND("AND");
        final String value;
        Clause(String value){
            this.value=value;
        }
        public String wrapWithSpace(){
            return " "+value+" ";
        }
    }

    public static <T,R> R doInWriteTransaction(BiFunction<EntityManager,T,R> function, T param){
        EntityManager em= JpaUtils.getEmF().createEntityManager();
        R result = null;
        em.getTransaction().begin();
        try {
             result= function.apply(em,param);
        }catch (Exception e){
            em.getTransaction().rollback();
        }
        em.getTransaction().commit();
        return result;
    }

    public static <T> void doInWriteTransaction(BiConsumer<EntityManager,T> consumer,T param){
        EntityManager em= JpaUtils.getEmF().createEntityManager();
        em.getTransaction().begin();
        try {
            consumer.accept(em,param);
        }catch (Exception e){
            em.getTransaction().rollback();
        }

        em.getTransaction().commit();
    }
}
