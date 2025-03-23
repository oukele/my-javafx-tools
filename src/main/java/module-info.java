module com.oukele.mytools {
    requires javafx.controls;
    requires javafx.fxml;
    requires cn.hutool;

    requires org.kordamp.bootstrapfx.core;
    requires juniversalchardet;

    opens com.oukele.mytools.controller to javafx.fxml;
    exports com.oukele.mytools;
}