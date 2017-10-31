package org.tenbitworks.interlock;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class InterlockAccessDTO {
	private String tenbitId;
	private List<String> rfidList;
	
	private boolean trainingRequired;
	
	private long accessTimeMS;
}
