package electricMeters.core.controls;

import electricMeters.core.DataType;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.json.JSONObject;

import java.util.Objects;

class JsonEditCell extends TableCell<JSONObject, Object> {

    private final TextField textField = new TextField();
    private final JsonColumn column;

    JsonEditCell(JsonColumn column) {
        this.column = column;
        textField.getStyleClass().add("table-text-field");

        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (getMask() != null && getMask().matches(change.getControlNewText())) {
                return change;
            }
            return null;
        }));
        textField.setOnAction(evt -> commitEditFromTextField());
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEditFromTextField();
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                textField.setText(getDataType().toString(getItem(), getFormat()));
                cancelEdit();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP) {
                commitEditFromTextField();
                event.consume();
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex() - 1, getTableColumn());
            }
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.ENTER) {
                commitEditFromTextField();
                event.consume();
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex() + 1, getTableColumn());
            }
        });
    }

    private void commitEditFromTextField() {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            commitEdit(null);
        } else {
            commitEdit(getDataType().fromString(text));
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setAlignment(column.getAlignment());
        if (getItem() != null) {
            textField.setText(column.getDataType().toString(getItem(), column.getFormat()));
        }
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        getStyleClass().add("editing-table-cell");
        textField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        getStyleClass().remove("editing-table-cell");
    }

    // commits the edit. Update property if possible and revert to text display
    @Override
    public void commitEdit(Object item) {
        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing() && !Objects.equals(item, getItem())) {
            TableView<JSONObject> table = getTableView();
            if (table != null) {
                TableColumn<JSONObject, Object> column = getTableColumn();
                TableColumn.CellEditEvent<JSONObject, Object> event = new TableColumn.CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
        getStyleClass().remove("editing-table-cell");
    }

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            // так пишут только говнокодеры
            switch (getDisplayType()) {
                case DEFAULT -> {
                    if (!String.valueOf(item).isEmpty()) {
                        String text = getDataType().toString(item, getFormat());
                        setText(text);
                    } else {
                        setText("");
                    }
                }
                case CHECK_BOX -> {
                    CheckBox checkBox = new CheckBox();
                    checkBox.getStyleClass().add("table-check-box");
                    checkBox.setDisable(true);
                    checkBox.setSelected(!item.toString().isEmpty() && Integer.parseInt(item.toString()) == 1);
                    setGraphic(checkBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
                case HYPERLINK -> {
                    if (!String.valueOf(item).isEmpty()) {
                        String text = getDataType().toString(item, getFormat());
                        Hyperlink hyperlink = new Hyperlink(text);
                        hyperlink.setOnAction(event -> column.getHyperlinkAction().accept(text));
                        setGraphic(hyperlink);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        setGraphic(null);
                        setText("");
                    }
                }
            }
        } else {
            setText("");
            setGraphic(textField);
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        boolean rowSelected = getTableView().getSelectionModel().getSelectedIndices().contains(getTableRow().getIndex());
        getTableRow().pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), rowSelected);
    }

    private DataType getDataType() {
        return column.getDataType();
    }

    private String getFormat() {
        return column.getFormat();
    }

    private JsonColumn.DisplayType getDisplayType() {
        return column.getDisplayType();
    }

    private Mask getMask() {
        return column.getMask();
    }

}
