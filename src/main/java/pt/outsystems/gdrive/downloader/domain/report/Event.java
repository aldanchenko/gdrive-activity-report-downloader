package pt.outsystems.gdrive.downloader.domain.report;

import java.util.List;
import java.util.Objects;

/**
 * Audit Activities Report Item event.
 */
public class Event {

    private String type;
    private String edit;
    private List<Parameter> parameters;

    /**
     * Build string from {@link #parameters}.
     *
     * @return String
     */
    public String getParametersAsString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (Objects.nonNull(parameters)) {
            for (Parameter parameter : parameters) {
                String parameterName = parameter.getName();

                if (Objects.nonNull(parameterName)) {
                    stringBuilder.append(parameterName).append(",");
                }

                Boolean parameterBoolValue = parameter.getBoolValue();

                if (Objects.nonNull(parameterBoolValue)) {
                    stringBuilder.append(parameterBoolValue).append(",");
                }

                String parameterValue = parameter.getValue();

                if (Objects.nonNull(parameterValue)) {
                    stringBuilder.append(parameterValue);
                }

                stringBuilder.append(";");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Audit Activities Report Item Event parameter.
     */
    public static class Parameter {
        private String name;
        private Boolean boolValue;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getBoolValue() {
            return boolValue;
        }

        public void setBoolValue(Boolean boolValue) {
            this.boolValue = boolValue;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
