package menon.cs6100.program2;

public class AgentStatistics {
	
	private int neighboringCellsExamined;
	private int numbersOfMarkersRead;
	private int numbersOfMarkersWritten;
	
	public AgentStatistics() {
		this.neighboringCellsExamined = 0;
		this.numbersOfMarkersRead = 0;
		this.numbersOfMarkersRead = 0;
	}

	int getNeighboringCellsExamined() {
		return neighboringCellsExamined;
	}

	void setNeighboringCellsExamined(int neighboringCellsExamined) {
		this.neighboringCellsExamined = neighboringCellsExamined;
	}

	int getNumbersOfMarkersRead() {
		return numbersOfMarkersRead;
	}

	void setNumbersOfMarkersRead(int numbersOfMarkersRead) {
		this.numbersOfMarkersRead = numbersOfMarkersRead;
	}

	int getNumbersOfMarkersWritten() {
		return numbersOfMarkersWritten;
	}

	void setNumbersOfMarkersWritten(int numbersOfMarkersWritten) {
		this.numbersOfMarkersWritten = numbersOfMarkersWritten;
	}
	

}
