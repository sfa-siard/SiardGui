package ch.admin.bar.siard2.gui.dialogs;

import ch.admin.bar.siard2.gui.SiardBundle;
import ch.admin.bar.siard2.gui.SiardGui;
import ch.admin.bar.siard2.gui.math.MaxOf;
import ch.enterag.utils.fx.FxSizes;
import ch.enterag.utils.fx.ScrollableDialog;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class HelpDialog extends ScrollableDialog {
    private static final SiardBundle sb = SiardBundle.getSiardBundle();

    private HelpDialog(Stage stage) {
        super(stage, sb.getHelpTitle());

        VBox root = new VBox(systemInfoBox(),
                             new Separator(Orientation.HORIZONTAL),
                             manualBox(),
                             new Separator(Orientation.HORIZONTAL),
                             okButtonBox());
        root.setSpacing(dHSPACING);
        root.setPadding(new Insets(10.0));
        Scene scene = new Scene(root);
        setScene(scene);
    }

    private Node manualBox() {
        Hyperlink manualLink = new Hyperlink(sb.getManualUrl());
        manualLink.setOnAction(actionEvent -> new SiardGui().openBrowser(sb.getManualUrlTarget()));
        VBox box = new VBox(new Text(sb.getManualText()), manualLink);
        box.setAlignment(Pos.TOP_CENTER);
        return box;
    }

    private Node okButtonBox() {
        Button btn = new Button(sb.getOk());
        btn.setDefaultButton(true);
        btn.setOnAction(actionEvent -> this.close());

        HBox hboxButton = new HBox(btn);
        hboxButton.setPadding(new Insets(dINNER_PADDING));
        hboxButton.setSpacing(dHSPACING);
        hboxButton.setAlignment(Pos.TOP_RIGHT);
        hboxButton.setMinWidth(FxSizes.getTextWidth(sb.getOk()));
        return hboxButton;
    }


    public static void showHelpDialog(Stage parent) {
        new HelpDialog(parent).showAndWait();
    }

    private VBox systemInfoBox() {
        Text txtTitle = new Text(SiardGui.getApplication() + " " + SiardGui.getVersion());
        SimpleDateFormat sdf = new SimpleDateFormat(sb.getDateFormat());
        Text txtCopyright = new Text("©" + sdf.format(SiardGui.getPublicationDate()) + " " + SiardGui.getCopyright());
        Text txtSubject = new Text(sb.getInfoSubject());

        VBox vboxTitle = new VBox(txtTitle, txtCopyright, txtSubject);
        vboxTitle.setPadding(new Insets(dINNER_PADDING));
        vboxTitle.setSpacing(dHSPACING);
        vboxTitle.setAlignment(Pos.TOP_CENTER);
        vboxTitle.setMinWidth(minWidthOf(txtTitle, txtCopyright, txtSubject));
        return vboxTitle;
    }

    private double minWidthOf(Text txtTitle, Text txtCopyright, Text txtSubject) {
        return new MaxOf(FxSizes.getTextWidth(txtTitle.getText()),
                         FxSizes.getTextWidth(txtCopyright.getText()),
                         FxSizes.getTextWidth(txtSubject.getText()))
                .get().doubleValue();
    }
}
