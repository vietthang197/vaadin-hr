package com.thanglv.vaadin.views.crud;

import com.thanglv.vaadin.views.MainLayout;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Route;

/**
 * A Designer generated component for the crud-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("crud-view")
@JsModule("./crud-view.ts")
@Route(value = "crud", layout = MainLayout.class)
public class CrudView extends LitTemplate {

    /**
     * Creates a new CrudView.
     */
    @Id("btnSubmit")
    private Button submitBtn;

    @Id("fullName")
    private TextField fullName;

    @Id("phone")
    private TextField phone;

    @Id("address")
    private TextField address;

    public CrudView() {
        // You can initialise any data required for the connected UI components here.
        submitBtn.addClickListener(event -> {
            Notification.show("Họ và tên: " + fullName.getValue() + "\nSố điện thoại: " + phone.getValue() + "\nĐịa chỉ: " + address.getValue() , 5000, Notification.Position.MIDDLE);
        });
    }

}
