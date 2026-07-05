package Dchumon.view

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import scalafx.stage.Stage

class AboutController:

   
    var stage: Option[Stage] = None

    @FXML
    private var AboutText: TextArea = _ 

    @FXML
    def initialize(): Unit =
        
        AboutText.setEditable(false) // Set `TextArea` to read-only.
        AboutText.setWrapText(true) // Ensure the text wraps correctly.

   
    @FXML
    def handleClose(): Unit =
        // Optional close logic (if there is an associated close button).
        stage.foreach(_.close())