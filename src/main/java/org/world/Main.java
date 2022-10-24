package org.world;

import org.world.mapping.ExcelMapper;
import org.world.persistence.JpaUtils;
import org.world.persistence.entity.Langue;
import org.world.persistence.entity.Pays;
import org.world.persistence.repository.LangQueryRepositoryJPQL;
import org.world.persistence.repository.LangQueryRepository;
import org.world.persistence.repository.PaysQueryRepositoryJPQL;
import org.world.persistence.repository.PaysQueryRepository;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        System.out.println(ExcelMapper.isNumeric("0000"));
        String t=  "toto est (plus mature que titi c-a-d) meilleur";
        System.out.println(getExpressionInComma(t));
        System.out.println(cleanExpressionInComma(t));
        System.out.println(decodeText("Reo mā’ohi","UTF8"));
        PaysQueryRepository paysQueryRepositoryInterface=new PaysQueryRepositoryJPQL();
        LangQueryRepository langQueryRepository=new LangQueryRepositoryJPQL();
        System.out.println(paysQueryRepositoryInterface.paysAvecLongueurDeNomFrancais());
        List<Pays> paysList=paysQueryRepositoryInterface.paysDontLaLangueEstLaPlusParlee();
        List<Pays> paysList1=paysQueryRepositoryInterface.paysDontLaLangueEstLaMoinsParlee();
        System.out.println(paysList);
        List<Pays> paysList2=paysQueryRepositoryInterface.paysDontLaLangueEstLaPlusParlee();
        List<Langue> langueList=langQueryRepository.langueParlerParPlusNbPays(7);
        List<Langue> langueList2=langQueryRepository.langueLaMoinsParler();
        List<Langue> langueList3=langQueryRepository.langueLaPlusParler();

        Long langueList4=langQueryRepository.nombrePaysParlant("Portugais");
        // fillDB();
       // read();
        //SELECT pl.langue_id, sum(p.`population`) as pop FROM `pays` as p left outer join pays_langue as pl ON p.id=pl.pays_id left join langue l on pl.langue_id=l.id group by pl.langue_id

    }
    public static String decodeText(String input, String encoding)  {
        try {
            return
                    new BufferedReader(
                            new InputStreamReader(
                                    new ByteArrayInputStream(input.getBytes()),
                                    Charset.forName(encoding)))
                            .readLine();
        } catch (IOException e) {
            e.printStackTrace();
           return null;
        }
    }

    public static void read(){
        PaysQueryRepository paysQueryRepositoryInterface=new PaysQueryRepositoryJPQL();
        LangQueryRepository langQueryRepositoryInterface=new LangQueryRepositoryJPQL();

        System.out.println("Avec Langue "+paysQueryRepositoryInterface.nombrePaysAvecLangue()+" ou "+paysQueryRepositoryInterface.paysAvecLangue().size()+" "+paysQueryRepositoryInterface.paysParlantAumoinsUneLangue().size());
        System.out.println("Sans Langue "+paysQueryRepositoryInterface.nombrePaysSansLangue()+" ou "+paysQueryRepositoryInterface.paysSansLangue().size());
        System.out.println("\n\n");
        System.out.println("Nb Langue parlé au Cam "+paysQueryRepositoryInterface.nombreDeLangueParlerPar("Cameroun"));
        System.out.println("\n\n");
        //nombrePaysNeParlantPas
        System.out.println("Nb Pays Qui parle Anglais "+paysQueryRepositoryInterface.nombrePaysParlant("Anglais"));
        System.out.println("Nb Pays Qui parle Anglais "+paysQueryRepositoryInterface.nombrePaysParlant(langQueryRepositoryInterface.charge("Anglais")));
        System.out.println("\n\n");
        System.out.println("Nb Pays Qui ne parle Anglais pas "+paysQueryRepositoryInterface.nombrePaysNeParlantPas("Anglais"));
        System.out.println("Nb Pays Qui ne parle Anglais pas "+paysQueryRepositoryInterface.nombrePaysNeParlantPas(langQueryRepositoryInterface.charge("Anglais")));

        System.out.println("\nNb Pays Qui parle Anglais ou Français "+paysQueryRepositoryInterface.nombrePaysParlant(Arrays.asList(langQueryRepositoryInterface.charge("Anglais"),langQueryRepositoryInterface.charge("Français"))));
        System.out.println("Nb Pays Qui ne parle ni Anglais ni Français "+paysQueryRepositoryInterface.nombrePaysNeParlantPas(Arrays.asList(langQueryRepositoryInterface.charge("Anglais"),langQueryRepositoryInterface.charge("Français"))));
        System.out.println("Nb Pays Qui ne parle pas à la fois Anglais et Français "+paysQueryRepositoryInterface.nombrePaysNeParlantPasALaFois(Arrays.asList(langQueryRepositoryInterface.charge("Anglais"),langQueryRepositoryInterface.charge("Français"))));
        System.out.println("Pays avec le plus de langue: "+paysQueryRepositoryInterface.paysAvecLePlusDeLangue().stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nNombre de Pays avec le plus de langue: "+paysQueryRepositoryInterface.nombreLanguePaysAvecLePlusDeLangue());
        System.out.println("Pays avec code Alpha 2 AR : "+paysQueryRepositoryInterface.paysAvecAlpha2("AR").getNames().getFrench());
        System.out.println("Pays avec code Alpha 3 ZAF : "+paysQueryRepositoryInterface.paysAvecAlpha3("ZAF").getNames().getFrench());
        System.out.println("Pays avec nom Anglais French Guiana : "+paysQueryRepositoryInterface.paysAvecNomAnglais("French Guiana").getNames().getFrench());
        System.out.println("Pays avec nom Français Haiti: "+paysQueryRepositoryInterface.paysAvecNomFrancais("Haiti").getNames().getFrench());
        System.out.println("\nTotale Pays: "+paysQueryRepositoryInterface.loadAll().size()+" : "+paysQueryRepositoryInterface.toutLesPays().size());
        System.out.println("\nPays ayant au plus de 5 langue " + paysQueryRepositoryInterface.paysAyantAuPlusNbLangue(5L).size());
        System.out.println("\nPays ayant au moins langue 5langue " + paysQueryRepositoryInterface.paysAyantAuMoinsNbLangue(5L).stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays ayant au moins langue 5langue " + paysQueryRepositoryInterface.paysAyantAuMoinsNbLangue(5L).stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays avec list de langue " + paysQueryRepositoryInterface.paysAvecListDeLangue().size());
        System.out.println("\nPays avec Liste Langue " + paysQueryRepositoryInterface.paysAvecListDeLangue().size());
        System.out.println("\nPays avec Nb Langue " + paysQueryRepositoryInterface.paysAvecNombreDeLangue().size());
        System.out.println("\n Pays parlant à la fois  " + paysQueryRepositoryInterface.paysParlantAlaFois(List.of(langQueryRepositoryInterface.charge("Espagnol"),langQueryRepositoryInterface.charge("Anglais"))).stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays parlant aumoins une des langues entre : français et espagnol  " + paysQueryRepositoryInterface.paysParlantAumoinsUne(List.of("Français","Espagnol")).stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays avec plus de 10 lettre dans leurs nom français  " + paysQueryRepositoryInterface.paysAvecPlusNbLettreDansLeurNomFrancais(10).stream().map(e->e.getNames().getFrench()).toList());
        System.out.println("\npremier pays dont la langue est plus parlée "+paysQueryRepositoryInterface.premierPaysAuNomFrancaisLePlusLong().getNames().getFrench());
        System.out.println("\nPays au nom français plus long  " + paysQueryRepositoryInterface.paysAuNomFrancaisLePlusLong().stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays parlant "+paysQueryRepositoryInterface.paysParlant("Portugais").stream().map(p->p.getNames().getFrench()).toList());
        System.out.println("\nPays BENIN Langue "+paysQueryRepositoryInterface.paysAvecNomFrancais("Benin").getLangues());
    }


    public static void fillDB(){
        String directory="/home/citydevweb/PT/worlCountries/src/main/resources/data";
        String filename ="Liste-Excel-des-pays-du-monde-gratuit-capitales-continent-nationalites.xlsx";
        Map<String,List<String>> countryByLangueComplete= ExcelMapper.countriesByLangueComplete(directory+"/paysLanguesOtherSource");
        Map<String,List<String>> langueByCountry=ExcelMapper.countriesByLangue(directory+"/wikipedia.lang");
        Map<String,List<String>> countryByLangue=reverseToCountryLangueMap(langueByCountry);

        List<Pays> countries=ExcelMapper.read(directory+"/"+filename);
        Map<String,List<String>> countriesByLangue=mergeCountryLangueMap(countryByLangue,countryByLangueComplete);

        Map<String,Long> countriesPopulation=ExcelMapper.populationByCountry(directory+"/pop");
        LangQueryRepository lQRepository=new LangQueryRepositoryJPQL();
        Map<String,Langue> langues= Optional.ofNullable(lQRepository.findAll())
                .map(langs->langs.stream().collect(Collectors.toMap(Langue::getNom, Function.identity())))
                .orElse(new HashMap<>());

        //Duplicate entry 'Français' for key 'UK_39hkpk548907voffs0cxdt8d3' love is crazy , love is easy

        countries.forEach(country->{
            String nomPays =country.getNames().getFrench();

            countriesByLangue.forEach((pays, listLangues) -> {

                if(estCePareil(nomPays,pays)){
                    listLangues.stream().map(langue->Langue.builder().nom(langue.trim()).build()).forEach(langue -> {

                        if(langues.containsKey(langue.getNom())){
                            country.addLangue(langues.get(langue.getNom()));
                        }else {
                            langues.put(langue.getNom(),langue);
                            country.addLangue(langue);
                        }

                    });
                }

                Long pop = getPaysPopulation(countriesPopulation,country);
                country.setPopulation(pop);
            });
        });

        EntityManager em= JpaUtils.getEmF().createEntityManager();
        em.getTransaction().begin();
        try {
            countries.forEach(pays -> {
                System.out.println(pays.getNationality());
                if(pays.getLangues()!=null)
                    pays.getLangues().forEach(System.out::println);
                em.persist(pays);
            });
        }catch (Exception e){
            e.printStackTrace();
            em.getTransaction().rollback();
        }


       // countries.forEach(pays ->  persist(pays,em));
        em.getTransaction().commit();
    }

    public static void persist(Pays country,EntityManager em){
        try {
            if(!em.getTransaction().isActive())em.getTransaction().begin();

            System.out.println(country.getNationality());
            if(country.getLangues()!=null){
                country.getLangues().forEach(System.out::println);
            }
            em.persist(country);
            em.getTransaction().commit();
        }catch (Exception e){
            System.out.println(" \n CATCH EXCEPTION");
            e.printStackTrace();
            System.out.println("EXCEPTION");
            System.out.println(country.getNames());
            System.out.println(country.getNationality());
            if(country.getLangues()!=null)
           country.getLangues().forEach(System.out::println);
           // em.getTransaction().rollback();
        }

    }

    static String toASCII(String text) {
        char[] output = new char[4 * text.length()];
        // ASCIIFoldingFilter.foldToASCII(text.toCharArray(), 0, output, 0, text.length());
        return new String(output).trim();
    }

    static Map<String,List<String>>  reverseToCountryLangueMap(Map<String,List<String>> langueByCountryMap){
        Map<String,List<String>>  result=new HashMap<>();
      List<String> countries= langueByCountryMap.values().stream().flatMap(Collection::stream).toList();
      countries.forEach(pays->{
          List<String> langueList=new ArrayList<>();
          langueByCountryMap.forEach((langue,paysList)->{
              if(paysList.contains(pays)){
                  langueList.add(langue);
              }
          });
          result.put(pays,langueList);
      });
      return result;
    }
    static Map<String,List<String>>  mergeCountryLangueMap(Map<String,List<String>> map1,Map<String,List<String>> map2){
       Map<String,List<String>> main,complete,result;

        if(map1.size()>=map2.size()){
            main=map1; complete=map2;
        }else {
            main=map2; complete=map1;
        }
        result=new HashMap<>(main.size());

        main.forEach((pays,langues)->{
            List<String> completeLangues=complete.get(pays);
            if(completeLangues==null){
                String rightKey= complete.keySet().stream().filter(completePays->estCePareil(completePays,pays)).findFirst().orElse(null);
                completeLangues=complete.get(rightKey);
            }
            result.put(pays,mergeLangues(completeLangues,langues));
        });
       return result;
    }

    public static List<String>  mergeLangues(List<String> langues1,List<String> langues2){
        List<String> main,complete,result;
        if(langues1==null){
            return new ArrayList<>(langues2);
        }

        if(langues2==null){
            return new ArrayList<>(langues1);
        }

        if(langues1.size()>langues2.size()){
            main=langues1; complete=langues2;
        }else{
            main=langues2; complete=langues1;
        }
        result=new ArrayList<>(main);

        complete.forEach(completeLang->{
            if(!contains(result,completeLang)){
               result.add(completeLang);
            }
        });
      return result;
    }

    public static boolean contains(List<String> list,String search){
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.NO_DECOMPOSITION);
       return list.stream().anyMatch(value->instance.equals(value,search));
    }

    public static Long getPaysPopulation(Map<String,Long> countriesPopulation,Pays country){
        Long pop = countriesPopulation.get(country.getNames().getFrench());

        if(pop==null) {
            //s -> s.toLowerCase().contains(french.toLowerCase()) || french.toLowerCase().contains(s.toLowerCase()
            String french = country.getNames().getFrench();
            String goodKey = countriesPopulation.keySet().stream()
                    .filter(s->estCePareil(s,french))
                    .findFirst().orElse(null);
            pop = countriesPopulation.get(goodKey);

            if(pop==null){
                //s -> s.toLowerCase().contains(english.toLowerCase()) || english.toLowerCase().contains(s.toLowerCase())
                String english = country.getNames().getEnglish();
                goodKey = countriesPopulation.keySet().stream()
                        .filter(s->estCePareil(s,english))
                        .findFirst().orElse(null);
                pop = countriesPopulation.get(goodKey);
            }
        }
        return pop;
    }
    public static boolean estCePareil(String nomPays1, String nomPays2){
        String pays1=nomPays1.toLowerCase(); String pays2=nomPays2.toLowerCase();
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.NO_DECOMPOSITION);

       return instance.equals(pays1,pays2)
               || pays1.contains(pays2)
               || pays2.contains(pays1)
               || cleanExpressionInComma(pays1).contains(pays2)
               || cleanExpressionInComma(pays2).contains(pays1)
               || estCePareilAvecPatch(pays1,pays2);
    }
    public static boolean estCePareilAvecPatch(String pays1, String pays2){
        String pays1Patch= getExpressionInComma(pays1) + cleanExpressionInComma(pays1);
        String pays2Patch= getExpressionInComma(pays2) + cleanExpressionInComma(pays2);
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.NO_DECOMPOSITION);
        return instance.equals(pays1Patch,pays2) || instance.equals(pays2Patch,pays1)
                || pays2.contains(pays1Patch) || pays1.contains(pays2Patch);
    }


    static String cleanExpressionInComma(String str){
      return str.replace("("+getExpressionInComma(str)+")","");
    }
    static String getExpressionInComma(String str){
        return hasExpressionInComma(str)? str.substring(str.indexOf("(")+1,str.indexOf(")")):"";
    }

    public static boolean hasExpressionInComma(String str){
        return str.contains("(") && str.contains(")");
    }



}