package de.hsos.swa.jonas.theater.playmanagement.boundary.dto;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class QueryParametersDTO {
    public String nameFilter;
    public ArrayList<String> statusFilter;
    public ArrayList<String> playTypeFilter;
    public ArrayList<String> performanceTypeFilter;
    public LocalDateTime startDateTimeFilter;
    public LocalDateTime endDateTimeFilter;
    public String include;
    public Long pageNumber;
    public Long pageSize;

    public QueryParametersDTO() {
    }

    public QueryParametersDTO(String nameFilter, ArrayList<String> statusFilter, ArrayList<String> playTypeFilter, ArrayList<String> performanceTypeFilter, String startDateTimeFilter, String endDateTimeFilter, String include, Long pageNumber, Long pageSize) {
        this.nameFilter = nameFilter;
        this.statusFilter = statusFilter;
        this.playTypeFilter = playTypeFilter;
        this.performanceTypeFilter = performanceTypeFilter;
        parseDates(startDateTimeFilter, endDateTimeFilter);
        this.include = include;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    private void parseDates(String startDateTimeFilter, String endDateTimeFilter){

        if(startDateTimeFilter != null && !startDateTimeFilter.isEmpty()){
            this.startDateTimeFilter = LocalDateTime.parse(startDateTimeFilter);
        }
        if(endDateTimeFilter != null && !endDateTimeFilter.isEmpty()){
            this.endDateTimeFilter = LocalDateTime.parse(endDateTimeFilter);
        }
        if (this.endDateTimeFilter != null && this.startDateTimeFilter!= null&& this.endDateTimeFilter.isBefore(this.startDateTimeFilter)) {
            LocalDateTime temp = this.endDateTimeFilter;
            this.endDateTimeFilter = this.startDateTimeFilter;
            this.startDateTimeFilter = temp;
        }
    }
}
