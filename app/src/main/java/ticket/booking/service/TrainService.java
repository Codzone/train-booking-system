package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;  // List holding all train data in memory
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";  // Path to train JSON

    // Constructor: Reads train data from JSON file into memory
    public TrainService() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }

    // Search trains that include both source and destination stations in the correct order
    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream()
                .filter(train -> validTrain(train, source, destination))
                .collect(Collectors.toList());
    }

    // Add or update a train in the train list
    public void addTrain(Train newTrain) {
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainNo()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);  // If train already exists, update it
        } else {
            trainList.add(newTrain);  // Otherwise, add as new
            saveTrainListToFile();
        }
    }

    // Update existing train details
    private void updateTrain(Train updatedTrain) {
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();  // Save updated list to file
        } else {
            // If train not found in list, fallback to adding it
            addTrain(updatedTrain);
        }
    }

    // Write the in-memory train list to the JSON file
    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Check if the train passes through source and destination in the right order
    private Boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();

        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());

        // Ensure both stations exist and source comes before destination
        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }
}
