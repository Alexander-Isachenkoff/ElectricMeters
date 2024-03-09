package electricMeters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import electricMeters.core.DbHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

@Getter
@NoArgsConstructor
public class CompanyData {

    private static final String COMPANY_JSON_FILE = "company.json";

    private String consumerName;
    private String contractNumber;
    private String consumerAddress;
    private String consumerPhoneNumber1;
    private String consumerPhoneNumber2;
    private String responsiblePerson;
    private String position;
    private int rateTypeID;
    private int voltageLevelID;
    @JsonIgnore
    private String rateTypeName;
    @JsonIgnore
    private String voltageLevelName;

    public CompanyData(String consumerName, String contractNumber, String consumerAddress, String consumerPhoneNumber1, String consumerPhoneNumber2, String responsiblePerson, String position, int rateTypeID, int voltageLevelID) {
        this.consumerName = consumerName;
        this.contractNumber = contractNumber;
        this.consumerAddress = consumerAddress;
        this.consumerPhoneNumber1 = consumerPhoneNumber1;
        this.consumerPhoneNumber2 = consumerPhoneNumber2;
        this.responsiblePerson = responsiblePerson;
        this.position = position;
        this.rateTypeID = rateTypeID;
        this.voltageLevelID = voltageLevelID;
    }

    public static CompanyData getCompanyData() {
        File file = new File(COMPANY_JSON_FILE);
        if (!file.exists()) {
            new CompanyData().save();
        }
        try {
            CompanyData companyData = new ObjectMapper().readValue(file, CompanyData.class);
            JSONObject rateType = DbHandler.getInstance().selectById("CAT_RATE_TYPES", companyData.rateTypeID);
            JSONObject voltageLevel = DbHandler.getInstance().selectById("CAT_VOLTAGE_LEVELS", companyData.voltageLevelID);
            companyData.rateTypeName = (rateType == null) ? "" : rateType.getString("NAME");
            companyData.voltageLevelName = (voltageLevel == null) ? "" : voltageLevel.getString("NAME");
            return companyData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(COMPANY_JSON_FILE), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
