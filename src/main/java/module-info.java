// MapCycleBuilder
// Copyright (c) 2025 Jericho Crosby (Chalwk)
// Licensed under the MIT License.

module com.chalwk {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.chalwk to javafx.fxml;
    exports com.chalwk;
}