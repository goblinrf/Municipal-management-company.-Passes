package company.desktop.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(Long id, String street, String entrance) { @Override
public String toString() {
    return street + ", подъезд " + entrance;
}}
