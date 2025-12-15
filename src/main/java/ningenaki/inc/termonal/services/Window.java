package ningenaki.inc.termonal.services;

import java.util.Arrays;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Window {
    int width = 80;
    int height = 24;

    char[][] matrix = new char[height][width];

    private void update() {
        char[] line = new char[width];
        Arrays.fill(line, 'A');
        Arrays.fill(matrix, line);
    }

    @Scheduled(fixedRate = 600)
    public void draw() {
        update();
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println();
        for(int i = 0; i< height ; i++) {
            for(int j = 0; j< width; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }
}