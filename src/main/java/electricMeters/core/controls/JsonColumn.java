package electricMeters.core.controls;

import electricMeters.util.DateUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.converter.DateStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

@Setter
@Getter
public class JsonColumn extends TableColumn<JSONObject, Object> {

    private String field;
    private Pos alignment;
    private DataType dataType = DataType.DEFAULT;
    private DisplayType displayType = DisplayType.DEFAULT;
    private EditCell.Mask mask;

    public JsonColumn() {
        this.setEditable(false);
        this.setCellValueFactory(param -> {
            JSONObject jsonObject = param.getValue();
            Object value = jsonObject.has(field) ? dataType.dataConverter.apply(jsonObject.get(field)) : "";
            return new SimpleObjectProperty<>(value);
        });
        this.setCellFactory(param -> new EditCell<>(EditCell.DEFAULT_CONVERTER, mask) {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    // так пишут только говнокодеры
                    if (displayType == DisplayType.CHECK_BOX) {
                        CheckBox checkBox = new CheckBox();
                        checkBox.getStyleClass().add("table-check-box");
                        checkBox.setDisable(true);
                        checkBox.setSelected(!item.toString().isEmpty() && Integer.parseInt(item.toString()) == 1);
                        setGraphic(checkBox);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        if (!String.valueOf(item).isEmpty()) {
                            String text = dataType.stringConverter.apply(item);
                            setText(text);
                        } else {
                            setText("");
                        }
                    }
                } else {
                    setText("");
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                boolean rowSelected = getTableView().getSelectionModel().getSelectedIndices().contains(getTableRow().getIndex());
                getTableRow().pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), rowSelected);
            }

        });
        this.setOnEditCommit(event -> {
            TableView<JSONObject> tableView = event.getTableView();
            tableView
                    .getItems()
                    .get(event.getTablePosition().getRow())
                    .put(field, event.getNewValue());
            tableView.refresh();
        });
        styleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.endsWith(getAlignmentStyleString(this.alignment))) {
                setAlignment(this.alignment);
            }
        });
    }

    private static String getAlignmentStyleString(Pos alignment) {
        return "-fx-alignment: " + alignment + ";";
    }

    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
        setStyle((getStyle() == null ? "" : getStyle()) + getAlignmentStyleString(alignment));
    }

    @AllArgsConstructor
    public enum DataType {
        DEFAULT(),
        REAL(Constants.decimalFormat::format),
        DATE(
                object -> Constants.dateConverter.fromString(object.toString()),
                object -> Constants.dateConverter.toString((Date) object)
        ),
        DATE_TIME(
                object -> DateUtil.toLocalDateTime(object.toString()),
                object -> DateUtil.toString((LocalDateTime) object)
        ),
        MONTH(object -> DateUtil.monthName((Integer) object));

        private final Function<Object, Object> dataConverter;
        private final Function<Object, String> stringConverter;

        DataType(Function<Object, String> stringConverter) {
            this.dataConverter = Function.identity();
            this.stringConverter = stringConverter;
        }

        DataType() {
            this.dataConverter = Function.identity();
            this.stringConverter = Object::toString;
        }

        private static class Constants {
            private static final DateStringConverter dateConverter = new DateStringConverter("dd.MM.yy");
            private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");
        }
    }

    public enum DisplayType {
        DEFAULT, CHECK_BOX
    }
    
}
