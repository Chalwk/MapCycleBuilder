# Map Cycle Generator

A JavaFX application for generating `mapcycle.txt` and `mapvotes.txt` files for game servers.

![Build Status](https://github.com/Chalwk/MapCycleBuilder/actions/workflows/build.yml/badge.svg)

## Features

- Drag and drop interface for managing map rotation
- Support for maps and gametypes
- Generates both map cycle and map vote files
- Validates server configuration settings
- Cross-platform compatibility

## Building

### Prerequisites
- Java JDK 8 or later
- JavaFX SDK
- Maven or Gradle (depending on your build setup)

### Build Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd map-cycle-generator
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```
   or if using Maven:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   ./gradlew run
   ```
   or
   ```bash
   java -jar build/libs/map-cycle-generator.jar
   ```

## Usage

1. Set your server directory where the files will be generated
2. Load your maps directory (containing .map files)
3. Load your gametypes directory (containing gametype folders)
4. Drag maps from the available list to the rotation list
5. Configure settings for each map entry as needed
6. Click "Generate Files" to create `mapcycle.txt` and `mapvotes.txt`

### File Formats

- **mapcycle.txt**: Simple `mapname:gametype` format for server rotation
- **mapvotes.txt**: `mapname:gametype:description` format with UTF-16 LE encoding for voting system

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.