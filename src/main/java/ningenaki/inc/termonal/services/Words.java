package ningenaki.inc.termonal.services;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Words {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File("src/main/resources/palavras5.json");
    List<List<String>> lists;
    List<String> all;
    Map<String, String> map;
    private static Words instance;

    private Words() {
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

                map = all.stream().collect(
                        Collectors.toMap(value -> normalize(value), value -> value));
            }
        } catch (Exception ex) {
            log.error("Erro gerando palavras", ex);
        }
    }

    public static Words getInstance() {
        if(instance == null) instance = new Words();
        return instance;
    }

    public String get(String word) {
        return map.get(word);
    }

    public String normalize(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public boolean isWordValid(String word) {
        return map.containsKey(word);
    }

    public String getRandomWord() {
        int r = (int) (Math.random() * all.size());
        return all.get(r);
    }

    public String getWordOfDay(int index, boolean normalized) {
        String wordOfDay = getWordOfDay(index);
        return normalized ? normalize(wordOfDay) : wordOfDay;
    }

    public String getWordOfDay(int index /* 0-6, todas as variacoes do termo */) {
        long now = System.currentTimeMillis();
        now = TimeUnit.MILLISECONDS.toDays(now);
        int size = lists.get(1).size();
        Random random = new Random(now);
        for (int i = 0; i < index; i++)
            random.nextInt(size);
        int r = random.nextInt(size);
        return lists.get(1).get(r);
    }
}