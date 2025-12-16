package ningenaki.inc.termonal.services;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Words {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("/src/main/resources/palavras5Letras.json");
    List<String> palavras5Letras;

    public String getRandomWord() {
        try {
            if (palavras5Letras == null) {
                palavras5Letras = mapper.readValue(file, new TypeReference<List<String>>() {
                });
            }
            int r = (int) (Math.random() * palavras5Letras.size());
            return palavras5Letras.get(r).toUpperCase();
        } catch (Exception ex) {
            log.error("Erro gerando palavras", ex);
            return "termo";
        }
    }
}