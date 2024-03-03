package electricMeters.core.controls;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

public class EditCell<S, T> extends TableCell<S, T> {

    public static final StringConverter<Object> DEFAULT_CONVERTER = new StringConverter<Object>() {
        @Override
        public String toString(Object object) {
            return String.valueOf(object);
        }

        @Override
        public String fromString(String string) {
            return string;
        }
    };
    // Text field for editing
    // TODO: allow this to be a plugable control.
    private final TextField textField = new TextField();

    private final StringConverter<T> converter;

    public EditCell(StringConverter<T> converter) {
        textField.getStyleClass().add("table-text-field");

        this.converter = converter;

        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        textField.setOnAction(evt -> commitEdit(this.converter.fromString(textField.getText())));
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(textField.getText()));
            }
        });
        textField.setOnKeyTyped(event -> {
            if (event.getCharacter().equals("\r")) {
                commitEdit(converter.fromString(textField.getText()));
                event.consume();
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex() + 1, getTableColumn());
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                textField.setText(converter.toString(getItem()));
                cancelEdit();
                event.consume();
            }
            if (event.getCode() == KeyCode.UP) {
                commitEdit(converter.fromString(textField.getText()));
                event.consume();
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex() - 1, getTableColumn());
            }
            if (event.getCode() == KeyCode.DOWN) {
                commitEdit(converter.fromString(textField.getText()));
                event.consume();
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex() + 1, getTableColumn());
            }
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(converter.toString(getItem()));
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
    public void commitEdit(T item) {
        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
        getStyleClass().remove("editing-table-cell");
    }

}