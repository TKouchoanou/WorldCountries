package org.world.mapping;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.world.persistence.entity.Pays;
import org.world.references.Continent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ExcelMapper {
    @SneakyThrows
    public static List<Pays> read(String fileLocation){
        FileInputStream file = new FileInputStream(fileLocation);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        return StreamSupport.stream(sheet.spliterator(), false)
                .skip(4)
                .map(ExcelMapper::rowToEntity)
                .collect(Collectors.toList());
    }
    public static Map<String,List<String>> countriesByLangue(String fileLocation){
        Map<String,List<String>> result=new HashMap<>();
        try {
            BufferedReader reader=new BufferedReader(new FileReader(fileLocation));
            String line=reader.readLine();
            String langue="";
            while (line!=null){
                List<String> countries=new ArrayList<>();
                int i =line.indexOf("[["); int j=line.indexOf("]]<ref>");
                if(i>0 &&j>0){
                    langue=line.substring(i+2,j).trim();
                    result.put(langue,countries);
                }else{
                    String pays;
                    i=line.indexOf("{{");
                    j=line.indexOf("}}");
                    if(i>=0&&j>0){
                        pays=line.substring(i+2,j).replace("pays|","").replace("flag|","").trim();
                        result.get(langue).add(pays);
                    }
                }
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  result;
    }
 /*
    Map result = map.entrySet().stream()
	.sorted(Map.Entry.comparingByKey())
	.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
	(oldValue, newValue) -> oldValue, LinkedHashMap::new));
 */
    public static Map<String,List<String>> countriesByLangueComplete(String fileLocation) {
        Map<String,List<String>> result;
        try {
            String lineSeparator=System.lineSeparator();
            List<String> lineList=Files.readAllLines(Path.of(fileLocation)).stream().map(line->line.isEmpty()?lineSeparator:line).toList();
            String allData= String.join("",lineList).replace(" et ",",");
            List<String> paysWithLangues= Arrays.stream(allData.split(","+lineSeparator)).toList();
            Function<String,String[]> mapToPaysWithArray= paysWithLangue-> Arrays.stream(paysWithLangue.trim().split("\n")).filter(lang->!lang.isEmpty()).toArray(String[]::new);
            Collector<String[],?,Map<String,List<String>>> mapCollector =Collectors.toMap(paysLangueArray->paysLangueArray[0].trim(), paysLangueArray->Arrays.stream(paysLangueArray[1].split(",")).toList(), (langues1,langues2)->langues1);
            result= paysWithLangues.stream().map(mapToPaysWithArray).peek(ExcelMapper::cleanLangue).collect(mapCollector);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  result;
    }
    public static void cleanLangue(String[]paysWithLangs){
        paysWithLangs[1]=paysWithLangs[1].replace("de facto:","").replace("régionale:","").replace("localement:","");
    }
    public static Map<String,Long> populationByCountry(String fileLocation){
        Map<String,Long> result=new HashMap<>();
        try {
            BufferedReader reader=new BufferedReader(new FileReader(fileLocation));
            String line=reader.readLine();

            while (line!=null){
                List<String> list= Arrays.stream(line.split(" ")).toList();
                List<String> doublet=list.stream().filter(ExcelMapper::isNotNumeric)
                        .filter(s->!s.equals("\t"))
                        .filter(s->!s.isEmpty())
                        .filter(s->!s.equals("-")).map(s->s.replace("l'","")).toList();
                if(doublet.get(0).length()<3){
                    doublet=doublet.stream().skip(1).toList();
                }
                String pays= doublet.stream().limit(doublet.size()/2).collect(Collectors.joining(" ")).trim();
                Long population=Long.valueOf(list.stream().skip(1).filter(ExcelMapper::isNumeric).collect(Collectors.joining()).trim());
                result.put(pays,population);
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  result;
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public static boolean isNotNumeric(String strNum) {
        return !isNumeric(strNum);
    }
     static Pays rowToEntity(Row row){
        Pays.Codes codes= Pays.Codes
                .builder()
                .alpha2(row.getCell(0).getStringCellValue().trim())
                .alpha3(row.getCell(1).getStringCellValue().trim())
                .numerical(Math.round(row.getCell(2).getNumericCellValue()))
                .build();
        Pays.Names names=Pays.Names
                .builder()
                .french(row.getCell(3).getStringCellValue().trim())
                .english(row.getCell(4).getStringCellValue().trim())
                .build();
        Optional<Cell> capital=Optional.ofNullable(row.getCell(5));
         Optional<Cell> nationality=Optional.ofNullable(row.getCell(8));
        return Pays.builder()
                .codes(codes)
                .names(names)
                .capital(capital.map(Cell::getStringCellValue).map(String::trim).orElse(null))
                .continent(Continent.from(row.getCell(7).getStringCellValue().trim()))
                .nationality(nationality.map(Cell::getStringCellValue).map(String::trim).orElse(null))
                .independent(Objects.equals(row.getCell(6).getStringCellValue().trim(), "oui"))
                .build();
    }
    //pf décompose trop aussi une grande fonction en micro opération?? trim
}
