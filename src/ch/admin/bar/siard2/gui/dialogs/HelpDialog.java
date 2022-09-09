package ch.admin.bar.siard2.gui.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;

import ch.admin.bar.siard2.gui.*;
import ch.enterag.utils.fx.*;
import ch.enterag.utils.logging.*;

import java.text.SimpleDateFormat;

public class HelpDialog extends ScrollableDialog {

    private static IndentLogger _il = IndentLogger.getIndentLogger(HelpDialog.class.getName());
    private static SiardBundle sb = SiardBundle.getSiardBundle();

    private HelpDialog(Stage stage) {
        super(stage, sb.getHelpTitle());

        VBox vboxTitle = systemInfoBox();

        StackPane root = new StackPane();
        root.getChildren().add(vboxTitle);

        Scene scene = new Scene(root, 480, 320);
        setScene(scene);
    }

    public static void showHelpDialog(Stage parent) {
        new HelpDialog(parent).showAndWait();
    }

    private VBox systemInfoBox() {
        Text txtTitle = new Text(SiardGui.getApplication() + " " + SiardGui.getVersion());
        SimpleDateFormat sdf = new SimpleDateFormat(sb.getDateFormat());
        Text txtCopyright = new Text("©" + sdf.format(SiardGui.getPublicationDate()) + " " + SiardGui.getCopyright());
        Text txtSubject = new Text(sb.getInfoSubject());

        double dMinWidth = 0.0;
        double dTextWidth = FxSizes.getTextWidth(txtTitle.getText());
        if (dMinWidth < dTextWidth)
            dMinWidth = dTextWidth;

        dTextWidth = FxSizes.getTextWidth(txtCopyright.getText());
        if (dMinWidth < dTextWidth)
            dMinWidth = dTextWidth;

        dTextWidth = FxSizes.getTextWidth(txtSubject.getText());
        if (dMinWidth < dTextWidth)
            dMinWidth = dTextWidth;
        /* VBox for title area */
        VBox vboxTitle = new VBox();
        vboxTitle.setPadding(new Insets(dINNER_PADDING));
        vboxTitle.setSpacing(dHSPACING);
        vboxTitle.setAlignment(Pos.TOP_CENTER);
        vboxTitle.getChildren().addAll(txtTitle, txtCopyright, txtSubject);
        vboxTitle.setMinWidth(dMinWidth);
        return vboxTitle;
    } /* createVBoxTitle */

}
