package ningenaki.inc.termonal.services;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Words {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("src/main/resources/palavras5.json");
    List<List<String>> lists;
    List<String> all;
    Map<String, String> normalized;

    public Words() {
        try {
            if (lists == null) {
                lists = mapper.readValue(file, new TypeReference<List<List<String>>>() {
                });
                lists.add(lists.get(0).stream().map(value -> value.toUpperCase()).collect(Collectors.toList()));
                lists.add(lists.get(1).stream().map(value -> value.toUpperCase()).collect(Collectors.toList()));
                lists.removeFirst();
                lists.removeFirst();

                all = new ArrayList<>();
                all.addAll(lists.get(0));
                all.addAll(lists.get(1));

                normalized = all.stream().collect(
                        Collectors.toMap(value -> normalize(value), value -> value));
                normalized.forEach((key, value) -> log.info(key + ": " + value));
            }
        } catch (Exception ex) {
            log.error("Erro gerando palavras", ex);
        }
    }

    public String normalize(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public boolean isWordValid(String word) {
        return normalized.containsKey(word);
    }

    public String getRandomWord() {
        int r = (int) (Math.random() * all.size());
        return all.get(r);
    }

    public String getWordOfDay(int index /* 0-7, todas as variacoes do termo */) {
        int r = 0; // basear no timestamp
        return lists.get(1).get(r);
    }
}