package com.thanglv.vaadin.views.crud;

import com.thanglv.vaadin.dto.CompanyIndustryDto;
import com.thanglv.vaadin.views.MainLayout;
import com.thanglv.vaadin.vm.CreateCompanyIndustryVM;
import com.thanglv.vaadin.vm.SearchCompanyIndustryVM;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.shared.communication.PushMode;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.logging.log4j.util.Strings;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@PageTitle("Quản lý ngành nghề công ty")
@Route(value = "company-industry", layout = MainLayout.class)
public class CrudView extends VerticalLayout {

    private final FormLayout formLayout = new FormLayout();
    private final TextField industryNameInput = new TextField("Nhập tên ngành nghề cần tìm kiếm");
    private final Button searchBtn = new Button("Tìm kiếm");
    private final Button cleanFormBtn = new Button("Làm mới");
    private final Button deleteDataBtn = new Button("Xoá bản ghi");
    private final Button createDataBtn = new Button("Thêm mới");
    private final Dialog createDataDialog = new Dialog();

    public CrudView() {
        init();
    }

    private void init() {
        Binder<SearchCompanyIndustryVM> binder = new Binder<>();
        SearchCompanyIndustryVM searchCompanyIndustryVM = new SearchCompanyIndustryVM();

        industryNameInput.setId("name");
        binder.forField(industryNameInput).withValidator(Strings::isNotBlank, "Vui lòng nhập tên ngành nghề").bind(SearchCompanyIndustryVM::getName, SearchCompanyIndustryVM::setName);

        cleanFormBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cleanFormBtn.getStyle().set("margin-left", "1em");
        cleanFormBtn.setIcon(VaadinIcon.REFRESH.create());
        cleanFormBtn.addClickListener(event -> {
            industryNameInput.clear();
            binder.refreshFields();
        });


        formLayout.add(industryNameInput);
        // Create submit button

        searchBtn.setIcon(VaadinIcon.SEARCH.create());
        searchBtn.addClickListener(event -> {
            BinderValidationStatus<SearchCompanyIndustryVM> binderValidationStatus = binder.validate();
            if (binderValidationStatus.hasErrors()) {
               binderValidationStatus.notifyBindingValidationStatusHandlers();
                return;
            }
            try {
                binder.writeBean(searchCompanyIndustryVM);
                System.out.println("Input: " + searchCompanyIndustryVM.getName());
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            Notification.show("Bạn vừa submit tên ngành nghề: " + industryNameInput.getValue(), 3000, Notification.Position.MIDDLE);
        });
        // Add form layout and submit button to the main layout
        // Create a Div to center the button
        Div buttonContainer = new Div(searchBtn, cleanFormBtn);
        buttonContainer.setWidth("100%");

        setHorizontalComponentAlignment(Alignment.START, buttonContainer);

        deleteDataBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteDataBtn.setIcon(VaadinIcon.TRASH.create());
        deleteDataBtn.getStyle().set("margin-left", "1em");
        deleteDataBtn.setEnabled(false);

        createDataBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createDataBtn.setIcon(VaadinIcon.PLUS.create());
        createDataBtn.addClickListener(event -> {
            createDataDialog.open();
        });

        Div buttonContainer2 = new Div();
        buttonContainer2.setWidth("100%");
        buttonContainer2.add(createDataBtn);
        buttonContainer2.add(deleteDataBtn);

        setHorizontalComponentAlignment(Alignment.START, buttonContainer2);


        Grid<CompanyIndustryDto> grid = new Grid<>(CompanyIndustryDto.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        Grid.Column<CompanyIndustryDto> idColumn = grid.addColumn(CompanyIndustryDto::getId).setHeader("Id"); // Add columns to display the properties of your data type
        idColumn.setVisible(false);
        grid.addColumn(CompanyIndustryDto::getName).setHeader("Tên ngành nghề");
        grid.addColumn(CompanyIndustryDto::getCreatedBy).setHeader("Người tạo");
        grid.addColumn(CompanyIndustryDto::getCreatedDate).setHeader("Ngày tạo");
        // Add an "Edit" button column
        Grid.Column<CompanyIndustryDto> editColumn = grid.addComponentColumn(item -> {
            Button editButton = new Button();
            editButton.setIcon(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_ICON);
            editButton.setTooltipText("Chỉnh sửa bản ghi");
            editButton.addClickListener(event -> {
                Notification.show("Bạn chọn chỉnh sửa bản ghi với ID: " + item.getId(), 5000, Notification.Position.MIDDLE);
            });
            return editButton;
        });
        editColumn.setHeader("Actions");

        grid.addSelectionListener(event -> {
            System.out.println("Số lượng bản ghi được chọn: " + event.getAllSelectedItems().size());
            deleteDataBtn.setEnabled(event.getAllSelectedItems().size() > 0);
        });

        DataProvider<CompanyIndustryDto, Void> dataProvider = DataProvider.fromCallbacks(
                query -> {

                    System.out.println("Page size: " + query.getPageSize());
                    System.out.println("Limit:" + query.getLimit());
                    System.out.println("Offset: " + query.getOffset());
                    System.out.println("Page: " + query.getPage());

                    CompanyIndustryDto corp1 = new CompanyIndustryDto();
                    corp1.setId(UUID.randomUUID().toString());
                    corp1.setName("Test");
                    corp1.setCreatedDate(LocalDateTime.now());
                    corp1.setCreatedBy("thanglv");

                    CompanyIndustryDto corp2 = new CompanyIndustryDto();
                    corp2.setId(UUID.randomUUID().toString());
                    corp2.setName("Test");
                    corp2.setCreatedDate(LocalDateTime.now());
                    corp2.setCreatedBy("thanglv");
                    return Stream.of(corp1, corp2);
                },
                query -> {
                    return 2;
                }
        );
        dataProvider.addDataProviderListener(event -> {
            System.out.println("Selected item : " + grid.getSelectedItems().size());
            deleteDataBtn.setEnabled(grid.getSelectedItems().size() > 0);
        });
        grid.setDataProvider(dataProvider);


        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Thông báo");
        dialog.setText(
                "Bạn có muốn xoá các bản ghi đã chọn?");

        dialog.setCancelable(false);

        dialog.setRejectable(true);
        dialog.setRejectText("Từ chối");
        dialog.addRejectListener(event -> {
            dialog.close();
        });

        dialog.setConfirmText("Xoá");
        dialog.addConfirmListener(event -> {
            Notification.show("Đang xoá!", 5000, Notification.Position.MIDDLE);
        });

        deleteDataBtn.setDisableOnClick(true);

        deleteDataBtn.addClickListener(event -> {
            final UI ui = UI.getCurrent();
            ui.accessSynchronously(() -> {
                ui.setEnabled(false);
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ui.setEnabled(true);
        });

        // init dialog
        createDataDialog.setHeaderTitle("Thêm mới ngành nghề công ty");
        createDataDialog.setCloseOnOutsideClick(false);
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> createDataDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        createDataDialog.getHeader().add(closeButton);

        TextField createIndustryNameInput = new TextField("Tên ngành nghề công ty");
        Binder<CreateCompanyIndustryVM> createCompanyIndustryVMBinder = new Binder<>();
        createCompanyIndustryVMBinder.forField(createIndustryNameInput).withValidator(Strings::isNotBlank, "Tên ngành nghề không được trống").bind(CreateCompanyIndustryVM::getName, CreateCompanyIndustryVM::setName);

        VerticalLayout dialogLayout = new VerticalLayout(createIndustryNameInput);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        createDataDialog.add(dialogLayout);

        Button saveButton = new Button("Thêm", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        final CreateCompanyIndustryVM createCompanyIndustryVM = new CreateCompanyIndustryVM();
        saveButton.addClickListener(event -> {
            BinderValidationStatus<CreateCompanyIndustryVM> binderValidationStatus = createCompanyIndustryVMBinder.validate();
            if (binderValidationStatus.hasErrors()) {
                binderValidationStatus.notifyBindingValidationStatusHandlers();
                return;
            }
            try {
                createCompanyIndustryVMBinder.writeBean(createCompanyIndustryVM);
                System.out.println("Input: " + createCompanyIndustryVM.getName());
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Huỷ", e -> createDataDialog.close());
        createDataDialog.getFooter().add(cancelButton);
        createDataDialog.getFooter().add(saveButton);
        createDataDialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                createCompanyIndustryVMBinder.refreshFields();
                createIndustryNameInput.clear();
            }
        });


        // Add form layout and button container to the main layout
        add(formLayout, buttonContainer, buttonContainer2, grid);
    }
}
