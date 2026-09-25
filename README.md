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
client/
  src/main/java/         # terminal UI and standalone client launcher
  src/main/resources/    # client assets
server/
  src/main/java/         # Spring Boot application, entities, repositories
  src/main/resources/    # server and database configuration
```

## Prerequisites

- JDK 21+
- Maven 3.9+
- A PostgreSQL-compatible database only for the optional server/database profile

## Running the App

Build both modules from the project root:

```bash
./mvnw clean package
```

The packaged application runs as a client by default and does not need database
credentials. This is the command to distribute to clients:

```bash
java -jar client/target/termonal-client-0.0.1-SNAPSHOT.jar
```

Database access is reserved for a trusted server/backend. Start that process
with the `database` profile and keep the password in its environment:

```bash
export SUPABASE_PASSWORD='your-database-password'
java -jar server/target/termonal-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=database
```

Optional database overrides are `SUPABASE_PROJECT` and `SUPABASE_HOST`.

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
- The database connection is configured through the optional `database` profile; never distribute its password with client JARs.
- The word list used by the game is provided in the resources folder.
