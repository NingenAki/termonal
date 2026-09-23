# Termonal

Termonal is a terminal-based word game inspired by Wordle, built with Java and Spring Boot. It runs in the terminal with a matrix-style animated background and supports multiple game modes: single, duo, and quartet.

## Features

- Terminal UI built with Java and TUI4J
- Animated matrix-style background
- Guessing gameplay in a Wordle-like interface
- Multiple board modes:
  - Single word
  - Duo words
  - Quartet words
- Keyboard navigation and letter input
- Persistent game state and user model scaffolding for future account features
- Spring Boot app structure ready for local and Supabase/PostgreSQL configuration

## Tech Stack

- Java 21
- Spring Boot 4
- Maven
- PostgreSQL / Supabase
- TUI4J for terminal rendering

## Project Structure

```text
src/
  main/
    java/
      ningenaki/inc/termonal/
        components/      # terminal UI components
        configs/         # app bootstrap and runner
        enums/           # style and palette definitions
        models/          # JPA entities
        repositories/    # repositories
        singletons/      # shared word dictionary
        states/          # game state classes
        utils/           # helpers
    resources/
      application.properties
      application-local.properties
      palavras5.json
```

## Prerequisites

- JDK 21+
- Maven 3.9+
- A PostgreSQL-compatible database (the project is configured for Supabase/Postgres)

## Running the App

From the project root:

```bash
./mvnw spring-boot:run
```

If you are using the local profile, the app will pick up the configuration from `src/main/resources/application-local.properties`.

## Controls

- Arrow keys: move cursor
- Backspace/Delete: clear letters
- Enter: submit guess
- Tab / Shift+Tab: switch tabs
- Esc: quit game

## Gameplay

The game presents a word grid with multiple simultaneous boards depending on the selected mode. Each board lets the player submit valid 5-letter words, and the app tracks progress, wins, and game-over states.

## Notes

- The project is still under active development.
- The database connection is configured through Spring properties and Supabase host settings.
- The word list used by the game is provided in the resources folder.
