package ningenaki.inc.termonal.services;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Words {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("src/main/resources/palavras5Letras.json");
    List<String> list;
    HashSet<String> set;

    public String getRandomWord() {
        try {
            if (list == null) {
                list = mapper.readValue(file, new TypeReference<List<String>>() {
                });
                set = new HashSet<>();
                set.addAll(list);
            }
            int r = (int) (Math.random() * list.size());
            return list.get(r).toUpperCase();
        } catch (Exception ex) {
            log.error("Erro gerando palavras", ex);
            return "termo";
        }
    }
}