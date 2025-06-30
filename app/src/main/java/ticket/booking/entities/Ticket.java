package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket
{
    @JsonProperty("ticket_id")
    private String ticketId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("source")
    private String source;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("date_of_travel")
    private String dateOfTravel;

    @JsonProperty("train")
    private Train train;

    @JsonProperty("ticket_info")
    private String ticketInfo;

    @JsonProperty("seat_row")
    private int seatRow;

    @JsonProperty("seat_col")
    private int seatCol;

    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, Train train, String ticketInfo, int seatRow, int seatCol) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
        this.ticketInfo = ticketInfo;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    public Ticket(Train train, int seatRow, int seatCol) {
        this.train = train;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    public Ticket(){};

    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s", ticketId, userId, source, destination, dateOfTravel);
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTrainId() {
        return ticketId;
    }

    public void setTrainId(String trainId) {
        this.ticketId = trainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateOfTravel() {
        return dateOfTravel;
    }

    public void setDateOfTravel(String dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(int seatCol) {
        this.seatCol = seatCol;
    }
}
